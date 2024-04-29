import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.Scanner;

public class Main {
    static int Wplus = 0;
    static int total = 0;
    public static Semaphore fix;
    public static Semaphore Readercontrol;
    public static Semaphore Writercontrol;
    public static int readers;
    public static int writers;
    public static int max;
    public static int message_num;
    public static int capacity;
    public static int people_num;
    public static int phil_num;
    public static int meal_num;

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i]);


            //check input
            if (args[0].equals("-A")) {

                if (args[1].equals("1")) {
                    Scanner scan = new Scanner(System.in);
                    System.out.print("Enter number of philosophers: ");
                    phil_num = scan.nextInt();
                    Chopstick[] chopsticks = new Chopstick[phil_num];

                    while (phil_num < 1) {
                        System.out.println("invalid input");
                        System.out.print("Enter number of philosophers: ");
                        phil_num = scan.nextInt();
                    }
                    System.out.print("Enter number of meals: ");
                    meal_num = scan.nextInt();
                    while (meal_num < 1) {
                        System.out.println("invalid input");
                        System.out.print("Enter number of meals: ");
                        meal_num = scan.nextInt();
                    }
                    //number of phils
                    for (int k = 0; k < phil_num; k++) {
                        chopsticks[k] = new Chopstick();
                    }
                    for (int ik = 0; i < phil_num; ik++) {
                        // new thread number then left stick then right
                        threads t1 = new threads(ik, chopsticks[ik], chopsticks[(ik + 1) % phil_num]);
                        //System.out.println(chopsticks[i]);
                        //System.out.println(chopsticks[(i + 1) % phil_num]);
                        t1.start();
                    }
                }
                else if (args[1].equals("2")) {

                    Scanner scan = new Scanner(System.in);
                    System.out.print("Enter number of people: ");
                    people_num = scan.nextInt();
                    System.out.print("Enter the capacity of a mailbox: ");
                    capacity = scan.nextInt();
                    System.out.print("Enter total number of messages: ");
                    message_num = scan.nextInt();
                    for (int ip = 0; ip < people_num; ip++) {
                        Person t2 = new Person(ip);
                        t2.start();
                    }

                }
                //else if 3
                else if (args[1].equals("3")){
                    Scanner scan = new Scanner(System.in);
                    System.out.print("Enter a Readers count: ");
                    readers = scan.nextInt();
                    System.out.print("Enter a Writers count: ");
                    writers = scan.nextInt();
                    System.out.print("Enter a Max readers count: ");
                    max = scan.nextInt();
                    Readercontrol = new Semaphore(max);
                    Writercontrol = new Semaphore(0);
                    //get all readers
                    for (int ir = 0; ir < readers; ir++) {
                        Reader R = new Reader(ir);
                        R.start();
                    }
                    //get all writers
                    for (int iw = 0; iw < writers; iw++) {
                        Writer W = new Writer(iw);
                        W.start();
                    }
                }
            }
        }








    }
    //class for the stick
    static class Chopstick
    {
        //creating a constructor of the Semaphore class that accepts the number permits
        public Semaphore mutex = new Semaphore(1);
        //public Semaphore mutexR = new Semaphore(2);

        //the method grabs the chopstick
        void grab()
        {

//acquires a permit from the semaphore
            mutex.acquireUninterruptibly();


        }

        //release the chopstick
        void release()
        {
//releases and acquire a permit and increases the number of available permits by one
            mutex.release();
        }


        //checks if the chopstick is free or not



    }
}


class threads extends Thread {

    Random rand = new Random();

    //[rotect meal count
    static Semaphore mealMutex = new Semaphore(1);
    // barrier
    static Semaphore barrier = new Semaphore(0);
    //barrier mutex
    static Semaphore barmutex = new Semaphore(1);
    static int Bcount = 0;
    static int B2count = 0;
    int tID;
    boolean hasSticks = false;

    Main.Chopstick leftchopstick;
    Main.Chopstick rightchopstick;

    public threads(int id, Main.Chopstick left, Main.Chopstick right) {
        tID = id;
        leftchopstick = left;
        rightchopstick = right;
    }

    public void run() {
        barmutex.acquireUninterruptibly();
        Bcount++;

        if (Bcount == Main.phil_num) {
            System.out.println("Philosopher " + tID + " is the last to arrive");
            System.out.println("All philosophers are present, they sit");
            barrier.release(Main.phil_num);
        } else {
            System.out.println("Philosopher " + tID + " ,has entered");
        }
        barmutex.release();
        barrier.acquireUninterruptibly();
        mealMutex.acquireUninterruptibly();
        //the testing
        //long start_time = System.nanoTime();
        while (Main.meal_num > 0) {
            mealMutex.release();
            while (!hasSticks) {
                hasSticks = true;
                leftchopstick.grab();
                System.out.println("Philosopher " + tID + " left chopstick is available");
                //Thread.yield();
                if (rightchopstick.mutex.tryAcquire()) {
                    System.out.println("Philosopher " + tID + " right chopstick is available");
                    hasSticks = true;
                } else {
                    leftchopstick.release();
                }
            }


            int ran = rand.nextInt(4);
            ran += 3;
            System.out.println("Philosopher " + tID + " is eating");
            for (int i = 0; i < ran; i++) {
                Thread.yield();
            }
            //if (!leftchopstick.isFree()) {
                System.out.println("Philosopher " + tID + " puts down left chopstick");
                leftchopstick.release();
                //Thread.yield();
                System.out.println("Philosopher " + tID + " puts down right chopstick");
                rightchopstick.release();
                mealMutex.acquireUninterruptibly();

            if (Main.meal_num > 0) {
                Main.meal_num -= 1;
                System.out.println("meals left " + (Main.meal_num));
                mealMutex.release();
                hasSticks = false;
                int ran2 = rand.nextInt(4);
                ran2 += 3;
                System.out.println("Philosopher " + tID + " is thinking");
                    for (int i = 0; i < ran2; i++) {
                        Thread.yield();
                    }
                }

            //}


            barmutex.acquireUninterruptibly();
            B2count++;

            if (B2count == Main.phil_num) {
                System.out.println("Philosopher " + tID + " is ready to leave");
                System.out.println("all Philosophers are ready to leave");

                barrier.release(Main.phil_num);
            } else {
                System.out.println("Philosopher " + tID + " is ready to leave");
            }
            barmutex.release();
            barrier.acquireUninterruptibly();
            System.out.println("Philosopher " + tID + " is leaving");

        }
        //long end_time = System.nanoTime();
        //System.out.printf("Runtime in milliseconds = ");
        //System.out.println((end_time - start_time) / 1000000.0);


    }
}




