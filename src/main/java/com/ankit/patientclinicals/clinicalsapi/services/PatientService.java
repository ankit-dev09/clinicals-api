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
}
