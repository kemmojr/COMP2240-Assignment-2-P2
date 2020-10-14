import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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

    public int activeThreadTimeRemaining(){
        return 0;
    }

    public void check(ArrayList<CustomerThread> customers){
        if (full.get()){
            if (avaliableSeats.availablePermits()==5) {
                time += 5;
                full.set(false);
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
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
                    System.out.println("The restaurant is full");
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

    public boolean allServed(){
        if (servedCustomers==totalCustomers && threadTimeRemaining <= 0){
            return true;
        }
        return false;
    }

    public void updateFull(){
        if (avaliableSeats.availablePermits()==0){
            full.set(true);
        }
    }

    public static void outputStats(){

        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (Exception e){

        }

        System.out.println("Customer\tArrives\t\tSeats\tLeaves");
        for (CustomerThread c:finishedCustomers){
            System.out.println(c);
        }
    }
}
