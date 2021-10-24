import java.util.Random;

public class Cook extends Thread implements Runnable{
    public Cook(int number) {
        super(String.format("Повар %d", number));
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
                index = orderList.findOrderACCEPTED();
            } finally {
                orderList.unlock();
            }

            if (index >= 0) {
                orderList.lock(index);
                try {
                    successful = orderList.canPrepareOrder(index);
                    if (successful) {
                        try {
                            Thread.sleep(timeOut);
                            orderList.prepareOrder(index, currentName);
                        } catch (InterruptedException e) {
                            successful = false;
                            e.printStackTrace();
                        }
                    }
                    if (successful) {
                        orderList.signalAll(index);
                    } else {
//                        try {
//                            orderList.await(index);
//                        } catch (InterruptedException e) {
//                            //                    e.printStackTrace();
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
