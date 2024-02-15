package com.esprit.tests;

import com.esprit.services.UserService;

public class Main {
    public static void main(String[] args) {
        UserService userService = new UserService();
        // User sponsor = new Sponsor("alisponsoring", "sponsor", 1000, "password", "sponsor", "ariana",
        //        new Date(108, 2, 2), "sponsor@rakcha.tn", "C:\\Users\\AliAMMARI\\Downloads\\cinema.jpg");
        //
        // User admin = new Admin("John", "admin", 1000, "password", "admin", "ariana",
        //        new Date(108, 2, 2), "admin@rakcha.tn", "C:\\Users\\AliAMMARI\\Downloads\\cinema.jpg");
        //
        // User responsable_de_cinema = new Responsable_de_cinema("John", "resp", 1000,
        // "password",
        // "responsable de cinema", "ariana",
        // new Date(100, 8, 9), "responsable_de_cinema@rakcha.tn",
        // "C:\\Users\\AliAMMARI\\Downloads\\cinema.jpg");
        //
        // User client = new Client("John", "client", 1000, "password", "client",
        // "ariana",
        // new Date(101, 7, 6), "client@rakcha.tn",
        // "C:\\Users\\AliAMMARI\\Downloads\\cinema.jpg");
        //
        // userService.create(sponsor);
        // userService.create(admin);
        // userService.create(responsable_de_cinema);
        // userService.create(client);
        //
        // userService.read();
        //
        // client.setNom("ali");
        // userService.update(client);
        //
        // userService.delete(sponsor);
        //
        // userService.sendMail("ali.ammari@esprit.tn", "Testing the javamail API...");
        userService.generateUserPDF();
    }
}
