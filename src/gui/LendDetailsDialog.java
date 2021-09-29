package gui;

import data.ItemsContainer;
import data.Lend;
import data.LendsContainer;
import data.User;
import exceptions.IllegalInputException;
import exceptions.LoadSaveException;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LendDetailsDialog extends JDialog {

    private final TextField
            textFieldDeposit,
            textFieldPlannedReturnDate,
            textFieldComment;
    private final Lend lend;
    private final User user;

    public LendDetailsDialog(JFrame parent, Lend lend, User user) {
        super(parent, "Details zur Leihe *" + lend.getId(), true);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setLayout(new GridLayout(0, 1));

        this.user = user;
        this.lend = lend;
        boolean isLent = !lend.isReturned();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        GuiUtils.createLabel(panel, "Artikel: #" + lend.getItem().getInventoryNumber() + " - " + lend.getItem().getDescription(), true);
        JButton button = new JButton("Artikel anzeigen");
        button.addActionListener(evt -> {
            dispose();
            new ArticleDetailsDialog(parent, lend.getItem(), user);
        });
        panel.add(button);
        add(panel);

        panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        GuiUtils.createLabel(panel, "Verliehen an " + lend.getPerson().getName() + " ab dem " + dtf.format(lend.getLendDate()), true);
        button = new JButton("Nutzer anzeigen");
        button.addActionListener(evt -> {
            dispose();
            new PersonDetailsDialog(parent, lend.getPerson(), user);
        });
        panel.add(button);
        add(panel);

        if (!isLent)
            GuiUtils.createLabel(this, "Zurückgegeben am " + dtf.format(lend.getReturnDate()), true);


        GuiUtils.createLabel(this, "Pfand:", true);
        textFieldDeposit = GuiUtils.createNewInput(this, "Pfand", lend.getDeposit(), true);
        textFieldDeposit.setEnabled(isLent);

        GuiUtils.createLabel(this, "Geplantes Rückgabedatum:", true);
        textFieldPlannedReturnDate = GuiUtils.createNewInput(this, "Geplantes Rückgabedatum (TT.MM.JJJJ)", dtf.format(lend.getExpectedReturnDate()), true);
        textFieldPlannedReturnDate.setEnabled(isLent);

        GuiUtils.createLabel(this, "Kommentar:", true);
        textFieldComment = GuiUtils.createNewInput(this, "Kommentar", lend.getComment(), true);

        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        GuiUtils.createLabel(this, "Letzte Bearbeitung von " + lend.getLastModifiedByUser() + " am " + dtf2.format(lend.getLastModifiedDate()) , true);

        JPanel panelButtons = new JPanel(new FlowLayout());
        add(panelButtons);

        Button btnSave = new Button("Speichern");
        btnSave.addActionListener(e -> this.saveLend());
        panelButtons.add(btnSave);

        Button btnReturnLend = new Button("Rückgabe");
        btnReturnLend.addActionListener(evt -> {
            int status = JOptionPane.showConfirmDialog(this, "Rückgabe bestätigen?");
            if (status == JOptionPane.OK_OPTION) {
                try {
                    lend.setReturnDate(LocalDate.now());
                    LendsContainer.instance().modifyLend(lend);
                    ItemsContainer.instance().modifyItem(lend.getItem());
                    dispose();
                } catch (IllegalInputException | LoadSaveException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, e.getMessage());
                }
            }
        });
        if (!isLent || lend.getLendDate().isAfter(LocalDate.now())) {
            btnReturnLend.setEnabled(false);
            btnReturnLend.setToolTipText("Der Artikel wurde bereits zurückgegeben oder der Verleihtag liegt in der Zukunft.");
        }
        panelButtons.add(btnReturnLend);

        Button btnDelete = new Button("Löschen");
        btnDelete.addActionListener(evt -> {
            int status = JOptionPane.showConfirmDialog(this, "Soll die Leihe aus der Historie gelöscht werden?");
            if (status == JOptionPane.OK_OPTION) {
                try {
                    LendsContainer.instance().unlinkLend(lend);
                } catch (LoadSaveException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                }
            }
            dispose();
        });
        if (isLent && lend.getLendDate().isBefore(LocalDate.now())) {
            btnDelete.setEnabled(false);
            btnDelete.setToolTipText("Eine Leihe kann nicht gelöscht werden, solange die Rückgabe nicht erfolgt ist.");
        } 
        panelButtons.add(btnDelete);

        Button btnCancel = new Button("Abbrechen");
        btnCancel.addActionListener(e -> dispose());
        panelButtons.add(btnCancel);

        pack();
        setResizable(false);
        setVisible(true);
    }

    private void saveLend() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern ("dd.MM.yyyy");
        try {
            lend.setDeposit(textFieldDeposit.getText());
            lend.setComment(textFieldComment.getText());
            lend.setExpectedReturnDate(LocalDate.parse(textFieldPlannedReturnDate.getText(), dtf));
            lend.setLastModifiedByUser(user.getUsername());
            lend.setLastModifiedDate(LocalDateTime.now());
            LendsContainer.instance().modifyLend(lend);
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
