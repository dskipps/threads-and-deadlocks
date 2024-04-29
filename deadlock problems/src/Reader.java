import java.util.concurrent.Semaphore;

class Reader extends Thread {
    static Semaphore countM = new Semaphore(1);
    static int rCount = 0;
    int rID;
    boolean gate = true;

    public Reader(int id) {
        rID = id;
    }

    public void run() {


        while (gate) {
            gate = false;
            Main.Readercontrol.acquireUninterruptibly();
            if(Main.Wplus == Main.writers){
                System.out.println("No Threads ready or runnable, and no pending interrupts.");
                System.exit(0);
            }
            Main.Readercontrol.release();
            Main.Readercontrol.acquireUninterruptibly();

            System.out.println("R" + rID + " started reading");
            countM.acquireUninterruptibly();
            rCount++;
            Main.total++;
            if (rCount == Main.max) {
                rCount = 0;
                Main.Writercontrol.release();
            }
            System.out.println("R" + rID + " finished reading. Total reads:" + Main.total);
            countM.release();


        }
    }

}
