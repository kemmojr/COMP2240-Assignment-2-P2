import jdk.nashorn.internal.objects.Global;

import java.io.FileInputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class P2 {
    public static void main(String args[]){
        Semaphore avaliableSeats = new Semaphore(5);
        int time = 0;

        ArrayList<CustomerThread> customers = new ArrayList<>();
        int numCustomers = 0;
        try {
            Scanner testReader = new Scanner(new FileInputStream(args[0]));
            Scanner reader = new Scanner(new FileInputStream(args[0]));
            int numItemsRead = 0;
            while (testReader.hasNext()){

                String currentItem = testReader.next();
                if (currentItem.equalsIgnoreCase("END")){
                    break;
                }
                numItemsRead++;
            }
            numCustomers = numItemsRead/3;
            System.out.println("There are " + numCustomers + " customers in this file");
            for (int i = 0; i < numCustomers; i++){

                int arriveTime = reader.nextInt();
                String id = reader.next();
                int eatingLength = reader.nextInt();
                customers.add(new CustomerThread(id,arriveTime,eatingLength,time,avaliableSeats));
            }

        } catch (Exception e){
            System.out.println("Reading from file failed");
        }

        Restaurant restaurant = new Restaurant(0, avaliableSeats, numCustomers);

        while (!restaurant.allServed()){
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (Exception e){
                System.out.println("Main Sleep 1 failed");
            }
            restaurant.check(customers);
            try {
                restaurant.updateFull();
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (Exception e){
                System.out.println("Main Sleep 2 failed");
            }

            Restaurant.incrementTime();
        }
        Restaurant.outputStats();
    }
}
