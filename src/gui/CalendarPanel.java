package gui;

import data.CalendarEvent;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class CalendarPanel extends JPanel {

    private final JLabel monthLabel;
    private final JPanel daysPanel;
    private ArrayList<DayButton> visibleDays;
    private final ArrayList<CalendarEvent> events;
    private final boolean readonly;
    private LocalDate startDate = null;
    private LocalDate endDate = null;

    private int currentYear, currentMonth;

    public CalendarPanel(boolean readonly) {
        super();
        this.readonly = readonly;
        events = new ArrayList<>();
        visibleDays = new ArrayList<>();

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
        monthLabel.setFont(GuiUtils.FONT_L);
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

    public void showMonth(LocalDate date) {
        showMonth(date.getYear(), date.getMonthValue());
    }

    public void showMonth(int year, int month) {
        currentMonth = month;
        currentYear = year;
        visibleDays.clear();

        daysPanel.removeAll();
        daysPanel.add(getBoldLabel("Mo"));
        daysPanel.add(getBoldLabel("Di"));
        daysPanel.add(getBoldLabel("Mi"));
        daysPanel.add(getBoldLabel("Do"));
        daysPanel.add(getBoldLabel("Fr"));
        daysPanel.add(getBoldLabel("Sa"));
        daysPanel.add(getBoldLabel("So"));

        LocalDate date = LocalDate.of(year, month, 1);

        monthLabel.setText(date.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()) + ", " + currentYear);

        int previousMonth = month - 1;
        int previousMonthYear = year;
        if (previousMonth == 0) {
            previousMonthYear--;
            previousMonth = 12;
        }

        int nextMonth = month + 1;
        int nextYear = year;
        if (nextMonth == 13) {
            nextMonth = 1;
            nextYear++;
        }

        LocalDate previousMonthDate = LocalDate.of(previousMonthYear, previousMonth, 1);
        int previousMonthDayCount = previousMonthDate.getMonth().length(previousMonthDate.isLeapYear());

        int dayOfWeek = date.getDayOfWeek().getValue();
        int day = 1;
        boolean previousMonthDays = false;
        if (dayOfWeek != 1) {
            day = previousMonthDayCount - dayOfWeek + 2;
            year = previousMonthYear;
            month = previousMonth;
            previousMonthDays = true;
        }

        int dayCount = date.getMonth().length(date.isLeapYear());

        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 7; c++) {
                LocalDate dayDate = LocalDate.of(year, month, day);
                DayButton dayButton = new DayButton(String.valueOf(day), dayDate);
                dayButton.setOpaque(true);
                if (!readonly && !dayButton.getDate().isBefore(LocalDate.now())) {
                    dayButton.addActionListener(e -> {
                        setStartAndEndDate(dayButton.getDate());
                        markSelectedDays();
                    });
                }
                if (isEventOnDate(dayDate))
                    dayButton.setBackground(Color.RED);
                if (dayDate.isEqual(LocalDate.now()))
                    dayButton.setForeground(Color.BLUE);
                daysPanel.add(dayButton);
                day++;
                if (previousMonthDays && day > previousMonthDayCount) {
                    previousMonthDays = false;
                    day = 1;
                    year = currentYear;
                    month = currentMonth;
                }
                if (!previousMonthDays && day > dayCount) {
                    day = 1;
                    year = nextYear;
                    month = nextMonth;
                }
                visibleDays.add(dayButton);
            }
        }
        markSelectedDays();
    }

    private void setStartAndEndDate(LocalDate date) {
        if (startDate == null && !isEventOnDate(date)) {
            startDate = date;
        } else if (startDate != null && endDate == null && date.isEqual(startDate)) {
            startDate = null;
        } else if (startDate != null && endDate != null && date.isEqual(startDate)) {
            startDate = endDate;
            endDate = null;
        } else if (endDate != null && date.isEqual(endDate)) {
            endDate = null;
        } else if (startDate != null && date.isAfter(startDate)) {
            if (!isEventInBetween(startDate, date)) {
                endDate = date;
            } else {
                JOptionPane.showMessageDialog(this, "In dem ausgewählten Zeitraum ist der Artikel bereits reserviert");
            }
        } else if (startDate != null && date.isBefore(startDate)) {
            if (!isEventInBetween(startDate, date)) {
                if (endDate == null)
                    endDate = startDate;
                startDate = date;
            } else {
                JOptionPane.showMessageDialog(this, "In dem ausgewählten Zeitraum ist der Artikel bereits reserviert");
            }
        }
    }

    private boolean isEventInBetween(LocalDate date1, LocalDate date2) {
        LocalDate first, second;
        if (date1.isBefore(date2)) {
            first = date1;
            second = date2;
        } else {
            first = date2;
            second = date1;
        }
        while (!first.isEqual(second.plusDays(1))) {
            if (isEventOnDate(first))
                return true;
            first = first.plusDays(1);
        }
        return false;
    }

    private void markSelectedDays() {
        for (DayButton day : visibleDays) {
            LocalDate date = day.getDate();
            if (startDate != null && endDate != null) {
                if (date.isEqual(startDate) || date.isEqual(endDate) || (date.isAfter(startDate) && date.isBefore(endDate)))
                    day.setSelected(true);
                else if (!isEventOnDate(date))
                    day.setSelected(false);
            } else if (endDate == null && startDate != null) {
                if (date.isEqual(startDate))
                    day.setSelected(true);
                else if (!isEventOnDate(date))
                    day.setSelected(false);
            } else if (!isEventOnDate(date)) {
                day.setSelected(false);
            }
        }
    }

    public boolean isEventOnDate(LocalDate date) {
        for (CalendarEvent e : events) {
            if (!date.isBefore(e.getStartDate()) && !date.isAfter(e.getEndDate())) {
                return true;
            }
        }
        return false;
    }

    public void linkEvent(CalendarEvent event) {
        if (!events.contains(event))
            events.add(event);
    }

    public void linkEvents(ArrayList<CalendarEvent> events) {
        this.events.addAll(events);
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}