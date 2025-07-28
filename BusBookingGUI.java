import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class BusBookingGUI extends JFrame {
    private String username;
    private JTextArea displayArea;

    public BusBookingGUI(String role, String username) {
        this.username = username;
        setTitle("Bus Booking System - " + role);
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton viewBusesBtn = new JButton("View Buses");
        JButton bookSeatBtn = new JButton("Book Seat");
        JButton viewBookingsBtn = new JButton("View My Bookings");
        JButton logoutBtn = new JButton("Logout");

        // buttonPanel.add(viewBusesBtn);

        if (role != null && role.trim().equalsIgnoreCase("admin")) {
            JButton viewAllBookingsBtn = new JButton("View All Bookings");
            JButton addBusBtn = new JButton("Add Bus");
            buttonPanel.add(viewAllBookingsBtn);
            buttonPanel.add(addBusBtn);

            viewAllBookingsBtn.addActionListener(e -> viewAllBookings());
            addBusBtn.addActionListener(_ -> addBus());
        }
        else {
            buttonPanel.add(bookSeatBtn);
            buttonPanel.add(viewBookingsBtn);
            buttonPanel.add(viewBusesBtn);
        }
        buttonPanel.add(logoutBtn);

        viewBusesBtn.addActionListener(e -> viewBuses());
        bookSeatBtn.addActionListener(e -> bookSeatDialog());
        viewBookingsBtn.addActionListener(e -> viewMyBookings());
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        add(buttonPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void viewBuses() {
        displayArea.setText("Available Buses:\n");
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM buses")) {

            while (rs.next()) {
                int busNo = rs.getInt("bus_number");
                int capacity = rs.getInt("seat_capacity");
                displayArea.append("Bus No: " + busNo + ", Seats: " + capacity + "\n");
            }
        } catch (SQLException e) {
            displayArea.setText("Error loading buses: " + e.getMessage());
        }
    }

    private void bookSeatDialog() {
        JTextField busField = new JTextField();
        JTextField dateField = new JTextField("YYYY-MM-DD");
        JTextField timeField = new JTextField("HH:MM:SS");

        Object[] inputs = {
            "Enter Bus Number:", busField,
            "Enter Departure Date:", dateField,
            "Enter Departure Time:", timeField
        };

        int result = JOptionPane.showConfirmDialog(this, inputs, "Book a Seat", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                int busNo = Integer.parseInt(busField.getText().trim());
                String date = dateField.getText().trim();
                String time = timeField.getText().trim();

                String sql = "INSERT INTO bookings (username, bus_number, departure_date, departure_time) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setInt(2, busNo);
                stmt.setString(3, date);
                stmt.setString(4, time);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Booking successful!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void viewMyBookings() {
        displayArea.setText("Your Bookings:\n");
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM bookings WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                displayArea.append("Bus: " + rs.getInt("bus_number") + ", Date: " + rs.getDate("departure_date") + ", Time: " + rs.getTime("departure_time") + "\n");
            }
        } catch (SQLException e) {
            displayArea.setText("Error loading your bookings: " + e.getMessage());
        }
    }

    private void viewAllBookings() {
        displayArea.setText("All Bookings:\n");
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM bookings")) {

            while (rs.next()) {
                displayArea.append("User: " + rs.getString("username") +
                                   ", Bus: " + rs.getInt("bus_number") +
                                   ", Date: " + rs.getDate("departure_date") +
                                   ", Time: " + rs.getTime("departure_time") + "\n");
            }
        } catch (SQLException e) {
            displayArea.setText("Error loading bookings: " + e.getMessage());
        }
    }

    private void addBus() {
    JTextField busNoField = new JTextField();
    JTextField seatsField = new JTextField();

    Object[] inputs = {
        "Enter Bus Number:", busNoField,
        "Enter Seat Capacity:", seatsField
    };

    int result = JOptionPane.showConfirmDialog(this, inputs, "Add Bus", JOptionPane.OK_CANCEL_OPTION);

    if (result == JOptionPane.OK_OPTION) {
        try (Connection conn = DBConnection.getConnection()) {
            int busNo = Integer.parseInt(busNoField.getText().trim());
            int seats = Integer.parseInt(seatsField.getText().trim());

            // Optional: Check if bus already exists
            PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM buses WHERE bus_number = ?");
            checkStmt.setInt(1, busNo);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "Bus already exists!");
                return;
            }

            String sql = "INSERT INTO buses (bus_number, seat_capacity) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, busNo);
            stmt.setInt(2, seats);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Bus added successfully.");
            viewBuses(); // Refresh the bus list

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}

}
