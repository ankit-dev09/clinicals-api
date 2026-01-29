package com.ankit.patientclinicals.clinicalsapi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ankit.patientclinicals.clinicalsapi.models.Patient;
import com.ankit.patientclinicals.clinicalsapi.repos.PatientRepository;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientRepository patientRepository;

    // GET all patients
    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();
        return ResponseEntity.ok(patients);
    }

    // GET patient by ID
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        Optional<Patient> patient = patientRepository.findById(id);
        return patient.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST - Create a new patient
    @PostMapping
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        Patient savedPatient = patientRepository.save(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPatient);
    }

    // PUT - Update an existing patient
    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @RequestBody Patient patientDetails) {
        Optional<Patient> patientOptional = patientRepository.findById(id);
        if (patientOptional.isPresent()) {
            Patient patient = patientOptional.get();
            patient.setFirstName(patientDetails.getFirstName());
            patient.setLastName(patientDetails.getLastName());
            patient.setAge(patientDetails.getAge());
            Patient updatedPatient = patientRepository.save(patient);
            return ResponseEntity.ok(updatedPatient);
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE patient by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        if (patientRepository.existsById(id)) {
            patientRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
