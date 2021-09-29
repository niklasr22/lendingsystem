package gui;

import data.*;
import exceptions.IllegalInputException;
import exceptions.LoadSaveException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

public class ArticleDialog extends JDialog implements ItemListener {
    private Category placeholderCategory, selectedCategory = null;
    private JPanel formWrapper;
    private JComboBox<Category> categorySelector;
    private JButton saveButton;
    private ArticleForm articleForm = null;
    private JSpinner numberSelector;
    private final User activeUser;

    public ArticleDialog(JFrame parent, User activeUser) {
        super(parent, "Artikel hinzufügen", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.activeUser = activeUser;

        ArrayList<Category> categories = new ArrayList<>();
        try {
            placeholderCategory = new Category("Kategorie auswählen");
            categories.add(placeholderCategory);
        } catch (IllegalInputException e) {
            dispose();
            return;
        }
        CategoriesContainer categoriesContainer;
        try {
            categoriesContainer = CategoriesContainer.instance();
            categories.addAll(categoriesContainer.getCategories());
        } catch (LoadSaveException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            dispose();
            return;
        }

        formWrapper = new JPanel(new BorderLayout());

        categorySelector = new JComboBox<>(categories.toArray(new Category[] {}));
        categorySelector.addItemListener(this);

        JScrollPane formScrollPane = new JScrollPane(formWrapper, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel buttonsAndNumberSelector = new JPanel();

        JLabel numberSelectorLabel = new JLabel("Kopien:");
        buttonsAndNumberSelector.add(numberSelectorLabel, BorderLayout.CENTER);

        numberSelector = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        buttonsAndNumberSelector.add(numberSelector, BorderLayout.CENTER);

        saveButton = new JButton("Speichern");
        saveButton.addActionListener(e -> this.saveArticle());
        saveButton.setEnabled(false);
        buttonsAndNumberSelector.add(saveButton, BorderLayout.CENTER);

        JButton cancelButton = new JButton("Abbrechen");
        cancelButton.addActionListener(e -> dispose());
        buttonsAndNumberSelector.add(cancelButton, BorderLayout.CENTER);

        add(categorySelector, BorderLayout.NORTH);
        add(formScrollPane);
        add(buttonsAndNumberSelector, BorderLayout.SOUTH);

        setSize(500, 500);
        setResizable(false);
        setVisible(true);
    }

    private void saveArticle() {
        if (selectedCategory == null || articleForm == null)
            return;

        try {
            numberSelector.commitEdit();
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Ungültige Kopien Anzahl");
            return;
        }

        try {
            for (int i = 1; i <= (Integer) numberSelector.getValue(); i++) {
                Item item = new Item(selectedCategory, articleForm.getItemDescription(), LocalDateTime.now(), activeUser.getUsername());
                for (Map.Entry<Property, TextField> entry : articleForm.getProperties().entrySet()) {
                    if (entry.getKey().isRequired() && entry.getValue().getText().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Der Eigenschaft " + entry.getKey() + " wurde kein Wert zugewiesen.");
                        return;
                    }
                    item.addProperty(entry.getKey().getDescription(), entry.getValue().getText());
                }

                ItemsContainer.instance().linkItem(item);
            }
        } catch (LoadSaveException | IllegalInputException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return;
        }
        dispose();
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
            formWrapper.removeAll();
            selectedCategory = (Category) categorySelector.getSelectedItem();
            if (selectedCategory != null && selectedCategory != placeholderCategory) {
                articleForm = new ArticleForm(selectedCategory);
                formWrapper.add(articleForm, BorderLayout.NORTH);
                saveButton.setEnabled(true);
            } else {
                saveButton.setEnabled(false);
            }
            formWrapper.updateUI();
        }
    }
}
