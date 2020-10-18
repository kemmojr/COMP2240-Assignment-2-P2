/*
COMP2240 Assignment 2 Problem 2
File: P2.java
Author: Timothy Kemmis
Std no. c3329386
Description: A program to simulate a restaurant with limited seating complying with COVID restrictions
by managing the seating resources between the incoming customers and waiting for cleaning if the restaurant reaches capacity.
Solution implemented using a semaphore
*/

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


public class P2 {
    public static void main(String args[]){
        Semaphore availableSeats = new Semaphore(5);//Semaphore to manage the number of empty/filled seats

        ArrayList<CustomerThread> customers = new ArrayList<>();//Arraylist for storing the CustomerThreads read in from the file
        int numCustomers = 0;//Total number of customers in the file
        try {
            Scanner testReader = new Scanner(new FileInputStream(args[0]));//A test scanner to iterate through all of the input file to calculate the number of customers
            Scanner reader = new Scanner(new FileInputStream(args[0]));//A scanner to read the data from the input file and create all the CustomerThreads
            int numItemsRead = 0;
            while (testReader.hasNext()){
                //While loop to work out exactly how many customers there are by counting the total number of separate strings
                String currentItem = testReader.next();
                if (currentItem.equalsIgnoreCase("END"))
                    break;
                numItemsRead++;
            }
            numCustomers = numItemsRead/3;//How many customers there are in the file. I have assumed that the input files used will be without formatting mistakes
            for (int i = 0; i < numCustomers; i++){
                //Creates all the customer objects from the input file
                int arriveTime = reader.nextInt();
                String id = reader.next();
                int eatingLength = reader.nextInt();
                customers.add(new CustomerThread(id, arriveTime, eatingLength, availableSeats));//Creates new threads for each customer and adds them to an ArrayList for storage
            }

        } catch (Exception e){
            System.out.println("Reading from file failed");
        }

        Restaurant restaurant = new Restaurant(availableSeats, numCustomers);//A restaurant class for managing the seating in the restaurant

        while (!restaurant.allServed()){//While loop that continues until all customers have been served, checking and starting the threads at the correct time
            try {
                TimeUnit.MILLISECONDS.sleep(2);//Wait statement to ensure that the threads or method checks don't execute out of order and time does not increase too rapidly
                restaurant.check(customers);//Enters and waits the customer threads as necessary
                TimeUnit.MILLISECONDS.sleep(2);//See above reason
            } catch (Exception e){
                System.out.println("Main Sleep failed");
            }
            Restaurant.incrementTime();//Increments the time
        }
        Restaurant.outputStats();//Outputs the customer statistics as per the spec
    }
}
