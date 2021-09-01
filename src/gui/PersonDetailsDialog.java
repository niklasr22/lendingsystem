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
import java.util.Vector;

public class PersonDetailsDialog extends JDialog {
    private final TextField nameTextField, addressTextField, phoneNumberTextField, emailTextField;
    private final Person person;
    private final User user;
    private final Vector<Lend> lendVector;
    private final JList<Lend> lendList;

    public PersonDetailsDialog(JFrame parent, Person person, User user) {
        super(parent, "Person bearbeiten");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.user = user;
        this.person = person;

        JPanel personAndLendsWrapper = new JPanel(new GridLayout(0, 2));

        JPanel personDataWrapper = new JPanel(new GridLayout(0, 1));
        GuiUtils.createLabel(personDataWrapper, "Name:", true);
        nameTextField = GuiUtils.createNewInput(personDataWrapper, "Name", person.getName(), 30, true);

        GuiUtils.createLabel(personDataWrapper, "Adresse:", true);
        addressTextField = GuiUtils.createNewInput(personDataWrapper, "Adresse", person.getAddress(), 30, true);

        GuiUtils.createLabel(personDataWrapper, "Telefonnummer:", true);
        phoneNumberTextField = GuiUtils.createNewInput(personDataWrapper, "Telefonnummer", person.getPhoneNumber(), 30, true);

        GuiUtils.createLabel(personDataWrapper, "Email Adresse:", true);
        emailTextField = GuiUtils.createNewInput(personDataWrapper, "Email Adresse", person.getEmail(), 30, true);

        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        GuiUtils.createLabel(personDataWrapper, "Letzte Bearbeitung von " + person.getLastModifiedByUser() + " am " + dtf2.format(person.getLastModifiedDate()) , true);

        personAndLendsWrapper.add(personDataWrapper);

        JPanel lendsWrapper = new JPanel(new BorderLayout());
        JLabel lendsLabel = new JLabel("aktuelle Leihen:");
        lendsLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        lendsWrapper.add(lendsLabel, BorderLayout.NORTH);
        lendVector = new Vector<>();
        lendList = new JList<>(lendVector);

        lendList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = lendList.locationToIndex(e.getPoint());
                    dispose();
                    new LendDetailsDialog(parent, lendVector.get(index), user);
                }
            }
        });
        JScrollPane lendScrollPane = new JScrollPane(lendList);
        getPersonLends();
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
        String[] name = nameTextField.getText().split(" ");
        this.person.setLastModifiedDate(LocalDateTime.now());
        this.person.setLastModifiedByUser(this.user.getUsername());
        try {
            this.person.setFirstName(name[0]);
            this.person.setLastName(name[1]);
            this.person.setAddress(addressTextField.getText());
            this.person.setEmail(emailTextField.getText());
            this.person.setPhoneNumber(phoneNumberTextField.getText());
            PersonsContainer.instance().modifyPerson(this.person);
        } catch (IllegalInputException | LoadSaveException e) {
            System.err.println(e.getMessage());
        }
        dispose();
    }

    private void getPersonLends() {
        try {
            for (Lend lend : LendsContainer.instance().getLends()) {
                if (lend.getPerson() == this.person) {
                    lendVector.add(lend);
                }
            }
        } catch (LoadSaveException e) {
            e.printStackTrace();
        }
    }
}
