package data;

import exceptions.IllegalInputException;
import exceptions.LoadSaveException;
import store.DataManagement;
import store.PersistenceItemsDB;

import java.util.ArrayList;
import java.util.Iterator;

public class ItemsContainer extends Container implements Iterable<Item> {

    private static ItemsContainer unique = null;
    private final ArrayList<Item> items;
    private final DataManagement<ItemsContainer, Item> store;

    private ItemsContainer() throws LoadSaveException {
        super();
        items = new ArrayList<>();

        CategoriesContainer.instance();

        store = new PersistenceItemsDB();
        store.load(this);
    }

    public static ItemsContainer instance() throws LoadSaveException {
        if (unique == null)
            return unique = new ItemsContainer();
        return unique;
    }

    public boolean linkItemLoading(Item item) throws IllegalInputException {
        if (item == null)
            return false;
        if (items.contains(item))
            throw new IllegalInputException("Der Artikel bereits vorhanden");
        items.add(item);
        return true;
    }

    public void linkItem(Item item) throws IllegalInputException, LoadSaveException {
        if (linkItemLoading(item)) {
            store.add(item);
            propertyChangeSupport.firePropertyChange("items", null, item);
        }
    }

    public void unlinkItem(Item item) throws LoadSaveException {
        if (items.contains(item)) {
            if (item.isLent())
                throw new LoadSaveException("Ein aktuell verliehener Artikel kann nicht gelöscht werden.", null);

            LendsContainer lendsContainer = LendsContainer.instance();
            ArrayList<Lend> lends = new ArrayList<>(lendsContainer.getLends());
            for (Lend lend : lends)
                lendsContainer.unlinkLend(lend);

            store.delete(item);
            items.remove(item);
            propertyChangeSupport.firePropertyChange("items", item, null);
        }
    }

    public void modifyItem(Item item) throws LoadSaveException {
        store.modify(item);
        propertyChangeSupport.firePropertyChange("items", null, item);
    }

    public Item search(int inventoryNumber) {
        for (Item item : items) {
            if (item.getInventoryNumber() == inventoryNumber)
                return item;
        }
        return null;
    }

    @Override
    public Iterator<Item> iterator() {
        return items.iterator();
    }

    public ArrayList<Item> getItems() {
        return items;
    }
}
