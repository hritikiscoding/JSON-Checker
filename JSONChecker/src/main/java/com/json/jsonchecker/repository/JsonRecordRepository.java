package com.json.jsonchecker.repository;

import com.json.jsonchecker.model.JsonRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JsonRecordRepository extends JpaRepository<JsonRecord, Long> {
    List<JsonRecord> findTop10ByOrderByCreatedAtDesc();
}