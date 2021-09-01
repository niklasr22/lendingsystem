package data;

import exceptions.IllegalInputException;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Lend extends SearchResult {
    private LocalDate lendDate;
    private LocalDate expectedReturnDate;
    private LocalDate returnDate;
    private Item item;
    private Person person;
    private String deposit, comment;
    private int id;
    private boolean returned;
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
        returned = this.returnDate != null;
        if (!returned) {
            item.setLend(this);
            returned = false;
        } else if(item.getLend() == this) {
            item.setLend(null);
        }
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
        return returned;
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

    @Override
    public String toString() {
        return "*" + getId() + ": " + getItem().getDescription();
    }
}
