package gui;

import data.User;
import data.UsersContainer;
import exceptions.LoadSaveException;
import store.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoginWindow extends JFrame {

    private final JLabel labelError;
    private final TextField textFieldUsername;
    private final PasswordField passwordField;
    private UsersContainer container;

    public LoginWindow() {
        super("Login - Verleihsystem");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(0, 1));

        try {
            this.container = UsersContainer.instance();
        } catch (LoadSaveException ignored) { }

        JLabel labelTitle = new JLabel("Login - Verleihsystem");
        labelTitle.setFont(GuiUtils.FONT_XL);
        labelTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(labelTitle);

        labelError = new JLabel("");
        labelError.setFont(GuiUtils.FONT_L);
        labelError.setForeground(Color.RED);
        labelError.setHorizontalAlignment(SwingConstants.CENTER);
        add(labelError);

        textFieldUsername = GuiUtils.createNewInput(this, "Benutzername", "", 50, true);
        passwordField = GuiUtils.createNewPasswordInput(this, "Passwort", "", 50, true);
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    login();
            }
        });

        JPanel panelButtons = new JPanel(new FlowLayout());
        add(panelButtons);

        Button loginButton = new Button("Anmelden");
        loginButton.addActionListener(e -> login());
        panelButtons.add(loginButton);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    DBConnection.instance().close();
                } catch (LoadSaveException ignored) {}
            }
        });

        pack();
        setResizable(false);
        setVisible(true);
    }

    private void login() {
        String userName = textFieldUsername.getText();
        String password = new String(passwordField.getPassword());
        User user = container.getUser(userName);
        if (user == null || !user.authenticateUser(userName, password)) {
            labelError.setText("Name oder Kennwort falsch");
        } else {
            dispose();
            new MainWindow(user);
        }
    }
}
