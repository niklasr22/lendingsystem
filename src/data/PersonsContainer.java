package data;

import exceptions.IllegalInputException;
import exceptions.LoadSaveException;
import store.DataManagement;
import store.PersistencePersonsDB;

import java.util.ArrayList;
import java.util.Iterator;

public class PersonsContainer extends Container implements Iterable<Person> {
    private static PersonsContainer unique = null;
    private final ArrayList<Person> persons;
    private final DataManagement<PersonsContainer, Person> store;

    private PersonsContainer() throws LoadSaveException {
        super();
        persons = new ArrayList<>();
        store = new PersistencePersonsDB();
        store.load(this);
    }

    public static PersonsContainer instance() throws LoadSaveException {
        return unique == null ? unique = new PersonsContainer() : unique;
    }

    public boolean linkPersonLoading(Person person) throws IllegalInputException {
        if (person == null)
            return false;
        if (this.persons.contains(person))
            throw new IllegalInputException("Person bereits vorhanden");
        persons.add(person);
        propertyChangeSupport.firePropertyChange("persons", null, person);
        return true;
    }

    public void linkPerson(Person person) throws IllegalInputException, LoadSaveException {
        if (linkPersonLoading(person)) {
            store.add(person);
        }
    }

    public void unlinkPerson(Person person) throws LoadSaveException {
        if (persons.contains(person)) {

            LendsContainer lendsContainer = LendsContainer.instance();
            ArrayList<Lend> lends = new ArrayList<>(lendsContainer.getLends());
            for (Lend lend : lends) {
                if (lend.getPerson() == person)
                    lendsContainer.unlinkLend(lend);
            }

            store.delete(person);
            persons.remove(person);
            propertyChangeSupport.firePropertyChange("persons", person, null);
        }
    }

    public void modifyPerson(Person person) throws LoadSaveException {
        store.modify(person);
        propertyChangeSupport.firePropertyChange("persons", null, person);
    }

    public Person searchPerson(int id) {
        for (Person person : persons) {
            if (person.getId() == id)
                return person;
        }
        return null;
    }

    public Person searchPerson(String email) {
        for (Person person : persons) {
            if (person.getEmail().equals(email))
                return person;
        }
        return null;
    }

    @Override
    public Iterator<Person> iterator() {
        return persons.iterator();
    }

    public ArrayList<Person> getPersons() {
        return persons;
    }
}
