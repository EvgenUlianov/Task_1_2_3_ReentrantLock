import java.util.Random;

public class Waiter extends Thread implements Runnable{
    public Waiter(int number) {
        super(String.format("Официант(ка) %d", number));
    }

    @Override
    public void run() {
//        final int timeOut15 = 15_000;
        Random random = new Random();
        final int timeOut = 500 + random.nextInt(1000);
        String currentName = super.getName();

        System.out.printf("%s в ресторане.\n", currentName);

        OrderList orderList = OrderList.get();

        while (true) {
            boolean successful = false;
            int index = -1;

            orderList.lock();
            try {
                index = orderList.findOrderORDEREDorPREPARED();
            } finally {
                orderList.unlock();
            }

            if (index >= 0) {
                orderList.lock(index);
                try {
                    successful = orderList.canAcceptOrCarryOrder(index);
                    if (successful) {
                        try {
                            Thread.sleep(timeOut);
                            orderList.acceptOrCarryOrder(index, currentName);
                        } catch (InterruptedException e) {
                            successful = false;
                            e.printStackTrace();
                        }
                    }
                    if (successful) {
                        orderList.signalAll(index);
                    } else  {
//                        try {
//                            orderList.await(index);
//                        } catch (InterruptedException e) {
////                    e.printStackTrace();
//                        }
                    }
                } finally {
                    orderList.unlock(index);
                }
            }

            if (interrupted())
                return;
        }
    }

}
