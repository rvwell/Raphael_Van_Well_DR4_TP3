import java.util.*;

public class App {
    public static void main(String[] args) {
        EmailSender myEmailService = new EmailService();
        InvoicePrinter invoicePrinter = new InvoicePrinter();

        Client client = new Client("João", "joao@email.com");

        Order order = new Order(client, myEmailService);
        order.addProduct("Notebook", 1, 3500.0);
        order.addProduct("Mouse", 2, 80.0);

        invoicePrinter.print(order);

        order.sendEmail();
    }
}

class OrderItem {
    private String product;
    private int quantity;
    private double price;

    public OrderItem(String product, int quantity, double price) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantidade precisa ser positiva");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Preço tem de ser positivo");
        }
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    public String getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getTotal() {
        return quantity * price;
    }
}

class Client {
    private final String name;
    private final String email;

    public Client(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}

class Order {
    private Client client;
    private List<OrderItem> items = new ArrayList<>();
    private double discountRate = 0.1;
    private EmailSender emailService;

    public Order(Client client, EmailSender emailService) {
        if (client == null) {
            throw new IllegalArgumentException("Client não pode ser nulo.");
        }
        this.client = client;
        this.emailService = emailService;
    }

    public Client getClient() {
        return client;
    }

    public void addProduct(String product, int quantity, double price) {
        this.items.add(new OrderItem(product, quantity, price));
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public double calculateSubtotal() {
        double subtotal = 0;
        for (OrderItem item : items) {
            subtotal += item.getTotal();
        }
        return subtotal;
    }

    public double calculateDiscountAmount() {
        return DiscountPolicy.calculateDiscount(calculateSubtotal(), discountRate);
    }

    public double calculateFinalTotal() {
        return calculateSubtotal() - calculateDiscountAmount();
    }

    public void sendEmail() {
        emailService.sendEmail(client.getEmail(), "Pedido recebido! Obrigado pela compra.");
    }
}

interface EmailSender {
    void sendEmail(String to, String message);
}

class EmailService implements EmailSender {

    @Override
    public void sendEmail(String to, String message) {
        System.out.println("Enviando e-mail para " + to + ": " + message);
    }
}

class DiscountPolicy {
    public static double calculateDiscount(double amount, double rate) {
        return amount * rate;
    }
}

class InvoicePrinter {

    public void print(Order order) {
        printClientInfo(order.getClient());
        printOrderItems(order.getItems());
        printSummary(order);
    }

    private void printClientInfo(Client client) {
        System.out.println("Cliente: " + client.getName());
    }

    private void printOrderItems(List<OrderItem> items) {
        for (OrderItem item : items) {
            System.out.println(item.getQuantity() + "x " + item.getProduct() + " - R$" + formatCurrency(item.getPrice()));
        }
    }

    private void printSummary(Order order) {
        System.out.println("Subtotal: R$" + formatCurrency(order.calculateSubtotal()));
        System.out.println("Desconto: R$" + formatCurrency(order.calculateDiscountAmount()));
        System.out.println("Total final: R$" + formatCurrency(order.calculateFinalTotal()));
    }

    private String formatCurrency(double amount) {
        return String.format("%.2f", amount);
    }
}