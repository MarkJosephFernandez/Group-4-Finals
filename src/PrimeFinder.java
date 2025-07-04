import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class PrimeFinder extends JFrame {
    private JTextField startField;
    private JTextField endField;
    private JTextArea resultArea;
    private JLabel countLabel;
    private JButton findButton;
    private JButton clearButton;
    private JProgressBar progressBar;
    private JRadioButton basicAlgorithm;
    private JRadioButton sieveAlgorithm;

    public PrimeFinder() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Prime Number Finder");
        setSize(700, 600);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void initializeComponents() {
        // Input components
        startField = new JTextField(10);
        endField = new JTextField(10);

        // Result components
        resultArea = new JTextArea(15, 50);
        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        resultArea.setBackground(Color.WHITE);

        countLabel = new JLabel("Prime count will appear here");
        countLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        countLabel.setForeground(new Color(0, 100, 0));

        // Buttons
        findButton = new JButton("Find Primes");
        findButton.setBackground(new Color(70, 130, 180));
        findButton.setForeground(Color.WHITE);
        findButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));

        clearButton = new JButton("Clear");
        clearButton.setBackground(new Color(220, 20, 60));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));

        // Progress bar
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);

        // Algorithm selection
        basicAlgorithm = new JRadioButton("Basic Algorithm", true);
        sieveAlgorithm = new JRadioButton("Sieve of Eratosthenes");
        ButtonGroup algorithmGroup = new ButtonGroup();
        algorithmGroup.add(basicAlgorithm);
        algorithmGroup.add(sieveAlgorithm);
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Input panel
        JPanel inputPanel = createInputPanel();

        // Result panel
        JPanel resultPanel = createResultPanel();

        // Button panel
        JPanel buttonPanel = createButtonPanel();

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(resultPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(new TitledBorder("Input Range"));
        GridBagConstraints gbc = new GridBagConstraints();

        // Start range
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        inputPanel.add(new JLabel("Start:"), gbc);

        gbc.gridx = 1;
        inputPanel.add(startField, gbc);

        // End range
        gbc.gridx = 2;
        inputPanel.add(new JLabel("End:"), gbc);

        gbc.gridx = 3;
        inputPanel.add(endField, gbc);

        // Algorithm selection
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        inputPanel.add(basicAlgorithm, gbc);

        gbc.gridx = 2; gbc.gridwidth = 2;
        inputPanel.add(sieveAlgorithm, gbc);

        return inputPanel;
    }

    private JPanel createResultPanel() {
        JPanel resultPanel = new JPanel(new BorderLayout(5, 5));
        resultPanel.setBorder(new TitledBorder("Results"));

        // Count label
        JPanel countPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        countPanel.add(countLabel);

        // Scrollable text area
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        resultPanel.add(countPanel, BorderLayout.NORTH);
        resultPanel.add(scrollPane, BorderLayout.CENTER);

        return resultPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout());

        buttonPanel.add(findButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(progressBar);

        return buttonPanel;
    }

    private void setupEventHandlers() {
        findButton.addActionListener(new ActionListener () {
            @Override
            public void actionPerformed(ActionEvent e) {
                findPrimes();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearResults();
            }
        });

        // Allow Enter key to trigger search
        startField.addActionListener(e -> findPrimes());
        endField.addActionListener(e -> findPrimes());
    }

    private void findPrimes() {
        try {
            int start = Integer.parseInt(startField.getText().trim());
            int end = Integer.parseInt(endField.getText().trim());

            if (start > end) {
                showError("Start value must be less than or equal to end value!");
                return;
            }

            if (start < 0 || end < 0) {
                showError("Please enter non-negative numbers!");
                return;
            }

            // Show progress bar for large rangesz
            if (end - start > 1000) {
                progressBar.setVisible(true);
                progressBar.setIndeterminate(true);
            }

            // Disable button during calculation
            findButton.setEnabled(false);

            // Use SwingWorker for background processing
            SwingWorker<List<Integer>, Void> worker = new SwingWorker<List<Integer>, Void>() {
                @Override
                protected List<Integer> doInBackground() throws Exception {
                    if (sieveAlgorithm.isSelected()) {
                        return sieveOfEratosthenes(start, end);
                    } else {
                        return findPrimesInRange(start, end);
                    }
                }

                @Override
                protected void done() {
                    try {
                        List<Integer> primes = get();
                        displayResults(start, end, primes);
                    } catch (Exception ex) {
                        showError("Error calculating primes: " + ex.getMessage());
                    } finally {
                        findButton.setEnabled(true);
                        progressBar.setVisible(false);
                    }
                }
            };

            worker.execute();

        } catch (NumberFormatException ex) {
            showError("Please enter valid integers!");
        }
    }

    private void clearResults() {
        startField.setText("");
        endField.setText("");
        resultArea.setText("");
        countLabel.setText("Prime count will appear here");
        startField.requestFocus();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    private void displayResults(int start, int end, List<Integer> primes) {
        if (primes.isEmpty()) {
            countLabel.setText("No prime numbers found in range " + start + " to " + end);
            resultArea.setText("No prime numbers found in the specified range.");
            return;
        }

        countLabel.setText("Found " + primes.size() + " prime numbers from " + start + " to " + end);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < primes.size(); i++) {
            sb.append(primes.get(i));

            if (i < primes.size() - 1) {
                sb.append(", ");
            }

            // New line every 10 numbers for better readability
            if ((i + 1) % 10 == 0 && i < primes.size() - 1) {
                sb.append("\n");
            }
        }

        resultArea.setText(sb.toString());
        resultArea.setCaretPosition(0); // Scroll to top
    }

    // Prime finding algorithms
    public static List<Integer> findPrimesInRange(int start, int end) {
        List<Integer> primes = new ArrayList<>();

        for (int num = start; num <= end; num++) {
            if (isPrime(num)) {
                primes.add(num);
            }
        }

        return primes;
    }

    public static boolean isPrime(int num) {
        if (num < 2) return false;
        if (num == 2) return true;
        if (num % 2 == 0) return false;

        for (int i = 3; i * i <= num; i += 2) {
            if (num % i == 0) {
                return false;
            }
        }

        return true;
    }

    public static List<Integer> sieveOfEratosthenes(int start, int end) {
        List<Integer> primes = new ArrayList<>();

        if (end < 2) return primes;

        boolean[] isPrime = new boolean[end + 1];
        for (int i = 2; i <= end; i++) {
            isPrime[i] = true;
        }

        for (int p = 2; p * p <= end; p++) {
            if (isPrime[p]) {
                for (int i = p * p; i <= end; i += p) {
                    isPrime[i] = false;
                }
            }
        }

        for (int i = Math.max(2, start); i <= end; i++) {
            if (isPrime[i]) {
                primes.add(i);
            }
        }

        return primes;
    }

    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
        } catch (Exception e) {
            // Use default look and feel
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PrimeFinder().setVisible(true);
            }
        });
    }
}