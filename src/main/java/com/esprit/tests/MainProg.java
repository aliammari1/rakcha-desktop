package com.esprit.tests;


import com.esprit.models.Produit;
import com.esprit.services.ProduitService;
import com.esprit.models.Categorie;
import com.esprit.services.CategorieService;
import com.esprit.utils.DataSource;

public class MainProg {
    public static void main(String[] args) {
        ProduitService p = new ProduitService();


       // p.create(new Produit("tableau1","100dt","tableau","tableau",new Categorie(1),10));
       //p.update(new Produit(3,"tableau1","200dt","tableau","tableau",new Categorie(1,"tableau","tableau"),10));
        p.read();
        //CategorieService c = new CategorieService();

       // c.create(new Categorie("tableau","tableau"));
        //c.create(new Categorie("stickers","stickers"));
    }


}
