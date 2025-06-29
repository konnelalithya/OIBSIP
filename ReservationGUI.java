import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class ReservationGUI extends JFrame {
    private JTextField usernameField, passwordField;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Connection conn;

    public ReservationGUI() {
        setTitle("🚆 Online Reservation System");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initDB();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(loginPanel(), "Login");
        mainPanel.add(buildReservationPanel(), "Reserve");
        mainPanel.add(buildCancelPanel(), "Cancel");

        add(mainPanel);
        cardLayout.show(mainPanel, "Login");
    }

    private void initDB() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/reservationdb", "root", "W7301@jqir#");
        } catch (SQLException e) {
            showError("DB Connection Failed: " + e.getMessage());
        }
    }

    private JPanel loginPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBackground(new Color(245, 250, 255));
    panel.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(Color.GRAY),
        "🔐 Login", TitledBorder.CENTER, TitledBorder.TOP));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5); // Small padding around components
    gbc.gridx = 0;
    gbc.anchor = GridBagConstraints.EAST;

    JLabel userLabel = new JLabel("Username:");
    JLabel passLabel = new JLabel("Password:");

    usernameField = new JTextField(15);
    passwordField = new JPasswordField(15);

    gbc.gridy = 0;
    panel.add(userLabel, gbc);
    gbc.gridx = 1;
    panel.add(usernameField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    panel.add(passLabel, gbc);
    gbc.gridx = 1;
    panel.add(passwordField, gbc);

    JButton loginBtn = styledButton("Login");
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    panel.add(loginBtn, gbc);

    loginBtn.addActionListener(e -> {
        String u = usernameField.getText();
        String p = passwordField.getText();
        if (u.equals("lalithya") && p.equals("123456789")) {
            cardLayout.show(mainPanel, "Reserve");
        } else {
            showError("Invalid credentials!");
        }
    });

    return panel;
}

    private JPanel buildReservationPanel() {
        JTextField[] fields = {
            new JTextField(15), new JTextField(15), new JTextField(15),
            new JTextField(15), new JTextField(15),
            new JTextField(15), new JTextField(15), new JTextField(15)
        };

        String[] labels = {
            "Name:", "Age:", "Gender:", "Train Number:", "Class Type:",
            "From Station:", "To Station:", "Journey Date (dd-mm-yyyy):"
        };

        JButton reserveBtn = styledButton("🎟️ Reserve");
        JButton goCancel = styledButton("❌ Go to Cancel");

        reserveBtn.addActionListener(e -> {
            String pnr = "PNR" + new Random().nextInt(10000);
            try {
                String trainName = getTrainName(Integer.parseInt(fields[3].getText()));
                String sql = "INSERT INTO reservations (pnr, name, age, gender, train_number, train_name, class_type, from_station, to_station, journey_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, pnr);
                pst.setString(2, fields[0].getText());
                pst.setInt(3, Integer.parseInt(fields[1].getText()));
                pst.setString(4, fields[2].getText());
                pst.setInt(5, Integer.parseInt(fields[3].getText()));
                pst.setString(6, trainName);
                pst.setString(7, fields[4].getText());
                pst.setString(8, fields[5].getText());
                pst.setString(9, fields[6].getText());
                pst.setDate(10, java.sql.Date.valueOf(convertToISODate(fields[7].getText())));
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "✅ Reserved! Your PNR: " + pnr);
            } catch (Exception ex) {
                showError("Reservation Failed: " + ex.getMessage());
            }
        });

        goCancel.addActionListener(e -> cardLayout.show(mainPanel, "Cancel"));

        JPanel panel = buildPanelWithFields("📝 Reserve Ticket", new Color(245, 255, 250), labels, fields, reserveBtn);
        panel.add(centeredPanel(goCancel));
        return panel;
    }

    private JPanel buildCancelPanel() {
        JTextField pnrField = new JTextField(15);
        JButton cancelBtn = styledButton("Cancel Ticket");

        cancelBtn.addActionListener(e -> {
            try {
                PreparedStatement pst = conn.prepareStatement("DELETE FROM reservations WHERE pnr = ?");
                pst.setString(1, pnrField.getText());
                int rows = pst.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "✅ Ticket Cancelled");
                } else {
                    showError("❌ PNR not found.");
                }
            } catch (SQLException ex) {
                showError("Cancellation Failed: " + ex.getMessage());
            }
        });

        return buildPanelWithFields("❌ Cancel Ticket", new Color(255, 245, 245),
            new String[]{"Enter PNR to Cancel:"},
            new JTextField[]{pnrField},
            cancelBtn);
    }

    // Utility panel builder
    private JPanel buildPanelWithFields(String title, Color bgColor, String[] labels, JTextField[] fields, JButton button) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), title, TitledBorder.CENTER, TitledBorder.TOP));
        panel.setBackground(bgColor);
        panel.add(Box.createVerticalStrut(10));

        for (int i = 0; i < labels.length; i++) {
            panel.add(formRow(labels[i], fields[i]));
        }
        panel.add(centeredPanel(button));
        return panel;
    }

    private JPanel formRow(String labelText, JTextField field) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row.setOpaque(false);
        row.add(new JLabel(labelText));
        row.add(field);
        field.setPreferredSize(new Dimension(180, 25));
        field.setBorder(new LineBorder(Color.GRAY, 1, true));
        return row;
    }

    private JPanel centeredPanel(JComponent comp) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.add(comp);
        return panel;
    }

    private JButton styledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(51, 153, 255));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Calibri", Font.BOLD, 14));
        btn.setBorder(new LineBorder(new Color(0, 120, 215), 1, true));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private String getTrainName(int trainNum) {
        Map<Integer, String> trainMap = Map.of(
            101, "Superfast Express",
            202, "Rajdhani Express",
            303, "Shatabdi Express"
        );
        return trainMap.getOrDefault(trainNum, "Unknown Train");
    }

    private String convertToISODate(String inputDate) {
        String[] parts = inputDate.split("-");
        return parts.length == 3 ? parts[2] + "-" + parts[1] + "-" + parts[0] : inputDate;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found.");
            return;
        }
        SwingUtilities.invokeLater(() -> new ReservationGUI().setVisible(true));
    }
}
