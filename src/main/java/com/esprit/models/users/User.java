package com.esprit.models.users;

import java.sql.Date;

public abstract class User {
    private int id;
    private String firstName;
    private String lastName;
    private int phoneNumber;
    private String password;
    private String role;
    private String address;
    private Date birthDate;
    private String email;
    private String photo_de_profil;

    protected User(final String firstName, final String lastName, final int phoneNumber, final String password, final String role, final String address,
                   final Date birthDate, final String email, final String photo_de_profil) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.role = role;
        this.address = address;
        this.birthDate = birthDate;
        this.email = email;
        this.photo_de_profil = photo_de_profil;
    }

    protected User(final int id, final String firstName, final String lastName, final int phoneNumber, final String password, final String role, final String address, final Date birthDate, final String email, final String photo_de_profil) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.role = role;
        this.address = address;
        this.birthDate = birthDate;
        this.email = email;
        this.photo_de_profil = photo_de_profil;
    }

    /**
     * @return int
     */
    public int getId() {
        return this.id;
    }

    /**
     * @param id
     */
    public void setId(final int id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public int getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(final int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(final String role) {
        this.role = role;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public Date getBirthDate() {
        return this.birthDate;
    }

    public void setBirthDate(final Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPhoto_de_profil() {
        return this.photo_de_profil;
    }

    public void setPhoto_de_profil(final String photo_de_profil) {
        this.photo_de_profil = photo_de_profil;
    }

    @Override
    public String toString() {
        return
                "id=" + this.id
                        + ", firstName='" + this.firstName + '\''
                        + ", lastName='" + this.lastName + '\''
                        + ", phoneNumber=" + this.phoneNumber
                        + ", password='" + this.password + '\''
                        + ", role='" + this.role + '\''
                        + ", address='" + this.address + '\''
                        + ", birthDate=" + this.birthDate
                        + ", email='" + this.email + '\''
                        + ", photo_de_profil='" + this.photo_de_profil;
    }
}
