import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomerThread extends Thread {
    private static Semaphore seatPass;
    private String ID;
    private int arriveTime, eatingLen;
    private int seatTime, leaveTime;

    public CustomerThread(String id, int aTime, int eLen, int t, Semaphore s){
        ID = id;
        arriveTime = aTime;
        eatingLen = eLen;
        seatPass = s;
    }

    public int getArriveTime(){
        return arriveTime;
    }

    public int getEatingLen(){
        return eatingLen;
    }

    @Override
    public void run() {
        seatTime = Restaurant.getTime();
        System.out.println("Customer " + ID + " entered at " + seatTime);
        while (true){
            if (Restaurant.getTime()-seatTime>=eatingLen)
                break;
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("Thread sleep failed");
            }
        }
        seatPass.release();
        leaveTime = Restaurant.getTime();
        System.out.println("Customer " + ID + " left at " + leaveTime);
    }

    @Override
    public String toString() {
        return  ID  + "\t\t\t" + arriveTime + "\t\t\t" + seatTime + "\t\t" + leaveTime;
    }
}
