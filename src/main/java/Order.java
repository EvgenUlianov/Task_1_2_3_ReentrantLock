
public class Order {
    private final String customersName;
    private final Customer customer;

    public Order(Customer customer, String customersName) {
        this.customer = customer;
        this.customersName = customersName;
    }

    public String getCustomersName() {
        return customersName;
    }

    public Customer getCustomer() {
        return customer;
    }
}
