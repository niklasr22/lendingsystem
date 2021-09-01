package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class SelectorView<E> extends JPanel {

    private final E linkedObject;
    private final List<ActionListener> actionListeners;

    private boolean active = false;

    private Color defaultBackgroundColor;
    private Color hoverBackgroundColor = Color.LIGHT_GRAY;
    private Color activeBackgroundColor = Color.LIGHT_GRAY;

    public SelectorView(E linkedObject) {
        super();
        if (linkedObject == null)
            throw new IllegalArgumentException();
        this.linkedObject = linkedObject;
        actionListeners = new ArrayList<>();
        defaultBackgroundColor = getBackground();

        setLayout(new FlowLayout());

        JLabel labelSettingName = new JLabel(linkedObject.toString());
        add(labelSettingName);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ActionEvent actionEvent = new ActionEvent(SelectorView.this, ActionEvent.ACTION_PERFORMED, "SearchSettingViewClicked");
                actionListeners.forEach(actionListener -> actionListener.actionPerformed(actionEvent));

                super.mouseClicked(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                SelectorView.super.setBackground(getHoverBackgroundColor());
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                if (isActive())
                    SelectorView.super.setBackground(getActiveBackgroundColor());
                else
                    SelectorView.super.setBackground(getDefaultBackgroundColor());
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    public void setHoverBackgroundColor(Color hoverBackgroundColor) {
        this.hoverBackgroundColor = hoverBackgroundColor;
    }

    public Color getHoverBackgroundColor() {
        return hoverBackgroundColor;
    }

    public void setActiveBackgroundColor(Color activeBackgroundColor) {
        this.activeBackgroundColor = activeBackgroundColor;
    }

    public Color getActiveBackgroundColor() {
        return activeBackgroundColor;
    }

    public Color getDefaultBackgroundColor() {
        return defaultBackgroundColor;
    }

    @Override
    public void setBackground(Color bg) {
        defaultBackgroundColor = bg;
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(super.getMaximumSize().width, getPreferredSize().height);
    }

    public E getLinkedObject() {
        return linkedObject;
    }

    public void addActionListener(ActionListener actionListener) {
        if (actionListener != null)
            actionListeners.add(actionListener);
    }

    public void deleteActionListener(ActionListener actionListener) {
        if (actionListener != null)
            actionListeners.remove(actionListener);
    }

    public void setActive(boolean active) {
        this.active = active;
        super.setBackground(active ? activeBackgroundColor : defaultBackgroundColor);
    }

    public boolean isActive() {
        return active;
    }
}