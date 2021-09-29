package gui;

import data.User;
import data.UsersContainer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UserDialog extends JDialog {
    private final TextField idInput;
    private final TextField nameInput;
    private final PasswordField passwordInput;
    private final PasswordField passwordConfirmInput;
    private final JCheckBox isAdminCheckBox;
    private final boolean firstUser;

    public UserDialog(JFrame parent, boolean firstUser) {
        super(parent, "Benutzer erstellen", true);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.firstUser = firstUser;

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(0, 5, 10, 5));

        idInput = GuiUtils.createNewInput(form, "Benutzerkennung", "", true);
        nameInput = GuiUtils.createNewInput(form, "Name, Vorname", "", true);
        passwordInput = GuiUtils.createNewPasswordInput(form, "Passwort", "", true);
        passwordConfirmInput = GuiUtils.createNewPasswordInput(form, "Passwort bestätigen", "", true);

        JPanel buttons = new JPanel();
        JButton saveButton = new JButton("Speichern");
        JButton cancelButton = new JButton("Abbrechen");

        cancelButton.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> saveUser());

        if (!firstUser) {
            isAdminCheckBox = new JCheckBox("Adminrechte", false);
            buttons.add(isAdminCheckBox);
        } else {
            isAdminCheckBox = new JCheckBox("Adminrechte", true);
        }

        buttons.add(saveButton);
        buttons.add(cancelButton);

        add(form);
        add(buttons, BorderLayout.SOUTH);
        pack();
        setResizable(false);
        setVisible(true);
    }

    private void saveUser() {
        String password = new String(passwordInput.getPassword());
        String passwordConfirm = new String(passwordConfirmInput.getPassword());

        if (idInput.getText().isEmpty() || nameInput.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID und Name dürfen nicht leer sein");
            return;
        }
        if (!password.equals(passwordConfirm)) {
            JOptionPane.showMessageDialog(this, "Passwörter stimmen nicht überein");
            return;
        }
        User user;
        try {
            user = new User(idInput.getText(), nameInput.getText(), password, isAdminCheckBox.isSelected());
            UsersContainer.instance().linkUser(user);
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return;
        }

        dispose();

        if (firstUser) {
            new MainWindow(user);
        }
    }
}