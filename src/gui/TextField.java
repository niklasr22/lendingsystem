package gui;

import javax.swing.*;
import java.awt.*;

public class TextField extends JTextField {

    private String placeholder;
    private Color placeholderForeground;

    public TextField() {
        this(null);
    }

    public TextField(String text) {
        super(text);
        setPlaceholderForeground(Color.gray);
        setPlaceholder("");
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        if (placeholder == null)
            throw new IllegalArgumentException();
        this.placeholder = placeholder;
    }

    public Color getPlaceholderForeground() {
        return placeholderForeground;
    }

    public void setPlaceholderForeground(Color placeholderForeground) {
        this.placeholderForeground = placeholderForeground;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (getText().isEmpty()) {
            Graphics2D graphics2D = (Graphics2D) g;
            graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            FontMetrics fontMetrics = g.getFontMetrics(getFont());
            graphics2D.setColor(getPlaceholderForeground());
            float fh = fontMetrics.getAscent() - fontMetrics.getDescent() - fontMetrics.getLeading();

            graphics2D.drawString(getPlaceholder(), getInsets().left, getHeight() / 2 + (int) fh / 2);
        }
    }
}
