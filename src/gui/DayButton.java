package gui;

import java.awt.*;
import java.time.LocalDate;

public class DayButton extends Button {
    private LocalDate date;

    public DayButton(String text, LocalDate date) {
        super(text);
        this.date = date;
        this.setBackground(Color.WHITE);
    }

    public LocalDate getDate() {
        return date;
    }

    public void setSelected(boolean selected) {
        if (selected) {
            this.setBackground(Color.GREEN);
        } else {
            this.setBackground(Color.WHITE);
        }
    }
}
