package gui;

import data.Person;
import data.PersonsContainer;
import data.User;
import exceptions.LoadSaveException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SearchResultPersonView extends SearchResultView<Person> {

    private final JFrame frame;

    public SearchResultPersonView(JFrame frame, Person content, User user) {
        super(content, user);
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

        label = new JLabel(getContent().getName() + " (" + getContent().getEmail() + ")");
        label.setFont(GuiUtils.FONT_L);
        c.gridx = 0;
        add(label, c);

        JPanel buttons = new JPanel(new GridLayout(1, 2));
        buttons.setOpaque(false);

        button = new JButton("Details");
        button.addActionListener(evt -> new PersonDetailsDialog(frame, getContent(), getActiveUser()));
        buttons.add(button);

        button = new JButton("Löschen");
        button.addActionListener(evt -> {
            int status = JOptionPane.showConfirmDialog(this, "Soll die Person, zusammen mit allen zugehörigen Leihen, wirklich gelöscht werden?");
            if (status == JOptionPane.OK_OPTION) {
                try {
                    PersonsContainer.instance().unlinkPerson(getContent());
                } catch (LoadSaveException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                }
            }
        });
        buttons.add(button);

        c.anchor = GridBagConstraints.LINE_END;
        c.gridx = 1;
        add(buttons, c);
    }
}
