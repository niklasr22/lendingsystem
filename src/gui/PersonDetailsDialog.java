package gui;

import data.*;
import exceptions.IllegalInputException;
import exceptions.LoadSaveException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PersonDetailsDialog extends JDialog {
    private final TextField firstnameTextField, lastnameTextField, addressTextField, phoneNumberTextField, emailTextField;
    private final Person person;
    private final User user;
    private final JList<Lend> lendJList;

    public PersonDetailsDialog(JFrame parent, Person person, User user) {
        super(parent, "Person bearbeiten");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.user = user;
        this.person = person;

        JPanel personAndLendsWrapper = new JPanel(new GridLayout(0, 2));

        JPanel personDataWrapper = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        personDataWrapper.add(GuiUtils.createLabel(personDataWrapper, "Vorname:", GuiUtils.FONT_M, true), gbc);

        gbc.gridx = 1;
        personDataWrapper.add(GuiUtils.createLabel(personDataWrapper, "Nachname:", GuiUtils.FONT_M, true), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        firstnameTextField = GuiUtils.createNewInput(null, "Vorname", person.getFirstName(), true);
        personDataWrapper.add(firstnameTextField, gbc);

        gbc.gridx = 1;
        lastnameTextField = GuiUtils.createNewInput(null, "Nachname", person.getLastName(), true);
        personDataWrapper.add(lastnameTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        personDataWrapper.add(GuiUtils.createLabel(null, "Adresse:", GuiUtils.FONT_M, true), gbc);

        gbc.gridy = 3;
        addressTextField = GuiUtils.createNewInput(null, "Adresse", person.getAddress(), true);
        personDataWrapper.add(addressTextField, gbc);

        gbc.gridy = 4;
        personDataWrapper.add(GuiUtils.createLabel(null, "Telefonnummer:", GuiUtils.FONT_M, true), gbc);

        gbc.gridy = 5;
        phoneNumberTextField = GuiUtils.createNewInput(null, "Telefonnummer", person.getPhoneNumber(), true);
        personDataWrapper.add(phoneNumberTextField, gbc);

        gbc.gridy = 6;
        personDataWrapper.add(GuiUtils.createLabel(null, "Email Adresse:", GuiUtils.FONT_M, true), gbc);

        gbc.gridy = 7;
        emailTextField = GuiUtils.createNewInput(null, "Email Adresse", person.getEmail(), true);
        personDataWrapper.add(emailTextField, gbc);

        gbc.gridy = 8;
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        personDataWrapper.add(GuiUtils.createLabel(null, "Letzte Bearbeitung von " + person.getLastModifiedByUser() + " am " + dtf2.format(person.getLastModifiedDate()) , true), gbc);

        personAndLendsWrapper.add(personDataWrapper);


        JPanel lendsWrapper = new JPanel(new BorderLayout());

        JPanel lendsTopBar = new JPanel(new GridLayout(1, 2));
        lendsWrapper.add(lendsTopBar, BorderLayout.NORTH);

        GuiUtils.createLabel(lendsTopBar, "Leihen:", false);

        JComboBox<String> lendsFilter = new JComboBox<>(new String[] {"Alle", "Aktiv", "Abgeschlossen", "Reserviert"});
        lendsFilter.setBorder(new EmptyBorder(5, 5, 5, 5));
        lendsFilter.setAlignmentX(SwingConstants.RIGHT);
        lendsFilter.addItemListener(e -> {
            Predicate<Lend> filter = null;
            switch (lendsFilter.getSelectedIndex()) {
                case 1:
                    filter = lend -> lend.getStatus() == Lend.PICKED_UP || lend.getStatus() == Lend.PICKED_UP_EXPIRED;
                    break;
                case 2:
                    filter = lend -> lend.getStatus() == Lend.RETURNED;
                    break;
                case 3:
                    filter = lend -> lend.getStatus() == Lend.RESERVED;
                    break;
            }
            loadPersonLends(filter);
        });
        lendsTopBar.add(lendsFilter);

        lendJList = new JList<>();
        lendJList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    dispose();
                    new LendDetailsDialog(parent, lendJList.getSelectedValue(), user);
                }
            }
        });

        JScrollPane lendScrollPane = new JScrollPane(lendJList);
        loadPersonLends(null);
        lendsWrapper.add(lendScrollPane);
        personAndLendsWrapper.add(lendsWrapper);

        add(personAndLendsWrapper);

        Button saveButton = new Button("Speichern");
        saveButton.addActionListener(e -> savePerson());
        Button cancelButton = new Button("Abbrechen");
        cancelButton.addActionListener(e -> dispose());
        JPanel buttonsWrapper = new JPanel();
        buttonsWrapper.add(saveButton);
        buttonsWrapper.add(cancelButton);
        add(buttonsWrapper, BorderLayout.SOUTH);

        pack();
        setResizable(false);
        setVisible(true);
    }

    private void savePerson() {
        this.person.setLastModifiedDate(LocalDateTime.now());
        this.person.setLastModifiedByUser(this.user.getUsername());
        try {
            this.person.setFirstName(firstnameTextField.getText());
            this.person.setLastName(lastnameTextField.getText());
            this.person.setAddress(addressTextField.getText());
            this.person.setEmail(emailTextField.getText());
            this.person.setPhoneNumber(phoneNumberTextField.getText());
            PersonsContainer.instance().modifyPerson(this.person);
        } catch (IllegalInputException | LoadSaveException e) {
            System.err.println(e.getMessage());
        }
        dispose();
    }

    private void loadPersonLends(Predicate<Lend> filter) {
        try {
            Stream<Lend> s = LendsContainer.instance().getLends().stream().filter(l -> l.getPerson() == this.person);
            if (filter != null)
                s = s.filter(filter);
            lendJList.setListData(s.toArray(Lend[]::new));
            lendJList.updateUI();
        } catch (LoadSaveException e) {
            e.printStackTrace();
        }
    }
}
