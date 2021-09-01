package gui;

import data.User;
import data.UsersContainer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UserDetailsDialog extends JDialog {
    private final TextField nameInput;
    private final PasswordField passwordInput;
    private final PasswordField passwordConfirmInput;
    private final JCheckBox isAdminCheckBox;
    private final User user;

    public UserDetailsDialog(JFrame parent, User user, User activeUser) {
        super(parent, "Benutzer bearbeiten", true);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.user = user;

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(0, 5, 10, 5));

        GuiUtils.createLabel(form, "Benutzerkennung:", true);
        TextField usernameInput = GuiUtils.createNewInput(form, "Benutzerkennung", user.getUsername(), 30, true);
        usernameInput.setEnabled(false);
        GuiUtils.createLabel(form, "Name:", true);
        nameInput = GuiUtils.createNewInput(form, "Name", user.getName(), 30, true);
        passwordInput = GuiUtils.createNewPasswordInput(form, "Neues Passwort", "", 30, true);
        passwordConfirmInput = GuiUtils.createNewPasswordInput(form, "Neues Passwort bestätigen", "", 30, true);

        JPanel buttons = new JPanel();
        JButton saveButton = new JButton("Speichern");
        JButton cancelButton = new JButton("Abbrechen");

        cancelButton.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> saveUser());

        isAdminCheckBox = new JCheckBox("Adminrechte", user.isAdmin());
        if (user == activeUser) {
            isAdminCheckBox.setEnabled(false);
            isAdminCheckBox.setToolTipText("Der angemeldete Nutzer kann sich nicht selbst die Adminrechte entziehen.");
        }
        buttons.add(isAdminCheckBox);

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

        if (nameInput.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Der Name darf nicht leer sein");
            return;
        }
        if (!password.equals(passwordConfirm)) {
            JOptionPane.showMessageDialog(this, "Passwörter stimmen nicht überein");
            return;
        }

        try {
            user.setName(nameInput.getText());
            user.setAdmin(isAdminCheckBox.isSelected());
            if (!password.isEmpty())
                user.createPasswordHash(password);
            UsersContainer.instance().modifyUser(user);
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return;
        }

        dispose();
    }
}