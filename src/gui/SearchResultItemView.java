package gui;

import data.Item;
import data.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SearchResultItemView extends SearchResultView<Item> {

    private final JFrame frame;

    public SearchResultItemView(JFrame frame, Item content, User activeUser) {
        super(content, activeUser);
        this.frame = frame;
    }

    @Override
    protected void createLayout() {
        setBackground(Color.white);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton button;
        JLabel label;

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWeights = new double[] { 0.45, 0.45, 0.1 };
        setLayout(gridBagLayout);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_START;
        c.weightx = 0.0;
        c.weighty = 0.0;

        label = new JLabel(getContent().getDescription());
        label.setFont(GuiUtils.FONT);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 1;
        add(label, c);

        label = new JLabel("#" + getContent().getInventoryNumber());
        label.setToolTipText("Inventarnummer");
        label.setFont(GuiUtils.FONT_MEDIUM);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        add(label, c);

        label = new JLabel();
        label.setFont(GuiUtils.FONT_MEDIUM);
        if (getContent().isLent()) {
            label.setText("Verliehen");
            label.setForeground(new Color(237, 130, 7));
        } else {
            label.setText("Verfügbar");
            label.setForeground(new Color(27, 142, 22));
        }
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        add(label, c);

        JPanel buttons = new JPanel(new GridLayout());
        buttons.setOpaque(false);

        button = new JButton("Details");
        button.addActionListener(e -> new ArticleDetailsDialog(frame, getContent(), getActiveUser()));
        buttons.add(button, c);


        button = new JButton("Verleihen");
        button.addActionListener(e -> new LendDialog(frame, getContent(), getActiveUser()));
        button.setEnabled(!getContent().isLent());
        buttons.add(button, c);

        c.anchor = GridBagConstraints.LINE_END;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 2;
        add(buttons, c);
    }
}
