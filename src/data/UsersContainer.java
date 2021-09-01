package data;

import exceptions.IllegalInputException;
import exceptions.LoadSaveException;
import store.DataManagement;
import store.PersistenceUsersDB;

import java.util.ArrayList;
import java.util.Iterator;

public class UsersContainer extends Container implements Iterable<User> {
    private final ArrayList<User> users = new ArrayList<>();
    private final DataManagement<UsersContainer, User> store = new PersistenceUsersDB();
    private static UsersContainer unique = null;

    private UsersContainer() throws LoadSaveException {
        super();
        store.load(this);
    }

    public static UsersContainer instance() throws LoadSaveException {
        return unique == null ? unique = new UsersContainer() : unique;
    }

    public Boolean linkUserLoading(User user) throws IllegalInputException {
        if (user == null)
            return false;
        if (users.contains(user))
            throw new IllegalInputException("Benutzer bereits vorhanden");
        users.add(user);
        propertyChangeSupport.firePropertyChange("users", null, user);
        return true;
    }

    public void linkUser(User user) throws IllegalInputException, LoadSaveException {
        if (linkUserLoading(user))
            store.add(user);
    }

    public void unlinkUser(User user) throws LoadSaveException {
        if (users.contains(user)) {
            propertyChangeSupport.firePropertyChange("users", user, null);
            users.remove(user);
            store.delete(user);
        }
    }

    public User getUser(String userName) {
        for (User someUser : users) {
            if (someUser.getUsername().equals(userName))
                return someUser;
        }
        return null;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void modifyUser(User user) throws LoadSaveException {
        store.modify(user);
        propertyChangeSupport.firePropertyChange("users", null, user);
    }

    public Boolean isEmpty() {
        return users.isEmpty();
    }

    @Override
    public Iterator<User> iterator() {
        return users.iterator();
    }
}
