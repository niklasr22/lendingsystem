package store;

import data.Lend;
import exceptions.LoadSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PersistenceLendsCsvExport {

    private final File file;
    private final static String DELIMITER = ";";

    public PersistenceLendsCsvExport(File file) {
        this.file = file;
    }

    public void save(List<Lend> lends) throws LoadSaveException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern ("dd.MM.yyyy");
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.ISO_8859_1))) {
            writer.println("Leihe-ID" + DELIMITER +
                    "Vorname" + DELIMITER +
                    "Nachname" + DELIMITER +
                    "E-Mail" + DELIMITER +
                    "Inventarnummer (Artikel)" + DELIMITER +
                    "Beschreibung (Artikel)" + DELIMITER +
                    "Verleihdatum" + DELIMITER +
                    "Erwartete Rückgabe" + DELIMITER +
                    "Pfand" + DELIMITER +
                    "Kommentar");
            for (Lend lend : lends) {
                writer.println(lend.getId() + DELIMITER +
                        '"' + lend.getPerson().getFirstName() + '"' + DELIMITER +
                        '"' + lend.getPerson().getLastName() + '"' + DELIMITER +
                        '"' + lend.getPerson().getEmail() + '"' + DELIMITER +
                        lend.getItem().getInventoryNumber() + DELIMITER +
                        '"' + lend.getItem().getDescription() + '"' + DELIMITER +
                        dtf.format(lend.getLendDate()) + DELIMITER +
                        dtf.format(lend.getExpectedReturnDate()) + DELIMITER +
                        '"' + lend.getDeposit() + '"' + DELIMITER +
                        '"' + lend.getComment() + '"');
            }
        } catch (IOException e) {
            throw new LoadSaveException("CSV Datei konnte nichte gespeichert werden", e);
        }
    }
}
