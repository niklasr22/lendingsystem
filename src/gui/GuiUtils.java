package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GuiUtils {
    public final static Font FONT = new Font("Helvetica", Font.PLAIN, 16);
    public final static Font FONT_LARGE = new Font("Helvetica", Font.PLAIN, 26);
    public final static Font FONT_MEDIUM = new Font("Helvetica", Font.PLAIN, 14);
    public final static Font FONT_SMALL = new Font("Helvetica", Font.PLAIN, 12);

    public static JLabel createLabel(Container parent, String text, boolean createFrame) {
        JLabel label = new JLabel(text);
        label.setFont(FONT);
        if (createFrame) {
            GridBagLayout layout = new GridBagLayout();
            layout.columnWeights = new double[] { 1.0 };

            JPanel panel = new JPanel(layout);
            panel.setBorder(new EmptyBorder(5, 5, 0, 5));
            parent.add(panel);

            panel.add(label);

            GridBagConstraints constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.HORIZONTAL;
            layout.setConstraints(label, constraints);
        } else {
            parent.add(label);
        }
        return label;
    }

    public static TextField createNewInput(Container parent, String placeholder, String value, int columns, boolean createFrame) {
        TextField textField = new TextField(value);
        textField.setPlaceholder(placeholder);
        textField.setColumns(columns);
        textField.setFont(FONT);
        if (createFrame) {
            GridBagLayout layout = new GridBagLayout();
            layout.columnWeights = new double[] { 1.0 };

            JPanel panel = new JPanel(layout);
            panel.setBorder(new EmptyBorder(5, 5, 5, 5));
            if (parent != null)
                parent.add(panel);

            panel.add(textField);

            GridBagConstraints constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.HORIZONTAL;
            layout.setConstraints(textField, constraints);
        } else {
            if (parent != null)
                parent.add(textField);
        }
        return textField;
    }

    public static PasswordField createNewPasswordInput(Container parent, String placeholder, String value, int columns, boolean createFrame) {
        PasswordField passwordField = new PasswordField(value);
        passwordField.setPlaceholder(placeholder);
        passwordField.setColumns(columns);
        passwordField.setFont(FONT);
        if (createFrame) {
            GridBagLayout layout = new GridBagLayout();
            layout.columnWeights = new double[] { 1.0 };

            JPanel panel = new JPanel(layout);
            panel.setBorder(new EmptyBorder(5, 5, 5, 5));
            parent.add(panel);

            panel.add(passwordField);

            GridBagConstraints constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.HORIZONTAL;
            layout.setConstraints(passwordField, constraints);
        } else {
            parent.add(passwordField);
        }
        return passwordField;
    }
}
