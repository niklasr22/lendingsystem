package data;

import exceptions.IllegalInputException;
import exceptions.LoadSaveException;
import util.CryptoUtils;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;

public class User extends SearchResult {
    private String username;
    private String name;
    private String passwordHash;
    private boolean admin = false;
    private UsersContainer users;

    public User(String userName, String name, String password, Boolean isAdmin) throws IllegalInputException {
        setUsername(userName);
        setName(name);
        setAdmin(isAdmin);
        createPasswordHash(password);
    }

    public User(String userName, String name, String password, Boolean isAdmin, Boolean hashPassword) throws IllegalInputException {
        setUsername(userName);
        setName(name);
        setAdmin(isAdmin);
        if (hashPassword) {
            createPasswordHash(password);
        } else {
            this.passwordHash = password;
        }
    }

    private static boolean checkUsername(String username) {
        return !username.isBlank() && username.length() >= 1 && username.length() <= 40;
    }

    private void setUsername(String username) throws IllegalInputException {
        if (username == null)
            throw new IllegalArgumentException();
        else if (checkUsername(username))
            this.username = username;
        else
            throw new IllegalInputException("Der Nutzername darf nicht leer sein, aber auch nicht mehr als 40 Zeichen enthalten.");
    }

    private static boolean checkName(String username) {
        return !username.isBlank() && username.length() <= 40;
    }

    public void setName(String name) throws IllegalInputException {
        if (name == null)
            throw new IllegalArgumentException();
        else if (checkName(name))
            this.name = name;
        else
            throw new IllegalInputException("Der Name darf nicht leer sein, aber auch nicht mehr als 40 Zeichen enthalten.");
    }

    public void createPasswordHash(String password) {
        try {
            passwordHash = CryptoUtils.generatePasswordHash(password);
        } catch(NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException();
        }
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean checkPassword(String password) {
        boolean isValid = false;
        try {
            isValid = CryptoUtils.validatePassword(password, getPasswordHash());
        } catch (InvalidKeySpecException | NoSuchAlgorithmException ignored) {

        }
        return isValid;
    }

    public boolean authenticateUser(String userName, String password) {
        try {
            users = UsersContainer.instance();
        } catch (LoadSaveException e) {
            e.printStackTrace();
        }
        User user = users.getUser(userName);
        if (user == null) {
            return false;
        }
        return user.checkPassword(password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Objects.equals(getUsername(), user.getUsername());
    }
}
