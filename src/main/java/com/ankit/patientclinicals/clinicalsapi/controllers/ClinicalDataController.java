package com.ankit.patientclinicals.clinicalsapi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.ankit.patientclinicals.clinicalsapi.models.ClinicalData;
import com.ankit.patientclinicals.clinicalsapi.services.ClinicalDataService;
import com.ankit.patientclinicals.clinicalsapi.dtos.ClinicalDataDTO;
import java.util.List;


@RestController
@RequestMapping("/api/clinicaldata")
public class ClinicalDataController {

    @Autowired
    private ClinicalDataService clinicalDataService;

    // GET all clinical data records
    @GetMapping
    public ResponseEntity<List<ClinicalData>> getAllClinicalData() {
        List<ClinicalData> clinicalDataList = clinicalDataService.getAllClinicalData();
        return ResponseEntity.ok(clinicalDataList);
    }

    // GET clinical data by ID
    @GetMapping("/{id}")
    public ResponseEntity<ClinicalData> getClinicalDataById(@PathVariable Long id) {
        ClinicalData clinicalData = clinicalDataService.getClinicalDataById(id);
        return ResponseEntity.ok(clinicalData);
    }

    // POST - Create new clinical data record
    @PostMapping
    public ResponseEntity<ClinicalData> createClinicalData(@Valid @RequestBody ClinicalData clinicalData) {
        ClinicalData savedClinicalData = clinicalDataService.createClinicalData(clinicalData);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedClinicalData);
    }

    // PUT - Update an existing clinical data record
    @PutMapping("/{id}")
    public ResponseEntity<ClinicalData> updateClinicalData(@PathVariable Long id, @Valid @RequestBody ClinicalData clinicalDataDetails) {
        ClinicalData updatedClinicalData = clinicalDataService.updateClinicalData(id, clinicalDataDetails);
        return ResponseEntity.ok(updatedClinicalData);
    }

    // DELETE clinical data by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClinicalData(@PathVariable Long id) {
        clinicalDataService.deleteClinicalData(id);
        return ResponseEntity.noContent().build();
    }

    // POST - Save clinical data using DTO with patient ID
    @PostMapping("/save")
    public ResponseEntity<ClinicalData> saveClinicalDataByPatientId(@Valid @RequestBody ClinicalDataDTO clinicalDataDTO) {
        ClinicalData savedClinicalData = clinicalDataService.saveClinicalDataByPatientId(clinicalDataDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedClinicalData);
    }
    

}
