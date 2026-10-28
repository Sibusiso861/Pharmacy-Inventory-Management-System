import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public LoginFrame() {
        setTitle("PIMS - Pharmacy System Login");
        setSize(420, 360);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header Panel (Teal Healthcare Banner)
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 21, 128)); // Professional Teal
        headerPanel.setPreferredSize(new Dimension(420, 75));

        JLabel lblTitle = new JLabel("HEALTH FIRST PHARMACY");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Inventory & Sales Management System");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(220, 245, 245));

        headerPanel.setLayout(new GridLayout(2, 1, 0, 0));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblSub.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(lblTitle);
        headerPanel.add(lblSub);

        // Form Panel
        JPanel formPanel = new JPanel(null);
        formPanel.setBackground(Color.WHITE);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setBounds(40, 30, 100, 25);
        formPanel.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtUsername.setBounds(140, 30, 220, 30);
        formPanel.add(txtUsername);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPass.setBounds(40, 80, 100, 25);
        formPanel.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setBounds(140, 80, 220, 30);
        formPanel.add(txtPassword);

        // Styled Login Button
        JButton btnLogin = new JButton("LOGIN");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(0, 55, 128));
        btnLogin.setForeground(Color.black);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setBounds(140, 135, 220, 35);

        btnLogin.addActionListener(e -> authenticateUser());
        formPanel.add(btnLogin);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void authenticateUser() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {

            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                int userId = rs.getInt("user_id");
                String fullName = rs.getString("full_name");

                JOptionPane.showMessageDialog(this, "Login Successful! Welcome " + fullName, "Access Granted", JOptionPane.INFORMATION_MESSAGE);
                dispose();

                if ("Admin".equalsIgnoreCase(role)) {
                    new AdminDashboard().setVisible(true);
                } else if ("Cashier".equalsIgnoreCase(role)) {
                    new CashierDashboard(userId).setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username or Password.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}