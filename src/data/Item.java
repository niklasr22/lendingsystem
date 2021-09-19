package data;

import exceptions.IllegalInputException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class Item extends SearchResult {
    private final HashMap<String, String> properties;
    private int inventoryNumber;
    private Category category;
    private String description;
    private Lend lend;
    private final ArrayList<Lend> lends;
    private LocalDateTime lastModified;
    private String lastModifiedByUser;
    private boolean available = true;

    public Item(Category category) {
        setCategory(category);
        properties = new HashMap<>();
        lends = new ArrayList<>();
    }

    public Item(Category category, String description, LocalDateTime lastModified, String lastModifiedByUser) throws IllegalInputException {
        setCategory(category);
        setDescription(description);
        setLastModifiedDate(lastModified);
        setLastModifiedByUser(lastModifiedByUser);
        properties = new HashMap<>();
        lends = new ArrayList<>();
    }

    public Item(Category category, String description, int inventoryNumber) throws IllegalInputException {
        setCategory(category);
        setDescription(description);
        setInventoryNumber(inventoryNumber);
        properties = new HashMap<>();
        lends = new ArrayList<>();
    }

    public Item(Category category, String description, int inventoryNumber, LocalDateTime lastModified, String lastModifiedByUser, boolean available) throws IllegalInputException {
        this(category, description, inventoryNumber);
        setLastModifiedDate(lastModified);
        setLastModifiedByUser(lastModifiedByUser);
        setAvailable(available);
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

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    // TODO: aktive leihe zurückgeben
    public Lend getLend() {
        return lend;
    }

    public ArrayList<Lend> getLends() {
        return lends;
    }

    public void linkLend(Lend lend) {
        if (!lends.contains(lend))
            lends.add(lend);
    }

    public void unlinkLend(Lend lend) {
        lends.remove(lend);
    }

    public boolean isAvailableFor(LocalDate start, LocalDate end) {
        for (Lend l : lends) {
            if (l.getStatus() == Lend.RETURNED)
                continue;
            LocalDate lendDate = l.getLendDate();
            LocalDate returnDate = l.getExpectedReturnDate();
            if (lendDate.isEqual(start) ||
                    lendDate.isEqual(end) ||
                    returnDate.isEqual(start) ||
                    returnDate.isEqual(end) ||
                    (start.isAfter(lendDate) && start.isBefore(returnDate)) ||
                    (end.isAfter(lendDate) && end.isBefore(returnDate)) ||
                    (lendDate.isAfter(start) && lendDate.isBefore(end)) ||
                    (returnDate.isAfter(start) && returnDate.isBefore(end)))
                return false;
        }
        return true;
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
