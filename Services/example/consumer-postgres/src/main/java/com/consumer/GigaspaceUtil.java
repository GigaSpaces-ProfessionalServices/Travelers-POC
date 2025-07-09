package com.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GigaspaceUtil {
    private static final Logger log = Logger.getLogger(GigaspaceUtil.class.getName());

    public static Map<String, List<String>> getObjectInfo() {
        try {
            String jsonResponse = fetchJson("http://xap-manager-service:8090/v2/spaces/space/objectsTypeInfo");
            log.info("JSON Response: " + jsonResponse);
            Map<String, List<String>> results = parseSpaceIdFields(jsonResponse);
            return results;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    private static String fetchJson(String urlString) throws Exception {
        log.info("Fetching JSON from: " + urlString);
        HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("HTTP GET failed with code: " + conn.getResponseCode());
        }
        log.info("Response Code: " + conn.getResponseCode());
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            return reader.lines().collect(Collectors.joining());
        } finally {
            conn.disconnect();
        }
    }

    private static Map<String, List<String>> parseSpaceIdFields(String jsonResponse) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonResponse);
        JsonNode metadataArray = root.path("objectTypesMetadata");

        Map<String, List<String>> resultList = new HashMap<>();

        for (JsonNode objType : metadataArray) {
            String objectName = objType.path("objectName").asText();
            log.info("Object Name: " + objectName);
            List<String> spaceIdFields = new ArrayList<>();

            for (JsonNode field : objType.path("schema")) {
                if (field.path("isSpaceId").asBoolean(false)) {
                    spaceIdFields.add(field.path("name").asText());
                    log.info("Space ID Field: " + field.path("name").asText());
                }
            }
            resultList.put(objectName, spaceIdFields);
        }
        return resultList;
    }
}
