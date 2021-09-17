package gui;

import data.*;
import exceptions.IllegalInputException;
import exceptions.LoadSaveException;
import store.DBConnection;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MainWindow extends JFrame {

    private User user;

    private final TextField textFieldSearch;

    private final JPanel resultsListingPanel;
    private final JPanel categoriesPanel;

    private SelectorView<SearchSetting> activeSearchSettingView = null;
    private SelectorView<Category> activeCategorySelectorView = null;

    private final PropertyChangeListener searchResultsPropertyChangeListener;

    public MainWindow(User user) {
        super("Verleihsystem");
        setUser(user);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        setLayout(new BorderLayout());

        // top bar
        JPanel topBar = new JPanel();
        add(topBar, BorderLayout.NORTH);

        textFieldSearch = new TextField();
        textFieldSearch.setPlaceholder("Suche");
        textFieldSearch.setColumns(50);
        textFieldSearch.setFont(GuiUtils.FONT_L);
        textFieldSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    search();
            }
        });
        topBar.add(textFieldSearch);

        Button btnSearch = new Button("Suche");
        btnSearch.addActionListener(evt -> search());
        topBar.add(btnSearch);

        // center panel with
        JPanel centerPanel = new JPanel();
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWeights = new double[] { 0.2, 0.6, 0.2 };
        centerPanel.setLayout(gridBagLayout);
        add(centerPanel, BorderLayout.CENTER);

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;

        JPanel searchSettingsPanel = new JPanel();
        JScrollPane searchSettingsScrollPane = new JScrollPane(searchSettingsPanel);
        searchSettingsScrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.DARK_GRAY));
        centerPanel.add(searchSettingsScrollPane, gridBagConstraints);

        resultsListingPanel = new JPanel();
        resultsListingPanel.setLayout(new BoxLayout(resultsListingPanel, BoxLayout.Y_AXIS));
        JScrollPane resultsScrollPane = new JScrollPane(resultsListingPanel);
        resultsScrollPane.setBorder(new LineBorder(Color.DARK_GRAY, 1));
        centerPanel.add(resultsScrollPane, gridBagConstraints);

        categoriesPanel = new JPanel();
        JScrollPane categoriesScrollPane = new JScrollPane(categoriesPanel);
        categoriesScrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.DARK_GRAY));
        centerPanel.add(categoriesScrollPane, gridBagConstraints);

        // baseline bar with buttons
        JPanel bottomButtonsBar = new JPanel();
        add(bottomButtonsBar, BorderLayout.SOUTH);

        Button btnLogout = new Button("Abmelden");
        btnLogout.addActionListener(e -> logout());
        bottomButtonsBar.add(btnLogout);

        Button btnExport = new Button("Exportieren");
        btnExport.addActionListener(e -> new ExportDialog(this));
        bottomButtonsBar.add(btnExport);

        Button btnNewCategory = new Button("Neue Kategorie");
        btnNewCategory.addActionListener(e -> new CategoryDialog(this));
        bottomButtonsBar.add(btnNewCategory);

        Button btnNewArticle = new Button("Neuer Artikel");
        btnNewArticle.addActionListener(e -> new ArticleDialog(this, getUser()));
        bottomButtonsBar.add(btnNewArticle);

        if (user.isAdmin()) {
            Button btnNewUser = new Button("Neuer Nutzer");
            btnNewUser.addActionListener(e -> new UserDialog(this, false));
            bottomButtonsBar.add(btnNewUser);
        }

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    DBConnection.instance().close();
                } catch (LoadSaveException loadSaveException) {
                    loadSaveException.printStackTrace();
                }
            }
        });

        searchResultsPropertyChangeListener = evt -> {
            try {
                updateSearchResults();
            } catch (LoadSaveException e) {
                e.printStackTrace();
            }
        };

        try {
            initSearchSettings(searchSettingsPanel);
            initCategories(categoriesPanel);
        } catch (LoadSaveException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

        setMinimumSize(new Dimension(1000, 800));
        setSize(1000, 800);
        setVisible(true);
    }

    private void initSearchSettings(JPanel parent) throws LoadSaveException {
        parent.setLayout(new BoxLayout(parent, BoxLayout.Y_AXIS));

        List<SearchSetting> searchSettingList = new ArrayList<>();
        searchSettingList.add(new SearchSetting("Artikel", ItemsContainer.instance(), true) {
            @Override
            public List<? extends SearchResult> listAll() throws LoadSaveException {
                if (getActiveCategory() == null ||getActiveCategory().getId() == -1)
                    return ItemsContainer.instance().getItems();
                else
                    return ItemsContainer
                            .instance()
                            .getItems()
                            .stream()
                            .filter(i -> i.getCategory() == getActiveCategory())
                            .collect(Collectors.toList());
            }

            @Override
            public List<? extends SearchResult> search(String search) throws LoadSaveException {
                String searchLC = search.toLowerCase();
                return ItemsContainer
                        .instance()
                        .getItems()
                        .stream()
                        .filter(i -> (getActiveCategory() == null || getActiveCategory().getId() == -1 || i.getCategory() == getActiveCategory()) && (("#" + i.getInventoryNumber()).contains(searchLC) || i.getDescription().toLowerCase().contains(searchLC)))
                        .collect(Collectors.toList());
            }
        });

        Comparator<Lend> lendComparator = (l1, l2) -> {
            if (l1.isReturned() && l2.isReturned()) {
                if (l1.getReturnDate().isBefore(l2.getReturnDate()))
                    return -1;
                else if (l1.getReturnDate().isEqual(l2.getReturnDate()))
                    return 0;
                else
                    return 1;
            } else if (!l1.isReturned() && !l2.isReturned()) {
                if (l1.getExpectedReturnDate().isBefore(l2.getExpectedReturnDate()))
                    return -1;
                else if(l1.getExpectedReturnDate().isEqual(l2.getExpectedReturnDate()))
                    return 0;
                else
                    return  1;
            } else {
                return l1.isReturned() ? 1 : -1;
            }
        };
        searchSettingList.add(new SearchSetting("Leihen", LendsContainer.instance(), true) {
            @Override
            public List<? extends SearchResult> listAll() throws LoadSaveException {
                if (getActiveCategory() == null || getActiveCategory().getId() == -1)
                    return LendsContainer
                            .instance()
                            .getLends()
                            .stream()
                            .sorted(lendComparator)
                            .collect(Collectors.toList());
                else
                    return LendsContainer
                            .instance()
                            .getLends()
                            .stream()
                            .filter(l -> l.getItem().getCategory() == getActiveCategory())
                            .sorted(lendComparator)
                            .collect(Collectors.toList());
            }

            @Override
            public List<? extends SearchResult> search(String search) throws LoadSaveException {
                String searchLC = search.toLowerCase();
                return LendsContainer
                        .instance()
                        .getLends()
                        .stream()
                        .filter(l -> (getActiveCategory() == null
                                        || getActiveCategory().getId() == -1
                                        || l.getItem().getCategory() == getActiveCategory()) &&
                                (("#" + l.getItem().getInventoryNumber()).contains(searchLC)
                                        || ("*" + l.getId()).contains(searchLC)
                                        || l.getPerson().getEmail().toLowerCase().contains(searchLC)
                                        || l.getPerson().getName().toLowerCase().contains(searchLC)
                                        || l.getItem().getDescription().toLowerCase().contains(searchLC)))
                        .sorted(lendComparator)
                        .collect(Collectors.toList());
            }
        });

        searchSettingList.add(new SearchSetting("Personen", PersonsContainer.instance(), false) {
            @Override
            public List<? extends SearchResult> listAll() throws LoadSaveException {
                return PersonsContainer.instance().getPersons();
            }

            @Override
            public List<? extends SearchResult> search(String search) throws LoadSaveException {
                String searchLC = search.toLowerCase();
                return PersonsContainer
                        .instance()
                        .getPersons()
                        .stream()
                        .filter(p -> p.getEmail().toLowerCase().contains(searchLC) || p.getName().toLowerCase().contains(searchLC))
                        .collect(Collectors.toList());
            }
        });

        searchSettingList.add(new SearchSetting("Kategorien", CategoriesContainer.instance(), false) {
            @Override
            public List<? extends SearchResult> listAll() throws LoadSaveException {
                return CategoriesContainer.instance().getCategories();
            }

            @Override
            public List<? extends SearchResult> search(String search) throws LoadSaveException {
                String searchLC = search.toLowerCase();
                return CategoriesContainer
                        .instance()
                        .getCategories()
                        .stream()
                        .filter(c -> c.getName().toLowerCase().contains(searchLC))
                        .collect(Collectors.toList());
            }
        });

        if (getUser().isAdmin())
            searchSettingList.add(new SearchSetting("Nutzer", UsersContainer.instance(), false) {
                @Override
                public List<? extends SearchResult> listAll() throws LoadSaveException {
                    return UsersContainer.instance().getUsers();
                }

                @Override
                public List<? extends SearchResult> search(String search) throws LoadSaveException {
                    String searchLC = search.toLowerCase();
                    return UsersContainer
                            .instance()
                            .getUsers()
                            .stream()
                            .filter(u -> u.getName().toLowerCase().contains(searchLC) && u.getUsername().toLowerCase().contains(searchLC))
                            .collect(Collectors.toList());
                }
            });

        searchSettingList.forEach(searchSetting -> {
            SelectorView<SearchSetting> searchSettingView = new SelectorView<>(searchSetting);
            searchSettingView.addActionListener(e -> setActiveSearchSettingView(searchSettingView));
            parent.add(searchSettingView);
            if (getActiveSearchSettingView() == null)
                setActiveSearchSettingView(searchSettingView);
        });
    }

    private void initCategories(JPanel parent) throws LoadSaveException {
        parent.setLayout(new BoxLayout(parent, BoxLayout.Y_AXIS));

        CategoriesContainer categories = CategoriesContainer.instance();
        categories.addPropertyChangeListener(evt -> updateCategories());

        updateCategories();
    }

    private void updateCategories() {
        categoriesPanel.removeAll();
        setActiveCategorySelectorView(null);
        try {
            ArrayList<Category> categories = new ArrayList<>(CategoriesContainer.instance().getCategories());
            categories.add(0, new Category("Alle"));
            categories.forEach(category -> {
                SelectorView<Category> categorySelectorView = new SelectorView<>(category);
                categorySelectorView.addActionListener(e -> setActiveCategorySelectorView(categorySelectorView));
                categoriesPanel.add(categorySelectorView);
                if (getActiveCategorySelectorView() == null)
                    setActiveCategorySelectorView(categorySelectorView);
            });
        } catch (LoadSaveException | IllegalInputException e) {
            e.printStackTrace();
        }
        categoriesPanel.updateUI();
    }

    private void search() {
        try {
            List<? extends SearchResult> searchResults = getActiveSearchSetting().search(textFieldSearch.getText());
            listSearchResults(searchResults);
        } catch (LoadSaveException loadSaveException) {
            loadSaveException.printStackTrace();
        }
    }


    private void updateSearchResults() throws LoadSaveException {
        if (getActiveSearchSetting() != null && getActiveCategory() != null)
            listSearchResults(getActiveSearchSetting().listAll());
    }

    private void listSearchResults(List<? extends SearchResult> searchResultList) {
        resultsListingPanel.removeAll();
        if (searchResultList != null)
            searchResultList.forEach(searchResult -> {
                SearchResultView<? extends SearchResult> searchResultItemView;
                if (searchResult instanceof Item)
                    searchResultItemView = new SearchResultItemView(this, (Item) searchResult, getUser());
                else if (searchResult instanceof Category)
                    searchResultItemView = new SearchResultCategoryView(this, (Category) searchResult);
                else if (searchResult instanceof User)
                    searchResultItemView = new SearchResultUserView(this, (User) searchResult, user);
                else if (searchResult instanceof Lend)
                    searchResultItemView = new SearchResultLendView(this, (Lend) searchResult, user);
                else if (searchResult instanceof Person)
                    searchResultItemView = new SearchResultPersonView(this, (Person) searchResult, user);
                else
                    return;
                resultsListingPanel.add(searchResultItemView);
            });
        resultsListingPanel.updateUI();
    }

    private SelectorView<SearchSetting> getActiveSearchSettingView() {
        return activeSearchSettingView;
    }

    private void setActiveSearchSettingView(SelectorView<SearchSetting> searchSettingView) {
        if (activeSearchSettingView != null) {
            getActiveSearchSetting().getAssociatedContainer().removePropertyChangeListener(searchResultsPropertyChangeListener);
            activeSearchSettingView.setActive(false);
        }
        activeSearchSettingView = searchSettingView;
        if (activeSearchSettingView != null)
            activeSearchSettingView.setActive(true);
        categoriesPanel.setVisible(getActiveSearchSetting().usesCategories());
        try {
            updateSearchResults();
        } catch (LoadSaveException e) {
            e.printStackTrace();
        }
        getActiveSearchSetting().getAssociatedContainer().addPropertyChangeListener(searchResultsPropertyChangeListener);
    }

    private SearchSetting getActiveSearchSetting() {
        return activeSearchSettingView.getLinkedObject();
    }

    private SelectorView<Category> getActiveCategorySelectorView() {
        return activeCategorySelectorView;
    }

    private void setActiveCategorySelectorView(SelectorView<Category> searchSettingView) {
        if (activeCategorySelectorView != null)
            activeCategorySelectorView.setActive(false);
        activeCategorySelectorView = searchSettingView;
        if (activeCategorySelectorView != null)
            activeCategorySelectorView.setActive(true);
        try {
            updateSearchResults();
        } catch (LoadSaveException e) {
            e.printStackTrace();
        }
    }

    private Category getActiveCategory() {
        if (activeCategorySelectorView != null)
            return activeCategorySelectorView.getLinkedObject();
        return null;
    }

    public void setUser(User user) {
        if (user != null)
            this.user = user;
        else
            throw new IllegalArgumentException();
    }

    public User getUser() {
        return user;
    }

    private void logout() {
        new LoginWindow();
        dispose();
    }
}
