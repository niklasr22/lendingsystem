package data;

import exceptions.LoadSaveException;

import java.util.List;

public abstract class SearchSetting {

    private String name;
    private final boolean usesCategories;
    private final Container associatedContainer;

    public SearchSetting(String name, Container associatedContainer, boolean usesCategories) { //Container<? extends SearchResult> associatedContainer
        setName(name);
        this.usesCategories = usesCategories;
        this.associatedContainer = associatedContainer;
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
}
