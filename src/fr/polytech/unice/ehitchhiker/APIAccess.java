/**
 * APIAccess.java, Created 25 janv. 2011 at 11:40:44
 *
 * Project : ehitchhikerAndroid
 * Package : fr.polytech.unice.ehitchhiker
 * 
 * Author : Christophe Dupont
 * Mail : mail@christophedupont.com
 * 
 */
package fr.polytech.unice.ehitchhiker;


import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kinder.utils.WebClient;


/**
 * @author kinder
 *
 */
public class APIAccess {
	public enum Response {
	    CONNEXION_ERROR, CONNEXION_NEED_INSCRIPTION, CONNEXION_OK, INSCRIPTION_DEJA_INSCRIT,
	    INSCRIPTION_PSEUDO_DEJA_PRIS, INSCRIPTION_OK, INSCRIPTION_ERROR
	}
	
	private static APIAccess mInstance;
	
	
	
	private APIAccess(){
		
	}
	
	public static APIAccess get(){
		if(null==mInstance){
			mInstance = new APIAccess();
		}
		
		return mInstance;
	}
	
	
	public Response sendConnectionRequestWithGoogleAccount(String google){
		JSONObject obj = new JSONObject();
		try {
			obj.put("CONNEXION", google);
			
			
			try {
				JSONObject jsonRes = WebClient.getJSONObjectFromPostJSONToUrl(obj, Constants.URL_CONNEXION_DECONNEXION);
				
				if(jsonRes.getString("CONNEXION").equals("REUSSIE")) return Response.CONNEXION_OK;
				if(jsonRes.getString("CONNEXION").equals("UTILISATEUR_INCONNU")) return Response.CONNEXION_NEED_INSCRIPTION;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Response.CONNEXION_ERROR ;
		
	}
	
	public ArrayList<Destination> getGoogleMapsSuggestionList(String address) {
		ArrayList<Destination> suggestionList = new ArrayList<Destination>();
		 JSONObject jsonRes = null;
		try {
			jsonRes  = WebClient.getJSONFromUrl(Constants.URL_GOOGLE_MAPS + address + "&sensor=true&language=fr");
			if (!jsonRes.getString("status").equals("OK")) {
				suggestionList.add(new Destination("Aucun r�sultat", 0, 0));
				return suggestionList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		JSONArray results = null;
		try {
			results  = jsonRes.getJSONArray("results");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (int i=0; i<results.length(); i++){
			try {
				String formatted_address = results.getJSONObject(i).getString("formatted_address");
				double longitude = results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
				double latitude = results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
				suggestionList.add(new Destination(formatted_address, latitude, longitude));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return suggestionList;
	}
	
	
	public Response sendInscriptionRequest(String utilisateur, String pseudo, String genre, String dateNaissance, String permis, ArrayList<Car> voitures){
		JSONArray cars = new JSONArray();
		for(int i=0; i<voitures.size(); i++){
			JSONObject v = new JSONObject();
			try {
				v.put("marque", voitures.get(i).marque);
				v.put("modele", voitures.get(i).modele);
				v.put("couleur", voitures.get(i).couleur);
				v.put("consommation", voitures.get(i).consommation);
				v.put("photo", voitures.get(i).photo);
				cars.put(v);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		JSONObject obj = new JSONObject();
		try {
			JSONObject inscrip = new JSONObject();
			inscrip.put("utilisateur", utilisateur);
			inscrip.put("pseudo", pseudo);
			inscrip.put("genre", genre);
			inscrip.put("dateNaissance", dateNaissance);
			inscrip.put("permis", permis);
			inscrip.put("voitures", cars);
			
			obj.put("INSCRIPTION", inscrip);
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		try {
			JSONObject jsonRes = WebClient.getJSONObjectFromPostJSONToUrl(obj, Constants.URL_INSCRIPTION);
			
			if(jsonRes.getString("INSCRIPTION").equals("UTILISATEUR_DEJA_ENREGISTRE")) return Response.INSCRIPTION_DEJA_INSCRIT;
			if(jsonRes.getString("INSCRIPTION").equals("PSEUDO_DEJA_PRIS")) return Response.INSCRIPTION_PSEUDO_DEJA_PRIS;
			return Response.INSCRIPTION_OK;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Response.INSCRIPTION_ERROR;
	}
}
