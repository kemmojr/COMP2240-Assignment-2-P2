

import java.io.FileInputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


public class P2 {
    public static void main(String args[]){
        Semaphore avaliableSeats = new Semaphore(5);//Semaphore to manage the number of empty/filled seats
        int time = 0;

        ArrayList<CustomerThread> customers = new ArrayList<>();//Arraylist for storing the customers read in from the file as threads
        int numCustomers = 0;
        try {
            Scanner testReader = new Scanner(new FileInputStream(args[0]));
            Scanner reader = new Scanner(new FileInputStream(args[0]));
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
                customers.add(new CustomerThread(id,arriveTime,eatingLength,time,avaliableSeats));
            }

        } catch (Exception e){
            System.out.println("Reading from file failed");
        }

        Restaurant restaurant = new Restaurant(0, avaliableSeats, numCustomers);//A restaurant class for managing the seating in the restaurant

        while (!restaurant.allServed()){//Continues until all customers have been served
            try {
                TimeUnit.MILLISECONDS.sleep(4);//Wait statement to ensure that the threads don't execute out of order which causes incorrect output
                restaurant.check(customers);//Enters and waits the customer threads as necessary
                TimeUnit.MILLISECONDS.sleep(4);//See above reason
            } catch (Exception e){
                System.out.println("Main Sleep failed");
            }
            Restaurant.incrementTime();//Increments the time
        }
        Restaurant.outputStats();//Outputs the customer statistics as per the spec
    }
}
