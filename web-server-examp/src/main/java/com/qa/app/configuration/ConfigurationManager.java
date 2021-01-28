package com.qa.app.configuration;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.qa.app.utilities.Json;

public class ConfigurationManager {

	private static ConfigurationManager configurationManager;
	private Configuration configuration;
	
	private ConfigurationManager() {
		
	}
	
	public static ConfigurationManager getInstance() {
		if (configurationManager == null) {
			configurationManager = new ConfigurationManager();
		}
		return configurationManager;
	}
	
	public void loadConfigurationFile(String path) throws HttpConfigurationException {
		FileReader fileReader;
		
		try {
			fileReader = new FileReader(path);
		} catch (FileNotFoundException e) {
			throw new HttpConfigurationException(e);
		}
		
		StringBuffer stringBuffer = new StringBuffer();
		int i; // -1 = end of stream
		
		try {
			while ((i = fileReader.read()) != -1) {
				stringBuffer.append((char) i);
			}
		} catch (IOException e) {
			throw new HttpConfigurationException(e);
		}
		
		JsonNode configuration;
		try {
			configuration = Json.parse(stringBuffer.toString());
		} catch (IOException e) {
			throw new HttpConfigurationException("Error occured while parsing the Configuration file", e);
		} 
		
		try {
			this.configuration = Json.fromJson(configuration, Configuration.class);
		} catch (IOException e) {
			throw new HttpConfigurationException("Error occured internally while parsing the Configuration file", e);
		}
		
		//fileReader.close();
	}
	
	public Configuration getCurrentConfiguration() throws HttpConfigurationException {
		if (configuration == null) {
			throw new HttpConfigurationException("No configuration has been set. Set a configuration using a Configuration file.");
		}
		return configuration;
	}
}
