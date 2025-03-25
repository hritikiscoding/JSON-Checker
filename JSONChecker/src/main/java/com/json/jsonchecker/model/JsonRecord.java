package com.json.jsonchecker.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data  // Lombok annotation for getters, setters, toString, equals, and hashCode
@Table(name = "json_records")
@NoArgsConstructor
public class JsonRecord {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jsonData;

    private LocalDateTime createdAt;
 // Custom constructor (excluding ID)
    public JsonRecord(String jsonData, LocalDateTime timestamp) {
        this.jsonData = jsonData;
        this.createdAt = timestamp;
    }
    
}