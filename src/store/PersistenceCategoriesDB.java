package store;

import data.CategoriesContainer;
import data.Category;
import data.Property;
import exceptions.IllegalInputException;
import exceptions.LoadSaveException;

import java.sql.*;

public class PersistenceCategoriesDB implements DataManagement<CategoriesContainer, Category> {

    public PersistenceCategoriesDB() throws LoadSaveException {
        createTables();
    }

    @Override
    public void load(CategoriesContainer container) throws LoadSaveException {
        try (Statement query = DBConnection.getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            String command = "SELECT * FROM categories";
            ResultSet categories = query.executeQuery(command);
            while (categories.next()) {
                try {
                    Category category = new Category(categories.getInt(1), categories.getString(2));

                    String commandProperties = "SELECT id, name, isrequired FROM categories_properties WHERE category_id = ?";
                    PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(commandProperties);
                    preparedStatement.setInt(1, category.getId());

                    ResultSet properties = preparedStatement.executeQuery();
                    while (properties.next()) {
                        category.addProperty(new Property(properties.getInt(1), properties.getString(2), properties.getBoolean(3)));
                    }

                    container.linkCategoryLoading(category);
                } catch (IllegalInputException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            throw new LoadSaveException("Loading failed", e);
        }
    }

    @Override
    public void add(Category element) throws LoadSaveException {
        PreparedStatement statement = null;
        try {
            Connection connection = DBConnection.getConnection();

            String command = "INSERT INTO categories (name) VALUES (?)";
            statement = connection.prepareStatement(command, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, element.getName());
            statement.executeUpdate();

            try {
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next())
                    element.setId(resultSet.getInt(1));
            } catch (IllegalInputException e) {
                e.printStackTrace();
            }

            statement.close();

            for (Property p : element.getProperties()) {
                statement = connection.prepareStatement("INSERT INTO categories_properties (name, category_id, isrequired) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, p.getDescription());
                statement.setInt(2, element.getId());
                statement.setBoolean(3, p.isRequired());
                statement.executeUpdate();

                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next())
                    p.setId(resultSet.getInt(1));

                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new LoadSaveException("Hinzufügen fehlgeschlagen", e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ignored) { }
            }
        }
    }

    @Override
    public void delete(Category element) throws LoadSaveException {
        PreparedStatement statement = null;
        try {
            Connection connection = DBConnection.getConnection();

            statement = connection.prepareStatement("DELETE FROM categories_properties WHERE category_id = ?");
            statement.setInt(1, element.getId());
            statement.executeUpdate();
            statement.close();

            statement = connection.prepareStatement("DELETE FROM categories WHERE id = ?");
            statement.setInt(1, element.getId());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new LoadSaveException("Löschen fehlgeschlagen", e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ignored) { }
            }
        }
    }

    @Override
    public void modify(Category element) {

    }

    @Override
    public void createTables() throws LoadSaveException {
        try {
            String query = "CREATE TABLE categories ("
                    + "id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) ,"
                    + " name VARCHAR (40) NOT NULL,"
                    + " PRIMARY KEY (id))";
            DBConnection.instance().createTable(query);
        } catch (SQLException e) {
            if (!e.getSQLState().equals("X0Y32")) {
                e.printStackTrace();
                throw new LoadSaveException("Couldn't create table", e);
            }
        }

        try {
            String query = "CREATE TABLE categories_properties ("
                    + "id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) ,"
                    + " name VARCHAR (40) NOT NULL,"
                    + " category_id INTEGER NOT NULL,"
                    + " isrequired BOOLEAN DEFAULT false,"
                    + " PRIMARY KEY (id),"
                    + " FOREIGN KEY (category_id) references categories (id))";
            DBConnection.instance().createTable(query);
        } catch (SQLException e) {
            if (!e.getSQLState().equals("X0Y32")) {
                e.printStackTrace();
                throw new LoadSaveException("Couldn't create table", e);
            }
        }
    }
}
