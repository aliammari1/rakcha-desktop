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
    public User(String firstName, String lastName, int phoneNumber, String password, String role, String address,
                Date birthDate, String email, String photo_de_profil) {
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
    public User(int id, String firstName, String lastName, int phoneNumber, String password, String role, String address, Date birthDate, String email, String photo_de_profil) {
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
        return id;
    }
    /** 
     * @param id
     */
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
    public String getPhoto_de_profil() {
        return photo_de_profil;
    }
    public void setPhoto_de_profil(String photo_de_profil) {
        this.photo_de_profil = photo_de_profil;
    }
    @Override
    public String toString() {
        return
                "id=" + id +
                        ", firstName='" + firstName + '\'' +
                        ", lastName='" + lastName + '\'' +
                        ", phoneNumber=" + phoneNumber +
                        ", password='" + password + '\'' +
                        ", role='" + role + '\'' +
                        ", address='" + address + '\'' +
                        ", birthDate=" + birthDate +
                        ", email='" + email + '\'' +
                        ", photo_de_profil='" + photo_de_profil;
    }
}
