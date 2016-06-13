package com.example.vincent.projetpeta;


import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
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


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.idUser);
		dest.writeString(this.dateNaissance);
		dest.writeCharArray(new char[]{this.sexe});
		dest.writeString(this.prenom);
		dest.writeString(this.pathPP);

	}
	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		@Override
		public User createFromParcel(Parcel source) {
			return new User(source);
		}

		@Override
		public User[] newArray(int size) {
			return new User[size];
		}
	};

	public User(Parcel in) {
		char[] lol = new char[1];
		this.idUser = in.readInt();
		this.dateNaissance = in.readString();
		in.readCharArray(lol);
		this.sexe = lol[0];
		this.prenom = in.readString();
		this.pathPP = in.readString();
	}

}
