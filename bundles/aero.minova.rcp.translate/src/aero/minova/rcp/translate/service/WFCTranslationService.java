package aero.minova.rcp.translate.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.nls.ILocaleChangeService;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import aero.minova.rcp.dataservice.IDataService;

@Component
public class WFCTranslationService extends TranslationService {

	private TranslationService parent;
	private IDataService dataService;

	Logger logger;

	private String customerId = "MIN";
	private String applicationId = "WFC";
	private Properties resources = new Properties();

	@Inject
	protected UISynchronize sync;

	private int propertyFilesToLoadCount = 0;

	public WFCTranslationService() {
	}

	@Inject
	@Optional
	private void getNotified1(@Named(TranslationService.LOCALE) Locale s) {
		System.out.println("Injected via context: " + s);
	}

	@Inject
	@Optional
	private void getNotified2(@UIEventTopic(ILocaleChangeService.LOCALE_CHANGE) Locale s) {
		System.out.println("Injected via event broker: " + s);
	}

	@Override
	public String translate(String key, String contributorURI) {
		if (key.startsWith("%")) {
			if (parent != null) {
				return parent.translate(key, contributorURI);
			} else {
				return key;
			}
		}

		return translate(key);
	}

	/**
	 * einen MINOVA String übersetzen
	 * 
	 * @param key
	 * @return
	 */
	private String translate(String key) {
		if (key.startsWith("@") && !key.startsWith("@@")) {
			key = key.substring(1);
			String value = resources.getProperty(key + "." + applicationId + "." + customerId);
			if (value != null)
				return translate(value);
			value = resources.getProperty(key + "." + customerId);
			if (value != null)
				return translate(value);
			value = resources.getProperty(key + "." + applicationId);
			if (value != null)
				return translate(value);
			value = resources.getProperty(key);
			return value == null ? key : value;
		} else {
			return key;
		}
	}

	@Inject
	@Optional
	void setLoggerFactory(LoggerFactory factory) {
		if (factory != null) {
			this.logger = factory.getLogger(getClass());
		} else {
			this.logger = null;
		}
	}

	@Inject
	@Optional
	void setApplicationId(@Named("aero.minova.rcp.applicationid") String id) {
		this.applicationId = id;
	}

	@Inject
	@Optional
	void setCustomerId(@Named("aero.minova.rcp.customerid") String id) {
		this.customerId = id;
	}

	@Inject
	@Optional
	void setLocale(Locale locale) {
		if (logger != null) {
			logger.info("Locale changed to: " + locale.getDisplayName(Locale.ENGLISH));
			logger.info(Platform.getInstallLocation().toString());
		}
	}

	@Inject
	@Optional
	void setDataService(IDataService dataService) {
		if (logger != null) {
			logger.info("DataService changed to: " + dataService);
			logger.info(Platform.getInstallLocation().toString());
		}
		this.dataService = dataService;
	}

	public void setTranslationService(TranslationService o) {
		this.parent = o;

		if (logger != null) {
			logger.info("Locale changed to: " + locale.getDisplayName(Locale.ENGLISH));
			try {
				logger.info(Platform.getInstanceLocation().getURL().toURI().toString());
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		loadResources();
	}

	/**
	 * Resource Bundle Search and Loading Strategy
	 *
	 * <p>
	 * getBundle uses the base name, the specified locale, and the default locale
	 * (obtained from Locale.getDefault) to generate a sequence of candidate bundle
	 * names. If the specified locale's language, script, country, and variant are
	 * all empty strings, then the base name is the only candidate bundle name.
	 * Otherwise, a list of candidate locales is generated from the attribute values
	 * of the specified locale (language, script, country and variant) and appended
	 * to the base name. Typically, this will look like the following:
	 * </p>
	 *
	 * baseName + "_" + language + "_" + script + "_" + country + "_" + variant <br>
	 * baseName + "_" + language + "_" + script + "_" + country <br>
	 * baseName + "_" + language + "_" + script <br>
	 * baseName + "_" + language + "_" + country + "_" + variant <br>
	 * baseName + "_" + language + "_" + country <br>
	 * baseName + "_" + language
	 * 
	 * @throws URISyntaxException
	 *
	 */
	private void loadResources() {
		String basePath, i18nPath;
		String filename;
		try {
			basePath = Platform.getInstanceLocation().getURL().toURI().toString();
			i18nPath = basePath + "i18n";
			File i18nDir = new File(new URI(i18nPath));
			if (!i18nDir.exists()) {
				i18nDir.mkdirs();
			}
			filename = "i18n/messages";
			resources = new Properties();
			loadProperties(basePath, filename + ".properties");
			if (locale == null) {
				return;
			}
			if (!isEmpty(locale.getLanguage())) {
				loadProperties(basePath, filename + "_" + locale.getLanguage() + ".properties");
				if (!isEmpty(locale.getCountry())) {
					loadProperties(basePath,
							filename + "_" + locale.getLanguage() + "_" + locale.getCountry() + ".properties");
					if (!isEmpty(locale.getVariant())) {
						loadProperties(basePath, filename + "_" + locale.getLanguage() + "_" + locale.getCountry() + "_"
								+ locale.getVariant() + ".properties");
					}
				}
				if (!isEmpty(locale.getScript())) {
					loadProperties(basePath,
							filename + "_" + locale.getLanguage() + "_" + locale.getScript() + ".properties");
					if (!isEmpty(locale.getCountry())) {
						loadProperties(basePath, filename + "_" + locale.getLanguage() + "_" + locale.getScript() + "_"
								+ locale.getCountry() + ".properties");
						if (!isEmpty(locale.getVariant())) {
							loadProperties(basePath, filename + "_" + locale.getLanguage() + "_" + locale.getScript()
									+ "_" + locale.getCountry() + "_" + locale.getVariant() + ".properties");
						}
					}
				}
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private boolean isEmpty(String value) {
		return value == null || value.isEmpty();
	}

	private void loadProperties(String path, String propertiesFilename) {
		FileInputStream is = null;
		File f;
		URI uri;
		try {
			uri = new URI(path + propertiesFilename);
			try {
				f = new File(uri);
				is = new FileInputStream(f);
				resources.load(is);
				logger.error("test");
			} catch (FileNotFoundException e) {
				// es gibt nicht alle Dateien

				try {
					CompletableFuture<String> fileFuture = dataService.getFile(propertiesFilename);
					propertyFilesToLoadCount++;
					fileFuture.thenAccept(bytes -> sync.asyncExec(() -> {
						try {
							byte[] file;
							try {
								String result = bytes.substring(1, bytes.length() - 2);
								String byteValues[] = result.split(",");
								file = new byte[byteValues.length];
								int i = 0;
								for (String string : byteValues) {
									file[i++] = Byte.parseByte(string);
								}
							} catch (NumberFormatException nfe) {
								// Es ist wohl keine Datei angekommen
								file = new byte[0];
							}
							Files.write(Path.of(uri), file);
							propertyFilesToLoadCount--;
							if (propertyFilesToLoadCount == 0) {
								loadResources();
							}
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}));
				} catch (NullPointerException npe) {
					logger.error("NPE " + propertiesFilename);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (is != null)
					try {
						is.close();
					} catch (IOException e) {
						if (logger != null) {
							logger.error(e.toString());
						} else {
							e.printStackTrace();
						}
					}
			}
		} catch (URISyntaxException e2) {
			e2.printStackTrace();
		}
	}
}
