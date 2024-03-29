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
import java.util.ArrayList;

public class LendDialog extends JDialog {

    private final TextField
            textFieldFirstName,
            textFieldLastName,
            textFieldAddress,
            textFieldPhone,
            textFieldMail,
            textFieldDeposit,
            textFieldComment,
            textFieldSearch;
    private final JTabbedPane userTabbedPane;
    private final JList<Person> personsList;
    private final Item item;
    private final User user;
    private final CalendarPanel calendarPanel;

    public LendDialog(JFrame parent, Item item, User user) {
        super(parent, "Neue Leihe", true);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        this.item = item;
        this.user = user;

        JPanel panelNewPerson = new JPanel(new GridLayout(0, 1));
        JPanel panelSearchPerson = new JPanel(new GridBagLayout());
        panelSearchPerson.setBorder(new EmptyBorder(5, 10, 5, 10));

        userTabbedPane = new JTabbedPane();
        userTabbedPane.addTab("Neue Person", panelNewPerson);
        userTabbedPane.addTab("Person suchen", panelSearchPerson);
        add(userTabbedPane);

        JPanel panelNames = new JPanel(new GridLayout());
        panelNewPerson.add(panelNames);

        textFieldFirstName = GuiUtils.createNewInput(panelNames, "Vorname", "", true);
        textFieldLastName = GuiUtils.createNewInput(panelNames, "Nachname", "", true);
        textFieldAddress = GuiUtils.createNewInput(panelNewPerson, "Adresse", "", true);
        textFieldPhone = GuiUtils.createNewInput(panelNewPerson, "Telefonnummer", "", true);
        textFieldMail = GuiUtils.createNewInput(panelNewPerson, "E-Mail", "", true);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;

        personsList = new JList<>();
        textFieldSearch = GuiUtils.createNewInput(null, "Suche (Name/Email)", "", false);
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

        calendarPanel = new CalendarPanel(this, parent, user, item, CalendarPanel.MODIFY);
        calendarPanel.setBorder(new EmptyBorder(10, 5, 10, 5));
        add(calendarPanel);
        calendarPanel.linkEvents(new ArrayList<>(item.getLends()));
        calendarPanel.showMonth(LocalDate.now());
        textFieldDeposit = GuiUtils.createNewInput(this, "Pfand", "", true);
        textFieldComment = GuiUtils.createNewInput(this, "Kommentar", "", true);

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
        if (calendarPanel.getStartDate() == null) {
            JOptionPane.showMessageDialog(this, "Bitte gültiges Datum auswählen");
            return;
        }
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

            Lend lend;
            if (calendarPanel.getEndDate() == null) {
                lend = new Lend(
                        item,
                        person,
                        calendarPanel.getStartDate(),
                        calendarPanel.getStartDate(),
                        null,
                        textFieldDeposit.getText(),
                        textFieldComment.getText(),
                        LocalDateTime.now(),
                        user.getUsername());
            } else {
                lend = new Lend(
                        item,
                        person,
                        calendarPanel.getStartDate(),
                        calendarPanel.getEndDate(),
                        null,
                        textFieldDeposit.getText(),
                        textFieldComment.getText(),
                        LocalDateTime.now(),
                        user.getUsername());
            }
            LendsContainer.instance().linkLend(lend);
            ItemsContainer.instance().modifyItem(item);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return;
        }
        dispose();
    }
}
