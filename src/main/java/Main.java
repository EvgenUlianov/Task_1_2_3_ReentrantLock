import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        System.out.println("Задача 3. Ресторан");

        final int numberOfCooks = 1;
        final int numberOfWaiters = 3;
        final int numberOfCostumers = 15;
        Restaurant restaurant = Restaurant.get();
        restaurant.setCustomersLimit(numberOfCostumers);


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
        final int timeOutLong = 15_000;
        BasicFunctions.sleep(timeOutLong);
        boolean needToClose = false;
        while (!needToClose) {
            needToClose = restaurant.allOrdersAreEATEN();
        }
        BasicFunctions.sleep(timeOut1);
        System.out.println("Ресторан закрывается");
        threadPool.shutdownNow();
    }
}
