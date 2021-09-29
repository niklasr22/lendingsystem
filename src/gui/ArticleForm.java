package gui;

import data.Category;
import data.Item;
import data.Property;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class ArticleForm extends JPanel {

    private TextField inputDescription;
    private final HashMap<Property, TextField> properties;
    private final GridBagConstraints gridBagConstraints;

    private ArticleForm() {
        super();
        setLayout(new GridBagLayout());
        properties = new HashMap<>();
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    }

    public ArticleForm(Category category) {
        this();

        inputDescription = GuiUtils.createNewInput(this, "Artikel Bezeichnung*", "", true);
        add(inputDescription, gridBagConstraints);
        gridBagConstraints.gridy++;

        for (Property property : category.getProperties()) {
            TextField field = GuiUtils.createNewInput(this, property.getDescription() + (property.isRequired() ? "*" : ""), "", true);
            add(field, gridBagConstraints);
            gridBagConstraints.gridy++;
            properties.put(property, field);
        }
    }

    public ArticleForm(Item item) {
        this();

        TextField inventoryNumber = GuiUtils.createNewInput(null, "Inventarnummer", "#" + item.getInventoryNumber(), true);
        inventoryNumber.setEnabled(false);
        inventoryNumber.setToolTipText("Inventarnummer");
        add(inventoryNumber, gridBagConstraints);
        gridBagConstraints.gridy++;

        add(GuiUtils.createLabel(null, "Artikel Bezeichnung:", GuiUtils.FONT_M, true), gridBagConstraints);
        gridBagConstraints.gridy++;

        inputDescription = GuiUtils.createNewInput(null, "Artikel Bezeichnung*", item.getDescription(), true);
        add(inputDescription, gridBagConstraints);

        for (Property property : item.getCategory().getProperties()) {
            add(GuiUtils.createLabel(null, property.getDescription() + ":", GuiUtils.FONT_M, true), gridBagConstraints);
            gridBagConstraints.gridy++;
            TextField field = GuiUtils.createNewInput(null, property.getDescription() + (property.isRequired() ? "*" : ""), item.getProperty(property.getDescription()), true);
            add(field, gridBagConstraints);
            gridBagConstraints.gridy++;
            properties.put(property, field);
        }
    }

    public HashMap<Property, TextField> getProperties() {
        return properties;
    }

    public String getItemDescription() {
        return inputDescription.getText();
    }
}
