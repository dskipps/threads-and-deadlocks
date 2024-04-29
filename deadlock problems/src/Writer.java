class Writer extends Thread {
    int wID;

    Writer(int id) {
        wID = id;
    }

    public void run() {

        Main.Writercontrol.acquireUninterruptibly();
        if(Main.total == Main.readers){
            System.out.println("No Threads ready or runnable, and no pending interrupts.");
            System.exit(0);
        }
        Main.Writercontrol.release();
        Main.Writercontrol.acquireUninterruptibly();
        Main.Wplus++;

        System.out.println("W" + wID + " started writing");
        System.out.println("W" + wID + " finished writing");
        for (int i = 0; i < Main.max; i++) {
            Main.Readercontrol.release();
        }





    }
}