package gui;

import data.SearchResult;
import data.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public abstract class SearchResultView<E extends SearchResult> extends JPanel {
    private E content;
    private final User activeUser;

    public SearchResultView(E content) {
        this(content, null);
    }

    public SearchResultView(E content, User activeUser) {
        setContent(content);
        this.activeUser = activeUser;
        createLayout();
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY),
                new EmptyBorder(10, 10, 10, 10)));
    }

    protected abstract void createLayout();

    private void setContent(E content) {
        this.content = content;
    }

    public E getContent() {
        return content;
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(super.getMaximumSize().width, getPreferredSize().height);
    }

    public User getActiveUser() {
        return activeUser;
    }
}
