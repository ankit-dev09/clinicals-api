package com.ankit.patientclinicals.clinicalsapi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ankit.patientclinicals.clinicalsapi.models.Patient;
import com.ankit.patientclinicals.clinicalsapi.repos.PatientRepository;
import com.ankit.patientclinicals.clinicalsapi.exceptions.ResourceNotFoundException;

import java.util.List;

/**
 * Service layer for Patient operations.
 * Contains all business logic for patient management including validations.
 */
@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    /**
     * Retrieve all patients
     * @return List of all patients
     */
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    /**
     * Retrieve a patient by ID
     * @param id - Patient ID
     * @return Patient if found
     * @throws ResourceNotFoundException if patient not found
     */
    public Patient getPatientById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Patient ID must be a positive number");
        }
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));
    }

    /**
     * Create a new patient with validation
     * @param patient - Patient object to create
     * @return Saved patient
     */
    public Patient createPatient(Patient patient) {
        validatePatient(patient);
        return patientRepository.save(patient);
    }

    /**
     * Update an existing patient with validation
     * @param id - Patient ID to update
     * @param patientDetails - Updated patient details
     * @return Updated patient
     * @throws ResourceNotFoundException if patient not found
     */
    public Patient updatePatient(Long id, Patient patientDetails) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Patient ID must be a positive number");
        }
        validatePatient(patientDetails);
        
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));
        
        patient.setFirstName(patientDetails.getFirstName());
        patient.setLastName(patientDetails.getLastName());
        patient.setAge(patientDetails.getAge());
        
        return patientRepository.save(patient);
    }

    /**
     * Delete a patient by ID
     * @param id - Patient ID to delete
     * @throws ResourceNotFoundException if patient not found
     */
    public void deletePatient(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Patient ID must be a positive number");
        }
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient not found with ID: " + id);
        }
        patientRepository.deleteById(id);
    }

    /**
     * Validate patient fields
     * @param patient - Patient to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validatePatient(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null");
        }
        
        if (patient.getFirstName() == null || patient.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required and cannot be empty");
        }
        
        if (patient.getFirstName().length() > 100) {
            throw new IllegalArgumentException("First name must not exceed 100 characters");
        }
        
        if (patient.getLastName() == null || patient.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required and cannot be empty");
        }
        
        if (patient.getLastName().length() > 100) {
            throw new IllegalArgumentException("Last name must not exceed 100 characters");
        }
        
        if (patient.getAge() == null || patient.getAge() <= 0) {
            throw new IllegalArgumentException("Age must be a positive number");
        }
        
        if (patient.getAge() > 150) {
            throw new IllegalArgumentException("Age must be less than or equal to 150");
        }
    }
}
