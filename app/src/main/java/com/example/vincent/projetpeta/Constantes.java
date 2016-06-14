package com.example.vincent.projetpeta;

/**
 * Created by Vincent on 24/05/2016.
 */
public class Constantes {



	/* Peta repositories */
	public final static String IP = "http://192.168.43.9";
	public static final String PETA_REP = IP + "/peta/";
	public static final String PETA_SERVICES = PETA_REP + "services/";
	public static final String URL_LOGIN_SERVICE = PETA_SERVICES + "login.php";
	public static final String URL_REGISTER_SERVICE = PETA_SERVICES + "register.php";
	public static final String URL_GET_USERS_SERVICE = PETA_SERVICES + "getUsers.php";
	public static final String URL_UPLOAD = PETA_SERVICES + "upload.php";
	public static final String CONVERSATIONS_REP = PETA_REP + "conversations/";

	public static final String LOGIN = "user_login";
	public static final String PRENOM = "user_prenom";
	public static final String SEXE = "user_sexe";
	public static final String DATE = "user_date_naissance";
	public static final String INFO_USER = "user_info";
	public static final String ID_USER = "id_user";
	public static final String PATH_PP = "pathPP";
}
