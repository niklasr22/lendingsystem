package store;

import data.Person;
import data.PersonsContainer;
import exceptions.LoadSaveException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PersistencePersonsDB implements DataManagement<PersonsContainer, Person> {

    public PersistencePersonsDB() throws LoadSaveException {
        createTables();
    }

    @Override
    public void load(PersonsContainer container) throws LoadSaveException {
        try (Statement query = DBConnection.getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            String command = "SELECT * FROM persons";
            DateTimeFormatter dtfWithTime = DateTimeFormatter.ofPattern ("dd.MM.yyyy HH:mm");
            ResultSet persons = query.executeQuery(command);
            while (persons.next()) {
                try {
                    Person person = new Person(
                            persons.getString("firstname"),
                            persons.getString("lastname"),
                            persons.getString("phone"),
                            persons.getString("mail"),
                            persons.getString("address"),
                            LocalDateTime.parse(persons.getString("last_modified_date"), dtfWithTime),
                            persons.getString("last_modified_by_user"));
                    person.setId(persons.getInt("id"));
                    container.linkPersonLoading(person);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new LoadSaveException("Das Laden der Personen ist fehlgeschlagen", e);
        }
    }

    @Override
    public void add(Person element) throws LoadSaveException {
        DateTimeFormatter dtfWithTime = DateTimeFormatter.ofPattern ("dd.MM.yyyy HH:mm");
        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(
                "INSERT INTO persons (firstname, lastname, address, phone, mail, last_modified_date, last_modified_by_user) VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, element.getFirstName());
            statement.setString(2, element.getLastName());
            statement.setString(3, element.getAddress());
            statement.setString(4, element.getPhoneNumber());
            statement.setString(5, element.getEmail());
            statement.setString(6, dtfWithTime.format(element.getLastModifiedDate()));
            statement.setString(7, element.getLastModifiedByUser());
            statement.executeUpdate();

            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next())
                element.setId(resultSet.getInt(1));

        } catch (SQLException e) {
            e.printStackTrace();
            throw new LoadSaveException("Das Speichern der Person ist fehlgeschlagen", e);
        }
    }

    @Override
    public void delete(Person element) throws LoadSaveException {
        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement("DELETE FROM persons WHERE id = ?")) {
            statement.setInt(1, element.getId());
            statement.executeUpdate();
        } catch (SQLException | LoadSaveException e) {
            e.printStackTrace();
            throw new LoadSaveException("Das Löschen der Person ist fehlgeschlagen", e);
        }
    }

    @Override
    public void modify(Person element) throws LoadSaveException {
        DateTimeFormatter dtfWithTime = DateTimeFormatter.ofPattern ("dd.MM.yyyy HH:mm");
        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(
                "UPDATE persons SET firstname = ?, lastname = ?, address = ?, phone = ?, mail = ?, last_modified_date = ?, last_modified_by_user = ? WHERE id = ?")) {
            statement.setString(1, element.getFirstName());
            statement.setString(2, element.getLastName());
            statement.setString(3, element.getAddress());
            statement.setString(4, element.getPhoneNumber());
            statement.setString(5, element.getEmail());
            statement.setString(6, dtfWithTime.format(element.getLastModifiedDate()));
            statement.setString(7, element.getLastModifiedByUser());
            statement.setInt(8, element.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new LoadSaveException("Das Bearbeiten der Person ist fehlgeschlagen", e);
        }
    }

    @Override
    public void createTables() throws LoadSaveException {
        try {
            String query = "CREATE TABLE persons ("
                    + "id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) ,"
                    + " firstname VARCHAR(40) NOT NULL,"
                    + " lastname VARCHAR(40) NOT NULL,"
                    + " address VARCHAR(120) NOT NULL,"
                    + " phone VARCHAR(40) NOT NULL,"
                    + " mail VARCHAR(50) NOT NULL,"
                    + " last_modified_date VARCHAR(16) NOT NULL,"
                    + " last_modified_by_user VARCHAR(40) NOT NULL,"
                    + " PRIMARY KEY (id))";
            DBConnection.instance().createTable(query);
        } catch (SQLException e) {
            if (!e.getSQLState().equals("X0Y32")) {
                e.printStackTrace();
                throw new LoadSaveException("Couldn't create table", e);
            }
        }
    }
}
