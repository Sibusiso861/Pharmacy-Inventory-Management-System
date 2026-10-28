import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class CashierDashboard extends JFrame {
    private int loggedInUserId;

    public CashierDashboard(int userId) {
        this.loggedInUserId = userId;
        setTitle("Cashier POS Portal - HealthFirst Pharmacy");
        setSize(900, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 60, 128)); // Teal Accent Header
        headerPanel.setPreferredSize(new Dimension(900, 55));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("Point of Sale (POS) Billing Workstation");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblUser = new JLabel("Terminal Active | User ID: " + loggedInUserId);
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUser.setForeground(new Color(220, 245, 245));

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblUser, BorderLayout.EAST);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabbedPane.addTab("  Point of Sale (POS) Cart  ", createPosPanel());
        tabbedPane.addTab("  Quick Stock Check  ", createStockCheckPanel());

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createPosPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblHeader = new JLabel("Current Customer Order & Cart Items");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHeader.setForeground(new Color(0, 70, 128));
        panel.add(lblHeader, BorderLayout.NORTH);

        String[] columns = {"ITEM ID", "MEDICINE NAME", "UNIT PRICE (R)", "QUANTITY", "TOTAL (R)"};
        DefaultTableModel cartModel = new DefaultTableModel(columns, 0);

        cartModel.addRow(new Object[]{1, "Paracetamol 500mg", "45.50", 2, "91.00"});
        cartModel.addRow(new Object[]{3, "Cough Syrup Expectorant", "85.00", 1, "85.00"});

        JTable cartTable = new JTable(cartModel);
        styleTable(cartTable);

        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));

        JButton btnClear = new JButton("Clear Cart");
        btnClear.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClear.setPreferredSize(new Dimension(120, 35));

        JButton btnCheckout = new JButton("Checkout & Print Bill");
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCheckout.setBackground(new Color(0, 47, 128));
        btnCheckout.setForeground(Color.BLACK);
        btnCheckout.setPreferredSize(new Dimension(180, 35));
        btnCheckout.setFocusPainted(false);
        btnCheckout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        bottomPanel.add(btnClear);
        bottomPanel.add(btnCheckout);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        btnCheckout.addActionListener(e -> {
            String billDetails = "====================================\n" +
                    "       HEALTHFIRST PHARMACY INVOICE       \n" +
                    "====================================\n" +
                    "Item 1: Paracetamol 500mg (x2) - R91.00\n" +
                    "Item 2: Cough Syrup Expectorant (x1) - R85.00\n" +
                    "------------------------------------\n" +
                    "TOTAL AMOUNT DUE: R176.00\n" +
                    "Payment Method: Cash / Card\n" +
                    "Status: PAID / Thank You!\n" +
                    "====================================";
            JOptionPane.showMessageDialog(this, billDetails, "Generated Bill Invoice Window", JOptionPane.INFORMATION_MESSAGE);
        });

        return panel;
    }

    private JPanel createStockCheckPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTable stockTable = new JTable();
        styleTable(stockTable);

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT medicine_id, name, company, price, quantity_in_stock, expiry_date FROM medicines")) {

            ResultSetMetaData metaData = rs.getMetaData();
            int colCount = metaData.getColumnCount();
            String[] cols = new String[colCount];
            for(int i = 1; i <= colCount; i++) {
                cols[i - 1] = metaData.getColumnName(i).toUpperCase();
            }

            DefaultTableModel model = new DefaultTableModel(cols, 0);
            while(rs.next()) {
                Object[] row = new Object[colCount];
                for(int i = 1; i <= colCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                model.addRow(row);
            }
            stockTable.setModel(model);
        } catch(Exception e) {
            e.printStackTrace();
        }

        JLabel lblStock = new JLabel("Medicine Price & Availability Quick Lookup");
        lblStock.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblStock.setForeground(new Color(0, 55, 128));

        panel.add(lblStock, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(stockTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(200, 235, 235));
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(240, 248, 248));
        header.setForeground(new Color(0, 51, 128));
        header.setPreferredSize(new Dimension(100, 32));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.LEFT);
        table.setDefaultRenderer(Object.class, centerRenderer);
    }
}