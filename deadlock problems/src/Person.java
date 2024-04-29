import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.Scanner;
class Person extends Thread {
    static int iteration = 0;
    static String[] messages_type = {"Hi", "How are you", "How is your day", "What time is it", "Where are you"};
    static Semaphore[] mailboxsem = new Semaphore[Main.people_num];
    static Semaphore[] freespace = new Semaphore[Main.people_num];
    static Semaphore mutex = new Semaphore(1);
    static String[][] MailBox = new String[Main.people_num][Main.capacity];

    Random P = new Random();
    Random timer = new Random();
    int tID;
    int p = 0;

    public Person(int id) {
        tID = id;
        for (int i = 0; i < Main.people_num; i++) {
            mailboxsem[i] = new Semaphore(1);
            freespace[i] = new Semaphore(1);
        }
    }

    public void run() {
        //String[][] MailBox = new String[Main.people_num][Main.capacity];
        //iterates messages
        while (p < Main.message_num) {
            //if access is true let in if not jump to else
            System.out.println("person " + tID + " enters the post office");


            // iteration can be 0 but dosent mean there is nothing
            //check if mailbox is empty or has something
            if (MailBox[tID][0] == null) {
                //print statments
                System.out.println("Person " + tID + " checks - he has 0 letters in mailbox.");
                System.out.println("Person " + tID + "'s mailbox is empty");
                //find a random person to send to
                int randP = P.nextInt(Main.people_num);
                //if the randP = tID change it
                while (tID == randP) {
                    randP = P.nextInt(Main.people_num);
                }

                //send a message to another person
                System.out.println("Person " + tID + " is trying to send to person " + randP);
                //need a semi for right here to send that free space will get grabbed( - )and mailbox( + ) for the person gets releases
                mailboxsem[randP].acquireUninterruptibly();
                int index = 0;

                for (int m = 0; m < Main.capacity; m++) {
                    if (MailBox[randP][m] == null) {
                        index = m;
                        break;
                    }
                }
                Random n = new Random();
                int ranN = n.nextInt(4);
                if(freespace[randP].tryAcquire()){
                    MailBox[randP][index] = messages_type[ranN];
                    System.out.println("message sent");
                }
                else{
                    System.out.println("mailbox is full");
                }
                //release
                freespace[randP].release();
                mailboxsem[randP].release();
            }
            //set an else for if mailbox is not empty
            else {
                mutex.acquireUninterruptibly();
                //check mailbox length to see how many are null
                for (int x = 0; x < MailBox.length; x++) {
                    if (MailBox[tID][x] == null) {
                        Thread.yield();
                    }
                    // if its not null go to the next
                    else {
                        iteration++;
                    }
                }
                mutex.release();
                mailboxsem[tID].acquireUninterruptibly();
                System.out.println("Person " + tID + " checks - he has " + iteration + " letters in mailbox.");
                //claim the first letter in the mailbox read it if there is another claim that after
                for (int l = 0; l < iteration; l++) {
                    System.out.println("person " + tID + " reads message " + l + ":" + MailBox[tID][l]);
                    MailBox[tID][l] = null;
                    p++;
                }
                mailboxsem[tID].release();


            }

            System.out.println("person " + tID + " leaves the post office");
            int timer1 = timer.nextInt(4);
            timer1 += 3;
            for (int q = 0; q < timer1; q++) {
                Thread.yield();
            }

        }

    }
}