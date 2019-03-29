import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HelloWorld {

    ArrayList<String> fruitsIn = new ArrayList<String>();
    final ArrayList<String> fruitsOut = new ArrayList<String>();
    final static Object mutex = new Object();

    private static final Logger LOGGER = LoggerFactory.getLogger(
            Thread.currentThread().getStackTrace()[0].getClassName() );


    public static void main(String []args){
        LOGGER.info("main(String []args)");

        final HelloWorld testHello = new HelloWorld();
        testHello.fruitsIn.add("Банан");
        testHello.fruitsIn.add("Яблоко");
        testHello.fruitsIn.add("Груша");
        testHello.fruitsIn.add("Арбуз");
        testHello.fruitsIn.add("Ананас");
        testHello.fruitsIn.add("Апельсин");
        testHello.test();
        System.exit(0);

    }

    public void test() {

        WorkerRunnable workerRunnable = new WorkerRunnable();
        Thread thread = new Thread(workerRunnable);
        thread.start();

        synchronized (mutex){
            try{
                LOGGER.info(String.format("start wait: %d", mutex.hashCode()));
                mutex.wait();
                LOGGER.info("stop wait");

                for(String fruit : fruitsIn){

                    mutex.wait((long) (500 * Math.random()));

                    fruitsOut.add(fruit);
                    LOGGER.info(String.format("fruitsOut.add(fruit) fruit = %s", fruit));
                    mutex.notify();
                    LOGGER.info(String.format(" test(): fruitsOut.notify()", fruit));

                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public class WorkerRunnable  implements Runnable {

        @Override
        public void run() {
            synchronized (mutex) {
                mutex.notify();
                LOGGER.info(String.format("fruitsOut.notify(): %d", mutex.hashCode()));
                String lastOutput = null;
                while (true){
                    try {
                        LOGGER.info("start wait");
                        mutex.wait();
                        LOGGER.info("stop wait");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    LOGGER.info("run() : while (true)");

                    if(fruitsOut.size()>0){

                        LOGGER.info("run() : if(fruitsOut.size()>0)");



                        String lastFruit = fruitsOut.get(fruitsOut.size()-1);
                        if(lastFruit!=lastOutput){
                            LOGGER.info("3");
                            System.out.println(lastFruit);
                            lastOutput = lastFruit;

                        }
                    }

                }
            }

        }
    }
}
