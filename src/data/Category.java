package data;

import exceptions.IllegalInputException;

import java.util.ArrayList;

public class Category extends SearchResult {
    private final ArrayList<Property> properties;
    private String name;
    private int id = -1;

    public Category(String name) throws IllegalInputException {
        setName(name);
        this.properties = new ArrayList<>();
    }

    public Category(int id, String name) throws IllegalInputException {
        setName(name);
        setId(id);
        this.properties = new ArrayList<>();
    }

    public void setId(int id) throws IllegalInputException {
        if (!checkId(id))
            throw new IllegalInputException("Ungültige ID für eine Kategorie");
        this.id = id;
    }

    public int getId() {
        return id;
    }

    private boolean checkId(int id) {
        return id >= 0;
    }

    private void setName(String name) throws IllegalInputException {
        if (!checkName(name))
            throw new IllegalInputException("Ungültige Bezeichnung für eine Kategorie");
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private boolean checkName(String name) {
        return name != null && !name.isEmpty();
    }

    public void addProperty(Property property) throws IllegalInputException {
        if (property == null)
            throw new IllegalArgumentException();
        if (!checkProperty(property))
            throw new IllegalInputException("Fehler: Es gibt mindestens zwei gleichnamige Eigenschaften");
        properties.add(property);
    }

    private boolean checkProperty(Property property) {
        return !properties.contains(property);
    }

    public ArrayList<Property> getProperties() {
        return this.properties;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        return o == this;
        /*if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Category category = (Category) o;
        return getName().equals(category.getName());*/
    }
}
