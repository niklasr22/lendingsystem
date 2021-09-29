package gui;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class DayButton {
    private LocalDate date;
    private JLabel label;

    public DayButton(Container parent, String text, LocalDate date) {
        label = GuiUtils.createLabel(parent, text, false);
        this.date = date;
        label.setForeground(Color.BLACK);
        label.setPreferredSize(new Dimension(40, 40));
        label.setHorizontalAlignment(SwingConstants.CENTER);
    }

    public LocalDate getDate() {
        return date;
    }

    public void setSelected(boolean selected) {
        if (selected) {
            label.setBackground(GuiUtils.GREEN);
        } else {
            label.setBackground(GuiUtils.LIGHT_GREY);
        }
    }

    public JLabel getLabel() {
        return label;
    }
}
