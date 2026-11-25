import java.io.*;
import java.util.Scanner;
public class CustomerServiceQueue {

    private static final String FILE_NAME = "customers.txt";
    private static final Scanner SCANNER = new Scanner(System.in);

    private static final CustomerQueue queue = new CustomerQueue();
    private static int nextTokenNumber = 101;

    public static void main(String[] args) {
        loadFromFile();

        System.out.println("==================================");
        System.out.println("     CUSTOMER SERVICE QUEUE");
        System.out.println("==================================");

        boolean running = true;

        while (running) {
            displayMenu();
            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    joinQueue();
                    break;
                case 2:
                    callNextCustomer();
                    break;
                case 3:
                    queue.displayQueue();
                    break;
                case 4:
                    leaveQueue();
                    break;
                case 5:
                    saveToFile();
                    System.out.println("Data saved successfully.");
                    System.out.println("Thank you! Program exiting.");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1 to 5.");
            }
        }

        SCANNER.close();
    }

    private static void displayMenu() {
        System.out.println("\n---------- MAIN MENU ----------");
        System.out.println("1. Join Queue");
        System.out.println("2. Call Next Customer");
        System.out.println("3. Display Queue");
        System.out.println("4. Leave Queue (by Token)");
        System.out.println("5. Exit");
    }

    private static void joinQueue() {
        System.out.print("Enter customer name: ");
        String name = readNonEmptyLine();

        System.out.print("Enter service required: ");
        String service = readNonEmptyLine();

        // Keep the text-file delimiter out of user-entered values.
        name = cleanField(name);
        service = cleanField(service);

        int peopleAhead = queue.size();
        String token = "Q" + nextTokenNumber++;

        Customer customer = new Customer(token, name, service);
        queue.enqueue(customer);

        System.out.println("\nToken issued: " + customer);
        System.out.println("People ahead: " + peopleAhead);
    }

    private static void callNextCustomer() {
        Customer customer = queue.dequeue();

        if (customer == null) {
            System.out.println("Queue is empty. No customer to serve.");
            return;
        }

        System.out.println("Now serving: " + customer.getToken()
                + " | " + customer.getName());
        System.out.println("Queue is now empty.");
        if (!queue.isEmpty()) {
            System.out.println("Remaining in queue: " + queue.size());
        }
    }

    private static void leaveQueue() {
        System.out.print("Enter token number: ");
        String token = SCANNER.nextLine().trim().toUpperCase();

        if (queue.removeByToken(token)) {
            System.out.println("Customer with token " + token
                    + " has left the queue.");
        } else {
            System.out.println("Token " + token + " not found.");
        }
    }

    private static void loadFromFile() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            System.out.println("No existing customer file found. Starting with an empty queue.");
            return;
        }

        int highestToken = 100;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }
                if (line.startsWith("#NEXT_TOKEN=")) {
                    try {
                        nextTokenNumber = Integer.parseInt(
                                line.substring("#NEXT_TOKEN=".length()).trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid token counter in file; it will be recalculated.");
                    }
                    continue;
                }

                String[] parts = line.split("\\|", 3);
                if (parts.length == 3) {
                    String token = parts[0].trim().toUpperCase();
                    String name = parts[1].trim();
                    String service = parts[2].trim();

                    if (!token.matches("Q\\d+") || name.isEmpty() || service.isEmpty()) {
                        System.out.println("Skipping invalid record: " + line);
                        continue;
                    }

                    queue.enqueue(new Customer(token, name, service));

                    try {
                        highestToken = Math.max(highestToken,
                                Integer.parseInt(token.substring(1)));
                    } catch (NumberFormatException ignored) {
                        // Invalid numeric suffix is already excluded by the token format check.
                    }
                } else {
                    System.out.println("Skipping invalid record: " + line);
                }
            }

            nextTokenNumber = Math.max(nextTokenNumber, highestToken + 1);
            System.out.println("Loaded " + queue.size() + " customer(s) from " + FILE_NAME + ".");
        } catch (IOException e) {
            System.out.println("Error loading customer data: " + e.getMessage());
        }
    }

    private static void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write("#NEXT_TOKEN=" + nextTokenNumber);
            writer.newLine();

            Node current = queue.getFront();
            while (current != null) {
                Customer customer = current.getCustomer();
                writer.write(customer.getToken() + "|"
                        + customer.getName() + "|"
                        + customer.getService());
                writer.newLine();
                current = current.getNext();
            }
        } catch (IOException e) {
            System.out.println("Error saving customer data: " + e.getMessage());
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static String readNonEmptyLine() {
        while (true) {
            String value = SCANNER.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.print("Input cannot be empty. Please enter again: ");
        }
    }

    private static String cleanField(String value) {
        return value.replace("|", "/");
    }
}

/** Stores the details of one customer. */
class Customer {
    private final String token;
    private final String name;
    private final String service;

    public Customer(String token, String name, String service) {
        this.token = token;
        this.name = name;
        this.service = service;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public String getService() {
        return service;
    }

    @Override
    public String toString() {
        return token + " | " + name + " | " + service;
    }
}

/** One node in the custom singly linked list. */
class Node {
    private final Customer customer;
    private Node next;

    public Node(Customer customer) {
        this.customer = customer;
        this.next = null;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }
}

/** Queue implementation using a custom LinkedList (FIFO). */
class CustomerQueue {
    private Node front;
    private Node rear;
    private int size;

    public CustomerQueue() {
        front = null;
        rear = null;
        size = 0;
    }

    public boolean isEmpty() {
        return front == null;
    }

    public int size() {
        return size;
    }

    public Node getFront() {
        return front;
    }

    // Insert a customer at the rear of the queue: O(1).
    public void enqueue(Customer customer) {
        Node newNode = new Node(customer);

        if (isEmpty()) {
            front = newNode;
            rear = newNode;
        } else {
            rear.setNext(newNode);
            rear = newNode;
        }

        size++;
    }

    // Remove the customer at the front of the queue: O(1).
    public Customer dequeue() {
        if (isEmpty()) {
            return null;
        }

        Customer served = front.getCustomer();
        front = front.getNext();
        size--;

        if (front == null) {
            rear = null;
        }

        return served;
    }

    // Display customers from front to rear: O(n).
    public void displayQueue() {
        if (isEmpty()) {
            System.out.println("Queue is empty.");
            return;
        }

        System.out.println("\n------ CUSTOMER QUEUE ------");
        Node current = front;
        int position = 1;

        while (current != null) {
            System.out.println(position + ". " + current.getCustomer());
            current = current.getNext();
            position++;
        }

        System.out.println("Total customers in queue: " + size);
    }

    // Search and remove a customer by token: O(n).
    public boolean removeByToken(String token) {
        if (isEmpty() || token == null || token.isEmpty()) {
            return false;
        }

        if (front.getCustomer().getToken().equalsIgnoreCase(token)) {
            dequeue();
            return true;
        }

        Node previous = front;
        Node current = front.getNext();

        while (current != null) {
            if (current.getCustomer().getToken().equalsIgnoreCase(token)) {
                previous.setNext(current.getNext());

                if (current == rear) {
                    rear = previous;
                }

                size--;
                return true;
            }

            previous = current;
            current = current.getNext();
        }

        return false;
    }
}
