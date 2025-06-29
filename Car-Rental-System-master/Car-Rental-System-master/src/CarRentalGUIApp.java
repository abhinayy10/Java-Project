import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class CarRentalGUIApp extends JFrame {
    // ===== Model Classes =====
    static class Car {
        private String carId, brand, model;
        private double basePricePerDay;
        private boolean isAvailable;

        public Car(String carId, String brand, String model, double basePricePerDay) {
            this.carId = carId;
            this.brand = brand;
            this.model = model;
            this.basePricePerDay = basePricePerDay;
            this.isAvailable = true;
        }

        public String getCarId() { return carId; }
        public String getBrand() { return brand; }
        public String getModel() { return model; }
        public boolean isAvailable() { return isAvailable; }
        public double calculatePrice(int days) { return basePricePerDay * days; }
        public void rent() { isAvailable = false; }
        public void returnCar() { isAvailable = true; }

        @Override
        public String toString() {
            return carId + ": " + brand + " " + model;
        }
    }

    static class Customer {
        private String customerId, name;
        public Customer(String customerId, String name) {
            this.customerId = customerId;
            this.name = name;
        }
        public String getCustomerId() { return customerId; }
        public String getName() { return name; }
    }

    static class Rental {
        private Car car;
        private Customer customer;
        private int days;

        public Rental(Car car, Customer customer, int days) {
            this.car = car;
            this.customer = customer;
            this.days = days;
        }

        public Car getCar() { return car; }
        public Customer getCustomer() { return customer; }
    }

    static class CarRentalSystem {
        private List<Car> cars = new ArrayList<>();
        private List<Customer> customers = new ArrayList<>();
        private List<Rental> rentals = new ArrayList<>();

        public void addCar(Car car) { cars.add(car); }
        public void addCustomer(Customer customer) { customers.add(customer); }
        public int getCustomerCount() { return customers.size(); }

        public List<Car> getAvailableCars() {
            List<Car> available = new ArrayList<>();
            for (Car car : cars) {
                if (car.isAvailable()) available.add(car);
            }
            return available;
        }

        public Car findCarById(String id) {
            for (Car c : cars) {
                if (c.getCarId().equalsIgnoreCase(id)) return c;
            }
            return null;
        }

        public void rentCar(Car car, Customer customer, int days) {
            if (car.isAvailable()) {
                car.rent();
                rentals.add(new Rental(car, customer, days));
            }
        }

        public void returnCar(Car car) {
            car.returnCar();
            rentals.removeIf(r -> r.getCar() == car);
        }
    }

    // ===== GUI Logic =====
    private final CarRentalSystem rentalSystem = new CarRentalSystem();

    public CarRentalGUIApp() {
        setTitle("Car Rental System");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Sample cars
        rentalSystem.addCar(new Car("C001", "Toyota", "Camry", 60));
        rentalSystem.addCar(new Car("C002", "Honda", "Accord", 70));
        rentalSystem.addCar(new Car("C003", "Mahindra", "Thar", 150));

        setupUI();
        setVisible(true);
    }

    private void setupUI() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        JButton rentBtn = new JButton("Rent a Car");
        JButton returnBtn = new JButton("Return a Car");
        JButton exitBtn = new JButton("Exit");

        rentBtn.addActionListener(e -> rentCarDialog());
        returnBtn.addActionListener(e -> returnCarDialog());
        exitBtn.addActionListener(e -> System.exit(0));

        panel.add(rentBtn);
        panel.add(returnBtn);
        panel.add(exitBtn);

        add(panel);
    }

    private void rentCarDialog() {
        String name = JOptionPane.showInputDialog(this, "Enter your name:");
        if (name == null || name.trim().isEmpty()) return;

        Customer customer = new Customer("CUS" + (rentalSystem.getCustomerCount() + 1), name);
        rentalSystem.addCustomer(customer);

        List<Car> availableCars = rentalSystem.getAvailableCars();
        if (availableCars.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No cars available.");
            return;
        }

        Car selectedCar = (Car) JOptionPane.showInputDialog(
                this, "Choose a car:", "Available Cars",
                JOptionPane.PLAIN_MESSAGE, null,
                availableCars.toArray(), availableCars.get(0));

        if (selectedCar == null) return;

        String daysStr = JOptionPane.showInputDialog(this, "Enter number of rental days:");
        if (daysStr == null || daysStr.trim().isEmpty()) return;

        try {
            int days = Integer.parseInt(daysStr);
            double totalPrice = selectedCar.calculatePrice(days);

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Total Price: $" + totalPrice + "\nConfirm rental?",
                    "Confirm Rental", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                rentalSystem.rentCar(selectedCar, customer, days);
                JOptionPane.showMessageDialog(this, "Car rented successfully.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number of days.");
        }
    }

    private void returnCarDialog() {
        String carId = JOptionPane.showInputDialog(this, "Enter Car ID to return:");
        if (carId == null || carId.trim().isEmpty()) return;

        Car car = rentalSystem.findCarById(carId);
        if (car != null && !car.isAvailable()) {
            rentalSystem.returnCar(car);
            JOptionPane.showMessageDialog(this, "Car returned successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Car ID or car not rented.");
        }
    }

    // ===== Main Entry Point =====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CarRentalGUIApp::new);
    }
}
