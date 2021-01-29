package com.qa.app;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qa.app.configuration.Configuration;
import com.qa.app.configuration.ConfigurationManager;
import com.qa.app.configuration.HttpConfigurationException;
import com.qa.app.core.ServerListenerThread;

public class Server implements Runnable {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(Server.class);

	private ConfigurationManager configurationManager;
	private Configuration configuration;
	private ServerListenerThread serverListenerThread;
	private String configurationFileLocation;
	
	public Server() {
		super();
	}
	
	public Server(String configurationFileLocation) {
		super();
		this.configurationFileLocation = configurationFileLocation;
	}

	@Override
	public void run() {
		try {
			LOGGER.info("Server starting...");
			loadConfiguration();
			
			LOGGER.info("USING PORT: " + configuration.getPort());
			LOGGER.info("USING VIEWS LOCATION: " + configuration.getViewsLocation());
			
			loadServer();
			
		} catch (HttpConfigurationException | IOException e) {
			LOGGER.error("Server failed to start.");
			e.printStackTrace();
		}
	}

	private void loadServer() throws IOException {
		serverListenerThread = new ServerListenerThread(configuration.getPort(),
										 				configuration.getViewsLocation());
		//serverListenerThread.setDaemon(true);
		serverListenerThread.start();
//		serverListenerThread.join();
	}

	private void loadConfiguration() throws HttpConfigurationException {
		configurationManager = ConfigurationManager.getInstance();
		
		// Load and get config file
		if (configurationFileLocation == null || configurationFileLocation.isEmpty()) {
			configurationManager.loadConfigurationFile("src/main/resources/http.json");
		} else {
			configurationManager.loadConfigurationFile(configurationFileLocation);
		}
		
		configuration = configurationManager.getCurrentConfiguration();
	}
}
