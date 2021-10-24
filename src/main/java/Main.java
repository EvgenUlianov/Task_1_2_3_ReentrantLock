import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        System.out.println("Задача 3. Ресторан");

        final int numberOfCooks = 1;
        final int numberOfWaiters = 3;
        final int numberOfCostumers = 15;


        final ExecutorService threadPool = Executors.newFixedThreadPool(numberOfCooks
                + numberOfWaiters
                + numberOfCostumers);

        for (int i = 0; i < numberOfCooks; i++)
            threadPool.execute(new Cook(i + 1));
        for (int i = 0; i < numberOfWaiters; i++)
            threadPool.execute(new Waiter(i + 1));
        for (int i = 0; i < numberOfCostumers; i++)
            threadPool.execute(new Customer(i + 1));

        final int timeOut1 = 1_000;
        OrderList orderList = OrderList.get();
        boolean needToClose = false;
        while (true) {

            orderList.lock();
            try {
                needToClose = orderList.allOrdersAreEATED();
            } finally {
                orderList.unlock();
            }

            try {
                Thread.sleep(timeOut1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (needToClose) {
                System.out.println("Ресторан закрывается");
                threadPool.shutdownNow();
                return;
            }
        }



    }
}
