/*
COMP2240 Assignment 2 Problem 2
File: CustomerThread.java
Author: Timothy Kemmis
Std no. c3329386
Description: A program to simulate a restaurant with limited seating complying with COVID restrictions
by managing the seating resources between the incoming customers and waiting for cleaning if the restaurant reaches capacity
*/

import java.util.concurrent.Semaphore;

public class CustomerThread extends Thread {//custom thread class from the original java thread class
    private static Semaphore seatPass;//shared Semaphore for managing the available seats
    private String ID;//Thread ID
    private int arriveTime, eatingLen;//Thread attributes from input file
    private int seatTime, leaveTime;//Thread metric tracking variables

    public CustomerThread(String id, int aTime, int eLen, Semaphore s){//CustomerThread constructor that initialises all of the information about the thread from the input file.
        ID = id;
        arriveTime = aTime;
        eatingLen = eLen;
        seatPass = s;//initialising an instance of the shared seat tracking semaphore
    }

    //getters
    public int getArriveTime(){
        return arriveTime;
    }

    public int getEatingLen(){
        return eatingLen;
    }

    @Override
    public void run() {//The overridden run method of the thread
        seatTime = Restaurant.getTime();//Metric tracking for when the customer took a seat using the shared time variable in Restaurant
        while (true){//While method that continues until the customer has been seated for their eating length
            if (Restaurant.getTime()-seatTime>=eatingLen)
                break;
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        seatPass.release();//Release the semaphore token once the customer has finished "eating"
        leaveTime = Restaurant.getTime();//metric tracking for when the customer leaves the restaurant
    }

    @Override
    public String toString() {//ToString method to format and output the statistics of the thread as per the spec
        return  ID  + "\t\t\t" + arriveTime + "\t\t\t" + seatTime + "\t\t" + leaveTime;
    }
}
