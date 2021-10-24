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
                index = orderList.findOrderORDEREDorPREPARED(0);
            } finally {
                orderList.unlock();
            }

            if (index >= 0) {
                boolean successfulLock = false;
                while (!successfulLock && index >= 0) {
                    successfulLock = orderList.tryLock(index);
                    if (!successfulLock) {
                        orderList.lock();
                        try {
                            index = orderList.findOrderORDEREDorPREPARED(index + 1);
                        } finally {
                            orderList.unlock();
                        }
                    }
                }
                if (index >= 0) {
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

            }

            if (interrupted())
                return;
        }
    }

}
