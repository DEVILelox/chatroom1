package ÁÄÌìÊÒ;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginWindow extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton, changePasswordButton;
    private Connection conn;

    public LoginWindow() {
        super("Login Window");

        // Create GUI components
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        changePasswordButton = new JButton("Change Password");

        // Add components to the window
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridLayout(4, 2));
        contentPane.add(usernameLabel);
        contentPane.add(usernameField);
        contentPane.add(passwordLabel);
        contentPane.add(passwordField);
        contentPane.add(loginButton);
        contentPane.add(registerButton);
        contentPane.add(changePasswordButton);

        // Add action listeners to the buttons
        loginButton.addActionListener(this);
        registerButton.addActionListener(this);
        changePasswordButton.addActionListener(this);

        // Connect to the database
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost/mydatabase", "root", "root");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Set window properties
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            try {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE username='" + username + "'");
                if (rs.next()) {
                    if (password.equals(rs.getString("password"))) {
                        JOptionPane.showMessageDialog(this, "Login successful");
                    } else {
                        JOptionPane.showMessageDialog(this, "Incorrect password");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "User not found");
                }
                rs.close();
                stmt.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == registerButton) {
            // Open the registration window
            RegistrationWindow regWindow = new RegistrationWindow(conn);
        } else if (e.getSource() == changePasswordButton) {
            // Open the change password window
            ChangePasswordWindow changePasswordWindow = new ChangePasswordWindow(conn);
        }
    }

    public static void main(String[] args) {
        new LoginWindow();
    }
}

class RegistrationWindow extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton registerButton;
    private Connection conn;

    public RegistrationWindow(Connection conn) {
        super("Registration Window");

        // Save the database connection
        this.conn = conn;

        // Create GUI components
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        registerButton = new JButton("Register");

        // Add components to the window
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridLayout(3, 2));
        contentPane.add(usernameLabel);
        contentPane.add(usernameField);
        contentPane.add(passwordLabel);
        contentPane.add(passwordField);
        contentPane.add(registerButton);

        // Add action listeners to the buttons
        registerButton.addActionListener(this);

        // Set window properties
        setSize(300, 100);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerButton) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            try {
                Statement stmt = conn.createStatement();
                int rows = stmt.executeUpdate("INSERT INTO users (username, password) VALUES ('" + username + "', '" + password + "')");
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Registration successful");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Registration failed");
                }
                stmt.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

class ChangePasswordWindow extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField oldPasswordField, newPasswordField, confirmNewPasswordField;
    private JButton changePasswordButton;
    private Connection conn;

    public ChangePasswordWindow(Connection conn) {
        super("Change Password Window");

        // Save the database connection
        this.conn = conn;

        // Create GUI components
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);
        JLabel oldPasswordLabel = new JLabel("Old Password:");
        oldPasswordField = new JPasswordField(20);
        JLabel newPasswordLabel = new JLabel("New Password:");
        newPasswordField = new JPasswordField(20);
        JLabel confirmNewPasswordLabel = new JLabel("Confirm New Password:");
        confirmNewPasswordField = new JPasswordField(20);
        changePasswordButton = new JButton("Change Password");

        // Add components to the window
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridLayout(5, 2));
        contentPane.add(usernameLabel);
        contentPane.add(usernameField);
        contentPane.add(oldPasswordLabel);
        contentPane.add(oldPasswordField);
        contentPane.add(newPasswordLabel);
        contentPane.add(newPasswordField);
        contentPane.add(confirmNewPasswordLabel);
        contentPane.add(confirmNewPasswordField);
        contentPane.add(changePasswordButton);

        // Add action listeners to the buttons
        changePasswordButton.addActionListener(this);

        // Set window properties
        setSize(350, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == changePasswordButton) {
            String username = usernameField.getText();
            String oldPassword = new String(oldPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmNewPassword = new String(confirmNewPasswordField.getPassword());
            try {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE username='" + username + "'");
                if (rs.next()) {
                    if (oldPassword.equals(rs.getString("password"))) {
                        if (newPassword.equals(confirmNewPassword)) {
                            int rows = stmt.executeUpdate("UPDATE users SET password='" + newPassword + "' WHERE username='" + username + "'");
                            if (rows > 0) {
                                JOptionPane.showMessageDialog(this, "Password changed successfully");
                                dispose();
                            } else {
                                JOptionPane.showMessageDialog(this, "Failed to change password");
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "New password and confirm new password do not match");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Incorrect old password");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "User not found");
                }
                rs.close();
                stmt.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

