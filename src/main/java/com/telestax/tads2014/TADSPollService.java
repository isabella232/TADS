package com.telestax.tads2014;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import com.google.gson.Gson;

/**
 * A simple CDI service which is able to say hello to someone
 * 
 * @author Jean Deruelle
 * 
 */
public class TADSPollService {

	static String TP_URL = "https://dev.tp.mu/holler/lookup_msisdn.php";
	static String TP_SMS_URL = "http://ast12.tp.mu/tadsms.php";
	static String DASHING_BASE_URL="http://ec2-79-125-68-2.eu-west-1.compute.amazonaws.com:80/";
	static String DASHING_URL = DASHING_BASE_URL + "widgets/welcome";
	static String DASHING_DRINK_URL = DASHING_BASE_URL + "widgets/";
	static String DASHING_HEATMAP_URL = DASHING_BASE_URL + "widgets/heatmap";
	static String CHARSET = "UTF-8";
	static String TOKEN = "9YtXr4AhVKaqBWrTmNYj2Px";
	static String COUNTRY = "original-country";

	String getBirthDate(String fromNumber) {
		if (fromNumber == null) {
			return "1969";
		}
		return fromNumber.substring(fromNumber.length() - 4,
				fromNumber.length());
	}

	PhoneNumberInformation getPhoneNumberInformation(String fromNumber) throws Exception {
		if (fromNumber == null) {
			return null;
		}
		if (fromNumber.startsWith("+")) {
			fromNumber = fromNumber.substring(1, fromNumber.length());
		} else if (fromNumber.startsWith("00")) {
			fromNumber = fromNumber.substring(2, fromNumber.length());
		} else if (fromNumber.startsWith("0")){
			fromNumber = fromNumber.substring(1, fromNumber.length());
			fromNumber = "44" + fromNumber;
		}

		String query = String.format("token=%s&msisdn=%s",
				URLEncoder.encode(TOKEN, CHARSET),
				URLEncoder.encode(fromNumber, CHARSET));

		System.out.println("url " + TP_URL + "?" + query);
		URLConnection connection = new URL(TP_URL + "?" + query)
				.openConnection();
		connection.setRequestProperty("Accept-Charset", CHARSET);

		HttpURLConnection httpConnection = (HttpURLConnection) connection;
		int status = httpConnection.getResponseCode();

		System.out.println("status " + status);
		if (status >= 200 && status < 300) {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String responseBody = "";
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				System.out.println("response line " + inputLine);
				responseBody = responseBody.concat(inputLine);
			}
			in.close();
			System.out.println("responseBody " + responseBody);
			Gson gson = new Gson();
			return gson.fromJson(
					responseBody, PhoneNumberInformation.class);
		}
		return null;
	}
	
	boolean sendSMS(String fromNumber, String drink)
			throws Exception {
		if (fromNumber == null) {
			return false;
		}
		if (fromNumber.startsWith("+")) {
			fromNumber = fromNumber.substring(1, fromNumber.length());
		} else if (fromNumber.startsWith("00")) {
			fromNumber = fromNumber.substring(2, fromNumber.length());
		} else if (fromNumber.startsWith("0")){
			fromNumber = fromNumber.substring(1, fromNumber.length());
			fromNumber = "44" + fromNumber;
		}
		
		String query = String.format("msisdn=%s&drink=%s",
				URLEncoder.encode(fromNumber, CHARSET),
				URLEncoder.encode(drink, CHARSET));
		
		System.out.println("url " + TP_SMS_URL + "?" + query);
		URLConnection connection = new URL(TP_SMS_URL + "?" + query)
				.openConnection();
		connection.setRequestProperty("Accept-Charset", CHARSET);

		HttpURLConnection httpConnection = (HttpURLConnection) connection;
		int status = httpConnection.getResponseCode();

		System.out.println("status " + status);
		if (status >= 200 && status < 300) {
			return true;
		}
		return false;
	}

	boolean updateDashingDashboard(TADS2014Response tads2014Response)
			throws Exception {
		HttpURLConnection con = (HttpURLConnection) new URL(DASHING_URL)
				.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestMethod("POST");

		OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
		wr.write("{ \"auth_token\": \"YOUR_AUTH_TOKEN\", \"text\": \""
				+ tads2014Response.getBirthDate() + " " + tads2014Response.getPhoneNumberInformation().getLocation() + " " + tads2014Response.getFavDrink() + "\" }");
		wr.flush();

		int status = con.getResponseCode();
		if (status >= 200 && status < 300) {
			return true;
		}
		return false;
	}
	
	boolean updateDashingHeatMap(TADS2014Response tads2014Response)
			throws Exception {
		HttpURLConnection con = (HttpURLConnection) new URL(DASHING_HEATMAP_URL)
				.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestMethod("POST");

		OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
		wr.write("{ \"auth_token\": \"YOUR_AUTH_TOKEN\", \"country\": \""
				+ tads2014Response.getPhoneNumberInformation().getLocation() + "\" }");
		wr.flush();

		int status = con.getResponseCode();
		if (status >= 200 && status < 300) {
			return true;
		}
		return false;
	}
	
	boolean updateDashingDrink(String drink)
			throws Exception {
		HttpURLConnection con = (HttpURLConnection) new URL(DASHING_DRINK_URL + drink)
				.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestMethod("POST");

		OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
		wr.write("{ \"auth_token\": \"YOUR_AUTH_TOKEN\", \"value\": \""
				+ drink + "\" }");
		wr.flush();

		int status = con.getResponseCode();
		if (status >= 200 && status < 300) {
			return true;
		}
		return false;
	}

	String getDrink(String drink) {
		if (drink == null) {
			return "no drink";
		}
		if (drink.equalsIgnoreCase("1")) {
			return "Beer";
		}
		if (drink.equalsIgnoreCase("2")) {
			return "Wine";
		}
		if (drink.equalsIgnoreCase("3")) {
			return "Soft Drink";
		}
		return "no drink";
	}
}
