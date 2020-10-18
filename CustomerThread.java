/*
COMP2240 Assignment 2 Problem 2
File: CustomerThread.java
Author: Timothy Kemmis
Std no. c3329386
*/

import java.util.concurrent.Semaphore;

public class CustomerThread extends Thread {//custom thread class from the original java thread class
    private static Semaphore seatPass;//shared Semaphore for managing the available seats
    private String ID;//Thread ID
    private int arriveTime, eatingLen;//Thread attributes from input file
    private int seatTime, leaveTime;//Thread metric tracking variables

    //CustomerThread constructor that initialises all of the information about the thread from the input file and the shared semaphore
    public CustomerThread(String id, int aTime, int eLen, Semaphore s){
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

    @Override//The overridden run method of the thread
    public void run() {
        seatTime = Restaurant.getTime();//Metric tracking for when the customer took a seat using the shared time variable in Restaurant
        while (true){//While method that continues until the customer has been seated for their eating length
            if (Restaurant.getTime()-seatTime>=eatingLen)
                break;
            try {
                Thread.sleep(1);//Short sleep that ensures the thread checks the "if" multiple times between time increases
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        seatPass.release();//Release the semaphore token once the customer has finished "eating"
        leaveTime = Restaurant.getTime();//metric tracking for when the customer leaves the restaurant
    }

    @Override//Outputs the ID, arrival time, seated time and left time of the thread
    public String toString() {//ToString method to format and output the statistics of the thread as per the spec
        return  ID  + "\t\t\t" + arriveTime + "\t\t\t" + seatTime + "\t\t" + leaveTime;
    }
}
