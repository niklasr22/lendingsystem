package gui;

import data.CategoriesContainer;
import data.Category;
import exceptions.LoadSaveException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SearchResultCategoryView extends SearchResultView<Category> {

    public SearchResultCategoryView(JFrame frame, Category content) {
        super(content);
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
        label.setFont(GuiUtils.FONT_L);
        c.gridx = 0;
        add(label, c);

        button = new JButton("Löschen");
        button.addActionListener(evt -> {
            int status = JOptionPane.showConfirmDialog(this, "Soll die Kategorie inklusive aller damit assoziierten Artikel gelöscht werden?");
            if (status == JOptionPane.OK_OPTION) {
                try {
                    CategoriesContainer.instance().unlinkCategory(getContent());
                } catch (LoadSaveException e) {
                    JOptionPane.showMessageDialog(this, "Kategorie konnte nicht gelöscht werden (" + e.getMessage() + ")");
                }
            }
        });
        c.anchor = GridBagConstraints.LINE_END;
        c.gridx = 1;
        add(button, c);
    }
}
