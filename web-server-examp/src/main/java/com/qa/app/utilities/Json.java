package com.qa.app.utilities;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Json {

	private static ObjectMapper objectMapper = objectMapper();
	
	public static ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		return objectMapper;
	}
	
	/**
	 * Parses a raw JSON String into a JsonNode.
	 * 
	 * @param jsonSource
	 * @return JsonNode
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	public static JsonNode parse(String jsonSource) throws JsonMappingException, JsonProcessingException {
		return objectMapper.readTree(jsonSource);
	}
	
	/**
	 * Converts a JsonNode to the specified Class of type T.
	 * 
	 * @param <T>
	 * @param jsonNode
	 * @param clazz
	 * @return T
	 * @throws JsonProcessingException
	 * @throws IllegalArgumentException
	 */
	public static <T> T fromJson(JsonNode jsonNode, Class<T> clazz) throws IOException {
		return objectMapper.treeToValue(jsonNode, clazz);
	}
	
	public static JsonNode toJson(Object object) {
		return objectMapper.valueToTree(object);
	}
	
	public static String stringify(JsonNode jsonNode) throws JsonProcessingException {
		return generateJson(jsonNode, false);
	}
	
	public static String stringifyPrettify(JsonNode jsonNode) throws JsonProcessingException {
		return generateJson(jsonNode, false);
	}
	
	private static String generateJson(Object object, boolean prettify) throws JsonProcessingException {
		ObjectWriter objectWriter = objectMapper.writer();
		
		if (prettify) {
			objectWriter.with(SerializationFeature.INDENT_OUTPUT);
		}
		
		return objectWriter.writeValueAsString(object);
	}
}
