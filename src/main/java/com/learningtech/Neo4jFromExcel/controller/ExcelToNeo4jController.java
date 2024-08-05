package com.learningtech.graphql_demo.controller;

import com.learningtech.graphql_demo.model.Section;
import com.learningtech.graphql_demo.service.ExcelToNeo4jService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class ExcelToNeo4jController {
    @Autowired
    private ExcelToNeo4jService excelToNeo4jService;

    @PostMapping("/uploadExcel")
    private ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select an Excel file to upload");
        }

        try {
            excelToNeo4jService.processExcel(file.getInputStream());
            return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
        }
    }

    @GetMapping("/getAll")
    private List<Object> getAll() {
        return excelToNeo4jService.getAll();
    }

    @GetMapping("/getSumPremium")
    private List<Map<String, Object>> getSumPremium() {
        return excelToNeo4jService.getSumPremium();
    }



}
