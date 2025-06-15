// EnhancedCarRentalSystem.java
// Demonstrates dynamic data structures, data hiding, encapsulation, authentication,
// loan request/transfer, account management, input validation, and proper resource cleanup

import java.util.*;
import java.io.*;
import java.util.logging.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// ===== Core Domain Classes =====

abstract class Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String make;
    private String model;
    private boolean available = true;
    protected double basePricePerDay;

    public Vehicle(String id, String make, String model, double basePricePerDay) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.basePricePerDay = basePricePerDay;
    }

    public String getId() { return id; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public boolean isAvailable() { return available; }
    public void rentOut() { available = false; }
    public void returned() { available = true; }

    // Polymorphic pricing
    public abstract double calculatePrice(int days);

    public double getBasePricePerDay() { return basePricePerDay; }

    @Override
    public String toString() {
        return String.format("[%s] %s %s - ₹%.2f/day (%s)",
                id, make, model, basePricePerDay, available ? "Available" : "Rented");
    }
}

class Car extends Vehicle {
    private static final long serialVersionUID = 1L;
    private int seatingCapacity;
    
    public Car(String id, String make, String model, double basePricePerDay, int seats) { 
        super(id, make, model, basePricePerDay); 
        this.seatingCapacity = seats; 
    }
    
    public int getSeatingCapacity() {
        return seatingCapacity;
    }
    
    @Override 
    public double calculatePrice(int days) { 
        // Apply small discount for cars with more seats
        double seatFactor = 1.0 - (seatingCapacity > 4 ? 0.05 : 0);
        return days * getBasePricePerDay() * seatFactor; 
    }
    
    @Override
    public String toString() {
        return super.toString() + String.format(" - %d seats", seatingCapacity);
    }
}
class Truck extends Vehicle {
    private static final long serialVersionUID = 1L;
    private double loadCapacity;
    
    public Truck(String id, String make, String model, double basePricePerDay, double load) { 
        super(id, make, model, basePricePerDay); 
        this.loadCapacity = load; 
    }
    
    public double getLoadCapacity() {
        return loadCapacity;
    }
    
    @Override 
    public double calculatePrice(int days) { 
        // Higher load capacity means higher price
        double loadFactor = 1.0 + (loadCapacity / 100.0);
        return days * getBasePricePerDay() * loadFactor; 
    }
    
    @Override
    public String toString() {
        return super.toString() + String.format(" - %.1f ton capacity", loadCapacity);
    }
}

class Customer implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String passwordHash;
    private double loanBalance = 0;
    private List<Rental> history = new ArrayList<>();

    public Customer(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.passwordHash = hash(password);
    }
    public String getId(){ return id;} public String getName(){ return name; }
    public boolean authenticate(String pass){ return passwordHash.equals(hash(pass)); }
    public double getLoanBalance(){ return loanBalance; }
    public void requestLoan(double amount){ loanBalance += amount; }
    public void transferLoanTo(Customer other, double amount){
        if(amount>loanBalance) throw new IllegalArgumentException("Insufficient loan to transfer");
        loanBalance -= amount;
        other.loanBalance += amount;
    }
    public void addHistory(Rental r){ history.add(r); }
    public List<Rental> getHistory(){ return history; }
    private String hash(String input){
        try{ MessageDigest md=MessageDigest.getInstance("SHA-256"); byte[] dig=md.digest(input.getBytes()); StringBuilder sb=new StringBuilder(); for(byte b:dig) sb.append(String.format("%02x",b)); return sb.toString();
        }catch(NoSuchAlgorithmException e){ throw new RuntimeException(e);} }
}

class Rental implements Serializable {
    private static final long serialVersionUID = 1L;
    private Vehicle vehicle;
    private Customer customer;
    private int days;
    private double total;
    private Date date;
    public Rental(Vehicle v, Customer c, int d){ vehicle=v; customer=c; days=d; total=v.calculatePrice(d); date=new Date(); }
    public void process(){ vehicle.rentOut(); customer.addHistory(this); Log.LOGGER.info("Processed " + this); }
    public void close(){ vehicle.returned(); Log.LOGGER.info("Closed " + this); }
    public Date getDate(){ return date; }
    public String toString(){ return String.format("%s rented by %s for %d days: ₹%.2f on %s", vehicle.getId(), customer.getName(), days, total, date); }
}

// ===== Utilities =====
class Log { public static final Logger LOGGER=Logger.getLogger("Rentals"); static { LOGGER.setLevel(Level.INFO); ConsoleHandler h=new ConsoleHandler(); h.setLevel(Level.INFO); LOGGER.addHandler(h);} }

class Persistence {
    public static void save(Object o, String file) throws IOException{ try(ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(file))){out.writeObject(o);} }
    @SuppressWarnings("unchecked")
    public static <T> T load(String file) throws IOException,ClassNotFoundException{ try(ObjectInputStream in=new ObjectInputStream(new FileInputStream(file))){return (T)in.readObject();} }
}

// ===== Exception Types =====
class AuthenticationException extends Exception { public AuthenticationException(String m){ super(m);} }
class EntityNotFoundException extends Exception { public EntityNotFoundException(String m){ super(m);} }

// ===== Application Logic =====
public class EnhancedCarRentalApp {
    private Map<String, Vehicle> vehicles = new HashMap<>();
    private Map<String, Customer> customers = new HashMap<>();
    private Scanner sc = new Scanner(System.in);
    
    private void seed(){ vehicles.put("C1",new Car("C1","Honda","Civic",2000,5)); vehicles.put("T1",new Truck("T1","Volvo","VNL",5000,10)); }
    
    // Add this utility class for terminal formatting
    class TerminalUI {
        // ANSI color codes
        public static final String RESET = "\u001B[0m";
        public static final String GREEN = "\u001B[32m";
        public static final String YELLOW = "\u001B[33m";
        public static final String BLUE = "\u001B[34m";
        public static final String PURPLE = "\u001B[35m";
        public static final String RED = "\u001B[31m";
        
        public static void printHeader(String text) {
            System.out.println("\n" + BLUE + "===== " + text + " =====" + RESET);
        }
        
        public static void printSuccess(String text) {
            System.out.println(GREEN + "✓ " + text + RESET);
        }
        
        public static void printInfo(String text) {
            System.out.println(YELLOW + "ℹ " + text + RESET);
        }
    }
    
    private void mainMenu() {
        while(true) {
            TerminalUI.printHeader("CAR RENTAL SYSTEM");
            System.out.println("1) " + TerminalUI.YELLOW + "Login" + TerminalUI.RESET);
            System.out.println("2) " + TerminalUI.GREEN + "Register" + TerminalUI.RESET);
            System.out.println("3) " + TerminalUI.PURPLE + "Exit" + TerminalUI.RESET);
            int ch = readInt(1, 3);
            try {
                switch(ch) {
                    case 1: login(); break;
                    case 2: register(); break;
                    default: cleanup(); return;
                }
            } catch(Exception e) {
                System.out.println(TerminalUI.YELLOW + "⚠ " + e.getMessage() + TerminalUI.RESET);
            }
        }
    }
    private void login() throws Exception {
        System.out.print("UserID: "); String uid=sc.next();
        Customer c=customers.get(uid);
        if(c==null) throw new AuthenticationException("User not found");
        System.out.print("Password: "); String pw=sc.next();
        if(!c.authenticate(pw)) throw new AuthenticationException("Invalid creds");
        userMenu(c);
    }
    private void register(){ System.out.print("ID: "); String id=sc.next(); System.out.print("Name: "); String nm=sc.next(); System.out.print("Password: "); String pw=sc.next();
        customers.put(id,new Customer(id,nm,pw)); System.out.println("Registered."); }
    private void userMenu(Customer c) throws Exception {
        while(true) {
            TerminalUI.printHeader("USER MENU: " + c.getName());
            System.out.println("1) " + TerminalUI.BLUE + "Rent Vehicle" + TerminalUI.RESET);
            System.out.println("2) " + TerminalUI.GREEN + "Return Vehicle" + TerminalUI.RESET);
            System.out.println("3) " + TerminalUI.YELLOW + "Request Loan" + TerminalUI.RESET);
            System.out.println("4) " + TerminalUI.YELLOW + "Transfer Loan" + TerminalUI.RESET);
            System.out.println("5) " + TerminalUI.PURPLE + "Show History" + TerminalUI.RESET);
            System.out.println("6) " + TerminalUI.PURPLE + "Sort History" + TerminalUI.RESET);
            System.out.println("7) " + TerminalUI.RED + "Delete Account" + TerminalUI.RESET);
            System.out.println("8) " + TerminalUI.BLUE + "Logout" + TerminalUI.RESET);
            int ch=readInt(1,8);
            switch(ch){ case 1: rentFlow(c); break; case 2: returnFlow(c); break;
                case 3: loanFlow(c); break; case 4: transferFlow(c); break;
                case 5: showHistory(c); break; case 6: sortHistory(c); break;
                case 7: deleteAccount(c); return; default: return;
            }
        }
    }
    private void rentFlow(Customer c) throws Exception {
        listVehicles(); System.out.print("VehicleID: "); String vid=sc.next(); Vehicle v=vehicles.get(vid);
        if(v==null||!v.isAvailable()) throw new EntityNotFoundException("Vehicle unavailable");
        System.out.print("Days: "); int d=readInt(1,365);
        Rental r=new Rental(v,c,d); r.process();
    }
    private void returnFlow(Customer c) {
        // return last
        List<Rental> h=c.getHistory(); if(h.isEmpty()){System.out.println("No rentals"); return;} Rental r=h.get(h.size()-1);
        r.close();
    }
    private void loanFlow(Customer c){ System.out.print("Amount: "); double amt=readDouble(0.01,1e6);
        c.requestLoan(amt); System.out.println("Loan new balance: ₹"+c.getLoanBalance()); }
    private void transferFlow(Customer c){ System.out.print("TargetID: "); String tid=sc.next(); Customer o=customers.get(tid);
        if(o==null) System.out.println("Not found"); else{ System.out.print("Amt: "); double a=readDouble(0.01,c.getLoanBalance()); c.transferLoanTo(o,a); System.out.println("Done."); }}
    private void showHistory(Customer c){ c.getHistory().forEach(System.out::println); }
    private void sortHistory(Customer c){ c.getHistory().sort(Comparator.comparing(Rental::getDate)); System.out.println("Sorted."); }
    private void deleteAccount(Customer c){ customers.remove(c.getId()); System.out.println("Deleted account."); }
    private void listVehicles(){ vehicles.values().forEach(System.out::println); }
    private int readInt(int lo, int hi) { 
        int x; 
        while(true) { 
            try { 
                x = Integer.parseInt(sc.next()); 
                if(x >= lo && x <= hi) return x; 
            } catch(Exception e) { } 
            System.out.print("Invalid, retry: "); 
        } 
    }
    private double readDouble(double lo, double hi) { 
        double x; 
        while(true) { 
            try { 
                x = Double.parseDouble(sc.next()); 
                if(x >= lo && x <= hi) return x; 
            } catch(Exception e) { } 
            System.out.print("Invalid, retry: "); 
        } 
    }
    private void cleanup(){ sc.close(); try{ Persistence.save(vehicles,"veh.dat"); Persistence.save(customers,"cus.dat"); }catch(Exception e){} System.out.println("Exiting."); }
    public static void main(String[] args){ EnhancedCarRentalApp app=new EnhancedCarRentalApp(); app.seed(); app.mainMenu(); }
}
