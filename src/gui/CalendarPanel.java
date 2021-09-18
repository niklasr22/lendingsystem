package gui;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class CalendarPanel extends JPanel {

    private final JLabel monthLabel;
    private final JPanel daysPanel;

    private int currentYear, currentMonth;

    public CalendarPanel() {
        super();
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0f;

        JButton btnPrevious = new JButton("<");
        btnPrevious.addActionListener(e -> {
            currentMonth--;
            if (currentMonth == 0) {
                currentYear--;
                currentMonth = 12;
            }
            showMonth(currentYear, currentMonth);
        });
        add(btnPrevious, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0f;

        monthLabel = new JLabel("September");
        add(monthLabel, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.0f;

        JButton btnNext = new JButton(">");
        btnNext.addActionListener(e -> {
            currentMonth++;
            if (currentMonth == 13) {
                currentYear++;
                currentMonth = 1;
            }
            showMonth(currentYear, currentMonth);
        });
        add(btnNext, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        daysPanel = new JPanel(new GridLayout(6, 7));
        add(daysPanel, gbc);

        LocalDate now = LocalDate.now();
        showMonth(now.getYear(), now.getMonthValue());
    }

    private JLabel getBoldLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Helvetica", Font.BOLD, 14));
        return label;
    }

    public void showMonth(int year, int month) {
        currentMonth = month;
        currentYear = year;

        daysPanel.removeAll();
        daysPanel.add(getBoldLabel("Mo"));
        daysPanel.add(getBoldLabel("Di"));
        daysPanel.add(getBoldLabel("Mi"));
        daysPanel.add(getBoldLabel("Do"));
        daysPanel.add(getBoldLabel("Fr"));
        daysPanel.add(getBoldLabel("Sa"));
        daysPanel.add(getBoldLabel("So"));

        LocalDate date = LocalDate.of(year, month, 1);

        monthLabel.setText(date.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()));

        int previousMonth = month - 1;
        int previousMonthYear = year;
        if (previousMonth == 0) {
            previousMonthYear--;
            previousMonth = 12;
        }

        LocalDate previousMonthDate = LocalDate.of(previousMonthYear, previousMonth, 1);
        int previousMonthDayCount = previousMonthDate.getMonth().length(previousMonthDate.isLeapYear());

        int dayOfWeek = date.getDayOfWeek().getValue();
        System.out.println(dayOfWeek + " " + previousMonthDayCount);
        int day = 1;
        boolean previousMonthDays = false;
        if (dayOfWeek != 1) {
            day = previousMonthDayCount - dayOfWeek + 2;
            previousMonthDays = true;
        }

        int dayCount = date.getMonth().length(date.isLeapYear());

        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 7; c++) {
                daysPanel.add(new JLabel(String.valueOf(day), SwingConstants.CENTER));
                day++;
                if (previousMonthDays && day > previousMonthDayCount) {
                    previousMonthDays = false;
                    day = 1;
                }
                if (!previousMonthDays && day > dayCount)
                    day = 1;
            }
        }
    }
}