package com.esprit.models.produits;

import java.util.Objects;

public class Panier {



        private int idpanier;
        private int quantity;

        private Produit produit;

        private Users users;


        public Panier(){

        }
        public Panier(int idpanier, Produit produit,int quantity , Users users) {
            this.idpanier = idpanier;
            this.quantity = quantity;
            this.produit = produit;
            this.users = users;

        }

        public Panier(Produit produit,int quantity,Users users) {
            this.quantity = quantity;
            this.produit = produit;
            this.users = users;
        }


        public int getIdPanier() {
            return idpanier;
        }

        public void setIdPanier(int idPanier) {
            this.idpanier = idPanier;
        }

        public int getIdpanier() {
            return idpanier;
        }

        public void setIdpanier(int idpanier) {
            this.idpanier = idpanier;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public Produit getProduit() {
            return produit;
        }

        public void setProduit(Produit produit) {
            this.produit = produit;
        }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Panier{" +
                "idpanier=" + idpanier +
                ", quantity=" + quantity +
                ", produit=" + produit +
                ", users=" + users +
                '}';
    }
}
