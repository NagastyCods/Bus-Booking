import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterFrame extends JFrame {
    public RegisterFrame() {
        setTitle("Register");
        setSize(300, 250);
        setLayout(new GridLayout(5, 2));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();

        JLabel roleLabel = new JLabel("Role (user/admin):");
        JTextField roleField = new JTextField();

        JButton registerBtn = new JButton("Register");

        add(userLabel); add(userField);
        add(passLabel); add(passField);
        add(roleLabel); add(roleField);
        add(new JLabel()); add(registerBtn);

        registerBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            String role = roleField.getText().trim().toLowerCase();

            if (!role.equals("user") && !role.equals("admin")) {
                JOptionPane.showMessageDialog(this, "Role must be 'user' or 'admin'.");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.setString(3, role);

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Registration successful!");
                dispose();
            } catch (SQLException ex) {
                if (ex.getMessage().contains("Duplicate entry")) {
                    JOptionPane.showMessageDialog(this, "Username already exists.");
                } else {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }
}