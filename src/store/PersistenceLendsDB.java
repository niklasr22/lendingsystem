package store;

import data.*;
import exceptions.LoadSaveException;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PersistenceLendsDB implements DataManagement<LendsContainer, Lend> {

    public PersistenceLendsDB() throws LoadSaveException {
        createTables();
    }

    @Override
    public void load(LendsContainer container) throws LoadSaveException {
        try (Statement query = DBConnection.getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            String command = "SELECT * FROM lends";
            ItemsContainer itemsContainer = ItemsContainer.instance();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern ("dd.MM.yyyy");
            DateTimeFormatter dtfWithTime = DateTimeFormatter.ofPattern ("dd.MM.yyyy HH:mm");
            PersonsContainer personsContainer = PersonsContainer.instance();
            ResultSet lends = query.executeQuery(command);
            while (lends.next()) {
                try {
                    String returnDate = lends.getString("return_date");
                    Lend lend = new Lend(
                            itemsContainer.search(lends.getInt("item_id")),
                            personsContainer.searchPerson(lends.getInt("person_id")),
                            LocalDate.parse(lends.getString("lend_date"), dtf),
                            LocalDate.parse(lends.getString("expected_return_date"), dtf),
                            returnDate == null ? null : LocalDate.parse(returnDate, dtf),
                            lends.getString("deposit"),
                            lends.getString("comment"),
                            LocalDateTime.parse(lends.getString("last_modified_date"), dtfWithTime),
                            lends.getString("last_modified_by_user"));
                    lend.setId(lends.getInt("id"));
                    container.linkLendLoading(lend);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            throw new LoadSaveException("Das Laden der Leihen ist fehlgeschlagen", e);
        }
    }

    @Override
    public void add(Lend element) throws LoadSaveException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern ("dd.MM.yyyy");
        DateTimeFormatter dtfWithTime = DateTimeFormatter.ofPattern ("dd.MM.yyyy HH:mm");
        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(
                "INSERT INTO lends (item_id, deposit, comment, lend_date, expected_return_date, return_date, last_modified_date, last_modified_by_user, person_id) VALUES (?, ?, ?, ?, ?, null, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, element.getItem().getInventoryNumber());
            statement.setString(2, element.getDeposit());
            statement.setString(3, element.getComment());
            statement.setString(4, dtf.format(element.getLendDate()));
            statement.setString(5, dtf.format(element.getExpectedReturnDate()));
            statement.setString(6, dtfWithTime.format(element.getLastModifiedDate()));
            statement.setString(7, element.getLastModifiedByUser());
            statement.setInt(8, element.getPerson().getId());
            statement.executeUpdate();

            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next())
                element.setId(resultSet.getInt(1));

        } catch (SQLException e) {
            e.printStackTrace();
            throw new LoadSaveException("Das Speichern der Leihe ist fehlgeschlagen", e);
        }
    }

    @Override
    public void delete(Lend element) throws LoadSaveException {
        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement("DELETE FROM lends WHERE id = ?")) {
            statement.setInt(1, element.getId());
            statement.executeUpdate();
        } catch (SQLException | LoadSaveException e) {
            e.printStackTrace();
            throw new LoadSaveException("Das Löschen des Artikels ist fehlgeschlagen", e);
        }
    }

    @Override
    public void modify(Lend element) throws LoadSaveException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern ("dd.MM.yyyy");
        DateTimeFormatter dtfWithTime = DateTimeFormatter.ofPattern ("dd.MM.yyyy HH:mm");
        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(
                "UPDATE lends SET deposit = ?, comment = ?, expected_return_date = ?, return_date = ?, last_modified_date = ?, last_modified_by_user = ?, person_id = ? WHERE id = ?")) {
            statement.setString(1, element.getDeposit());
            statement.setString(2, element.getComment());
            statement.setString(3, dtf.format(element.getExpectedReturnDate()));
            statement.setString(4, element.isReturned() ? dtf.format(element.getReturnDate()) : null);
            statement.setString(5, dtfWithTime.format(element.getLastModifiedDate()));
            statement.setString(6, element.getLastModifiedByUser());
            statement.setInt(7, element.getPerson().getId());
            statement.setInt(8, element.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new LoadSaveException("Das Aktualisieren der Leihe ist fehlgeschlagen", e);
        }
    }

    @Override
    public void createTables() throws LoadSaveException {
        try {
            String query = "CREATE TABLE lends ("
                    + "id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) ,"
                    + " item_id INTEGER NOT NULL,"
                    + " comment VARCHAR(250) NOT NULL,"
                    + " deposit VARCHAR(100) NOT NULL,"
                    + " lend_date VARCHAR(10) NOT NULL,"
                    + " expected_return_date VARCHAR(10) NOT NULL,"
                    + " return_date VARCHAR(10),"
                    + " last_modified_date VARCHAR(16) NOT NULL,"
                    + " last_modified_by_user VARCHAR(40) NOT NULL,"
                    + " person_id INTEGER NOT NULL,"
                    + " PRIMARY KEY (id),"
                    + " FOREIGN KEY (person_id) references persons (id),"
                    + " FOREIGN KEY (item_id) references items (id))";
            DBConnection.instance().createTable(query);
        } catch (SQLException e) {
            if (!e.getSQLState().equals("X0Y32")) {
                e.printStackTrace();
                throw new LoadSaveException("Couldn't create table", e);
            }
        }
    }
}
