package gui;

import data.Category;
import data.Item;
import data.Property;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.HashMap;

public class ArticleForm extends JPanel {

    private TextField inputDescription;
    private HashMap<Property, TextField> properties;

    private ArticleForm() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(0, 5, 10, 5));
    }

    public ArticleForm(Category category) {
        this();

        inputDescription = GuiUtils.createNewInput(this, "Artikel Bezeichnung*", "", 30, true);

        properties = new HashMap<>();
        for (Property property : category.getProperties()) {
            properties.put(property, GuiUtils.createNewInput(this, property.getDescription() + (property.isRequired() ? "*" : ""), "", 30, true));
        }
    }

    public ArticleForm(Item item) {
        this();

        TextField inventoryNumber = GuiUtils.createNewInput(this, "Inventarnummer", "#" + item.getInventoryNumber(), 30, true);
        inventoryNumber.setEnabled(false);
        inventoryNumber.setToolTipText("Inventarnummer");

        GuiUtils.createLabel(this, "Artikel Bezeichnung:", true);
        inputDescription = GuiUtils.createNewInput(this, "Artikel Bezeichnung*", item.getDescription(), 30, true);

        properties = new HashMap<>();
        for (Property property : item.getCategory().getProperties()) {
            GuiUtils.createLabel(this, property.getDescription() + ":", true);
            properties.put(property, GuiUtils.createNewInput(this, property.getDescription() + (property.isRequired() ? "*" : ""), item.getProperty(property.getDescription()), 30, true));
        }
    }

    public HashMap<Property, TextField> getProperties() {
        return properties;
    }

    public String getItemDescription() {
        return inputDescription.getText();
    }
}
