package com.pi.misc.dgprofilemanager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

public class ProtocolHelper {

	public static String buildAuthorizationUrl(String authorizationEndPoint, String clientId, String redirectUri, String scope) {
		String authorizationUrl = authorizationEndPoint + "?";
		authorizationUrl += "client_id=" + urlEncodeParameter(clientId);
		authorizationUrl += "&response_type=code";
		authorizationUrl += "&redirect_uri=" + urlEncodeParameter(redirectUri);
		authorizationUrl += "&scope=" + scope;
		return authorizationUrl;
	}

	public static String getAccessToken(String tokenEndPoint, String code, String clientId, String clientSecret, String redirectUri)
			throws JSONException, ProtocolException, IOException {

		String tokenUrl = tokenEndPoint;
		String tokenRequest = "client_id=" + clientId;
		tokenRequest += "&client_secret=" + clientSecret;
		tokenRequest += "&grant_type=authorization_code";
		tokenRequest += "&code=" + code;
		tokenRequest += "&redirect_uri=" + urlEncodeParameter(redirectUri);

		HttpClientResponse httpResponse = HttpClientHelper.doPost(tokenUrl, tokenRequest);

		if (httpResponse.responseCode == 200) {
			JSONObject jsonResponse = new JSONObject(httpResponse.responseData);
			if (jsonResponse.has("access_token")) {
				System.out.println("Returning access token  " + jsonResponse.getString("access_token"));
				return jsonResponse.getString("access_token");
			}
		}
		return null;
	}

	public static JSONObject getUser(String userEndPoint, String accessToken) throws JSONException, MalformedURLException, ProtocolException, IOException  {
		HttpClientResponse httpResponse = HttpClientHelper.doGet(userEndPoint, "Bearer " + accessToken);

		if (httpResponse.responseCode == 200) {
			JSONObject jsonResponse = new JSONObject(httpResponse.responseData);
			return jsonResponse;
		}
		return null;
	}
	
	public static JSONObject modifyUser(String userEndPoint, String accessToken, JSONObject modifiedUser) throws JSONException, IOException  {
		HttpClientResponse httpResponse = HttpClientHelper.doPut(userEndPoint, "Bearer " + accessToken, modifiedUser.toString());

		if (httpResponse.responseCode == 200) {
			JSONObject jsonResponse = new JSONObject(httpResponse.responseData);
			return jsonResponse;
		}
		return null;
	}

	private static String urlEncodeParameter(String param) {
		String returnString = "";

		try {
			returnString = URLEncoder.encode(param, "UTF8");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return returnString;
	}
}
