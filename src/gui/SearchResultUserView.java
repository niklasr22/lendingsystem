package gui;

import data.User;
import data.UsersContainer;
import exceptions.LoadSaveException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SearchResultUserView extends SearchResultView<User> {

    private final JFrame frame;

    public SearchResultUserView(JFrame frame, User content, User activeUser) {
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
        gridBagLayout.columnWeights = new double[] { 0.9, 0.1 };
        setLayout(gridBagLayout);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_START;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridy = 0;

        label = new JLabel(getContent().getName());
        label.setFont(GuiUtils.FONT);
        c.gridx = 0;
        add(label, c);

        JPanel panel = new JPanel(new GridLayout(1, 2));

        button = new JButton("Details");
        button.addActionListener(evt -> new UserDetailsDialog(frame, getContent(), getActiveUser()));
        panel.add(button);

        button = new JButton("Löschen");
        button.addActionListener(evt -> {
            int status = JOptionPane.showConfirmDialog(this, "Soll der Nutzer wirklich gelöscht werden?");
            if (status == JOptionPane.OK_OPTION) {
                try {
                    UsersContainer.instance().unlinkUser(getContent());
                } catch (LoadSaveException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                }
            }
        });
        if (getActiveUser() == getContent()) {
            button.setEnabled(false);
            button.setToolTipText("Der aktuell eingeloggte Nutzer kann nicht gelöscht werden.");
        }
        panel.add(button);

        c.anchor = GridBagConstraints.LINE_END;
        c.gridx = 1;
        add(panel, c);
    }
}
