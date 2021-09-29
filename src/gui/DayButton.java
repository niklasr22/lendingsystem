package gui;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class DayButton extends JLabel {
    private final LocalDate date;

    public DayButton(String text, LocalDate date) {
        super(text);
        this.date = date;
        setForeground(Color.BLACK);
        setPreferredSize(new Dimension(40, 40));
        setFont(GuiUtils.FONT_M);
        setHorizontalAlignment(SwingConstants.CENTER);
        setOpaque(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public LocalDate getDate() {
        return date;
    }

    public void setSelected(boolean selected) {
        if (selected)
            setBackground(GuiUtils.GREEN);
        else
            setBackground(GuiUtils.LIGHT_GREY);
    }
}
