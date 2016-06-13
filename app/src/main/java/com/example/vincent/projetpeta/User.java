package com.example.vincent.projetpeta;


public class User {
	private int idUser;
	private String dateNaissance;
	private char sexe;
	private String prenom;
	private String pathPP;

	public User(int idUser, String dateNaissance, char sexe, String prenom, String pathPP) {
		this.idUser = idUser;
		this.dateNaissance = dateNaissance;
		this.sexe = sexe;
		this.prenom = prenom;
		this.pathPP = pathPP;
	}

	public User() {
		this(-1, "0000-00-00", 'M', "MISSING_NAME", "scarlett.jpg");
	}

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}

	public String getDateNaissance() {
		return dateNaissance;
	}

	public void setDateNaissance(String dateNaissance) {
		this.dateNaissance = dateNaissance;
	}

	public char getSexe() {
		return sexe;
	}

	public void setSexe(char sexe) {
		this.sexe = sexe;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getPathPP() {
		return pathPP;
	}

	public void setPathPP(String pathPP) {
		this.pathPP = pathPP;
	}


}
