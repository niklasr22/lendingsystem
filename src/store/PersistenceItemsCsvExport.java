package store;

import data.Item;
import exceptions.LoadSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PersistenceItemsCsvExport {

    private final File file;
    private final static String DELIMITER = ";";

    public PersistenceItemsCsvExport(File file) {
        this.file = file;
    }

    public void save(List<Item> items) throws LoadSaveException {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.ISO_8859_1))) {
            writer.println("Inventarnummer" + DELIMITER +
                    "Beschreibung" + DELIMITER +
                    "Kategorie" + DELIMITER +
                    "Verliehen");
            for (Item item : items) {
                writer.println(item.getInventoryNumber() + DELIMITER +
                        '"' +  item.getDescription() + '"' + DELIMITER +
                        '"' + item.getCategory().getName() + '"' + DELIMITER +
                        '"' + (item.isLent() ? "ja" : "nein") + '"');
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new LoadSaveException("CSV Datei konnte nichte gespeichert werden", e);
        }
    }
}
