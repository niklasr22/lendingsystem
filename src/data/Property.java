package data;

import exceptions.IllegalInputException;

public class Property {
    private String description;
    private boolean required;
    private int id;

    public Property(String description, boolean required) throws IllegalInputException {
        setDescription(description);
        setRequired(required);
    }

    public Property(int id, String description, boolean required) throws IllegalInputException {
        setId(id);
        setDescription(description);
        setRequired(required);
    }

    public Property(String description) throws IllegalInputException {
        setDescription(description);
        setRequired(false);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setDescription(String description) throws IllegalInputException {
        if (!checkDescription(description))
            throw new IllegalInputException("Ungültiger Name für eine Eigenschaft");
        this.description = description;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    private boolean checkDescription(String description) {
        return description != null && !description.isEmpty();
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isRequired() {
        return this.required;
    }

    @Override
    public String toString() {
        String asterisk = "";
        if (this.isRequired())
            asterisk = "*";
        return this.description + asterisk;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !this.getClass().equals(obj.getClass()))
            return false;
        Property otherProperty = (Property) obj;
        return this.getDescription().compareToIgnoreCase(otherProperty.getDescription()) == 0;
    }
}
