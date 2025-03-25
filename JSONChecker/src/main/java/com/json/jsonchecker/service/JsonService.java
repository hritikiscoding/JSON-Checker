package com.json.jsonchecker.service;

import com.json.jsonchecker.model.JsonRecord;
import com.json.jsonchecker.repository.JsonRecordRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JsonService {
    private final JsonRecordRepository repository;
    private final ObjectMapper objectMapper;

    public JsonService(JsonRecordRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public String validateAndPrettyPrint(String jsonString) throws JsonProcessingException{
        JsonNode jsonNode = objectMapper.readTree(jsonString);  // Validate JSON
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
    }

    public String compressJson(String jsonString) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(jsonString);
        return objectMapper.writeValueAsString(jsonNode);
    }

    public void saveJson(String jsonString) {
        JsonRecord jsonrecord = new JsonRecord(jsonString, LocalDateTime.now());
        repository.save(jsonrecord);
    }

    public List<JsonRecord> getLast10Records() {
        return repository.findTop10ByOrderByCreatedAtDesc();
    }
}