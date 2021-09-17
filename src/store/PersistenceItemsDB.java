package store;

import data.CategoriesContainer;
import data.Item;
import data.ItemsContainer;
import data.Property;
import exceptions.IllegalInputException;
import exceptions.LoadSaveException;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PersistenceItemsDB implements DataManagement<ItemsContainer, Item> {

    public PersistenceItemsDB() throws LoadSaveException {
        createTables();
    }

    @Override
    public void load(ItemsContainer container) throws LoadSaveException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        try (Statement query = DBConnection.getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            String command = "SELECT * FROM items";
            CategoriesContainer categoriesContainer = CategoriesContainer.instance();
            ResultSet items = query.executeQuery(command);
            while (items.next()) {
                try {
                    Item item = new Item(
                            categoriesContainer.searchCategory(items.getInt("category_id")),
                            items.getString("description"),
                            items.getInt("id"),
                            LocalDateTime.parse(items.getString("last_modified_date"), dtf),
                            items.getString("last_modified_by_user"),
                            items.getBoolean("available"));

                    String commandProperties = "SELECT name, property_value FROM items_properties, categories_properties WHERE items_properties.item_id = ? and items_properties.property_id = categories_properties.id";
                    PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(commandProperties);
                    preparedStatement.setInt(1, item.getInventoryNumber());

                    ResultSet properties = preparedStatement.executeQuery();
                    while (properties.next()) {
                        item.addProperty(properties.getString("name"), properties.getString("property_value"));
                    }

                    container.linkItemLoading(item);
                } catch (IllegalInputException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new LoadSaveException("Loading failed", e);
        }
    }

    @Override
    public void add(Item element) throws LoadSaveException {
        PreparedStatement statement = null;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        try {
            Connection connection = DBConnection.getConnection();

            String command = "INSERT INTO items (description, category_id, available, last_modified_date, last_modified_by_user) VALUES (?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(command, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, element.getDescription());
            statement.setInt(2, element.getCategory().getId());
            statement.setBoolean(3, element.isAvailable());
            statement.setString(4, dtf.format(element.getLastModifiedDate()));
            statement.setString(5, element.getLastModifiedByUser());
            statement.executeUpdate();

            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next())
                element.setInventoryNumber(resultSet.getInt(1));

            statement.close();

            for (Property p : element.getCategory().getProperties()) {
                command = "INSERT INTO items_properties (item_id, property_id, property_value) VALUES (?, ?, ?)";
                statement = connection.prepareStatement(command);
                statement.setInt(1, element.getInventoryNumber());
                statement.setInt(2, p.getId());
                statement.setString(3, element.getProperty(p.getDescription()));
                statement.executeUpdate();
                statement.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new LoadSaveException("Das Speichern des Artikels ist fehlgeschlagen", e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ignored) { }
            }
        }
    }

    @Override
    public void delete(Item element) throws LoadSaveException {
        PreparedStatement statement = null;
        try {
            Connection connection = DBConnection.getConnection();

            statement = connection.prepareStatement("DELETE FROM items_properties WHERE item_id = ?");
            statement.setInt(1, element.getInventoryNumber());
            statement.executeUpdate();
            statement.close();

            statement = connection.prepareStatement("DELETE FROM items WHERE id = ?");
            statement.setInt(1, element.getInventoryNumber());
            statement.executeUpdate();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new LoadSaveException("Das Löschen des Artikels ist fehlgeschlagen", e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ignored) { }
            }
        }
    }

    @Override
    public void modify(Item element) throws LoadSaveException {
        PreparedStatement statement = null;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        try {
            Connection connection = DBConnection.getConnection();

            String command = "UPDATE items SET description = ?, last_modified_date = ?, last_modified_by_user = ?, available = ? WHERE id = ?";
            statement = connection.prepareStatement(command);
            statement.setString(1, element.getDescription());
            statement.setString(2, dtf.format(element.getLastModifiedDate()));
            statement.setString(3, element.getLastModifiedByUser());
            statement.setBoolean(4, element.isAvailable());
            statement.setInt(5, element.getInventoryNumber());
            statement.executeUpdate();

            statement.close();

            for (Property p : element.getCategory().getProperties()) {
                command = "UPDATE items_properties SET property_value = ? WHERE item_id = ? and property_id = ?";
                statement = connection.prepareStatement(command);
                statement.setString(1, element.getProperty(p.getDescription()));
                statement.setInt(2, element.getInventoryNumber());
                statement.setInt(3, p.getId());
                statement.executeUpdate();
                statement.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new LoadSaveException("Das Bearbeiten des Artikels ist fehlgeschlagen", e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ignored) { }
            }
        }

    }

    @Override
    public void createTables() throws LoadSaveException {
        try {
            String query = "CREATE TABLE items ("
                    + "id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) ,"
                    + " description VARCHAR (80) NOT NULL,"
                    + " category_id INTEGER NOT NULL,"
                    + " available BOOLEAN NOT NULL,"
                    + " last_modified_date VARCHAR(16) NOT NULL, "
                    + " last_modified_by_user VARCHAR(40) NOT NULL, "
                    + " PRIMARY KEY (id), "
                    + " FOREIGN KEY (category_id) references categories (id))";
            DBConnection.instance().createTable(query);
        } catch (SQLException e) {
            if (!e.getSQLState().equals("X0Y32")) {
                e.printStackTrace();
                throw new LoadSaveException("Couldn't create table", e);
            }
        }

        try {
            String query = "CREATE TABLE items_properties ("
                    + "id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) ,"
                    + " item_id INTEGER NOT NULL,"
                    + " property_id INTEGER NOT NULL,"
                    + " property_value VARCHAR (80) NOT NULL,"
                    + " PRIMARY KEY (id),"
                    + " FOREIGN KEY (item_id) references items (id),"
                    + " FOREIGN KEY (property_id) references categories_properties (id))";
            DBConnection.instance().createTable(query);
        } catch (SQLException e) {
            if (!e.getSQLState().equals("X0Y32")) {
                e.printStackTrace();
                throw new LoadSaveException("Couldn't create table", e);
            }
        }
    }
}
