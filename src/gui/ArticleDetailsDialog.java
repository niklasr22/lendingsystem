package gui;

import data.*;
import exceptions.IllegalInputException;
import exceptions.LoadSaveException;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

public class ArticleDetailsDialog extends JDialog {

    private final Item item;
    private final ArticleForm articleForm;
    private final User user;

    public ArticleDetailsDialog(JFrame parent, Item item, User user) {
        super(parent, "Artikel Details (#" + item.getInventoryNumber() + ")", true);
        this.item = item;
        this.user = user;

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JPanel formWrapper = new JPanel();
        articleForm = new ArticleForm(item);
        formWrapper.add(articleForm);

        JScrollPane formScrollPane = new JScrollPane(formWrapper, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel buttonsPanel = new JPanel();

        JButton saveButton = new JButton("Speichern");
        saveButton.addActionListener(e -> this.updateArticle());
        buttonsPanel.add(saveButton);

        JButton lendButton = new JButton("Verleihen");
        lendButton.addActionListener(e -> {
            dispose();
            new LendDialog(parent, item, user);
        });
        if (item.isLent()) {
            lendButton.setEnabled(false);
            lendButton.setToolTipText("Ein aktuell verliehener Artikel kann nicht nochmals verliehen werden.");
        }
        buttonsPanel.add(lendButton);

        JButton deleteButton = new JButton("Löschen");
        deleteButton.addActionListener(e -> this.deleteArticle());
        if (item.isLent()) {
            deleteButton.setEnabled(false);
            deleteButton.setToolTipText("Ein aktuell verliehener Artikel kann nicht gelöscht werden.");
        }
        buttonsPanel.add(deleteButton);

        JButton cancelButton = new JButton("Abbrechen");
        cancelButton.addActionListener(e -> dispose());
        buttonsPanel.add(cancelButton);

        add(formScrollPane);

        JPanel infoPanel = new JPanel(new GridLayout(0, 1));

        if (item.isLent()) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            GuiUtils.createLabel(panel, "Dieser Artikel ist aktuell verliehen.", true);
            JButton openLendButton = new JButton("Leihe anzeigen");
            openLendButton.addActionListener(evt -> {
                dispose();
                new LendDetailsDialog(parent, item.getLend(), user);
            });
            panel.add(openLendButton);
            infoPanel.add(panel);
        } else {
            GuiUtils.createLabel(infoPanel, "Dieser Artikel ist aktuell nicht verliehen.", true);
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        GuiUtils.createLabel(infoPanel, "Zuletzt bearbeitet von " + item.getLastModifiedByUser() + " am " + item.getLastModifiedDate().format(dtf), true);

        add(infoPanel);

        CalendarPanel calendarPanel = new CalendarPanel();
        add(calendarPanel);
        calendarPanel.linkEvents(new ArrayList<>(item.getLends()));
        calendarPanel.showMonth(LocalDate.now());

        add(buttonsPanel);
        pack();
        setMaximumSize(new Dimension(500, 500));
        setResizable(false);
        setVisible(true);
    }

    private void deleteArticle() {
        int status = JOptionPane.showConfirmDialog(this, "Soll dieser Artikel wirklich gelöscht werden?");
        if(status == JOptionPane.OK_OPTION) {
            try {
                ItemsContainer.instance().unlinkItem(item);
            } catch (LoadSaveException e) {
                JOptionPane.showMessageDialog(this, "Artikel konnte nicht gelöscht werden (" + e.getMessage() + ")");
            }
        }
        dispose();
    }

    private void updateArticle() {
        String lastModifiedByUser = this.user.getUsername();
        item.setLastModifiedByUser(lastModifiedByUser);
        item.setLastModifiedDate(LocalDateTime.now());
        try {
            item.setDescription(articleForm.getItemDescription());
            for (Map.Entry<Property, TextField> entry : articleForm.getProperties().entrySet()) {
                if (entry.getKey().isRequired() && entry.getValue().getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Der Eigenschaft " + entry.getKey() + " wurde kein Wert zugewiesen.");
                    return;
                }
                item.addProperty(entry.getKey().getDescription(), entry.getValue().getText());
            }
            ItemsContainer.instance().modifyItem(item);
        } catch (LoadSaveException | IllegalInputException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return;
        }
        dispose();
    }

}
