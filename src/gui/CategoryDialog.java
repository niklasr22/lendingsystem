package gui;

import data.CategoriesContainer;
import data.Category;
import data.Property;
import exceptions.IllegalInputException;
import exceptions.LoadSaveException;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class CategoryDialog extends JDialog {

    private final TextField textFieldName;
    private final TextField propertyName;
    private final JList<Property> propertyList;
    private final Vector<Property> properties;
    private final JCheckBox requiredBox;

    public CategoryDialog(JFrame parent) {
        super(parent, "Neue Kategorie erstellen", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        properties = new Vector<>();

        JPanel nameWrapper = new JPanel(new BorderLayout());
        textFieldName = GuiUtils.createNewInput(nameWrapper, "Kategoriename", "", true);
        add(nameWrapper, BorderLayout.NORTH);

        JPanel categoryWrapper = new JPanel();
        categoryWrapper.setLayout(new BorderLayout());

        JList<String> articleDescriptionPropertyList = new JList<>(new String[] {"Artikel Bezeichnung*"});
        articleDescriptionPropertyList.setEnabled(false);
        articleDescriptionPropertyList.setFont(GuiUtils.FONT_M);

        GridBagLayout gbl = new GridBagLayout();
        JPanel propertiesWrapper = new JPanel(gbl);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0f;
        gbc.weighty = 0.0f;
        propertiesWrapper.add(articleDescriptionPropertyList, gbc);

        gbc.gridy = 2;
        gbc.weighty = 1.0f;
        propertyList = new JList<>(properties);
        propertyList.setFont(GuiUtils.FONT_M);
        propertiesWrapper.add(propertyList, gbc);

        JScrollPane propertiesScrollPane = new JScrollPane(propertiesWrapper);
        categoryWrapper.add(propertiesScrollPane);

        JPanel addPropertyWrapper = new JPanel();
        addPropertyWrapper.setLayout(new GridLayout(2, 2));
        propertyName = GuiUtils.createNewInput(addPropertyWrapper, "Neue Eigenschaft", "", true);

        requiredBox = new JCheckBox("Pflichtfeld");
        addPropertyWrapper.add(requiredBox);

        JButton addPropertyButton = new JButton("Eigenschaft hinzufügen");
        addPropertyButton.addActionListener(evt -> {
            try {
                addProperty();
            } catch (IllegalInputException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        });
        addPropertyWrapper.add(addPropertyButton);

        JButton removePropertyButton = new JButton("Ausgewählte Eigenschaften entfernen");
        removePropertyButton.addActionListener(e -> removeProperties());
        addPropertyWrapper.add(removePropertyButton);
        categoryWrapper.add(addPropertyWrapper, BorderLayout.SOUTH);
        add(categoryWrapper, BorderLayout.CENTER);

        JPanel buttons = new JPanel();

        Button saveButton = new Button("Speichern");
        saveButton.addActionListener(e -> this.saveCategory());
        buttons.add(saveButton);

        Button cancelButton = new Button("Abbrechen");
        cancelButton.addActionListener(e -> dispose());
        buttons.add(cancelButton);

        add(buttons, BorderLayout.SOUTH);

        add(categoryWrapper);

        setSize(1000, 800);
        setVisible(true);
    }

    private void saveCategory() {
        try {
            CategoriesContainer categories = CategoriesContainer.instance();
            Category newCategory = new Category(textFieldName.getText());
            for (Property p : properties)
                newCategory.addProperty(p);
            categories.linkCategory(newCategory);
            dispose();
        } catch (LoadSaveException | IllegalInputException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void addProperty() throws IllegalInputException {
        boolean required = requiredBox.isSelected();
        properties.add(new Property(propertyName.getText(), required));
        propertyList.updateUI();
        propertyName.setText("");
        requiredBox.setSelected(false);
    }

    private void removeProperties() {
        List<Property> selectedProperties = propertyList.getSelectedValuesList();
        properties.removeAll(selectedProperties);
        propertyList.updateUI();
    }
}
