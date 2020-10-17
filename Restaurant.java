/*
COMP2240 Assignment 2 Problem 2
File: Restaurant.java
Author: Timothy Kemmis
Std no. c3329386
Description: A program to simulate a restaurant with limited seating complying with COVID restrictions
by managing the seating resources between the incoming customers and waiting for cleaning if the restaurant reaches capacity
*/

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


public class Restaurant {
    private static int time, servedCustomers;//Shared time variable and variable for tracking the number of customers that have entered the store
    private static AtomicBoolean full;//Atomic boolean used for tracking if the restaurant has reached max capacity and requires cleaning
    private static Semaphore availableSeats;//initialising an instance of the shared seat tracking semaphore
    private int totalCustomers;//The total number of customers to be served from the input file
    private static int threadTimeRemaining;//The amount of time remaining until the last running thread finishes
    private static ArrayList<CustomerThread> finishedCustomers;//An ArrayList used for holding the threads that have been executed

    //Constructor for the restaurant that initialises the restaurant variables and adds the shared semaphore and the total number of customers from the input file
    public Restaurant(Semaphore s, int numTotalCustomers){
        time = 0;
        availableSeats = s;
        totalCustomers = numTotalCustomers;
        servedCustomers = 0;
        full = new AtomicBoolean(false);
        threadTimeRemaining = 0;
        finishedCustomers = new ArrayList<>();
    }

    //getter
    public static int getTime() {
        return time;
    }

    //increments the time and negates the passed time from the time left to finish the longest running thread
    public static void incrementTime(){
        time++;
        threadTimeRemaining--;
    }

    //Updates the time left to finish the longest running thread if the argument (the time to finish the current thread) will finish at a later time
    public void updateThreadTimeRemaining(int remainingTime){
        if (remainingTime > threadTimeRemaining)
            threadTimeRemaining = remainingTime;
    }

    //The function that is called repeatedly that checks the list of all CustomerThreads and starts the Thread if the CustomerThread start time is the current time
    public void check(ArrayList<CustomerThread> customers){
        if (full.get()){//If the restaurant is full then don't start any threads until all current customers have finished eating and the 5 minute cleaning has occurred
            if (availableSeats.availablePermits()==5) {
                time += 5;
                full.set(false);
            } else{
                return;
            }
        }

        int size = customers.size();
        for(int i=size; i > 0; i--){//loop that iterates through all of the customers waiting and enters them if the seating will allow it
            CustomerThread c = customers.get(0);
            if (c.getArriveTime()<=time){
                if (availableSeats.availablePermits()>0){
                    customers.remove(c);
                    updateThreadTimeRemaining(c.getEatingLen());
                    finishedCustomers.add(c);
                    enter(c);
                    if (availableSeats.availablePermits()==0){//If the last seat has been taken then set the restaurant to full and stop entering customers
                        full.set(true);
                        break;
                    }
                }
            }
        }
    }

    //Method that acquires a semaphore token for the seat and starts the thread
    public void enter(CustomerThread c){
        try {
            availableSeats.acquire();
            c.start();
            servedCustomers++;
        } catch (Exception e){
            System.out.println("Entering failed");
        }
    }

    public boolean allServed(){//Check to see if all of the customers have been served (entered) and have finished executing (eaten for their eating time)
        if (servedCustomers==totalCustomers && threadTimeRemaining <= 0){
            return true;
        }
        return false;
    }

    public static void outputStats(){//Ouputs the statistics of the customers

        try {
            TimeUnit.MILLISECONDS.sleep(4);//A wait to ensure that the last threads have finished executing
        } catch (Exception e){
            System.out.println("Stat wait failed");
        }
        System.out.println("Customer\tArrives\t\tSeats\tLeaves");//Ouput formatting as per spec
        for (CustomerThread c:finishedCustomers){//iterates through all customers and outputs their statistics
            System.out.println(c);
        }
    }
}
