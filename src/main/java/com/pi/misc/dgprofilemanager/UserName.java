package com.pi.misc.dgprofilemanager;

import org.json.JSONException;
import org.json.JSONObject;

public class UserName {

	private String formatted;
	private String familyName;
	private String givenName;

	public UserName() {
		super();
	}

	public UserName(String formatted, String familyName, String givenName) {
		super();
		this.formatted = formatted;
		this.familyName = familyName;
		this.givenName = givenName;
	}

	public UserName(JSONObject userObject) throws JSONException {
		JSONObject name = (JSONObject) userObject.get("name");
		setFamilyName(name.getString("familyName"));
		setGivenName(name.getString("givenName"));
		setFormatted(name.getString ("formatted"));
	}

	public String getFormatted() {
		return formatted;
	}

	public void setFormatted(String formatted) {
		this.formatted = formatted;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	
	public JSONObject returnJsonObject () throws JSONException {
		JSONObject nameObject = new JSONObject();
		nameObject.put("formatted", getFormatted());
		nameObject.put("familyName", getFamilyName());
		nameObject.put("givenName", getGivenName());
		return nameObject;
	}

}
