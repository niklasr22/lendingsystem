package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GuiUtils {
    public final static Font FONT_XL = new Font("Helvetica", Font.PLAIN, 26);
    public final static Font FONT_L = new Font("Helvetica", Font.PLAIN, 16);
    public final static Font FONT_M = new Font("Helvetica", Font.PLAIN, 14);
    public final static Font FONT_S = new Font("Helvetica", Font.PLAIN, 12);

    public final static Color GREEN = new Color(27, 142, 22);
    public final static Color ORANGE = new Color(237, 130, 7);
    public final static Color RESERVED = new Color(255, 128, 128);
    public final static Color BLUE = new Color(0, 72, 186);
    public final static Color LIGHT_GREY = new Color(230, 230, 230);
    public final static Color DEFAULT_BACKGROUND = UIManager.getColor("Panel.background");


    public static JLabel createLabel(Container parent, String text, boolean addBorder) {
        return createLabel(parent, text, FONT_L, addBorder);
    }

    public static JLabel createLabel(Container parent, String text, Font font, boolean addBorder) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        if (addBorder)
            label.setBorder(new EmptyBorder(5, 5, 0, 5));
        if (parent != null)
            parent.add(label);
        return label;
    }

    public static TextField createNewInput(Container parent, String placeholder, String value, boolean addBorder) {
        TextField textField = new TextField(value);
        textField.setPlaceholder(placeholder);
        textField.setFont(FONT_L);

        if (addBorder)
            textField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(DEFAULT_BACKGROUND, 5), textField.getBorder()));
        if (parent != null)
            parent.add(textField);
        return textField;
    }

    public static PasswordField createNewPasswordInput(Container parent, String placeholder, String value, boolean addBorder) {
        PasswordField passwordField = new PasswordField(value);
        passwordField.setPlaceholder(placeholder);
        passwordField.setFont(FONT_L);

        if (addBorder)
            passwordField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(DEFAULT_BACKGROUND, 5), passwordField.getBorder()));
        if (parent != null)
            parent.add(passwordField);
        return passwordField;
    }
}
