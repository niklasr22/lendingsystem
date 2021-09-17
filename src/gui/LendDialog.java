package gui;

import data.*;
import exceptions.LoadSaveException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LendDialog extends JDialog {

    private final TextField
            textFieldFirstName,
            textFieldLastName,
            textFieldAddress,
            textFieldPhone,
            textFieldMail,
            textFieldDeposit,
            textFieldLendDate,
            textFieldPlannedReturnDate,
            textFieldComment,
            textFieldSearch;
    private final JLabel availabiltyIndicator;
    private final JTabbedPane userTabbedPane;
    private final JList<Person> personsList;
    private final Item item;
    private final User user;

    public LendDialog(JFrame parent, Item item, User user) {
        super(parent, "Neue Leihe", true);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        this.item = item;
        this.user = user;

        FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT);

        JPanel panelNewPerson = new JPanel(new GridLayout(0, 1));
        JPanel panelSearchPerson = new JPanel(new GridBagLayout());
        panelSearchPerson.setBorder(new EmptyBorder(5, 10, 5, 10));

        userTabbedPane = new JTabbedPane();
        userTabbedPane.addTab("Neue Person", panelNewPerson);
        userTabbedPane.addTab("Person suchen", panelSearchPerson);
        add(userTabbedPane);

        JPanel panelNames = new JPanel(flowLayout);
        panelNewPerson.add(panelNames);

        textFieldFirstName = GuiUtils.createNewInput(panelNames, "Vorname", "", 25, false);
        textFieldLastName = GuiUtils.createNewInput(panelNames, "Nachname", "", 25, false);
        textFieldAddress = GuiUtils.createNewInput(panelNewPerson, "Adresse", "", 50, true);
        textFieldPhone = GuiUtils.createNewInput(panelNewPerson, "Telefonnummer", "", 50, true);
        textFieldMail = GuiUtils.createNewInput(panelNewPerson, "E-Mail", "", 50, true);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;

        personsList = new JList<>();
        textFieldSearch = GuiUtils.createNewInput(null, "Suche (Name/Email)", "", 50, false);
        textFieldSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                updatePersonSearchResults();
            }
        });
        updatePersonSearchResults();
        panelSearchPerson.add(textFieldSearch, constraints);

        JScrollPane personsScrollPane = new JScrollPane(personsList);
        personsScrollPane.setBackground(Color.RED);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0;
        constraints.gridy = 1;
        constraints.gridheight = 1;
        panelSearchPerson.add(personsScrollPane, constraints);

        KeyAdapter datesKeyListener = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent event) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                try {
                    LocalDate lendDate = LocalDate.parse(textFieldLendDate.getText(), dtf);
                    LocalDate expectedReturnDate = LocalDate.parse(textFieldPlannedReturnDate.getText(), dtf);
                    if (lendDate.isBefore(expectedReturnDate) || lendDate.isEqual(expectedReturnDate)) {
                        if (item.isAvailableFor(lendDate, expectedReturnDate)) {
                            availabiltyIndicator.setText("Verfügbar");
                            availabiltyIndicator.setForeground(new Color(27, 142, 22));
                        } else {
                            availabiltyIndicator.setText("Im gewählten Zeitraum nicht verfügbar");
                            availabiltyIndicator.setForeground(Color.RED);
                        }
                    } else {
                        availabiltyIndicator.setText("Das Rückgabedatum muss nach dem Verleihdatum liegen.");
                        availabiltyIndicator.setForeground(Color.RED);
                    }
                } catch (DateTimeParseException ignored) {
                    availabiltyIndicator.setText("Mindestens eines der beiden Daten ist ungültig.");
                    availabiltyIndicator.setForeground(Color.RED);
                }
            }
        };

        JPanel panelDateInputs = new JPanel(flowLayout);
        add(panelDateInputs);
        textFieldLendDate = GuiUtils.createNewInput(panelDateInputs, "Leihtermin (TT.MM.JJJJ)", "", 25, false);
        textFieldLendDate.addKeyListener(datesKeyListener);
        textFieldPlannedReturnDate = GuiUtils.createNewInput(panelDateInputs, "Geplantes Rückgabedatum (TT.MM.JJJJ)", "", 25, false);
        textFieldPlannedReturnDate.addKeyListener(datesKeyListener);
        availabiltyIndicator = GuiUtils.createLabel(this, "", true);
        availabiltyIndicator.setFont(GuiUtils.FONT_M);
        textFieldDeposit = GuiUtils.createNewInput(this, "Pfand", "", 50, true);
        textFieldComment = GuiUtils.createNewInput(this, "Kommentar", "", 50, true);

        JPanel panelButtons = new JPanel(new FlowLayout());
        add(panelButtons);

        Button btnSave = new Button("Speichern");
        btnSave.addActionListener(e -> this.saveLend());
        panelButtons.add(btnSave);

        Button btnCancel = new Button("Abbrechen");
        btnCancel.addActionListener(e -> dispose());
        panelButtons.add(btnCancel);

        pack();
        setResizable(false);
        setVisible(true);
    }

    private void updatePersonSearchResults() {
        try {
            String search = textFieldSearch.getText().toLowerCase();
            personsList.setListData(PersonsContainer.
                    instance()
                    .getPersons()
                    .stream()
                    .filter(p -> p.getName().contains(search) || p.getEmail().contains(search))
                    .toArray(Person[]::new));
            personsList.updateUI();
        } catch (LoadSaveException loadSaveException) {
            loadSaveException.printStackTrace();
        }
    }

    private void saveLend() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        try {
            Person person = null;
            if (userTabbedPane.getSelectedIndex() == 0) {
                person = new Person(
                        textFieldFirstName.getText(),
                        textFieldLastName.getText(),
                        textFieldPhone.getText(),
                        textFieldMail.getText(),
                        textFieldAddress.getText(),
                        LocalDateTime.now(),
                        user.getUsername());
                PersonsContainer.instance().linkPerson(person);
            } else if (userTabbedPane.getSelectedIndex() == 1) {
                person = personsList.getSelectedValue();
            }

            if (person == null) {
                JOptionPane.showMessageDialog(this, "Es muss entweder eine neue Person erstellt oder eine bestehende Person ausgewählt werden.");
                return;
            }

            Lend lend = new Lend(
                    item,
                    person,
                    LocalDate.parse(textFieldLendDate.getText(), dtf),
                    LocalDate.parse(textFieldPlannedReturnDate.getText(), dtf),
                    null,
                    textFieldDeposit.getText(),
                    textFieldComment.getText(),
                    LocalDateTime.now(),
                    user.getUsername());
            LendsContainer.instance().linkLend(lend);
            ItemsContainer.instance().modifyItem(item);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Mindestens ein Datum entspricht nicht dem Format TT.MM.YYYY");
            return;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return;
        }
        dispose();
    }

}
