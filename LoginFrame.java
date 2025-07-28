

import javax.swing.*;
import java.awt.*;
import java.sql.*;


public class LoginFrame extends JFrame {
    public LoginFrame() {
        setTitle("Login");
        setSize(300, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 2));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        add(userLabel); add(userField);
        add(passLabel); add(passField);
        add(loginBtn); add(registerBtn);

        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "SELECT role FROM users WHERE username = ? AND password = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, password);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String role = rs.getString("role");
                    JOptionPane.showMessageDialog(this, "Login successful as " + role);
                    dispose();
                    new BusBookingGUI(role, username);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        });

        registerBtn.addActionListener(e -> new RegisterFrame());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginFrame();
    }
}