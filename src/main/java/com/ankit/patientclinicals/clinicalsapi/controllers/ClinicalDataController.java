package com.ankit.patientclinicals.clinicalsapi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ankit.patientclinicals.clinicalsapi.models.ClinicalData;
import com.ankit.patientclinicals.clinicalsapi.models.Patient;
import com.ankit.patientclinicals.clinicalsapi.repos.ClinicalDataRepository;
import com.ankit.patientclinicals.clinicalsapi.repos.PatientRepository;
import com.ankit.patientclinicals.clinicalsapi.dtos.ClinicalDataDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/clinicaldata")
public class ClinicalDataController {

    @Autowired
    private ClinicalDataRepository clinicalDataRepository;

    @Autowired
    private PatientRepository patientRepository;

    // GET all clinical data records
    @GetMapping
    public ResponseEntity<List<ClinicalData>> getAllClinicalData() {
        List<ClinicalData> clinicalDataList = clinicalDataRepository.findAll();
        return ResponseEntity.ok(clinicalDataList);
    }

    // GET clinical data by ID
    @GetMapping("/{id}")
    public ResponseEntity<ClinicalData> getClinicalDataById(@PathVariable Long id) {
        Optional<ClinicalData> clinicalData = clinicalDataRepository.findById(id);
        return clinicalData.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST - Create new clinical data record
    @PostMapping
    public ResponseEntity<ClinicalData> createClinicalData(@RequestBody ClinicalData clinicalData) {
        ClinicalData savedClinicalData = clinicalDataRepository.save(clinicalData);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedClinicalData);
    }

    // PUT - Update an existing clinical data record
    @PutMapping("/{id}")
    public ResponseEntity<ClinicalData> updateClinicalData(@PathVariable Long id, @RequestBody ClinicalData clinicalDataDetails) {
        Optional<ClinicalData> clinicalDataOptional = clinicalDataRepository.findById(id);
        if (clinicalDataOptional.isPresent()) {
            ClinicalData clinicalData = clinicalDataOptional.get();
            clinicalData.setComponentName(clinicalDataDetails.getComponentName());
            clinicalData.setComponentValue(clinicalDataDetails.getComponentValue());
            clinicalData.setMeasuredDateTime(clinicalDataDetails.getMeasuredDateTime());
            ClinicalData updatedClinicalData = clinicalDataRepository.save(clinicalData);
            return ResponseEntity.ok(updatedClinicalData);
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE clinical data by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClinicalData(@PathVariable Long id) {
        if (clinicalDataRepository.existsById(id)) {
            clinicalDataRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // POST - Save clinical data using DTO with patient ID
    @PostMapping("/save")
    public ResponseEntity<?> saveClinicalDataByPatientId(@RequestBody ClinicalDataDTO clinicalDataDTO) {
        Optional<Patient> patientOptional = patientRepository.findById(clinicalDataDTO.getPatientId());
        
        if (!patientOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Patient with ID " + clinicalDataDTO.getPatientId() + " not found");
        }

        Patient patient = patientOptional.get();
        ClinicalData clinicalData = new ClinicalData();
        clinicalData.setComponentName(clinicalDataDTO.getComponentName());
        clinicalData.setComponentValue(clinicalDataDTO.getComponentValue());
        clinicalData.setPatient(patient);

        ClinicalData savedClinicalData = clinicalDataRepository.save(clinicalData);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedClinicalData);
    }
    

}
