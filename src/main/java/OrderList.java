import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OrderList implements Lock, Condition {

    private final int ORDER_HAS_NOT_FOUND = -1;

    private enum OrderStatus {
        ORDERED,
        ACCEPTED,
        PREPARED,
        CARRIED,
        EATED
    }

    private class Order implements Lock, Condition {
        private OrderStatus status;
        private final String customersName;

        public Order(String customersName) {
            this.customersName = customersName;
            status = OrderStatus.ORDERED;
            lock = new ReentrantLock();
            condition = newCondition();
        }

        // Lock ++

        private final Lock lock;

        @Override
        public void lock() {
            lock.lock();
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            lock.lockInterruptibly();
        }

        @Override
        public boolean tryLock() {
            return lock.tryLock();
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            return lock.tryLock(time, unit);
        }

        @Override
        public void unlock() {
            lock.unlock();
        }

        // Lock --

        // Condition ++

        private final Condition condition;

        @Override
        public Condition newCondition() {
            return lock.newCondition();
        }

        @Override
        public void await() throws InterruptedException {
            condition.await();
        }

        @Override
        public void awaitUninterruptibly() {
            condition.awaitUninterruptibly();
        }

        @Override
        public long awaitNanos(long nanosTimeout) throws InterruptedException {
            return condition.awaitNanos(nanosTimeout);
        }

        @Override
        public boolean await(long time, TimeUnit unit) throws InterruptedException {
            return condition.await(time, unit);
        }

        @Override
        public boolean awaitUntil(Date deadline) throws InterruptedException {
            return condition.awaitUntil(deadline);
        }

        @Override
        public void signal() {
            condition.signal();
        }

        @Override
        public void signalAll() {
            condition.signalAll();
        }

        // Condition --

    }

    private final List<Order> orders;

    public void addOrder(String customersName) {
        orders.add(new Order(customersName));
        System.out.printf("%s сделал заказ.\n", customersName);
    }

    public int findOrderByCustomersName(String customersName) {
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            if (order.customersName.equals(customersName))
                return i;
        }
        return ORDER_HAS_NOT_FOUND;
    }

    private int findOrderByStatus(OrderStatus status, int fromIndex) {
        for (int i = fromIndex; i < orders.size(); i++) {
            Order order = orders.get(i);
            if (order.status == status)
                return i;
        }
        return ORDER_HAS_NOT_FOUND;
    }

    public String getCustomersName(int index) throws IndexOutOfBoundsException {
        return orders.get(index).customersName;
    }

    public int findOrderORDEREDorPREPARED(int fromIndex) {
        int indexORDERED = findOrderByStatus(OrderStatus.ORDERED, fromIndex);
        int indexPREPARED = findOrderByStatus(OrderStatus.PREPARED, fromIndex);
        if (indexORDERED > ORDER_HAS_NOT_FOUND && indexPREPARED > ORDER_HAS_NOT_FOUND) {
            return Math.min(indexORDERED, indexPREPARED);
        } else if (indexORDERED == ORDER_HAS_NOT_FOUND && indexPREPARED > ORDER_HAS_NOT_FOUND) {
            return indexPREPARED;
        } else if (indexORDERED > ORDER_HAS_NOT_FOUND && indexPREPARED == ORDER_HAS_NOT_FOUND) {
            return indexORDERED;
        } else  {
            return ORDER_HAS_NOT_FOUND;
        }
    }
    public int findOrderACCEPTED() {
        return findOrderByStatus(OrderStatus.ACCEPTED, 0);
    }

    public int findOrderCARRIED() {
        return findOrderByStatus(OrderStatus.CARRIED, 0);
    }

    public boolean allOrdersAreEATED() {
        if (orders.size() == 0)
            return false;
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            if (order.status != OrderStatus.EATED)
                return false;
        };
        return true;
    }

    public boolean canAcceptOrCarryOrder (int index) throws IndexOutOfBoundsException {
        Order order = orders.get(index);
        if (order.status == OrderStatus.ORDERED){
            return true;
        } else if (order.status == OrderStatus.PREPARED){
            return true;
        }
        return false;
    }

    public void acceptOrCarryOrder (int index, String waitersName) throws IndexOutOfBoundsException {
        Order order = orders.get(index);
        if (order.status == OrderStatus.ORDERED){
            order.status = OrderStatus.ACCEPTED;
            System.out.printf("%s принял заказ у %s.\n", waitersName, order.customersName);
        } else if (order.status == OrderStatus.PREPARED){
            order.status = OrderStatus.CARRIED;
            System.out.printf("%s отнес блюдо для %s.\n", waitersName, order.customersName);
        }
    }


    public boolean canPrepareOrder (int index) throws IndexOutOfBoundsException {
        Order order = orders.get(index);
        if (order.status == OrderStatus.ACCEPTED){
            return true;
        }
        return false;
    }
    public void prepareOrder (int index, String cooksName) throws IndexOutOfBoundsException {
        Order order = orders.get(index);
        order.status = OrderStatus.PREPARED;
        System.out.printf("%s приготовил блюдо для %s.\n", cooksName, order.customersName);
    }


    public boolean canEatOrder (int index) throws IndexOutOfBoundsException {
        Order order = orders.get(index);
        if (order.status == OrderStatus.CARRIED){
            return true;
        }
        return false;
    }

    public void eatOrder (int index) throws IndexOutOfBoundsException {
        Order order = orders.get(index);
        order.status = OrderStatus.EATED;
        System.out.printf("%s съел блюдо.\n", order.customersName);
    }

    // Lock ++

    private final Lock lock;

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        lock.lockInterruptibly();
    }

    @Override
    public boolean tryLock() {
        return lock.tryLock();
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return lock.tryLock(time, unit);
    }

    @Override
    public void unlock() {
        lock.unlock();
    }

    public void lock(int index) throws IndexOutOfBoundsException{
        Order order = orders.get(index);
        order.lock();
    }

    public boolean tryLock(int index) throws IndexOutOfBoundsException {
        Order order = orders.get(index);
        return order.tryLock();
    }

    public void unlock(int index) throws IndexOutOfBoundsException{
        Order order = orders.get(index);
        order.unlock();
    }

    // Lock --


    // Condition ++

    private final Condition condition;

    @Override
    public Condition newCondition() {
        return lock.newCondition();
    }

    @Override
    public void await() throws InterruptedException {
        condition.await();
    }

    @Override
    public void awaitUninterruptibly() {
        condition.awaitUninterruptibly();
    }

    @Override
    public long awaitNanos(long nanosTimeout) throws InterruptedException {
        return condition.awaitNanos(nanosTimeout);
    }

    @Override
    public boolean await(long time, TimeUnit unit) throws InterruptedException {
        return condition.await(time, unit);
    }

    @Override
    public boolean awaitUntil(Date deadline) throws InterruptedException {
        return condition.awaitUntil(deadline);
    }

    @Override
    public void signal() {
        condition.signal();
    }

    @Override
    public void signalAll() {
        condition.signalAll();
    }

    public void await(int index) throws IndexOutOfBoundsException, InterruptedException {
        Order order = orders.get(index);
        order.await();
    }

    public void signalAll(int index) throws IndexOutOfBoundsException{
        Order order = orders.get(index);
        order.signalAll();
    }


    // Condition --

    // SingleTone ++

    private OrderList(){
        orders = new ArrayList<>();

        lock = new ReentrantLock();
        condition = newCondition();
    };

    private static class Holder {
        public static final OrderList ORDER_LIST = new OrderList();
    }

    public static OrderList get()  {
        return Holder.ORDER_LIST;
    }

    // SingleTone --

}
