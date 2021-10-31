import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Restaurant {

    private final Lock lockWaiters;
    private final Lock lockCooks;
    private final Lock lockCustomers;


    private final Condition conditionWaiters;
    private final Condition conditionCooks;
    private final Condition conditionCustomers;

    private final Queue<Order> ordersOrdered;
    private final Queue<Order> ordersAccepted;
    private final Queue<Order> ordersPrepared;
    private final List<Order> ordersCarried;

    private int customersLimit;
    private int customersAte;

    public void setCustomersLimit(int customersLimit) {
        this.customersLimit = customersLimit;
    }

    public void addOrder(Customer customer, String customersName, int timeOut) {

        BasicFunctions.sleep(timeOut);
        lockWaiters.lock();
        try{
            BasicFunctions.sleep(timeOut);
            Order order = new Order(customer, customersName);
            ordersOrdered.add(order);
            System.out.printf("%s сделал заказ.\n", customersName);

            conditionWaiters.signalAll();
        } finally {

            lockWaiters.unlock();
        }
    }

    public void acceptOrCarryOrder(String performersName, int timeOut){
        lockWaiters.lock();
        Order order = null;
        boolean needAccept = false;
        boolean needCarry = false;
        try {
            while (ordersOrdered.isEmpty() && ordersPrepared.isEmpty()) {
                conditionWaiters.await();//TIME_OUT_SHORT, TimeUnit.MILLISECONDS
            }
            if (!ordersOrdered.isEmpty()) {
                order = ordersOrdered.poll();
                needAccept = true;
            } else { //if (!ordersPrepared.isEmpty()) {
                order = ordersPrepared.poll();
                needCarry = true;
            }
        } catch (InterruptedException ignored) {
        } finally {
            lockWaiters.unlock();
        }
        BasicFunctions.sleep(timeOut);
        if (needAccept && order != null)  {
            lockCooks.lock();
            try{
                ordersAccepted.add(order);
                System.out.printf("%s принял заказ у %s.\n", performersName, order.getCustomersName());
                conditionCooks.signalAll();
            } finally {
                lockCooks.unlock();
            }
        } else if (needCarry && order != null){
            lockCustomers.lock();
            try{
                ordersCarried.add(order);
               // BasicFunctions.sleep(timeOut);
                System.out.printf("%s отнес блюдо для %s.\n", performersName, order.getCustomersName());
                conditionCustomers.signalAll();
            } finally {
                lockCustomers.unlock();
            }
        }
    }

    public void prepareOrder(String performersName, int timeOut){
        lockCooks.lock();
        Order order = null;
        try{
            while (ordersAccepted.isEmpty()){
                conditionCooks.await();//TIME_OUT_SHORT, TimeUnit.MILLISECONDS
            }
            order = ordersAccepted.poll();
        } catch (InterruptedException ignored) {
        } finally {
            lockCooks.unlock();
        }
        if (order != null)  {
            BasicFunctions.sleep(timeOut);
            lockWaiters.lock();
            try{
                ordersPrepared.add(order);
                System.out.printf("%s приготовил блюдо для %s.\n", performersName, order.getCustomersName());
                conditionWaiters.signalAll();
            } finally {
                lockWaiters.unlock();
            }
        }
    }

    public void eatOrder(Customer customer, String performersName, int timeOut){
        BasicFunctions.sleep(timeOut);
        lockCustomers.lock();
        try{
            Order order = findOrderByCustomer(customer);
            while (order == null){
                conditionCustomers.await();//TIME_OUT_SHORT, TimeUnit.MILLISECONDS
                order = findOrderByCustomer(customer);
            }
            ordersCarried.remove(order);
            customersAte++;
            System.out.printf("%s съел блюдо.\n", performersName);
        } catch (InterruptedException ignored){
        } finally {
            lockCustomers.unlock();
        }
    }

    private Order findOrderByCustomer(Customer customer) {
        if (ordersCarried.isEmpty())
            return null;

        for (Order order : ordersCarried) {
            if (order.getCustomer() == customer)
                return order;

        }
        return null;

    }

    public boolean allOrdersAreEATEN() {
        lockCustomers.lock();
        try{
            return customersAte >= customersLimit;
        } finally {
            lockCustomers.unlock();
        }
    }

    // SingleTone ++

    private Restaurant(){
        ordersOrdered   = new ArrayDeque<>();
        ordersAccepted  = new ArrayDeque<>();
        ordersPrepared = new ArrayDeque<>();
        ordersCarried   = new ArrayList<>();


        lockWaiters   = new ReentrantLock();
        lockCooks = new ReentrantLock();
        lockCustomers = new ReentrantLock();


        conditionWaiters   = lockWaiters.newCondition();
        conditionCooks = lockCooks.newCondition();
        conditionCustomers = lockCustomers.newCondition();

        customersLimit = 0;
        customersAte = 0;
    }

    private static class Holder {
        public static final Restaurant RESTAURANT = new Restaurant();
    }

    public static Restaurant get()  {
        return Holder.RESTAURANT;
    }

    // SingleTone --
}
