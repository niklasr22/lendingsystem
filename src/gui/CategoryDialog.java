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

        JPanel nameWrapper = new JPanel();
        textFieldName = GuiUtils.createNewInput(nameWrapper, "Kategoriename", "", 30, true);
        add(nameWrapper, BorderLayout.NORTH);

        JPanel categoryWrapper = new JPanel();
        categoryWrapper.setLayout(new BorderLayout());

        propertyList = new JList<>(properties);
        JScrollPane propertiesScrollPane = new JScrollPane(propertyList);
        categoryWrapper.add(propertiesScrollPane);

        JPanel addPropertyWrapper = new JPanel();
        addPropertyWrapper.setLayout(new GridLayout(2, 2));
        propertyName = GuiUtils.createNewInput(addPropertyWrapper, "neue Eigenschaft", "", 30, true);

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
        Button cancelButton = new Button("Abbrechen");
        cancelButton.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> this.saveCategory());
        buttons.add(saveButton);
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
