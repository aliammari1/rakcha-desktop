package com.esprit.models;

import com.esprit.utils.DataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;

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
    private Blob photo_de_profil;

    public User(String firstName, String lastName, int phoneNumber, String password, String role, String address,
                Date birthDate, String email, Blob photo_de_profil) {
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

    public User(String firstName, String lastName, int phoneNumber, String password, String role, String address,
                Date birthDate, String email, String photo_de_profil_filepath) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.role = role;
        this.address = address;
        this.birthDate = birthDate;
        this.email = email;
        try {
            byte[] imageBytes = Files.readAllBytes(Path.of(photo_de_profil_filepath));
            this.photo_de_profil = DataSource.getInstance().getConnection().createBlob();
            this.photo_de_profil.setBytes(0, imageBytes);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public User(int id, String firstName, String lastName, int phoneNumber, String password, String role, String address, Date birthDate, String email, Blob photo_de_profil) {
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Blob getPhoto_de_profil() {
        return photo_de_profil;
    }

    public void setPhoto_de_profil(String photo_de_profil) {
        File file = new File(photo_de_profil);
        try (InputStream in = new FileInputStream(file)) {
            this.photo_de_profil.setBytes(file.length(), in.readAllBytes());
        } catch (SQLException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void setPhoto_de_profil(Blob photo_de_profil) {
        this.photo_de_profil = photo_de_profil;
    }

    @Override
    public String toString() {
        return "User{" +
                "photo_de_profil=" + photo_de_profil +
                ", id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber=" + phoneNumber +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", address='" + address + '\'' +
                ", birthDate=" + birthDate +
                ", email='" + email + '\'' +
                '}';
    }
}
