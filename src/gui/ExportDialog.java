package gui;

import data.*;
import exceptions.IllegalInputException;
import exceptions.LoadSaveException;
import store.PersistenceItemsCsvExport;
import store.PersistenceLendsCsvExport;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ExportDialog extends JDialog {

    private JCheckBox expiredCheckBox, lentCheckBox;
    private JComboBox<Category> categorySelector;

    public ExportDialog(Frame parent) {
        super(parent, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Exportienen");
        JLabel header = new JLabel("Liste der Gegenstände exportieren");
        header.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(header, BorderLayout.NORTH);

        try {
            ArrayList<Category> categories = new ArrayList<>(CategoriesContainer.instance().getCategories());
            categories.add(0, new Category("Alle"));
            categorySelector = new JComboBox<>(categories.toArray(new Category[] {}));
        } catch (LoadSaveException e) {
            JOptionPane.showMessageDialog(parent, e.getMessage());
            return;
        } catch (IllegalInputException e) {
            e.printStackTrace();
            return;
        }

        lentCheckBox = new JCheckBox("Nur aktuell verliehene Artikel exportieren");
        lentCheckBox.setBorder(new EmptyBorder(20, 5, 0, 5));

        expiredCheckBox = new JCheckBox("Nur Artikel mit abgelaufener Rückgabefrist exportieren");
        expiredCheckBox.setBorder(new EmptyBorder(20, 5, 0, 5));
        expiredCheckBox.addActionListener(evt -> {
            if (expiredCheckBox.isSelected()) {
                lentCheckBox.setSelected(true);
                lentCheckBox.setEnabled(false);
            } else {
                lentCheckBox.setEnabled(true);
            }
        });

        JPanel selectionWrapper = new JPanel(new BorderLayout());
        selectionWrapper.setBorder(new EmptyBorder(10, 20, 30, 20));
        selectionWrapper.add(categorySelector, BorderLayout.NORTH);
        selectionWrapper.add(lentCheckBox, BorderLayout.CENTER);
        selectionWrapper.add(expiredCheckBox, BorderLayout.SOUTH);
        add(selectionWrapper, BorderLayout.CENTER);

        JButton cancelButton = new JButton("Abbrechen");
        cancelButton.addActionListener(e -> dispose());

        JButton exportButton = new JButton("Exportieren");
        exportButton.addActionListener(this::export);

        JPanel buttons = new JPanel();
        buttons.add(exportButton);
        buttons.add(cancelButton);
        add(buttons, BorderLayout.SOUTH);

        pack();
        setResizable(false);
        setVisible(true);
    }

    private void export(ActionEvent evt) {
        Category category = (Category) categorySelector.getSelectedItem();
        if (category == null) {
            JOptionPane.showMessageDialog(this, "Es wurde keine gültige Kategorie gewählt");
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV-Datei (.csv)", "csv"));
        int status = fileChooser.showSaveDialog(this);
        if (status == JFileChooser.APPROVE_OPTION) {
            File csvFile = fileChooser.getSelectedFile();
            if (!expiredCheckBox.isSelected() && !lentCheckBox.isSelected()) {
                PersistenceItemsCsvExport persistenceCsvExport = new PersistenceItemsCsvExport(csvFile);
                try {
                    List<Item> items;
                    if (category.getId() == -1)
                        items = ItemsContainer.instance().getItems();
                    else
                        items = ItemsContainer.instance().getItems().stream().filter(i -> i.getCategory() == category).collect(Collectors.toList());
                    persistenceCsvExport.save(items);
                } catch (LoadSaveException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                    return;
                }
            } else {
                PersistenceLendsCsvExport persistenceLendsCsvExport = new PersistenceLendsCsvExport(csvFile);
                try {
                    Predicate<Lend> baseFilter = l -> (category.getId() == -1 || l.getItem().getCategory() == category) && l.getReturnDate() == null;
                    Predicate<Lend> filter = baseFilter;
                    if (lentCheckBox.isSelected() && expiredCheckBox.isSelected()) {
                        filter = l -> baseFilter.test(l) && l.getExpectedReturnDate().isBefore(LocalDate.now());
                    }
                    List<Lend> lends = LendsContainer.instance().getLends().stream().filter(filter).collect(Collectors.toList());
                    persistenceLendsCsvExport.save(lends);
                } catch (LoadSaveException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                    return;
                }
            }
            dispose();
        }
    }
}
