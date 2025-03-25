package com.json.jsonchecker.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.json.jsonchecker.model.JsonRecord;
import com.json.jsonchecker.service.JsonService;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/json")
@Tag(name = "JSON Checker API", description = "API for validating, formatting, compressing, and storing JSON data.")
public class JsonController {
    private final JsonService jsonService;

    public JsonController(JsonService jsonService) {
        this.jsonService = jsonService;
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate & Prettify JSON", description = "Validates the input JSON and returns a formatted version if valid.")
    @ApiResponse(responseCode = "200", description = "Valid JSON")
    @ApiResponse(responseCode = "400", description = "Invalid JSON")
    @RateLimiter(name = "jsonRateLimiter")
    public ResponseEntity<?> validateJson(@RequestBody String json) {
    	 try {
             String jsonString = jsonService.compressJson(json);
             jsonService.saveJson(jsonString);
             return ResponseEntity.ok()
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(jsonString);
         }
    	 catch (Exception e) {
         	Map<String, Object> errorResponse = new HashMap<>();
             errorResponse.put("error", "Invalid JSON format");
             errorResponse.put("details", e.getMessage());
             return ResponseEntity.badRequest().body(errorResponse);
         }
    }

    @PostMapping("/compress")
    @Operation(summary = "Compress JSON", description = "Removes unnecessary spaces and formats JSON into a single-line string.")
    @ApiResponse(responseCode = "200", description = "Compressed JSON returned successfully")
    @RateLimiter(name = "jsonRateLimiter")
    public ResponseEntity<?> compressJson(@RequestBody String json) {
        try {
            String jsonString = jsonService.compressJson(json);
            jsonService.saveJson(jsonString);
            return ResponseEntity.ok(jsonString);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid JSON format", "details", e.getMessage()));
        }
    }

    @GetMapping("/history")
    @Operation(summary = "Get Last 10 Records", description = "Retrieves the last 10 JSON records stored in the database.")
    @ApiResponse(responseCode = "200", description = "Returns last 10 JSON records")
    public ResponseEntity<List<Map<String, Object>>> getLast10Records() {
    	List<JsonRecord> records = jsonService.getLast10Records();
        
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> formattedRecords = new ArrayList<>();

        for (JsonRecord jsonrecord : records) {
            try {
                Map<String, Object> jsonMap = objectMapper.readValue(jsonrecord.getJsonData(), new TypeReference<>() {});
                Map<String, Object> responseEntry = new LinkedHashMap<>();
                responseEntry.put("id", jsonrecord.getId());
                responseEntry.put("jsonData", jsonMap);
                responseEntry.put("createdAt", jsonrecord.getCreatedAt());
                formattedRecords.add(responseEntry);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        
        return ResponseEntity.ok(formattedRecords);
    }
}