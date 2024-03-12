package com.esprit.models.evenements;

import com.esprit.models.users.Client;

public class Feedback {

    String commentaire;
    private int IDFeedback;
    private Client userevenement;
    private Evenement feedbackevenement;

    public Feedback(int IDFeedback, Evenement feedbackevenement, Client userevenement, String commentaire) {
        this.IDFeedback = IDFeedback;
        this.feedbackevenement = feedbackevenement;
        this.userevenement = userevenement;
        this.commentaire = commentaire;
    }

    public Feedback(Evenement feedbackevenement, Client userevenement, String commentaire) {
        this.feedbackevenement = feedbackevenement;
        this.userevenement = userevenement;
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

    public Client getUserevenement() {
        return userevenement;
    }

    public void setUserevenement(Client userevenement) {
        this.userevenement = userevenement;
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
                ", id_user=" + userevenement +
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
