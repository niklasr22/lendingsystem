package data;

import exceptions.IllegalInputException;
import exceptions.LoadSaveException;
import store.DataManagement;
import store.PersistenceCategoriesDB;

import java.util.ArrayList;
import java.util.Iterator;

public class CategoriesContainer extends Container implements Iterable<Category> {
    private static CategoriesContainer unique = null;
    private final ArrayList<Category> categories;
    private final DataManagement<CategoriesContainer, Category> store;

    private CategoriesContainer() throws LoadSaveException {
        super();
        categories = new ArrayList<>();
        store = new PersistenceCategoriesDB();
        store.load(this);
    }

    public static CategoriesContainer instance() throws LoadSaveException {
        return unique == null ? unique = new CategoriesContainer() : unique;
    }

    public boolean linkCategoryLoading(Category category) throws IllegalInputException {
        if (category == null)
            return false;
        if (categories.contains(category))
            throw new IllegalInputException();
        categories.add(category);
        propertyChangeSupport.firePropertyChange("categories", null, category);
        return true;
    }

    public void linkCategory(Category category) throws IllegalInputException, LoadSaveException {
        if (linkCategoryLoading(category))
            store.add(category);
    }

    public void unlinkCategory(Category category) throws LoadSaveException {
        if (categories.contains(category)) {
            ItemsContainer itemsContainer = ItemsContainer.instance();
            for (Item item : new ArrayList<>(itemsContainer.getItems())) {
                if (item.getCategory() == category)
                    itemsContainer.unlinkItem(item);
            }
            store.delete(category);
            categories.remove(category);

            propertyChangeSupport.firePropertyChange("categories", category, null);
        }
    }

    public Category searchCategory(int id) {
        for (Category c : categories) {
            if (c.getId() == id)
                return c;
        }
        return null;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    @Override
    public Iterator<Category> iterator() {
        return categories.iterator();
    }

}
