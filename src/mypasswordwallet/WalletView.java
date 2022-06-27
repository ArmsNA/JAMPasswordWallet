package mypasswordwallet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.function.Supplier;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

public class WalletView extends JFrame {

    private int loginIndex;

    private final int TIMEOUT_DELAY;

    private JTextField searchBar = null, urlField = null, userIDField = null,
            loginTitleField = null, revealedTextField = null, revealedMasterField = null,
            hintField = null, newRevealedMasterField, newHintField = null;

    private JPasswordField passwordField = null, masterField = null, newMasterField = null;

    private JLabel urlLabel = null, passwordLabel = null, userIDLabel = null,
            loginTitleLabel = null, revealedTextLabel = null, welcomeLabel = null,
            masterLabel = null, hintLabel = null, revealedMasterLabel = null, newMasterLabel = null,
            newHintLabel = null, newRevealedMasterLabel;

    private JPanel loginDoPanel = null, loginContentPanel = null, loginPanel = null,
            homePanel = null, homeLoginsPanel = null, homeDoPanel = null, homeBottomPanel = null,
            loginSidePanel = null, lockPanel = null, lockContentPanel = null,
            lockWelcomePanel = null, lockSidePanel = null, newPanel = null, newContentPanel = null,
            newSidePanel = null, newWelcomePanel = null;

    private GridLayout loginGrid = null, homeGrid = null, lockGrid = null, newGrid = null;

    private BorderLayout loginBorder = null, homeBorder = null, lockBorder = null, newBorder = null;

    private FlowLayout loginFlow = null, homeFlow = null, lockFlow = null, newFlow = null;

    private JButton goHomeButton = null, updateLoginButton = null, deleteLoginButton = null,
            searchButton = null, addLoginButton = null, revealTextButton = null,
            clearTitleButton = null, clearUserButton = null, clearPassButton = null,
            clearURLButton = null, clearRevealedButton = null, copyTitleButton = null,
            copyUserButton = null, copyPassButton = null, copyURLButton = null, copyRevealedButton = null,
            verifyMasterButton = null, logoutButton = null, resetMasterButton = null, hintButton = null,
            revealMasterButton = null, confirmMasterButton = null, confirmHintButton = null, revealNewMasterButton = null;

    private Timer timer = null;

    private final static Logger LOGGER = Logger.getLogger(WalletView.class.getName());

    private Handler fh = null;

    private Supplier<String> logMsg = null;

    private final WalletCtrl walletCtrl;

    private static boolean IS_TIMER_EXPIRED;

    public WalletView(WalletCtrl walletCtrl) {
        this.walletCtrl = walletCtrl;

        TIMEOUT_DELAY = 300000;
        IS_TIMER_EXPIRED = false;

        initializeComponents();
    }

    private void initializeComponents() {
        this.setSize(800, 450);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);


        //Initalize new screen contents
        newFlow = new FlowLayout(FlowLayout.CENTER);
        newWelcomePanel = new JPanel(newFlow);
        newWelcomePanel.setBackground(Color.CYAN);

        newGrid = new GridLayout(3, 1);
        newContentPanel = new JPanel(newGrid);
        newContentPanel.setBackground(Color.CYAN);

        newSidePanel = new JPanel(newGrid);
        newSidePanel.setBackground(Color.CYAN);

        newBorder = new BorderLayout();
        newPanel = new JPanel(newBorder);

        welcomeLabel = new JLabel("Welcome to your password wallet");
        welcomeLabel.setFont(new Font("Garamond", Font.BOLD, 24));

        newMasterLabel = new JLabel("Enter a Master Password:");
        newMasterField = new JPasswordField();

        newRevealedMasterLabel = new JLabel("Revealed Master:");
        newRevealedMasterLabel.setVisible(false);

        newRevealedMasterField = new JTextField();
        newRevealedMasterField.setEditable(false);
        newRevealedMasterField.setVisible(false);

        newHintLabel = new JLabel("Set up a hint (Optional):");

        newHintField = new JTextField();

        if (walletCtrl.getHint() != null) {
            newHintField.setText(walletCtrl.getHint());
        }

        confirmMasterButton = new JButton("Confirm");
        revealNewMasterButton = new JButton("Reveal");
        confirmHintButton = new JButton("Confirm");

        newWelcomePanel.add(welcomeLabel);

        newContentPanel.add(newMasterLabel);
        newContentPanel.add(newMasterField);
        newContentPanel.add(newRevealedMasterLabel);
        newContentPanel.add(newRevealedMasterField);
        newContentPanel.add(newHintLabel);
        newContentPanel.add(newHintField);

        newSidePanel.add(confirmMasterButton);
        newSidePanel.add(revealNewMasterButton);
        newSidePanel.add(confirmHintButton);

        confirmMasterButton.addActionListener(event -> setMasterPassword());
        revealNewMasterButton.addActionListener(event -> revealNewMaster());
        confirmHintButton.addActionListener(event -> setHint());

        //Initalize lock screen contents
        lockFlow = new FlowLayout(FlowLayout.CENTER);
        lockWelcomePanel = new JPanel(lockFlow);
        lockWelcomePanel.setBackground(Color.CYAN);

        lockGrid = new GridLayout(3, 1);
        lockContentPanel = new JPanel(lockGrid);
        lockContentPanel.setBackground(Color.CYAN);

        lockSidePanel = new JPanel(lockGrid);
        lockSidePanel.setBackground(Color.CYAN);

        lockBorder = new BorderLayout();
        lockPanel = new JPanel(lockBorder);
        lockPanel.setBackground(Color.CYAN);

        welcomeLabel = new JLabel("Welcome to your password wallet");
        welcomeLabel.setFont(new Font("Garamond", Font.BOLD, 24));

        masterLabel = new JLabel("Master Password:");
        masterField = new JPasswordField();

        revealedMasterLabel = new JLabel("Revealed Master:");
        revealedMasterLabel.setVisible(false);

        revealedMasterField = new JTextField();
        revealedMasterField.setEditable(false);
        revealedMasterField.setVisible(false);

        hintLabel = new JLabel("Hint:");
        hintLabel.setVisible(false);

        hintField = new JTextField();
        hintField.setEditable(false);
        hintField.setVisible(false);

        verifyMasterButton = new JButton("Verify");
        revealMasterButton = new JButton("Reveal");
        hintButton = new JButton("Hint");

        lockWelcomePanel.add(welcomeLabel);

        lockContentPanel.add(masterLabel);
        lockContentPanel.add(masterField);
        lockContentPanel.add(revealedMasterLabel);
        lockContentPanel.add(revealedMasterField);
        lockContentPanel.add(hintLabel);
        lockContentPanel.add(hintField);

        lockSidePanel.add(verifyMasterButton);
        lockSidePanel.add(revealMasterButton);
        lockSidePanel.add(hintButton);

        verifyMasterButton.addActionListener(event -> verifyMaster());
        revealMasterButton.addActionListener(event -> revealMaster());
        hintButton.addActionListener(event -> displayHint());

        //Initialize home page contents
        homeFlow = new FlowLayout(FlowLayout.CENTER);
        homeDoPanel = new JPanel(homeFlow);
        homeDoPanel.setBackground(Color.CYAN);

        homeBottomPanel = new JPanel(homeFlow);
        homeBottomPanel.setBackground(Color.CYAN);

        homeGrid = new GridLayout(5, 10);
        homeLoginsPanel = new JPanel(homeGrid);
        homeLoginsPanel.setBackground(Color.CYAN);

        homeBorder = new BorderLayout();
        homePanel = new JPanel(homeBorder);

        addLoginButton = new JButton();
        addLoginButton.setIcon(getIcon("plus.png", 25, 25));

        searchBar = new JTextField(15);
        searchBar.setHorizontalAlignment(JTextField.CENTER);

        searchButton = new JButton();
        searchButton.setIcon(getIcon("search.png", 25, 25));

        logoutButton = new JButton("Logout");
        resetMasterButton = new JButton("Reset Master");

        homeDoPanel.add(addLoginButton);
        homeDoPanel.add(searchBar);
        homeDoPanel.add(searchButton);

        homeBottomPanel.add(logoutButton);
        homeBottomPanel.add(resetMasterButton);

        addLoginButton.addActionListener(event -> addLogin());
        searchButton.addActionListener(event -> searchFunction());
        logoutButton.addActionListener(event -> logout());
        resetMasterButton.addActionListener(event -> switchToNewPage());

        updateLoginButtonPanel();

        //Initialize login page contents
        loginFlow = new FlowLayout(FlowLayout.CENTER);
        loginDoPanel = new JPanel(loginFlow);
        loginDoPanel.setBackground(Color.CYAN);

        loginGrid = new GridLayout(5, 2);
        loginContentPanel = new JPanel(loginGrid);
        loginContentPanel.setBackground(Color.CYAN);

        loginBorder = new BorderLayout();
        loginPanel = new JPanel(loginBorder);

        loginSidePanel = new JPanel(loginGrid);
        loginSidePanel.setBackground(Color.CYAN);

        loginTitleLabel = new JLabel("Login Title:");
        loginTitleField = new JTextField();

        userIDLabel = new JLabel("Username:");
        userIDField = new JTextField();

        passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        revealedTextLabel = new JLabel("Revealed Password:");
        revealedTextLabel.setVisible(false);

        revealedTextField = new JTextField();
        revealedTextField.setVisible(false);
        revealedTextField.setEditable(false);

        revealTextButton = new JButton();
        revealTextButton.setIcon(getIcon("preview.png", 50, 50));

        clearTitleButton = new JButton();
        clearTitleButton.setIcon(getIcon("clear.png", 50, 50));

        clearUserButton = new JButton();
        clearUserButton.setIcon(getIcon("clear.png", 50, 50));

        clearPassButton = new JButton();
        clearPassButton.setIcon(getIcon("clear.png", 50, 50));

        clearURLButton = new JButton();
        clearURLButton.setIcon(getIcon("clear.png", 50, 50));

        clearRevealedButton = new JButton();
        clearRevealedButton.setIcon(getIcon("cancel.png", 50, 50));
        clearRevealedButton.setVisible(false);

        copyTitleButton = new JButton();
        copyTitleButton.setIcon(getIcon("clipboard.png", 50, 50));

        copyUserButton = new JButton();
        copyUserButton.setIcon(getIcon("clipboard.png", 50, 50));

        copyPassButton = new JButton();
        copyPassButton.setIcon(getIcon("clipboard.png", 50, 50));

        copyURLButton = new JButton();
        copyURLButton.setIcon(getIcon("clipboard.png", 50, 50));

        copyRevealedButton = new JButton();
        copyRevealedButton.setIcon(getIcon("cancel.png", 50, 50));
        copyRevealedButton.setVisible(false);

        deleteLoginButton = new JButton();
        deleteLoginButton.setIcon(getIcon("trash.png", 50, 50));

        goHomeButton = new JButton();
        goHomeButton.setIcon(getIcon("home.png", 50, 50));

        updateLoginButton = new JButton();
        updateLoginButton.setIcon(getIcon("update.png", 50, 50));

        urlLabel = new JLabel("URL: ");
        urlField = new JTextField();

        loginContentPanel.add(loginTitleLabel);
        loginContentPanel.add(loginTitleField);
        loginContentPanel.add(userIDLabel);
        loginContentPanel.add(userIDField);
        loginContentPanel.add(passwordLabel);
        loginContentPanel.add(passwordField);
        loginContentPanel.add(urlLabel);
        loginContentPanel.add(urlField);
        loginContentPanel.add(revealedTextLabel);
        loginContentPanel.add(revealedTextField);

        loginSidePanel.add(clearTitleButton);
        loginSidePanel.add(copyTitleButton);
        loginSidePanel.add(clearUserButton);
        loginSidePanel.add(copyUserButton);
        loginSidePanel.add(clearPassButton);
        loginSidePanel.add(copyPassButton);
        loginSidePanel.add(clearURLButton);
        loginSidePanel.add(copyURLButton);
        loginSidePanel.add(clearRevealedButton);
        loginSidePanel.add(copyRevealedButton);

        loginDoPanel.add(goHomeButton);
        loginDoPanel.add(revealTextButton);
        loginDoPanel.add(updateLoginButton);
        loginDoPanel.add(deleteLoginButton);

        clearTitleButton.addActionListener(event -> clearField("title"));
        clearUserButton.addActionListener(event -> clearField("user"));
        clearPassButton.addActionListener(event -> clearField("pass"));
        clearURLButton.addActionListener(event -> clearField("url"));

        copyTitleButton.addActionListener(event -> copyText("title"));
        copyUserButton.addActionListener(event -> copyText("user"));
        copyPassButton.addActionListener(event -> copyText("pass"));
        copyURLButton.addActionListener(event -> copyText("url"));

        revealTextButton.addActionListener(event -> revealText());
        updateLoginButton.addActionListener(event -> update());
        deleteLoginButton.addActionListener(event -> delete());
        goHomeButton.addActionListener(event -> goHomeWithCheck());

        timer = new Timer(TIMEOUT_DELAY, event -> sessionTimeout());
        
        //Outputs logs in XML format to a file called wallet.log
        //  log file persists through instances and is appended
        //  when the log() method is called
        try {
            fh = new FileHandler("wallet.log", true);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            LOGGER.addHandler(fh);
        }

        //Send user to program start page
        if (walletCtrl.getMasterHash() == null) {
            switchToNewPage();
        } else {
            switchToLockPage();
        }
    }

    private void goHomeWithCheck() {
        if (walletCtrl.isLoginNullOrEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a value to "
                    + "update the field(s) with-- "
                    + "\nAny one field cannot be left empty",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else if (areLoginFieldsEmpty() || doFieldsMatchMappedLogin()) {
            if (JOptionPane.showConfirmDialog(this, "You may have unsaved changes-- "
                    + "\nClick \"yes\" to go home "
                    + "and discard changes or \"cancel\"/\"no\" to stay.") == 0) {
                goHomeWithoutCheck();
            }
        } else {
            goHomeWithoutCheck();
        }
    }

    private void goHomeWithoutCheck() {
        timer.start();

        loginIndex = walletCtrl.getFileSize() - 1;

        setLoginButtonPanelVisibility(true);
        revealedTextLabel.setVisible(false);
        revealedTextField.setVisible(false);
        copyRevealedButton.setVisible(false);
        clearRevealedButton.setVisible(false);

        this.setTitle("JAMPasswordWallet Home");
        this.setContentPane(homePanel);
        this.getContentPane().add(homeDoPanel, BorderLayout.NORTH);
        this.getContentPane().add(homeLoginsPanel, BorderLayout.CENTER);
        this.getContentPane().add(homeBottomPanel, BorderLayout.SOUTH);
        this.validate();
        this.repaint();
    }

    private void switchToLoginPage() {
        timer.stop();

        //Condition to only log access to previously created logins; 
        //  does not log access to newly created logins
        if (walletCtrl.getLogin(loginIndex).getLoginTitle() != null) {
            log("warning", "Login accessed: " + walletCtrl.getLogin(loginIndex).getLoginTitle());
        }

        displayFieldValues();
        this.setTitle("JAMPasswordWallet Login");
        this.setContentPane(loginPanel);
        this.getContentPane().add(loginContentPanel, BorderLayout.CENTER);
        this.getContentPane().add(loginSidePanel, BorderLayout.EAST);
        this.getContentPane().add(loginDoPanel, BorderLayout.SOUTH);
        this.validate();
        this.repaint();
    }

    private void switchToLockPage() {
        timer.stop();

        this.setTitle("JAMPasswordWallet");
        this.setContentPane(lockPanel);
        this.getContentPane().add(lockWelcomePanel, BorderLayout.NORTH);
        this.getContentPane().add(lockContentPanel, BorderLayout.CENTER);
        this.getContentPane().add(lockSidePanel, BorderLayout.EAST);
        this.validate();
        this.repaint();
    }

    private void switchToNewPage() {
        timer.stop();

        if (walletCtrl.getMasterHash() == null) {
            this.setTitle("JAMPasswordWallet");
            this.setContentPane(newPanel);
            this.getContentPane().add(newWelcomePanel, BorderLayout.NORTH);
            this.getContentPane().add(newContentPanel, BorderLayout.CENTER);
            this.getContentPane().add(newSidePanel, BorderLayout.EAST);
            this.validate();
            this.repaint();
        } else {
            IS_TIMER_EXPIRED = false;
            timer.restart();

            if (JOptionPane.showConfirmDialog(this, "Are you sure you want to reset your master password?") == 0) {
                if (!IS_TIMER_EXPIRED) {
                    IS_TIMER_EXPIRED = false;
                    timer.restart();
                    if (JOptionPane.showConfirmDialog(this, "Click \"yes\" to confirm reset") == 0) {
                        if (!IS_TIMER_EXPIRED) {
                            timer.stop();
                            newMasterField.setText("");

                            this.setTitle("JAMPasswordWallet");
                            this.setContentPane(newPanel);
                            this.getContentPane().add(newWelcomePanel, BorderLayout.NORTH);
                            this.getContentPane().add(newContentPanel, BorderLayout.CENTER);
                            this.getContentPane().add(newSidePanel, BorderLayout.EAST);
                            this.validate();
                            this.repaint();
                        }
                    }
                }
            }
        }
    }

    private void sessionTimeout() {
        masterField.setText("");
        switchToLockPage();
        IS_TIMER_EXPIRED = true;
        
        log("fine", "User logged out for inactivity");

        JOptionPane.showMessageDialog(this, "Signed out for inactivity", "Session Timeout", JOptionPane.PLAIN_MESSAGE);

    }

    private void setMasterPassword() {
        if (!getNewMasterFieldContents().isEmpty()) {
            walletCtrl.overwriteMaster();

            JOptionPane.showMessageDialog(this, "Master password successfully set!");
            goHomeWithoutCheck();
        } else {
            JOptionPane.showMessageDialog(this, "You must set a master password!"
                    + "\nMaster password field cannot be left empty.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void setHint() {
        walletCtrl.overwriteHint();
        JOptionPane.showMessageDialog(this, "Hint successfully set!");
    }

    private void displayHint() {
        if (hintLabel.isVisible() && hintField.isVisible()) {
            hintLabel.setVisible(false);
            hintField.setVisible(false);
        } else if (walletCtrl.getHint() == null) {
            hintField.setText("");
            hintLabel.setVisible(true);
            hintField.setVisible(true);
        } else {
            hintField.setText(walletCtrl.getHint());
            hintLabel.setVisible(true);
            hintField.setVisible(true);
        }
    }

    private String getMasterFieldContentsAsHash() {
        String masterHash = "";

        try {
            PBEKeySpec keySpec = new PBEKeySpec(getMasterFieldContents().toCharArray(),
                    walletCtrl.getMasterSalt(), walletCtrl.getMasterIterations(), walletCtrl.getMasterKeyLength());
            SecretKeyFactory skf = SecretKeyFactory.getInstance(walletCtrl.getMasterHashAlgorithm());
            byte[] hashed = skf.generateSecret(keySpec).getEncoded();

            for (byte b : hashed) {
                masterHash += b;
            }

            masterHash = masterHash.replaceAll("-", "");
            return masterHash;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println(e.getMessage());
        }

        return masterHash;

    }

    private void verifyMaster() {
        if (getMasterFieldContentsAsHash().equals(walletCtrl.getMasterHash())) {
            log("severe", "Successful password attempt");

            hintLabel.setVisible(false);
            hintField.setVisible(false);
            revealedMasterLabel.setVisible(false);
            revealedMasterField.setVisible(false);

            goHomeWithoutCheck();
        } else {
            log("warning", "Invalid password attempt");

            JOptionPane.showMessageDialog(this, "Incorrect Password",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        IS_TIMER_EXPIRED = false;
        timer.restart();

        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?") == 0) {
            log("fine", "User logged out manually");

            masterField.setText("");

            switchToLockPage();
        }
    }

    private void addLogin() {
        if (walletCtrl.getFileSize() <= 0) {
            loginIndex = 0;
        } else {
            loginIndex = walletCtrl.getFileSize() - 1;
        }

        final int index = loginIndex;

        JButton login = new JButton();
        homeLoginsPanel.add(login);
        login.addActionListener(event -> switchToLoginPage());

        if (walletCtrl.getFileSize() > 0) {
            loginIndex++;
            walletCtrl.addNewLogin(loginIndex);
            login.addActionListener(event -> setLoginIndex(index + 1));
        } else {
            walletCtrl.addNewLogin(0);
            login.addActionListener(event -> setLoginIndex(index));
        }

        log("fine", "New login added");

        switchToLoginPage();

        walletCtrl.updateLogin();
    }

    private void searchFunction() {
        IS_TIMER_EXPIRED = false;
        timer.restart();

        if (loginIndex < 0) {
            JOptionPane.showMessageDialog(this, "No logins to search for",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            setLoginButtonPanelVisibility(false);

            for (int i = 0; i < homeLoginsPanel.getComponentCount(); i++) {
                if (getLoginButtonAtIndex(i).getText().contains(searchBar.getText().toUpperCase())) {
                    getLoginButtonAtIndex(i).setVisible(true);
                }
            }
        }
    }

    private void revealText() {
        if (revealedTextLabel.isVisible() && revealedTextField.isVisible()) {
            revealedTextLabel.setVisible(false);
            revealedTextField.setVisible(false);
            copyRevealedButton.setVisible(false);
            clearRevealedButton.setVisible(false);
        } else {
            revealedTextField.setText(getPasswordFieldContents());

            revealedTextLabel.setVisible(true);
            revealedTextField.setVisible(true);
            copyRevealedButton.setVisible(true);
            clearRevealedButton.setVisible(true);
        }
    }

    private void revealMaster() {
        if (revealedMasterLabel.isVisible() && revealedMasterField.isVisible()) {
            revealedMasterLabel.setVisible(false);
            revealedMasterField.setVisible(false);
        } else {
            revealedMasterField.setText(getMasterFieldContents());

            revealedMasterLabel.setVisible(true);
            revealedMasterField.setVisible(true);
        }
    }

    private void revealNewMaster() {
        if (newRevealedMasterLabel.isVisible() && newRevealedMasterField.isVisible()) {
            newRevealedMasterLabel.setVisible(false);
            newRevealedMasterField.setVisible(false);
        } else {
            newRevealedMasterField.setText(getNewMasterFieldContents());

            newRevealedMasterLabel.setVisible(true);
            newRevealedMasterField.setVisible(true);
        }
    }

    private ImageIcon getIcon(String imgFileName, int width, int height) {
        Image img = new ImageIcon(imgFileName).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private void displayFieldValues() {
        loginTitleField.setText(walletCtrl.getLogin(loginIndex).getLoginTitle());
        userIDField.setText(walletCtrl.getLogin(loginIndex).getUserID());
        passwordField.setText(walletCtrl.getLogin(loginIndex).getPassHash());
        urlField.setText(walletCtrl.getLogin(loginIndex).getUrl());
    }

    private void copyText(String text) {
        String textToBeCopied = "";
        StringSelection ss;

        switch (text) {
            case "title":
                textToBeCopied = walletCtrl.getLogin(loginIndex).getLoginTitle();
                break;
            case "user":
                textToBeCopied = walletCtrl.getLogin(loginIndex).getUserID();
                break;
            case "pass":
                textToBeCopied = walletCtrl.getLogin(loginIndex).getPassHash();
                break;
            case "url":
                textToBeCopied = walletCtrl.getLogin(loginIndex).getUrl();
            default:
                break;
        }

        ss = new StringSelection(textToBeCopied);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
    }

    private void clearField(String text) {

        switch (text) {
            case "title":
                loginTitleField.setText("");
                break;
            case "user":
                userIDField.setText("");
                break;
            case "pass":
                passwordField.setText("");
                break;
            case "url":
                urlField.setText("");
            default:
                break;
        }
    }

    private void update() {
        if (areLoginFieldsEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a value to "
                    + "update the field(s) with (field cannot be left empty)",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            walletCtrl.updateLogin();
            log("fine", "Successful login update; login updated: "
                    + walletCtrl.getLogin(loginIndex).getLoginTitle());

            JButton logButton = getLoginButtonAtIndex(loginIndex);
            logButton.setText(getLoginTitleFieldContents().toUpperCase());
            JOptionPane.showMessageDialog(this, "Login updated!");
        }
    }

    private void delete() {
        walletCtrl.deleteLogin();
        
        //Condition to only log deletion to previously created logins; 
        //  does not log deletion of newly created logins from add method
        if (!getLoginButtonAtIndex(loginIndex).getText().isEmpty()) {
            log("warning", "Login deleted: " + getLoginButtonAtIndex(loginIndex).getText());
        }

        JOptionPane.showMessageDialog(this, "Login deleted!");

        homeLoginsPanel.remove(loginIndex);
        loginIndex--;

        homeLoginsPanel.removeAll();
        updateLoginButtonPanel();

        goHomeWithoutCheck();
    }

    private void updateLoginButtonPanel() {
        for (int i = 0; i < walletCtrl.getFileSize(); i++) {
            final int index = i;
            JButton logButton = new JButton(walletCtrl.getLogin(i).getLoginTitle().toUpperCase());
            homeLoginsPanel.add(logButton);

            logButton.addActionListener(event -> switchToLoginPage());
            logButton.addActionListener(event -> setLoginIndex(index));
        }
    }

    private void setLoginIndex(int index) {
        this.loginIndex = index;
    }

    private void setLoginButtonPanelVisibility(boolean condition) {
        for (int j = 0; j < homeLoginsPanel.getComponentCount(); j++) {
            getLoginButtonAtIndex(j).setVisible(condition);
        }
    }

    public int getLoginIndex() {
        return loginIndex;
    }

    private void log(String severity, String msg) {
        severity = severity.toUpperCase();
        LOGGER.setLevel(Level.parse(severity));
        logMsg = () -> msg;
        switch (severity) {
            case "SEVERE":
                LOGGER.severe(logMsg);
                break;
            case "WARNING":
                LOGGER.warning(logMsg);
                break;
            case "FINE":
                LOGGER.fine(logMsg);
                break;
            case "FINEST":
                LOGGER.finest(logMsg);
            default:
                break;
        }
    }

    private JButton getLoginButtonAtIndex(int index) {
        return ((JButton) homeLoginsPanel.getComponent(index));
    }

    public String getLoginTitleFieldContents() {
        return loginTitleField.getText();
    }

    public String getUserIDFieldContents() {
        return userIDField.getText();
    }

    public String getPasswordFieldContents() {
        String password = "";
        for (char c : passwordField.getPassword()) {
            password += c;
        }
        return password;
    }

    public String getMasterFieldContents() {
        String master = "";
        for (char c : masterField.getPassword()) {
            master += c;
        }
        return master;
    }

    public String getNewMasterFieldContents() {
        String master = "";
        for (char c : newMasterField.getPassword()) {
            master += c;
        }
        return master;
    }

    public String getNewHintFieldContents() {
        return newHintField.getText();
    }

    public String getURLFieldContents() {
        return urlField.getText();
    }

    private boolean areLoginFieldsEmpty() {
        return (getLoginTitleFieldContents().isEmpty() || getUserIDFieldContents().isEmpty()
                || getPasswordFieldContents().isEmpty() || getURLFieldContents().isEmpty());
    }

    private boolean doFieldsMatchMappedLogin() {
        return (!walletCtrl.getLogin(loginIndex).getLoginTitle().equals(getLoginTitleFieldContents())
                || !walletCtrl.getLogin(loginIndex).getUserID().equals(getUserIDFieldContents())
                || !walletCtrl.getLogin(loginIndex).getPassHash().equals(getPasswordFieldContents())
                || !walletCtrl.getLogin(loginIndex).getUrl().equals(getURLFieldContents()));
    }
}
