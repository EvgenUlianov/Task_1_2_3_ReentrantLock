import java.util.Random;

public class Waiter extends Thread implements Runnable{
    public Waiter(int number) {
        super(String.format("Официант(ка) %d", number));
    }

    @Override
    public void run() {
        Random random = new Random();
        final int timeOut = 500 + random.nextInt(1000);
        String currentName = super.getName();

        System.out.printf("%s в ресторане.\n", currentName);
        Restaurant restaurant = Restaurant.get();

        boolean needToClose = false;
        while (!needToClose) {

            restaurant.acceptOrCarryOrder(currentName, timeOut);
            needToClose = restaurant.allOrdersAreEATEN();

        }
    }
}
