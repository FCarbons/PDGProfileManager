package com.pi.misc.dgprofilemanager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.util.StringUtils;

public class HttpClientHelper {

	public static HttpClientResponse doPost(String url, String postBody) throws MalformedURLException, ProtocolException, IOException {

		HttpClientResponse responseObject = new HttpClientResponse();

		HttpsURLConnection httpsURLConnection = getConnectionForMethod(url, "POST", null);
		httpsURLConnection.connect();
		System.out.println("POSTing data: " + url + " " + postBody);
		DataOutputStream postData = new DataOutputStream(httpsURLConnection.getOutputStream());
		postData.writeBytes(postBody);
		postData.flush();
		postData.close();

		int responseCode = httpsURLConnection.getResponseCode();
		BufferedReader responseReader = getResponseReader(httpsURLConnection);

		System.out.println("Got response code: " + responseCode);

		String responseLine = null;
		String responseData = "";

		while ((responseLine = responseReader.readLine()) != null) {
			responseData += responseLine;
		}

		responseObject.responseCode = responseCode;
		responseObject.responseData = responseData;
		httpsURLConnection.disconnect();
		return responseObject;
	}

	public static HttpClientResponse doGet(String url, String authorizationHeader) throws MalformedURLException, ProtocolException, IOException {

		System.out.println("Creating GET request to: " + url);
		HttpClientResponse responseObject = new HttpClientResponse();

		HttpsURLConnection httpsURLConnection = getConnectionForMethod(url, "GET", authorizationHeader);
		httpsURLConnection.connect();

		int responseCode = httpsURLConnection.getResponseCode();
		BufferedReader responseReader = getResponseReader (httpsURLConnection);

		System.out.println("Got response code: " + responseCode);
	
		String responseLine = null;
		String responseData = "";

		while ((responseLine = responseReader.readLine()) != null) {
			responseData += responseLine;
		}

		responseObject.responseCode = responseCode;
		responseObject.responseData = responseData;

		httpsURLConnection.disconnect();

		return responseObject;
	}

	
	public static HttpClientResponse doPut(String url, String authorizationHeader, String putBody) throws IOException {

		System.out.println("Creating PUT request to: " + url);
		HttpClientResponse responseObject = new HttpClientResponse();

		trustAllHosts();

		HttpsURLConnection httpsURLConnection = getConnectionForMethod(url, "PUT", authorizationHeader);
		httpsURLConnection.connect();
		System.out.println("POSTing data: " + url + " " + putBody);

		DataOutputStream postData = new DataOutputStream(httpsURLConnection.getOutputStream());
		postData.writeBytes(putBody);
		postData.flush();
		postData.close();

		int responseCode = httpsURLConnection.getResponseCode();
		BufferedReader responseReader = getResponseReader(httpsURLConnection);

		System.out.println("Got response code: " + responseCode);

		String responseLine = null;
		String responseData = "";

		while ((responseLine = responseReader.readLine()) != null) {
			responseData += responseLine;
		}

		responseObject.responseCode = responseCode;
		responseObject.responseData = responseData;

		httpsURLConnection.disconnect();

		return responseObject;
	}

	
	private static BufferedReader getResponseReader(HttpsURLConnection httpsURLConnection) throws IOException {
		if (httpsURLConnection.getErrorStream() != null) {
			return new BufferedReader(new InputStreamReader(httpsURLConnection.getErrorStream(), "UTF-8"));
		} else {
			return new  BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream(), "UTF-8"));
		}

	}

	
	private static HttpsURLConnection getConnectionForMethod(String postUrl, String method, String authorizationHeader) throws MalformedURLException,
			IOException, ProtocolException {
		trustAllHosts();
		URL url = new URL(postUrl);

		HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();

		// TODO: DEV ONLY! Remove before deploying in production
		httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		});

		httpsURLConnection.setRequestMethod(method);
		httpsURLConnection.setUseCaches(false);
		httpsURLConnection.setDoInput(true);

		if (isOutputEnabled(method)) {
			httpsURLConnection.setDoOutput(true);
		}

		if (!StringUtils.isEmpty(authorizationHeader)) {
			httpsURLConnection.setRequestProperty("Authorization", authorizationHeader);
		}

		return httpsURLConnection;
	}

	private static boolean isOutputEnabled(String method) {
		if (method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PUT"))
			return true;
		else
			return false;

	}

	// TODO: DEV ONLY! Remove before deploying in production
	private static void trustAllHosts() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[] {};
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
