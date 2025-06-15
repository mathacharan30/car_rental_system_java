import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// ---------------- Vehicle Base Class ----------------
abstract class Vehicle {
    protected String vehicleID;
    protected String type;
    protected int seats;
    protected double costPerDay;
    protected boolean available;

    public Vehicle(String id, String t, int s, double cost) {
        this.vehicleID = id;
        this.type = t;
        this.seats = s;
        this.costPerDay = cost;
        this.available = true;
    }

    public abstract void display();

    public boolean isAvailable() {
        return available;
    }

    public void setAvailability(boolean status) {
        this.available = status;
    }

    public double calculateCost(int days) {
        return costPerDay * days;
    }

    public String getID() {
        return vehicleID;
    }

    public String getType() {
        return type;
    }
}

// ---------------- Derived Vehicle Classes ----------------
class Car extends Vehicle {
    public Car(String id, int s, double cost) {
        super(id, "Car", s, cost);
    }

    public void display() {
        System.out.println("[Car] ID: " + vehicleID + " | Seats: " + seats +
            " | ₹" + costPerDay + "/day | Available: " + (available ? "Yes" : "No"));
    }
}

class Bike extends Vehicle {
    public Bike(String id, int s, double cost) {
        super(id, "Bike", s, cost);
    }

    public void display() {
        System.out.println("[Bike] ID: " + vehicleID + " | Seats: " + seats +
            " | ₹" + costPerDay + "/day | Available: " + (available ? "Yes" : "No"));
    }
}

class Truck extends Vehicle {
    public Truck(String id, int s, double cost) {
        super(id, "Truck", s, cost);
    }

    public void display() {
        System.out.println("[Truck] ID: " + vehicleID + " | Seats: " + seats +
            " | ₹" + costPerDay + "/day | Available: " + (available ? "Yes" : "No"));
    }
}

// ---------------- Rental Record ----------------
class RentalRecord {
    String vehicleID;
    String vehicleType;
    LocalDateTime rentTime;
    int duration;
    double totalCost;

    public RentalRecord(String id, String type, LocalDateTime time, int days, double cost) {
        this.vehicleID = id;
        this.vehicleType = type;
        this.rentTime = time;
        this.duration = days;
        this.totalCost = cost;
    }

    public void display() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        System.out.println("Vehicle: " + vehicleType + " [" + vehicleID + "]"
                + " | Days: " + duration
                + " | Total Cost: ₹" + totalCost
                + " | Rented on: " + rentTime.format(formatter));
    }
}

// ---------------- User Class ----------------
class User {
    private String username;
    private String password;
    private List<RentalRecord> history = new ArrayList<>();

    public User(String u, String p) {
        this.username = u;
        this.password = p;
    }

    public String getUsername() {
        return username;
    }

    public boolean validate(String u, String p) {
        return this.username.equals(u) && this.password.equals(p);
    }

    public void addHistory(RentalRecord record) {
        history.add(record);
    }

    public void showHistory() {
        if (history.isEmpty()) {
            System.out.println("No rental history found.");
            return;
        }
        for (RentalRecord r : history) {
            r.display();
        }
    }
}

// ---------------- Main App Class ----------------
public class AutoRentPro {
    static List<Vehicle> vehicles = new ArrayList<>();
    static List<User> users = new ArrayList<>();
    static User currentUser = null;
    static Scanner sc = new Scanner(System.in);

    static void seedVehicles() {
        vehicles.add(new Car("CAR001", 4, 1500));
        vehicles.add(new Car("CAR002", 5, 1800));
        vehicles.add(new Bike("BIKE001", 2, 500));
        vehicles.add(new Bike("BIKE002", 1, 300));
        vehicles.add(new Truck("TRUCK001", 2, 3000));
        vehicles.add(new Truck("TRUCK002", 3, 3500));
    }

    static Vehicle findVehicleByID(String id) {
        for (Vehicle v : vehicles) {
            if (v.getID().equals(id)) return v;
        }
        return null;
    }

    static void rentVehicle() {
        System.out.println("\nAvailable Vehicles:");
        for (Vehicle v : vehicles) {
            if (v.isAvailable()) v.display();
        }

        System.out.print("\nEnter Vehicle ID to rent: ");
        String id = sc.next();

        Vehicle v = findVehicleByID(id);
        if (v == null || !v.isAvailable()) {
            System.out.println("Invalid or unavailable vehicle!");
            return;
        }

        System.out.print("Enter number of days to rent: ");
        int days = sc.nextInt();

        double cost = v.calculateCost(days);
        LocalDateTime now = LocalDateTime.now();

        RentalRecord record = new RentalRecord(v.getID(), v.getType(), now, days, cost);
        currentUser.addHistory(record);
        v.setAvailability(false);

        System.out.println("Vehicle rented successfully! Total cost: ₹" + cost);
    }

    static void returnVehicle() {
        System.out.print("Enter Vehicle ID to return: ");
        String id = sc.next();

        Vehicle v = findVehicleByID(id);
        if (v == null || v.isAvailable()) {
            System.out.println("This vehicle is not currently rented.");
            return;
        }

        v.setAvailability(true);
        System.out.println("Vehicle returned successfully.");
    }

    static void userMenu() {
        int choice;
        do {
            System.out.println("\n=== User Menu ===");
            System.out.println("1. Rent Vehicle");
            System.out.println("2. Return Vehicle");
            System.out.println("3. Show Rental History");
            System.out.println("4. Delete My Account");
            System.out.println("5. Logout");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1 -> rentVehicle();
                case 2 -> returnVehicle();
                case 3 -> currentUser.showHistory();
                case 4 -> {
                    users.removeIf(u -> u.getUsername().equals(currentUser.getUsername()));
                    currentUser = null;
                    System.out.println("Account deleted.");
                    return;
                }
                case 5 -> {
                    currentUser = null;
                    System.out.println("Logged out.");
                }
                default -> System.out.println("Invalid choice.");
            }
        } while (currentUser != null);
    }

    static void registerUser() {
        System.out.print("Enter new username: ");
        String u = sc.next();
        System.out.print("Enter password: ");
        String p = sc.next();

        for (User user : users) {
            if (user.getUsername().equals(u)) {
                System.out.println("Username already exists!");
                return;
            }
        }
        users.add(new User(u, p));
        System.out.println("Registration successful!");
    }

    static void loginUser() {
        System.out.print("Username: ");
        String u = sc.next();
        System.out.print("Password: ");
        String p = sc.next();

        for (User user : users) {
            if (user.validate(u, p)) {
                currentUser = user;
                System.out.println("Login successful!");
                userMenu();
                return;
            }
        }
        System.out.println("Invalid credentials!");
    }

    public static void main(String[] args) {
        seedVehicles();
        int choice;
        do {
            System.out.println("\n===== AutoRent Pro =====");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1 -> loginUser();
                case 2 -> registerUser();
                case 3 -> System.out.println("Goodbye!");
                default -> System.out.println("Invalid option!");
            }
        } while (choice != 3);
    }
}
