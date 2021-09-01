package data;

import exceptions.IllegalInputException;

import java.time.LocalDateTime;
import java.util.HashMap;

public class Item extends SearchResult {
    private final HashMap<String, String> properties;
    private int inventoryNumber;
    private Category category;
    private String description;
    private Lend lend;
    private LocalDateTime lastModified;
    private String lastModifiedByUser;

    public Item(Category category) {
        setCategory(category);
        properties = new HashMap<>();
    }

    public Item(Category category, String description, LocalDateTime lastModified, String lastModifiedByUser) throws IllegalInputException {
        setCategory(category);
        setDescription(description);
        setLastModifiedDate(lastModified);
        setLastModifiedByUser(lastModifiedByUser);
        properties = new HashMap<>();
    }

    public Item(Category category, String description, int inventoryNumber) throws IllegalInputException {
        setCategory(category);
        setDescription(description);
        setInventoryNumber(inventoryNumber);
        properties = new HashMap<>();
    }

    public Item(Category category, String description, int invertoryNumber, LocalDateTime lastModified, String lastModifiedByUser) throws IllegalInputException {
        setCategory(category);
        setDescription(description);
        setInventoryNumber(invertoryNumber);
        properties = new HashMap<>();
        setLastModifiedDate(lastModified);
        setLastModifiedByUser(lastModifiedByUser);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) throws IllegalInputException {
        if (checkDescription(description))
            this.description = description;
        else
            throw new IllegalInputException("Die Beschreibung eines Artikels darf nicht leer sein");
    }

    private static boolean checkDescription(String description) {
        return !description.isEmpty();
    }

    public int getInventoryNumber() {
        return inventoryNumber;
    }

    public void setInventoryNumber(int inventoryNumber) {
        this.inventoryNumber = inventoryNumber;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean isLent() {
        return lend != null;
    }

    public void setLend(Lend lend) {
        this.lend = lend;
    }

    public Lend getLend() {
        return lend;
    }

    public void addProperty(String propertyName, String value) {
        properties.put(propertyName, value);
    }

    public String getProperty(String propertyName) {
        return properties.get(propertyName);
    }

    public void setLastModifiedDate(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public LocalDateTime getLastModifiedDate() {
        return this.lastModified;
    }

    public void setLastModifiedByUser(String lastModifiedByUser) {
        this.lastModifiedByUser = lastModifiedByUser;
    }

    public String getLastModifiedByUser() {
        return this.lastModifiedByUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Item item = (Item) o;
        return getInventoryNumber() == item.getInventoryNumber();
    }
}
