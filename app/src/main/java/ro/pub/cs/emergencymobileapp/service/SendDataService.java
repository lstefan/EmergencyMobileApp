package ro.pub.cs.emergencymobileapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

import ro.pub.cs.emergencymobileapp.dto.IncidentRequestDTO;

/**
 *
 */
public class SendDataService {

	private static SendDataService sendDataServiceInstance = null;

	protected SendDataService() {

	}

	public static SendDataService getSendDataService() {
		if(sendDataServiceInstance == null) {
			sendDataServiceInstance = new SendDataService();
		}
		return sendDataServiceInstance;
	}

	public String sendIncident(IncidentRequestDTO incidentRequestDTO) {

		HttpURLConnection conn = null;
		OutputStream os = null;
		InputStream is = null;
		BufferedReader br = null;
		String result = "";

		try {
			//constants
			URL url = new URL("http://192.168.1.102:8080/incidents");

			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String message = ow.writeValueAsString(incidentRequestDTO);

			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			conn.setReadTimeout(10000 /*milliseconds*/);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("POST");
			String basicAuth = "Basic dXNlcjpwYXNzd29yZA==";
			conn.setRequestProperty ("Authorization", basicAuth);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setFixedLengthStreamingMode(message.getBytes().length);

			//make some HTTP header nicety
			conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
			conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

			//open
			conn.connect();

			//setup send
			os = new BufferedOutputStream(conn.getOutputStream());
			os.write(message.getBytes());
			//clean up
			os.flush();

			//do something with response
			int responseCode = conn.getResponseCode();
			is = new BufferedInputStream(conn.getInputStream());
			br = new BufferedReader(new InputStreamReader(is));

			String inputLine = "";
			StringBuffer sb = new StringBuffer();
			while ((inputLine = br.readLine()) != null) {
				sb.append(inputLine);
			}
			result = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//clean up
			try {
				if (os != null) {
					os.close();
				}
				if (is != null) {
					is.close();
				}
				if (conn != null) {
					conn.disconnect();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	public String sendFrame(byte[] frame) {
		String hostname = "192.168.1.102";
		int port = 999;

		// declaration section:
		// clientSocket: our client socket
		// os: output stream
		// is: input stream

		Socket clientSocket = null;
		DataOutputStream os = null;
		BufferedReader is = null;
		String result = "";

		// Initialization section:
		// Try to open a socket on the given port
		// Try to open input and output streams

		try {
			clientSocket = new Socket(hostname, port);
			os = new DataOutputStream(clientSocket.getOutputStream());
			is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: " + hostname);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: " + hostname);
		}

		// If everything has been initialized then we want to write some data
		// to the socket we have opened a connection to on the given port

		if (clientSocket == null || os == null || is == null) {
			System.err.println("Something is wrong. One variable is null.");
			return result;
		}

		try {
			os.write(frame);
            os.writeBytes("\r\n");

			// clean up:
			// close the output stream
			// close the input stream
			// close the socket

			os.close();
			is.close();
			clientSocket.close();
		} catch (UnknownHostException e) {
			System.err.println("Trying to connect to unknown host: " + e);
		} catch (IOException e) {
			System.err.println("IOException:  " + e);
		}
		return result;
	}
}
