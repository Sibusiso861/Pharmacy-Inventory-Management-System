import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {
        setTitle("Admin Portal - HealthFirst Pharmacy Management System");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Top Banner
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(15, 76, 129)); // Classic Dark Blue Header
        headerPanel.setPreferredSize(new Dimension(950, 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("System Administrator Control Center");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblTagline = new JLabel("HealthFirst Pharmacy | Management Portal");
        lblTagline.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblTagline.setForeground(new Color(200, 225, 255));

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblTagline, BorderLayout.EAST);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Adding Tabs
        tabbedPane.addTab("  Manage Medicines  ", createMedicinesPanel());
        tabbedPane.addTab("  Manage Suppliers  ", createSuppliersPanel());
        tabbedPane.addTab("  Manage Users  ", createUsersPanel());
        tabbedPane.addTab("  Analytical Reports  ", createReportsPanel());

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createMedicinesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblInfo = new JLabel("Inventory Stock Master Table");
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblInfo.setForeground(new Color(15, 76, 129));
        panel.add(lblInfo, BorderLayout.NORTH);

        JTable table = new JTable();
        styleTable(table);
        loadTableData(table, "SELECT * FROM medicines");

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSuppliersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblInfo = new JLabel("Registered Medical Suppliers Directory");
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblInfo.setForeground(new Color(15, 76, 129));
        panel.add(lblInfo, BorderLayout.NORTH);

        JTable table = new JTable();
        styleTable(table);
        loadTableData(table, "SELECT * FROM suppliers");

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblInfo = new JLabel("System Users & Cashier Roles Directory");
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblInfo.setForeground(new Color(15, 76, 129));
        panel.add(lblInfo, BorderLayout.NORTH);

        JTable table = new JTable();
        styleTable(table);
        loadTableData(table, "SELECT user_id, username, role, full_name FROM users");

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JLabel lblSelect = new JLabel("Select Filter Report: ");
        lblSelect.setFont(new Font("Segoe UI", Font.BOLD, 13));

        String[] reportOptions = {"Sales Report", "Item-Wise Report", "Low Stock Report", "Expiry Report (Next Month)"};
        JComboBox<String> cmbReports = new JComboBox<>(reportOptions);
        cmbReports.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        topPanel.add(lblSelect);
        topPanel.add(cmbReports);
        panel.add(topPanel, BorderLayout.NORTH);

        JTable reportTable = new JTable();
        styleTable(reportTable);
        loadTableData(reportTable, "SELECT * FROM sales");

        JScrollPane scrollPane = new JScrollPane(reportTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        cmbReports.addActionListener(e -> {
            String selected = (String) cmbReports.getSelectedItem();
            if (selected != null) {
                if (selected.contains("Low Stock")) {
                    loadTableData(reportTable, "SELECT * FROM medicines WHERE quantity_in_stock <= reorder_level");
                } else if (selected.contains("Expiry")) {
                    loadTableData(reportTable, "SELECT * FROM medicines WHERE expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 1 MONTH)");
                } else if (selected.contains("Sales")) {
                    loadTableData(reportTable, "SELECT * FROM sales");
                } else {
                    loadTableData(reportTable, "SELECT m.name, SUM(si.quantity_sold) as total_sold FROM sale_items si JOIN medicines m ON si.medicine_id = m.medicine_id GROUP BY m.medicine_id");
                }
            }
        });

        return panel;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(210, 230, 250));
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(240, 244, 248));
        header.setForeground(new Color(15, 76, 129));
        header.setPreferredSize(new Dimension(100, 32));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.LEFT);
        table.setDefaultRenderer(Object.class, centerRenderer);
    }

    private void loadTableData(JTable table, String query) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            String[] columnNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = metaData.getColumnName(i).toUpperCase();
            }

            DefaultTableModel model = new DefaultTableModel(columnNames, 0);
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                model.addRow(row);
            }
            table.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}