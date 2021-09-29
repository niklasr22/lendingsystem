package data;

import exceptions.IllegalInputException;
import exceptions.LoadSaveException;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Lend extends SearchResult implements CalendarEvent {
    public final static int RESERVED = 0;
    public final static int PICKED_UP = 1;
    public final static int PICKED_UP_EXPIRED = 2;
    public final static int RETURNED = 3;

    private LocalDate lendDate;
    private LocalDate expectedReturnDate;
    private LocalDate returnDate;
    private Item item;
    private Person person;
    private String deposit, comment;
    private int id, status;
    private LocalDateTime lastModifiedDate;
    private String lastModifiedByUser;

    public Lend(Item item, Person person, LocalDate lendDate, LocalDate expectedReturnDate, LocalDate returnDate, String deposit, String comment, LocalDateTime lastModifiedDate, String lastModifiedByUser) throws Exception{
        setItem(item);
        setPerson(person);
        setLendDate(lendDate);
        setExpectedReturnDate(expectedReturnDate);
        setReturnDate(returnDate);
        setDeposit(deposit);
        setComment(comment);
        setLastModifiedDate(lastModifiedDate);
        setLastModifiedByUser(lastModifiedByUser);
        setStatus(RESERVED);
    }

    public Lend(Item item, Person person, LocalDate lendDate, LocalDate expectedReturnDate, LocalDate returnDate, String deposit, String comment, LocalDateTime lastModifiedDate, String lastModifiedByUser, int status) throws Exception{
        this(item, person, lendDate, expectedReturnDate, returnDate, deposit, comment, lastModifiedDate, lastModifiedByUser);
        setStatus(status);
    }

    private void setItem(Item item) {
        this.item = item;
    }

    private void setPerson(Person person) {
        this.person = person;
    }

    private void setLendDate(LocalDate lendDate) {
        this.lendDate = lendDate;
    }

    public void setExpectedReturnDate(LocalDate expectedReturnDate) throws IllegalInputException {
        if (!checkExpectedReturnDate(expectedReturnDate))
            throw new IllegalInputException("Ungültiges voraussichtliches Rückgabedatum");
        this.expectedReturnDate = expectedReturnDate;
    }

    public void setReturnDate(LocalDate returnDate) throws IllegalInputException {
        if (!checkReturnDate(returnDate))
            throw new IllegalInputException("Ungültiges Rückgabedatum");
        this.returnDate = returnDate;
    }

    public void returnItem() throws LoadSaveException, IllegalInputException {
        setReturnDate(LocalDate.now());
        setStatus(RETURNED);
        getItem().setAvailable(true);
        ItemsContainer.instance().modifyItem(getItem());
    }

    private boolean checkExpectedReturnDate(LocalDate expectedReturnDate) {
        return !expectedReturnDate.isBefore(getLendDate());
    }

    private boolean checkReturnDate(LocalDate returnDate) {
        return returnDate == null || !returnDate.isBefore(getLendDate());
    }

    public Item getItem() {
        return this.item;
    }

    public Person getPerson() {
        return this.person;
    }

    public LocalDate getLendDate() {
        return this.lendDate;
    }

    public LocalDate getExpectedReturnDate() {
        return this.expectedReturnDate;
    }

    public LocalDate getReturnDate() {
        return this.returnDate;
    }

    public boolean isReturned() {
        return status == RETURNED;
    }

    public void pickUpItem() throws LoadSaveException {
        setStatus(PICKED_UP);
        getItem().setAvailable(false);
        ItemsContainer.instance().modifyItem(getItem());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) throws IllegalInputException {
        if (checkDeposit(deposit))
            this.deposit = deposit;
        else
            throw new IllegalInputException("Der Eintrag für das Pfand darf nicht mehr als 100 Zeichen enthalten.");
    }

    private static boolean checkDeposit(String deposit) {
        return deposit.length() <= 100;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) throws IllegalInputException {
        if (checkComment(deposit))
            this.comment = comment;
        else
            throw new IllegalInputException("Der Kommentar darf nicht mehr als 250 Zeichen enthalten. (Aktuell: " + comment.length() + ")");
    }

    private static boolean checkComment(String comment) {
        return comment.length() <= 250;
    }

    public String getLastModifiedByUser() {
        return lastModifiedByUser;
    }

    public void setLastModifiedByUser(String lastModifiedByUser) {
        this.lastModifiedByUser = lastModifiedByUser;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    private void setStatus(int status) {
        if (status == PICKED_UP && expectedReturnDate.isBefore(LocalDate.now()))
            this.status = PICKED_UP_EXPIRED;
        else
            this.status = status;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        String status;
        switch (getStatus()) {
            case RETURNED:
                status = "abgeschlossen";
                break;
            case RESERVED:
                status = "reserviert";
                break;
            case PICKED_UP_EXPIRED:
                status = "verspätete Rückgabe";
                break;
            case PICKED_UP:
                status = "laufend";
                break;
            default:
                status = "";
        }
        return "*" + getId() + ": " + getItem().getDescription() + " (" + status + ")";
    }

    @Override
    public LocalDate getStartDate() {
        return getLendDate();
    }

    @Override
    public LocalDate getEndDate() {
        return getStatus() == RETURNED ? getReturnDate() : getExpectedReturnDate();
    }
}
