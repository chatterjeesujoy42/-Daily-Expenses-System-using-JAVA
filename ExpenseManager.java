import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Vector;

public class ExpenseManager {
    private JFrame frame;
    private JTable table;
    private DefaultTableModel model;
    private JTextField dateField, descField, amountField;
    private JLabel totalExpenseLabel;
    private double totalExpense = 0.0;
    
    public ExpenseManager() {
        frame = new JFrame("Personal Expense Manager");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Table setup
        String[] columns = {"Date", "Description", "Amount"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Input fields
        JPanel inputPanel = new JPanel(new GridLayout(2, 3));
        dateField = new JTextField();
        descField = new JTextField();
        amountField = new JTextField();
        JButton addButton = new JButton("Add");
        addButton.addActionListener(new AddExpenseListener());

        inputPanel.add(new JLabel("Date:"));
        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(dateField);
        inputPanel.add(descField);
        inputPanel.add(amountField);

        // Bottom panel
        JPanel bottomPanel = new JPanel();
        totalExpenseLabel = new JLabel("Total Expense: 0.0");
        bottomPanel.add(totalExpenseLabel);
        bottomPanel.add(addButton);

        // Add components to frame
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        
        loadExpenses();
        frame.setVisible(true);
    }

    private class AddExpenseListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String date = dateField.getText();
            String description = descField.getText();
            String amountStr = amountField.getText();
            
            if (date.isEmpty() || description.isEmpty() || amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields must be filled!");
                return;
            }
            
            try {
                double amount = Double.parseDouble(amountStr);
                totalExpense += amount;
                model.addRow(new Object[]{date, description, amount});
                totalExpenseLabel.setText("Total Expense: " + totalExpense);
                saveExpense(date, description, amount);
                
                dateField.setText("");
                descField.setText("");
                amountField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid amount format!");
            }
        }
    }

    private void saveExpense(String date, String description, double amount) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("expenses.txt", true))) {
            writer.write(date + "," + description + "," + amount + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadExpenses() {
        try (BufferedReader reader = new BufferedReader(new FileReader("expenses.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    model.addRow(parts);
                    totalExpense += Double.parseDouble(parts[2]);
                }
            }
            totalExpenseLabel.setText("Total Expense: " + totalExpense);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExpenseManager::new);
    }
}
