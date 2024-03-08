package com.esprit.models.evenements;

public class Feedback {

    int IDFeedback;
    int id_user;
    String commentaire;
    private Evenement feedbackevenement;

    public Feedback(int IDFeedback, Evenement feedbackevenement, int id_user, String commentaire) {
        this.IDFeedback = IDFeedback;
        this.feedbackevenement = feedbackevenement;
        this.id_user = id_user;
        this.commentaire = commentaire;
    }

    public Feedback(Evenement feedbackevenement, int id_user, String commentaire) {
        this.feedbackevenement = feedbackevenement;
        this.id_user = id_user;
        this.commentaire = commentaire;
    }

    public int getIDFeedback() {
        return IDFeedback;
    }

    public void setIDFeedback(int IDFeedback) {
        this.IDFeedback = IDFeedback;
    }

    public Evenement getFeedbackevenement() {
        return feedbackevenement;
    }

    public void setFeedbackevenement(Evenement feedbackevenement) {
        this.feedbackevenement = feedbackevenement;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "IDFeedback=" + IDFeedback +
                ", feedbackevenement=" + feedbackevenement +
                ", id_user=" + id_user +
                ", commentaire='" + commentaire + '\'' +
                '}';
    }

    public Evenement getID_f() {
        return feedbackevenement;
    }

    public String getNom_Evenement() {
        return feedbackevenement.getNom();
    }
}
