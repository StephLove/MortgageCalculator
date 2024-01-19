import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class MortgageCalculator extends JFrame {
    private JTextField loanAmountField;
    private JTextField loanLengthField;
    private JTextField interestRateField;
    private JTextArea resultArea;
    private JPanel chartPanel;

    public MortgageCalculator() {
        setTitle("Mortgage Payment");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initializeComponents();
        layoutComponents();
    }

    private void initializeComponents() {
        loanAmountField = new JTextField(10);
        loanLengthField = new JTextField(10);
        interestRateField = new JTextField(10);
        resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);

        chartPanel = new JPanel();

        JButton calculateButton = new JButton("Calculate");
        calculateButton.addActionListener(e -> calculatePayments());
       // calculateButton.setIcon(new ImageIcon("calculate-icon.png"));

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearFields());
       // clearButton.setIcon(new ImageIcon("clear-icon.png"));
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputPanel.add(new JLabel("Total Loan Amount:"));
        inputPanel.add(loanAmountField);
        inputPanel.add(new JLabel("Loan Length (years):"));
        inputPanel.add(loanLengthField);
        inputPanel.add(new JLabel("Annual Interest Rate (%):"));
        inputPanel.add(interestRateField);

        JPanel buttonPanel = new JPanel();
        JButton calculateButton = new JButton("Calculate");
        calculateButton.addActionListener(e -> calculatePayments());
        // calculateButton.setIcon(new ImageIcon("calculate-icon.png"));
        buttonPanel.add(calculateButton);

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearFields());
        // clearButton.setIcon(new ImageIcon("clear-icon.png"));
        buttonPanel.add(clearButton);

        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(new JScrollPane(resultArea), BorderLayout.SOUTH);
        add(chartPanel, BorderLayout.EAST);
    }

    private void calculatePayments() {
        try {
            double loanAmount = Double.parseDouble(loanAmountField.getText());
            int loanLengthYears = Integer.parseInt(loanLengthField.getText());
            int loanLengthMonths = loanLengthYears * 12;
            double annualInterestRate = Double.parseDouble(interestRateField.getText());

            double monthlyInterestRate = annualInterestRate / 12 / 100;
            double monthlyPayment = (loanAmount * monthlyInterestRate) /
                    (1 - Math.pow(1 + monthlyInterestRate, -loanLengthMonths));

            double remainingPrincipal = loanAmount;
            double totalInterestPaid = 0;
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

            for (int month = 1; month <= loanLengthMonths; month++) {
                double interestPayment = remainingPrincipal * monthlyInterestRate;
                double principalPayment = monthlyPayment - interestPayment;
                remainingPrincipal -= principalPayment;
                totalInterestPaid += interestPayment;


                resultArea.append(String.format("Month %d: Principal Paid = %s, Interest Paid = %s, Remaining Principal = %s\n",
                        month, currencyFormat.format(principalPayment), currencyFormat.format(interestPayment), currencyFormat.format(remainingPrincipal)));


                dataset.addValue(principalPayment, "Principal Paid", String.valueOf(month));
                dataset.addValue(interestPayment, "Interest Paid", String.valueOf(month));
            }


            resultArea.append(String.format("Total Cost of Loan = %s\n", currencyFormat.format(loanAmount + totalInterestPaid)));
            resultArea.setText(resultArea.getText()); // Update the JTextArea
            updateChart(dataset);
            this.setSize(1000, 600); // Increased window size after calculations for more space
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid numeric values.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        loanAmountField.setText("");
        loanLengthField.setText("");
        interestRateField.setText("");
        resultArea.setText("");
        chartPanel.removeAll();
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    private void updateChart(DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBarChart(
                "Principal vs Interest Payments Over Time",
                "Month",
                "Amount",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        this.chartPanel.removeAll();
        this.chartPanel.add(chartPanel);
        this.chartPanel.revalidate();
        this.chartPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MortgageCalculator().setVisible(true));
    }
}
