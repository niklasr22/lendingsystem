package data;

import exceptions.IllegalInputException;

import java.time.LocalDateTime;

public class Person extends SearchResult {
    private int id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String address;
    private LocalDateTime lastModifiedDate;
    private String lastModifiedByUser;
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}";

    public Person(String firstName, String lastName, String phoneNumber, String email, String address, LocalDateTime lastModifiedDate, String lastModifiedByUser) throws IllegalInputException {
        setFirstName(firstName);
        setLastName(lastName);
        setPhoneNumber(phoneNumber);
        setEmail(email);
        setAddress(address);
        setLastModifiedDate(lastModifiedDate);
        setLastModifiedByUser(lastModifiedByUser);
    }

    public void setFirstName(String firstName) throws IllegalInputException {
        if (checkName(firstName))
            this.firstName = firstName;
        else
            throw new IllegalInputException("Der Vorname darf nicht leer sein und nicht mehr als 40 Zeichen enthalten");
    }

    public void setLastName(String lastName) throws IllegalInputException {
        if (checkName(lastName))
            this.lastName = lastName;
        else
            throw new IllegalInputException("Der Nachname darf nicht leer sein und nicht mehr als 40 Zeichen enthalten");
    }

    public void setPhoneNumber(String phoneNumber) throws IllegalInputException {
        if (checkPhoneNumber(phoneNumber))
            this.phoneNumber = phoneNumber;
        else
            throw new IllegalInputException("Ungültige Telfonnummer oder die Nummer enthält mehr als 40 Zeichen.");
    }

    public void setEmail(String email) throws IllegalInputException {
        if (checkEmail(email))
            this.email = email;
        else
            throw new IllegalInputException("Ungültige Email Adresse oder die Email enthält mehr als 50 Zeichen.");
    }

    public void setAddress(String address) throws IllegalInputException {
        if (checkAddress(address))
            this.address = address;
        else
            throw new IllegalInputException("Die Adresse darf nicht mehr als 120 Zeichen enthalten.");
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getName() {
        return this.firstName + " " + this.lastName;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getEmail() {
        return this.email;
    }

    public String getAddress() {
        return this.address;
    }

    private boolean checkName(String name) {
        return name != null && !name.isBlank() && name.length() <= 40;
    }

    private static boolean checkPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.length() <= 40 && (phoneNumber.isEmpty() || (phoneNumber.startsWith("0") || phoneNumber.startsWith("+")) && phoneNumber.matches("\\+?\\d+"));
    }

    private static boolean checkEmail(String email) {
        return email != null && email.length() <= 50 && email.matches(EMAIL_REGEX);
    }

    private boolean checkAddress(String address) {
        return address != null && address.length() <= 120;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return getName() + " (" + getEmail() + ")";
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getLastModifiedByUser() {
        return lastModifiedByUser;
    }

    public void setLastModifiedByUser(String lastModifiedByUser) {
        this.lastModifiedByUser = lastModifiedByUser;
    }
}
