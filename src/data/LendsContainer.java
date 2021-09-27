package data;

import exceptions.IllegalInputException;
import exceptions.LoadSaveException;
import store.DataManagement;
import store.PersistenceLendsDB;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

public class LendsContainer extends Container implements Iterable<Lend> {
    private static LendsContainer unique = null;
    private final ArrayList<Lend> lends;
    private final DataManagement<LendsContainer, Lend> store;

    private LendsContainer() throws LoadSaveException {
        super();
        this.lends = new ArrayList<>();

        PersonsContainer.instance();

        store = new PersistenceLendsDB();
        store.load(this);
    }

    public static LendsContainer instance() throws LoadSaveException {
        return unique == null ? unique = new LendsContainer() : unique;
    }

    public boolean linkLendLoading(Lend lend) throws IllegalInputException {
        if (lend == null)
            return false;
        if (lends.contains(lend))
            throw new IllegalInputException("Leihe bereits vorhanden");
        lends.add(lend);
        lend.getItem().linkLend(lend);
        return true;
    }

    public void linkLend(Lend lend) throws IllegalInputException, LoadSaveException {
        if (linkLendLoading(lend)) {
            store.add(lend);
            propertyChangeSupport.firePropertyChange("lends", null, lend);
        }
    }

    public void unlinkLend(Lend lend) throws LoadSaveException {
        if (lends.contains(lend)) {
            if (!lend.isReturned() && !lend.getLendDate().isAfter(LocalDate.now()))
                throw new LoadSaveException("Eine Leihe muss zurückgegeben werden, bevor sie gelöscht werden kann.", null);
            if (!lend.isReturned()) {
                lend.getItem().unlinkLend(lend);
                ItemsContainer.instance().modifyItem(lend.getItem());
            }
            store.delete(lend);
            lends.remove(lend);
            propertyChangeSupport.firePropertyChange("lends", lend, null);
        }
    }

    public void modifyLend(Lend lend) throws LoadSaveException {
        store.modify(lend);
        propertyChangeSupport.firePropertyChange("lends", null, lend);
    }

    public ArrayList<Lend> getLends() {
        return lends;
    }

    @Override
    public Iterator<Lend> iterator() {
        return lends.iterator();
    }
}
