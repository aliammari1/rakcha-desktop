package com.esprit.models.evenements;

public class Feedback {

    String commentaire;
    private int IDFeedback;
    private int userevenement;
    private Evenement feedbackevenement;

    public Feedback(int IDFeedback, Evenement feedbackevenement, int userevenement, String commentaire) {
        this.IDFeedback = IDFeedback;
        this.feedbackevenement = feedbackevenement;
        this.userevenement = userevenement;
        this.commentaire = commentaire;
    }

    public Feedback(Evenement feedbackevenement, int userevenement, String commentaire) {
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

    public int getUserevenement() {
        return userevenement;
    }

    public void setUserevenement(int userevenement) {
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
