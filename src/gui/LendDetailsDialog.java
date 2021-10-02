package gui;

import data.Lend;
import data.LendsContainer;
import data.User;
import exceptions.IllegalInputException;
import exceptions.LoadSaveException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class LendDetailsDialog extends JDialog {

    private final TextField
            textFieldDeposit,
            textFieldComment;
    private final Lend lend;
    private final User user;
    private final CalendarPanel calendarPanel;

    public LendDetailsDialog(JFrame parent, Lend lend, User user) {
        super(parent, "Details zur Leihe *" + lend.getId(), true);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

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
        GuiUtils.createLabel(panel, "Verliehen an " + lend.getPerson().getName(), true);
        button = new JButton("Nutzer anzeigen");
        button.addActionListener(evt -> {
            dispose();
            new PersonDetailsDialog(parent, lend.getPerson(), user);
        });
        panel.add(button);
        add(panel);

        calendarPanel = new CalendarPanel(this, parent, user, lend.getItem(), lend.getStatus() == Lend.RETURNED);
        calendarPanel.setBorder(new EmptyBorder(10, 5, 10, 5));
        add(calendarPanel);
        calendarPanel.linkEvents(new ArrayList<>(lend.getItem().getLends()));
        calendarPanel.showMonth(LocalDate.now());
        calendarPanel.setLend(lend);

        if (!isLent) {
            panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            GuiUtils.createLabel(panel, "Zurückgegeben am " + dtf.format(lend.getReturnDate()), true);
            add(panel);
        }

        panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        GuiUtils.createLabel(panel, "Pfand:", true);
        add(panel);
        textFieldDeposit = GuiUtils.createNewInput(this, "Pfand", lend.getDeposit(), true);
        textFieldDeposit.setEnabled(isLent);

        panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        GuiUtils.createLabel(panel, "Kommentar:", true);
        add(panel);
        textFieldComment = GuiUtils.createNewInput(this, "Kommentar", lend.getComment(), true);

        JPanel infoPanel = new JPanel(new GridLayout(0, 1));
        add(infoPanel);

        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        GuiUtils.createLabel(infoPanel, "Letzte Bearbeitung von " + lend.getLastModifiedByUser() + " am " + dtf2.format(lend.getLastModifiedDate()) , true);

        JPanel panelButtons = new JPanel(new FlowLayout());
        add(panelButtons);

        Button btnSave = new Button("Speichern");
        btnSave.addActionListener(e -> this.saveLend());
        panelButtons.add(btnSave);

        if (getLend().getStatus() == Lend.PICKED_UP || getLend().getStatus() == Lend.PICKED_UP_EXPIRED) {
            Button btnReturnLend = new Button("Rückgabe");
            btnReturnLend.addActionListener(evt -> {
                int status = JOptionPane.showConfirmDialog(this, "Rückgabe bestätigen?");
                if (status == JOptionPane.OK_OPTION) {
                    try {
                        getLend().returnItem();
                        LendsContainer.instance().modifyLend(getLend());
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
        } else if (getLend().getStatus() == Lend.RESERVED) {
            JButton btnPickUpLend = new JButton("Abholung");
            btnPickUpLend.addActionListener(evt -> {
                int status = JOptionPane.showConfirmDialog(this, "Abholung bestätigen?");
                if (status == JOptionPane.OK_OPTION) {
                    try {
                        getLend().pickUpItem();
                        LendsContainer.instance().modifyLend(getLend());
                        dispose();
                    } catch (LoadSaveException e) {
                        JOptionPane.showMessageDialog(this, e.getMessage());
                    }
                }
            });
            btnPickUpLend.setEnabled(getLend().getItem().isAvailable() && !getLend().getLendDate().isAfter(LocalDate.now()));
            panelButtons.add(btnPickUpLend);
        }

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
        if (calendarPanel.getStartDate() == null) {
            JOptionPane.showMessageDialog(this, "Bitte gültiges Datum eintagen");
            return;
        }
        if (calendarPanel.getEndDate() == null) {
            try {
                lend.setDeposit(textFieldDeposit.getText());
                lend.setComment(textFieldComment.getText());
                lend.setLendDate(calendarPanel.getStartDate());
                lend.setExpectedReturnDate(calendarPanel.getStartDate());
                lend.setLastModifiedByUser(user.getUsername());
                lend.setLastModifiedDate(LocalDateTime.now());
                LendsContainer.instance().modifyLend(lend);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
                return;
            }
        } else {
            try {
                lend.setDeposit(textFieldDeposit.getText());
                lend.setComment(textFieldComment.getText());
                lend.setLendDate(calendarPanel.getStartDate());
                lend.setExpectedReturnDate(calendarPanel.getEndDate());
                lend.setLastModifiedByUser(user.getUsername());
                lend.setLastModifiedDate(LocalDateTime.now());
                LendsContainer.instance().modifyLend(lend);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
                return;
            }
        }
        dispose();
    }

    public Lend getLend() {
        return lend;
    }
}
