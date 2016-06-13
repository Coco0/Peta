package com.example.vincent.projetpeta;

/**
 * @author Vincent
 */
public class VideoFile {
	private final int idMiniature;
	private String name;
	private String date;
	private int idSender;

	public VideoFile(String name, String date, int idSender, int idMiniature) {
		this.name = name;
		this.date = date;
		this.idSender = idSender;
		this.idMiniature = idMiniature;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getIdSender() {
		return idSender;
	}

	public void setIdSender(int idSender) {
		this.idSender = idSender;
	}

	public int getIdMiniature() {
		return idMiniature;
	}
}
