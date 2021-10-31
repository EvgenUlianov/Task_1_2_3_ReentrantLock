import java.util.Random;

public class Customer extends Thread implements Runnable{
    private int priority;
    public Customer(int number) {
        super(String.format("Покупатель %d", number));
        priority = number;
    }

    @Override
    public void run() {
        Random random = new Random();
        final int firstTimeOut = 500 + priority * 250;
        final int timeOut = 500 + random.nextInt(1000);
        String currentName = super.getName();
        Restaurant restaurant = Restaurant.get();

        BasicFunctions.sleep(firstTimeOut);
        System.out.printf("%s в ресторане.\n", currentName);


        restaurant.addOrder(this, currentName, timeOut);

        boolean successful = false;

        restaurant.eatOrder(this, currentName, timeOut);
        System.out.printf("%s покинул ресторан.\n", currentName);
        return;

    }

}
