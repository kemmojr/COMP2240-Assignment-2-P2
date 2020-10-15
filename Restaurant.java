

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


public class Restaurant {
    private static int time, servedCustomers;
    private static AtomicBoolean full;
    private static Semaphore avaliableSeats;
    private int totalCustomers;
    private static int threadTimeRemaining;
    private static ArrayList<CustomerThread> finishedCustomers;
    
    public Restaurant(int t, Semaphore s, int numTotalCustomers){
        time = t;
        avaliableSeats = s;
        totalCustomers = numTotalCustomers;
        servedCustomers = 0;
        full = new AtomicBoolean(false);
        threadTimeRemaining = 0;
        finishedCustomers = new ArrayList<>();
    }

    public static int getTime() {
        return time;
    }

    public static void incrementTime(){
        time++;
        threadTimeRemaining--;
    }

    public void updateThreadTimeRemaining(int remainingTime){
        if (remainingTime > threadTimeRemaining)
            threadTimeRemaining = remainingTime;
    }

    public void check(ArrayList<CustomerThread> customers){
        if (full.get()){
            if (avaliableSeats.availablePermits()==5) {
                time += 5;
                full.set(false);
                try {
                    TimeUnit.MILLISECONDS.sleep(4);
                } catch (Exception e){
                    System.out.println("Restaurant sleep failed");
                }

            } else
                return;
        }
        int size = customers.size();

        for(int i=size; i > 0; i--){
            CustomerThread c = customers.get(0);
            if (c.getArriveTime()<=time){
                if (avaliableSeats.availablePermits()>0){
                    customers.remove(c);
                    updateThreadTimeRemaining(c.getEatingLen());
                    finishedCustomers.add(c);
                    enter(c);
                } else {
                    full.set(true);
                    break;
                }
            }
        }
    }

    public void enter(CustomerThread c){
        try {
            avaliableSeats.acquire();
            c.start();
            servedCustomers++;
        } catch (Exception e){
            System.out.println("Entering failed");
        }
    }

    public boolean allServed(){//Check to see if all of the customers have been served and have finished executing
        if (servedCustomers==totalCustomers && threadTimeRemaining <= 0){
            return true;
        }
        return false;
    }

    public static void outputStats(){//Ouputs the statistics of the customers

        try {
            TimeUnit.MILLISECONDS.sleep(4);//A wait to ensure that all threads have finished executing
        } catch (Exception e){

        }
        System.out.println("Customer\tArrives\t\tSeats\tLeaves");//Ouput formatting as per spec
        for (CustomerThread c:finishedCustomers){
            System.out.println(c);
        }
    }
}
