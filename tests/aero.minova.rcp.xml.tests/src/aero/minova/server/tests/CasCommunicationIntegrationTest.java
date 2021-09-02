package aero.minova.server.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.ValueDeserializer;
import aero.minova.rcp.model.ValueSerializer;

class CasCommunicationIntegrationTest {

	private String username = "admin";
	private String password = "rqgzxTf71EAx8chvchMi";
	// Dies ist unser üblicher Server, von welchen wir unsere Daten abfragen
	private String server = "http://publictest.minova.com:17280/cas";

	private HttpClient httpClient;
	private Authenticator authentication;
	private Gson gson;

	@BeforeEach
	public void setup() throws UnsupportedEncodingException {
		String encodedUser = new String(username.getBytes(), StandardCharsets.ISO_8859_1.toString());
		String encodedPW = new String(password.getBytes(), StandardCharsets.ISO_8859_1.toString());
		authentication = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(encodedUser, encodedPW.toCharArray());
			}
		};
		// TODO: fix certificate-problems
		httpClient = HttpClient.newBuilder()//
				.sslContext(disabledSslVerificationContext())//
				.version(HttpClient.Version.HTTP_2) //
				.authenticator(authentication).build();
		gson = new Gson();
		gson = new GsonBuilder() //
				.registerTypeAdapter(Value.class, new ValueSerializer()) //
				.registerTypeAdapter(Value.class, new ValueDeserializer()) //
				.setPrettyPrinting() //
				.create();
	}

	private static SSLContext disabledSslVerificationContext() {
		// Remove certificate validation
		SSLContext sslContext = null;

		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
		} };

		try {
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, trustAllCerts, new SecureRandom());
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			throw new RuntimeException(e);
		}
		return sslContext;
	}

	@Test
	void getAuthentificationIndexHTML() throws Exception {
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(server)).build();
		HttpResponse<String> response = null;
		try {
			response = httpClient.send(request, BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		assertNotNull(response);
		assertEquals(302, response.statusCode());
	}

	@Test
	void postProcedureWithResultSet() throws Exception {
		String body = "{\n" + "    \"name\": \"xpcasWorkingTimeServiceResolve\",\n" + "    \"columns\": [\n" + "        {\n"
				+ "            \"name\": \"KeyLong\",\n" + "            \"type\": \"INTEGER\"\n" + "        }\n" + "        , {\n"
				+ "            \"name\": \"KeyText\",\n" + "            \"type\": \"STRING\"\n" + "        }\n" + "        , {\n"
				+ "            \"name\": \"FilterLastAction\",\n" + "            \"type\": \"BOOLEAN\"\n" + "        }\n" + "    ],\n" + "    \"rows\": [\n"
				+ "        {\n" + "            \"values\" : [\n" + "                \"n-2\"\n" + "                , null\n" + "                , \"b-0\"\n"
				+ "            ]\n" + "        }\n" + "    ]\n" + "}";

		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(server + "/data/procedure")) //
				.header("Content-Type", "application/json") //
				.POST(BodyPublishers.ofString(body)).build();
		HttpResponse<String> response = null;
		try {
			response = httpClient.send(request, BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		assertNotNull(response);
		assertTrue(response.body().length() > 0);
		SqlProcedureResult sql = gson.fromJson(response.body(), SqlProcedureResult.class);
		Table newTable = sql.getResultSet();
		assertNotNull(newTable);
		assertEquals(3, newTable.getColumnCount());
		assertEquals(Integer.valueOf(2), newTable.getRows().get(0).getValue(0).getIntegerValue());
		assertEquals("ZPROGRAM", newTable.getRows().get(0).getValue(1).getStringValue());
		assertEquals(null, newTable.getRows().get(0).getValue(2));
	}

	@Test
	void postProcedureWithReturnCode() throws Exception {
		String body = "{\n" + "    \"name\": \"spInsertWorkingTime\",\n" + "    \"columns\": [\n" + "        {\n" + "            \"name\": \"KeyLong\",\n"
				+ "            \"type\": \"INTEGER\"\n" + "        }\n" + "        , {\n" + "            \"name\": \"EmployeeKey\",\n"
				+ "            \"type\": \"INTEGER\"\n" + "        }\n" + "        , {\n" + "            \"name\": \"ServiceContractKey\",\n"
				+ "            \"type\": \"INTEGER\"\n" + "        }\n" + "        , {\n" + "            \"name\": \"OrderReceiverrKey\",\n"
				+ "            \"type\": \"INTEGER\"\n" + "        }\n" + "        , {\n" + "            \"name\": \"ServiceObjectKey\",\n"
				+ "            \"type\": \"INTEGER\"\n" + "        }\n" + "        , {\n" + "            \"name\": \"ServiceKey\",\n"
				+ "            \"type\": \"INTEGER\"\n" + "        }\n" + "        , {\n" + "            \"name\": \"BookingDate\",\n"
				+ "            \"type\": \"INSTANT\"\n" + "        }\n" + "        , {\n" + "            \"name\": \"StartDate\",\n"
				+ "            \"type\": \"INSTANT\"\n" + "        }\n" + "        , {\n" + "            \"name\": \"EndDate\",\n"
				+ "            \"type\": \"INSTANT\"\n" + "        }\n" + "        , {\n" + "            \"name\": \"RenderedQuantity\",\n"
				+ "            \"type\": \"DOUBLE\"\n" + "        }\n" + "        , {\n" + "            \"name\": \"ChargedQuantity\",\n"
				+ "            \"type\": \"DOUBLE\"\n" + "        }\n" + "        , {\n" + "            \"name\": \"Description\",\n"
				+ "            \"type\": \"STRING\"\n" + "        }\n" + "        , {\n" + "            \"name\": \"Spelling\",\n"
				+ "            \"type\": \"BOOLEAN\"\n" + "        }\n" + "    ],\n" + "    \"rows\": [\n" + "        {\n" + "            \"values\" : [\n"
				+ "                \"n-1\"\n" + "                , \"n-1\"\n" + "                , \"n-81\"\n" + "                , \"n-1\"\n"
				+ "                , \"n-8\"\n" + "                , \"n-31\"\n" + "                , \"i-2020-08-05T00:00:00.00Z\"\n"
				+ "                , \"i-2020-08-05T16:00:00.00Z\"\n" + "                , \"i-2020-08-05T17:00:00.00Z\"\n" + "                , \"d-1\"\n"
				+ "                , \"d-0.5\"\n" + "                , \"s-Test via CAS\"\n" + "                , \"b-0\"\n" + "            ]\n" + "        }\n"
				+ "    ]\n" + "}";

		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(server + "/data/procedure")) //
				.header("Content-Type", "application/json") //
				.POST(BodyPublishers.ofString(body)).build();
		HttpResponse<String> response = null;
		try {
			response = httpClient.send(request, BodyHandlers.ofString());
			// System.out.println(response.body());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		assertEquals(500, response.statusCode());
	}

	@Test
	void ensureLoginWorks() {

		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(server + "/login")).build();
		HttpResponse<String> response = null;
		try {
			response = httpClient.send(request, BodyHandlers.ofString());
			// System.out.println(response.body());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		assertNotNull(response);
		assertEquals(200, response.statusCode());
	}

	@Test
	void getIndexData() {
		String body = "{\"name\":\"xvcasWorkingTimeIndex2\",\"columns\":[{\"name\":\"\\u0026\",\"type\":\"BOOLEAN\",\"outputType\":\"OUTPUT\",\"label\":\"\\u0026\",\"readOnly\":false,\"required\":false,\"isLookup\":false,\"visible\":true},{\"name\":\"KeyLong\",\"type\":\"INTEGER\",\"outputType\":\"OUTPUT\",\"label\":\"@WorkingTime.KeyLong\",\"decimals\":0,\"readOnly\":false,\"required\":false,\"isLookup\":false,\"visible\":true},{\"name\":\"EmployeeText\",\"type\":\"STRING\",\"outputType\":\"OUTPUT\",\"label\":\"@WorkingTime.EmployeeText\",\"readOnly\":false,\"required\":false,\"isLookup\":false,\"visible\":true},{\"name\":\"CustomerText\",\"type\":\"STRING\",\"outputType\":\"OUTPUT\",\"label\":\"@WorkingTime.CustomerText\",\"readOnly\":false,\"required\":false,\"isLookup\":false,\"visible\":true},{\"name\":\"ProjectText\",\"type\":\"STRING\",\"outputType\":\"OUTPUT\",\"label\":\"@WorkingTime.ProjectText\",\"readOnly\":false,\"required\":false,\"isLookup\":false,\"visible\":true},{\"name\":\"ServiceText\",\"type\":\"STRING\",\"outputType\":\"OUTPUT\",\"label\":\"@WorkingTime.ServiceText\",\"readOnly\":false,\"required\":false,\"isLookup\":false,\"visible\":true},{\"name\":\"BookingDate\",\"type\":\"INSTANT\",\"outputType\":\"OUTPUT\",\"label\":\"@WorkingTime.BookingDate\",\"dateTimeType\":\"DATE\",\"readOnly\":false,\"required\":false,\"isLookup\":false,\"visible\":true},{\"name\":\"StartDate\",\"type\":\"INSTANT\",\"outputType\":\"OUTPUT\",\"label\":\"@WorkingTime.StartDate\",\"dateTimeType\":\"TIME\",\"readOnly\":false,\"required\":false,\"isLookup\":false,\"visible\":true},{\"name\":\"EndDate\",\"type\":\"INSTANT\",\"outputType\":\"OUTPUT\",\"label\":\"@WorkingTime.EndDate\",\"dateTimeType\":\"TIME\",\"readOnly\":false,\"required\":false,\"isLookup\":false,\"visible\":true},{\"name\":\"RenderedQuantity\",\"type\":\"DOUBLE\",\"outputType\":\"OUTPUT\",\"label\":\"@WorkingTime.RenderedQuantity\",\"decimals\":2,\"readOnly\":false,\"required\":false,\"isLookup\":false,\"visible\":true},{\"name\":\"ChargedQuantity\",\"type\":\"DOUBLE\",\"outputType\":\"OUTPUT\",\"label\":\"@WorkingTime.ChargedQuantity\",\"decimals\":2,\"readOnly\":false,\"required\":false,\"isLookup\":false,\"visible\":true},{\"name\":\"Description\",\"type\":\"STRING\",\"outputType\":\"OUTPUT\",\"label\":\"@WorkingTime.Description\",\"readOnly\":false,\"required\":false,\"isLookup\":false,\"visible\":true},{\"name\":\"ServiceContractText\",\"type\":\"STRING\",\"outputType\":\"OUTPUT\",\"label\":\"@WorkingTime.ServiceContractText\",\"readOnly\":false,\"required\":false,\"isLookup\":false,\"visible\":true},{\"name\":\"Assigned\",\"type\":\"BOOLEAN\",\"outputType\":\"OUTPUT\",\"label\":\"@WorkingTime.Assigned\",\"readOnly\":false,\"required\":false,\"isLookup\":false,\"visible\":true},{\"name\":\"LastDate\",\"type\":\"INSTANT\",\"outputType\":\"OUTPUT\",\"label\":\"@WorkingTime.LastDate\",\"dateTimeType\":\"DATETIME\",\"readOnly\":false,\"required\":false,\"isLookup\":false,\"visible\":true},{\"name\":\"InvoiceText\",\"type\":\"STRING\",\"outputType\":\"OUTPUT\",\"label\":\"@WorkingTime.InvoiceText\",\"readOnly\":false,\"required\":false,\"isLookup\":false,\"visible\":true}],\"rows\":[{\"values\":[\"b-false\",null,null,null,null,null,null,null,null,null,null,null,null,null,null,null]}]}";

		String url = server + "/data/index";
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)) //
				.header("Content-Type", "application/json") //
				.method("GET", BodyPublishers.ofString(body)).build();

		HttpResponse<String> response = null;
		try {
			response = httpClient.send(request, BodyHandlers.ofString());
			// System.out.println(response.body());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		assertEquals(200, response.statusCode());
	}

	@Test
	@DisplayName("CAS Issue #184, HTTP Version 1")
	void ensureThatTheServerUsesAnAncientProtocol() {
		String body = "";
		String url = server + "/data/index";
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)) //
				.header("Content-Type", "application/json") //
				.method("GET", BodyPublishers.ofString(body)).build();

		HttpResponse<String> response = null;
		try {
			response = httpClient.send(request, BodyHandlers.ofString());
			// System.out.println(response.body());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		assertEquals(HttpClient.Version.HTTP_1_1, response.version());
	}

	@Test
	@DisplayName("Passwort mit Umlaut, WFC Issue #743")
	void ensureLoginWithUmlautInPassword() throws UnsupportedEncodingException {
		String username = "tästuser";
		String password = "täst";
		System.out.println("Username: " + username + ", passwort: " + password);
		String encodedUser = new String(username.getBytes(), StandardCharsets.ISO_8859_1.toString());
		String encodedPW = new String(password.getBytes(), StandardCharsets.ISO_8859_1.toString());

		
		 System.out.println("Default Charset=" + Charset.defaultCharset());
		 
		
		Authenticator authenticator = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(encodedUser, encodedPW.toCharArray());
			}
		};
		HttpClient build = HttpClient.newBuilder()//
				.sslContext(disabledSslVerificationContext())//
				.authenticator(authenticator).build();

		String body = "";
		String url = server + "/ping";
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)) //
				.header("User-Agent", "usertest")//
				.header("Content-Type", "application/xml; charset=utf-8") //
				.method("GET", BodyPublishers.ofString(body)).build();

		HttpResponse<String> response = null;
		try {
			response = build.send(request, BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		assertEquals(200, response.statusCode());
	}

}
