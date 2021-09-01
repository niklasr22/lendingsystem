import data.CategoriesContainer;
import data.Category;
import data.Property;
import data.UsersContainer;
import exceptions.IllegalInputException;
import exceptions.LoadSaveException;
import gui.LoginWindow;
import gui.UserDialog;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        UsersContainer usersContainer;
        try {
            usersContainer = UsersContainer.instance();
            if (usersContainer.isEmpty()) {
                // Create default categories
                CategoriesContainer categories = CategoriesContainer.instance();
                categories.getCategories().clear();

                Category thingsCategory = new Category("Gegenstände");
                thingsCategory.addProperty(new Property("Serien-/Modellnummer", false));
                thingsCategory.addProperty(new Property("Zustand (1-5)", false));
                thingsCategory.addProperty(new Property("Kommentar", false));
                categories.linkCategory(thingsCategory);

                Category keysCategory = new Category("Schlüssel");
                keysCategory.addProperty(new Property("Zugehöriges Schloss", false));
                keysCategory.addProperty(new Property("Kommentar", false));
                categories.linkCategory(keysCategory);

                new UserDialog(null, true);
            } else {
                new LoginWindow();
            }
        } catch (LoadSaveException | IllegalInputException e) {
            throw new RuntimeException(e);
        }
    }
}
