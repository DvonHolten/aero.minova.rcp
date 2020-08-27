package aero.minova.rcp.dataservice.internal;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.CompletableFuture;

import org.osgi.service.component.annotations.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.plugin1.model.Table;
import aero.minova.rcp.plugin1.model.Value;
import aero.minova.rcp.plugin1.model.ValueDeserializer;
import aero.minova.rcp.plugin1.model.ValueSerializer;

@Component
public class DataService implements IDataService {

	private HttpRequest request;
	private HttpClient httpClient;
	private Authenticator authentication;
	private Gson gson;

	private void init() {
		
		authentication = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("admin", "admin".toCharArray());
			}
		};
		httpClient = HttpClient.newBuilder().authenticator(authentication).build();

		gson = new Gson();
		gson = new GsonBuilder() //
				.registerTypeAdapter(Value.class, new ValueSerializer()) //
				.registerTypeAdapter(Value.class, new ValueDeserializer()) //
				.setPrettyPrinting() //
				.create();
	}

	@Override
	public CompletableFuture<Table> getDataAsync(String tableName, Table seachTable) {
		init();
		String body = gson.toJson(seachTable);
		request = HttpRequest.newBuilder().uri(URI.create("http://mintest.minova.com:8084/data/index")) //
				.header("Content-Type", "application/json") //
				.method("GET", BodyPublishers.ofString(body))//
				.build();
		
		CompletableFuture<Table> future = httpClient.sendAsync(request, BodyHandlers.ofString())
	      .thenApply(t -> gson.fromJson( t.body(), Table.class));
		
		return future;
		
	}
}
