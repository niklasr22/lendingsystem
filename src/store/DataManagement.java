package store;

import exceptions.LoadSaveException;

public interface DataManagement<C, E> {
    void load(C container) throws LoadSaveException;
    void add(E element) throws LoadSaveException;
    void delete(E element) throws LoadSaveException;
    void modify(E element) throws LoadSaveException;
    void createTables() throws LoadSaveException;
}