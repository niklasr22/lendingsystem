package gui;

import data.Lend;
import data.LendsContainer;
import data.User;
import exceptions.IllegalInputException;
import exceptions.LoadSaveException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SearchResultLendView extends SearchResultView<Lend> {

    private final JFrame frame;

    public SearchResultLendView(JFrame frame, Lend content, User user) {
        super(content, user);
        this.frame = frame;
    }

    @Override
    protected void createLayout() {
        int lendStatus = getContent().getStatus();

        setBackground(Color.white);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton button;
        JLabel label;

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWeights = new double[]{0.3, 0.6, 0.1};
        setLayout(gridBagLayout);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.LINE_START;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.gridheight = 1;

        label = new JLabel("*" + getContent().getId() + ": " + getContent().getItem().getDescription() + "(#" + getContent().getItem().getInventoryNumber() + ")" + " " + getContent().getPerson().getName());
        label.setFont(GuiUtils.FONT_L);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        add(label, c);

        label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setFont(GuiUtils.FONT_L);

        switch (lendStatus) {
            case Lend.RESERVED:
                label.setText("Reserviert");
                break;
            case Lend.PICKED_UP:
                label.setText("Abgeholt");
                label.setForeground(GuiUtils.GREEN);
                break;
            case Lend.PICKED_UP_EXPIRED:
                label.setText("Verspätet");
                label.setForeground(Color.RED);
                break;
            case Lend.RETURNED:
                label.setText("Abgeschlossen");
        }

        c.gridx = 2;
        c.gridwidth = 1;
        add(label, c);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        label = new JLabel("Ab: " + dtf.format(getContent().getLendDate()));
        label.setFont(GuiUtils.FONT_L);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        add(label, c);

        label = new JLabel();
        if (getContent().isReturned()) {
            label.setText("Bis: " + dtf.format(getContent().getReturnDate()));
        } else {
            label.setText("Bis: " + dtf.format(getContent().getExpectedReturnDate()) + " (geplant)");
            if (lendStatus == Lend.PICKED_UP_EXPIRED || (lendStatus == Lend.RESERVED && getContent().getExpectedReturnDate().isBefore(LocalDate.now())))
                label.setForeground(Color.RED);
            else
                label.setForeground(GuiUtils.GREEN);
        }
        label.setFont(GuiUtils.FONT_L);
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 1;
        add(label, c);

        JPanel buttons = new JPanel(new GridLayout());
        buttons.setOpaque(false);

        button = new JButton("Details");
        button.addActionListener(e -> new LendDetailsDialog(frame, getContent(), getActiveUser()));
        buttons.add(button);

        if (lendStatus == Lend.RESERVED) {
            button = new JButton("Abholung");
            button.addActionListener(evt -> {
                int status = JOptionPane.showConfirmDialog(this, "Abholung bestätigen?");
                if (status == JOptionPane.OK_OPTION) {
                    try {
                        getContent().pickUpItem();
                        LendsContainer.instance().modifyLend(getContent());
                    } catch (LoadSaveException e) {
                        JOptionPane.showMessageDialog(this, e.getMessage());
                    }
                }
            });
            button.setEnabled(getContent().getItem().isAvailable() && !getContent().getLendDate().isAfter(LocalDate.now()) && !getContent().getExpectedReturnDate().isBefore(LocalDate.now()));
            buttons.add(button);
        }
        if (lendStatus == Lend.RETURNED || lendStatus == Lend.RESERVED) {
            button = new JButton("Löschen");
            button.addActionListener(evt -> {
                int status = JOptionPane.showConfirmDialog(this, "Soll die Leihe aus der Historie gelöscht werden?");
                if (status == JOptionPane.OK_OPTION) {
                    try {
                        LendsContainer.instance().unlinkLend(getContent());
                    } catch (LoadSaveException e) {
                        JOptionPane.showMessageDialog(this, e.getMessage());
                    }
                }
            });
            buttons.add(button);
        } else if (lendStatus == Lend.PICKED_UP || lendStatus == Lend.PICKED_UP_EXPIRED) {
            button = new JButton("Rückgabe");
            button.addActionListener(evt -> {
                int status = JOptionPane.showConfirmDialog(this, "Rückgabe bestätigen?");
                if (status == JOptionPane.OK_OPTION) {
                    try {
                        getContent().returnItem();
                        LendsContainer.instance().modifyLend(getContent());
                    } catch (LoadSaveException | IllegalInputException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this, e.getMessage());
                    }
                }
            });
            buttons.add(button);
        }

        c.anchor = GridBagConstraints.LINE_END;
        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 2;
        add(buttons, c);
    }
}
