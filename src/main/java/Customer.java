import java.util.Random;

public class Customer extends Thread implements Runnable{
    private int priority;
    public Customer(int number) {
        super(String.format("Покупатель %d", number));
        priority = number;
    }

    @Override
    public void run() {
//        final int timeOut15 = 15_000;
        Random random = new Random();
        final int firstTimeOut = 500 + priority * 250;
        final int timeOut = 500 + random.nextInt(1000);
        String currentName = super.getName();

        try {
            Thread.sleep(firstTimeOut);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("%s в ресторане.\n", currentName);

        try {
            Thread.sleep(timeOut);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        OrderList orderList = OrderList.get();
        orderList.lock();
        try {
            orderList.addOrder(currentName);
            orderList.signalAll();
        } finally {
            orderList.unlock();
        }

        while (true) {
            boolean successful = false;
            int index = -1;


            orderList.lock();
            try {
                index = orderList.findOrderByCustomersName(currentName);
            } finally {
                orderList.unlock();
            }

            if (index >= 0) {
                orderList.lock(index);
                try {
                    successful = orderList.canEatOrder(index);
                    if (successful) {
                        try {
                            Thread.sleep(timeOut);
                            orderList.eatOrder(index);
                        } catch (InterruptedException e) {
                            successful = false;
                            e.printStackTrace();
                        }
                    }
                    if (!successful){
//                        try {
//                            orderList.await(index);
//                        } catch (InterruptedException e) {
////                        e.printStackTrace();
//                        }
                    }
                } finally {
                    orderList.unlock(index);

                }


            }


            if (successful) {

                try {
                    Thread.sleep(timeOut);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.printf("%s покинул ресторан.\n", currentName);
                return;
            } /*else
            */

            if (interrupted())
                return;
        }
    }

}
