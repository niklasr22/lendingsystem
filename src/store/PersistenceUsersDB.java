package store;

import data.*;
import exceptions.IllegalInputException;
import exceptions.LoadSaveException;

import java.sql.*;

public class PersistenceUsersDB implements DataManagement<UsersContainer, User> {
    public PersistenceUsersDB() throws LoadSaveException {
        createTables();
    }

    @Override
    public void load(UsersContainer container) throws LoadSaveException {
        try (Statement query = DBConnection.getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            String command = "SELECT * FROM users";
            ResultSet users = query.executeQuery(command);
            while (users.next()) {
                User user = new User(users.getString("username"), users.getString("name"), users.getString("passwordhash"), users.getBoolean("admin"), false);
                container.linkUserLoading(user);
            }
        } catch (SQLException | IllegalInputException e) {
            e.printStackTrace();
            throw new LoadSaveException("Das Laden der Nutzer ist fehlgeschlagen", e);
        }
    }

    @Override
    public void add(User user) throws LoadSaveException {
        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement("INSERT INTO users VALUES (?, ?, ?, ?)")) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getName());
            statement.setString(3, user.getPasswordHash());
            statement.setBoolean(4, user.isAdmin());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new LoadSaveException("Das Hinzufügen des Nutzers ist fehlgeschlagen", e);
        }
    }

    @Override
    public void delete(User user) throws LoadSaveException {
        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement("DELETE FROM users WHERE username = ?")) {
            statement.setString(1, user.getUsername());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new LoadSaveException("Das Löschen des Nutzers ist fehlgeschlagen", e);
        }
    }

    @Override
    public void modify(User element) throws LoadSaveException {
        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement("UPDATE users SET name = ?, passwordhash = ?, admin = ? WHERE username = ?")) {
            statement.setString(1, element.getName());
            statement.setString(2, element.getPasswordHash());
            statement.setBoolean(3, element.isAdmin());
            statement.setString(4, element.getUsername());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new LoadSaveException("Das Bearbeiten des Nutzers ist fehlgeschlagen", e);
        }
    }

    @Override
    public void createTables() throws LoadSaveException {
        try {
            String query = "CREATE TABLE users ("
                    + "username VARCHAR(40) NOT NULL, "
                    + "name VARCHAR (40) NOT NULL, "
                    + "passwordhash VARCHAR (600) NOT NULL, "
                    + "admin BOOLEAN DEFAULT FALSE, "
                    + "PRIMARY KEY (username))";
            DBConnection.instance().createTable(query);
        } catch (SQLException e) {
            if (e.getErrorCode() != 30000) {
                throw new LoadSaveException("Couldn't create table", e);
            }
        }
    }
}
