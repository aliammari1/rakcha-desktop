package com.esprit.services.series;

import com.esprit.models.series.Serie;
import com.esprit.services.series.DTO.SerieDto;
import com.esprit.utils.mydatabase;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IServiceSerieImpl implements IServiceSerie<Serie> {
    private List<Serie> seriesList;  // Assurez-vous d'initialiser cette liste
    private Connection connection;
    public IServiceSerieImpl(){
        connection= mydatabase.getInstance().getConnection();
    }
    @Override
    public void ajouter(Serie serie) throws SQLException {
        String req = "INSERT INTO series (nom, resume, directeur, pays, image, idcategorie) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setString(1, serie.getNom());
        ps.setString(2, serie.getResume());
        ps.setString(3, serie.getDirecteur());
        ps.setString(4, serie.getPays());
        ps.setString(5, serie.getImage());
        ps.setInt(6, serie.getIdcategorie());
        ps.executeUpdate();
        ps.close();
    }


    @Override
    public void modifier(Serie serie) throws SQLException {
        String req = "UPDATE series set nom = ?, resume = ?,directeur = ?, pays = ?,image = ? ,idcategorie = ? where idserie = ?;";
        PreparedStatement st = connection.prepareStatement(req);
        st.setString(1, serie.getNom());
        st.setString(2, serie.getResume());
        st.setString(3, serie.getDirecteur());
        st.setString(4, serie.getPays());
        st.setString(5,serie.getImage());
        st.setInt(6,serie.getIdcategorie());
        st.setInt(7,serie.getIdserie());
        st.executeUpdate();
        System.out.println("serie modifier avec succes");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM series WHERE idserie = ?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("serie supprimee avec succes");
    }

    @Override
    public List<SerieDto> recuperer() throws SQLException {
        List<SerieDto> serieDto = new ArrayList<>();
        String req = "SELECT * FROM series ";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(req);
        while (rs.next()){
            SerieDto s = new SerieDto();
            s.setIdserie(rs.getInt("idserie"));
            s.setNom(rs.getString("nom"));
            s.setResume(rs.getString("resume"));
            s.setDirecteur(rs.getString("directeur"));
            s.setPays(rs.getString("pays"));
            s.setImage(rs.getString("image"));
            s.setIdcategorie(rs.getInt("idcategorie"));
            String req2 = "SELECT categories.nom FROM categories WHERE idcategorie = ? ";
            PreparedStatement ps = connection.prepareStatement(req2);
            System.out.println(s.getIdcategorie());
            ps.setInt(1, s.getIdcategorie());
            ResultSet rs2 = ps.executeQuery();
            while (rs2.next()){
                s.setNomCategories(rs2.getString("nom"));
            }
            serieDto.add(s);
        }
        return serieDto;
    }

    @Override
    public List<Serie> recuperers() throws SQLException {
        List<Serie> series = new ArrayList<>();
        String req = "SELECT * FROM series";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(req);


        while (rs.next()){
            Serie serie = new Serie();
            serie.setIdserie(rs.getInt("idserie"));
            serie.setNom(rs.getString("nom"));
            serie.setResume(rs.getString("resume"));
            serie.setDirecteur(rs.getString("directeur"));
            serie.setPays(rs.getString("pays"));
            serie.setImage(rs.getString("image"));
            serie.setIdcategorie(rs.getInt("idcategorie"));

            series.add(serie);

        }
        return series;

    }

    @Override
    public List<Serie> recuperes(int id) throws SQLException {
        List<Serie> series = new ArrayList<>();
        String req = "SELECT * FROM series Where idcategorie=?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        while (rs.next()){
            Serie serie = new Serie();
            serie.setIdserie(rs.getInt("idserie"));
            serie.setNom(rs.getString("nom"));
            serie.setResume(rs.getString("resume"));
            serie.setDirecteur(rs.getString("directeur"));
            serie.setPays(rs.getString("pays"));
            serie.setImage(rs.getString("image"));
            serie.setIdcategorie(rs.getInt("idcategorie"));
            series.add(serie);
        }
        return series;
    }



///

    }



