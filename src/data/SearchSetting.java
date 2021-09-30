package data;

import exceptions.LoadSaveException;

import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public abstract class SearchSetting<E extends SearchResult> {

    private String name;
    private final boolean usesCategories;
    private final Container associatedContainer;
    private final HashMap<String, Predicate<E>> filters;
    private Predicate<E> activeFilter;

    public SearchSetting(String name, Container associatedContainer, boolean usesCategories) {
        setName(name);
        this.usesCategories = usesCategories;
        this.associatedContainer = associatedContainer;
        this.filters = new HashMap<>();
    }

    public abstract List<? extends SearchResult> listAll() throws LoadSaveException;

    public abstract List<? extends SearchResult> search(String search) throws LoadSaveException;

    private void setName(String name) {
        this.name = name;
    }

    public boolean usesCategories() {
        return usesCategories;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    public Container getAssociatedContainer() {
        return associatedContainer;
    }

    public SearchSetting<E> addFilter(String name, Predicate<E> filter) {
        filters.put(name, filter);
        return this;
    }

    public HashMap<String, Predicate<E>> getFilters() {
        return filters;
    }

    public void setActiveFilter(String filter) {
        activeFilter = filters.get(filter);
    }

    public Predicate<E> getActiveFilter() {
        return activeFilter != null ? activeFilter : l -> true;
    }
}
