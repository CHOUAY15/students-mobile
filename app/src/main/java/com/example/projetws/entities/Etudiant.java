package com.example.projetws.entities;

import java.io.Serializable;

public class Etudiant implements Serializable {
    private Integer id;
    private String nom;
    private String prenom;
    private String ville;
    private String sexe;
    private String imageUrl;

    // Base URL for images - you can put this in a constants file
    private static final String BASE_IMAGE_URL = "http://10.0.2.2:4000/api/images/";

    // Constructor
    public Etudiant() {
    }

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Helper method to get the complete image URL
    public String getFullImageUrl() {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            return BASE_IMAGE_URL + imageUrl;
        }
        return null;
    }
}