package aero.minova.server.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.runners.MethodSorters;

import aero.minova.rcp.dataservice.HashService;
import aero.minova.rcp.dataservice.internal.DataService;

/**
 * Integration test for the data service
 * 
 * @author Lars
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class DataServiceTest {

	private String username = "admin";
	private String password = "rqgzxTf71EAx8chvchMi";
	// Dies ist unser üblicher Server, von welchen wir unsere Daten abfragen
	private String server = "https://publictest.minova.com:17280";

	DataService dataService;

	@BeforeEach
	void configureDataService(@TempDir Path path) {
		URI uri = path.toUri();
		String stringUri = uri.toString();
		dataService = new DataService();
		dataService.setCredentials(username, password, server, 
				URI.create(stringUri));
	}

	@Test
	@DisplayName("Simple test to easily debug the created URI")
	void canCreateUri(@TempDir Path path) {
		URI uri = path.toUri();
		String stringUri = uri.toString() + File.separator;
		assertNotNull(uri);
		assertTrue(stringUri.startsWith("file"));
		assertFalse(stringUri.endsWith(";"));
	}

	@Test
	@DisplayName("Ensures the server returns not 200 for files that do not exit")
	void ensureThatWeThrowAnExceptionForMissingFiles() {
		assertThrows(RuntimeException.class, () -> {
			dataService.getHashForFile("test").join();	
		});
	}
	
	@Test
	@DisplayName("Ensures that the server can hash application.mdi")
	void hashApplicationMdi() {
		String join = dataService.getHashForFile("application.mdi").join();
		assertNotNull(join);
	}
	

	@Test
	@DisplayName("Ensure that we can download a translation file from the server")
	void ensureThatMessageFileCanBeDownloaded() throws IOException {
		String serverTranslation = dataService.getHashedFile("i18n/messages_en_US.properties").join();
		assertFalse(serverTranslation.contains("Internal Server Error"),
				"Download von message file sollte nicht zu Fehler führen");
	}

	@Test
	@Disabled("First the above test needs to run")
	@DisplayName("Cached translation file should be the same as the file from the server")
	void compareLocalMessageFileWithServerVersion() throws IOException {
		// first call should download and create the cached file
		String hashTranslationFile = HashService
				.hashFile(Path.of("resources", "translations", "messages_en_US.properties").toFile());
		// second call should read the cached file

		String serverTranslation = dataService.getHashedFile("i18n/messages_en_US.properties").join();

		// TODO Check that really the hash version was used, maybe Mockito can be used
		// to wrap the data service?
		assertEquals(hashTranslationFile, serverTranslation);
	}

	
	

}
