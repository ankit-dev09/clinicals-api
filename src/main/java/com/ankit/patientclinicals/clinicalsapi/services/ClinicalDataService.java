package com.ankit.patientclinicals.clinicalsapi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ankit.patientclinicals.clinicalsapi.models.ClinicalData;
import com.ankit.patientclinicals.clinicalsapi.models.Patient;
import com.ankit.patientclinicals.clinicalsapi.repos.ClinicalDataRepository;
import com.ankit.patientclinicals.clinicalsapi.repos.PatientRepository;
import com.ankit.patientclinicals.clinicalsapi.dtos.ClinicalDataDTO;
import com.ankit.patientclinicals.clinicalsapi.exceptions.ResourceNotFoundException;

import java.util.List;

/**
 * Service layer for ClinicalData operations.
 * Contains all business logic for clinical data management including validations.
 */
@Service
public class ClinicalDataService {

    @Autowired
    private ClinicalDataRepository clinicalDataRepository;

    @Autowired
    private PatientRepository patientRepository;

    /**
     * Retrieve all clinical data records
     * @return List of all clinical data
     */
    public List<ClinicalData> getAllClinicalData() {
        return clinicalDataRepository.findAll();
    }

    /**
     * Retrieve clinical data by ID
     * @param id - Clinical Data ID
     * @return ClinicalData if found
     * @throws ResourceNotFoundException if clinical data not found
     */
    public ClinicalData getClinicalDataById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Clinical Data ID must be a positive number");
        }
        return clinicalDataRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clinical data not found with ID: " + id));
    }

    /**
     * Create a new clinical data record with validation
     * @param clinicalData - ClinicalData object to create
     * @return Saved clinical data
     */
    public ClinicalData createClinicalData(ClinicalData clinicalData) {
        validateClinicalData(clinicalData);
        return clinicalDataRepository.save(clinicalData);
    }

    /**
     * Update an existing clinical data record with validation
     * @param id - Clinical Data ID to update
     * @param clinicalDataDetails - Updated clinical data details
     * @return Updated clinical data
     * @throws ResourceNotFoundException if clinical data not found
     */
    public ClinicalData updateClinicalData(Long id, ClinicalData clinicalDataDetails) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Clinical Data ID must be a positive number");
        }
        validateClinicalData(clinicalDataDetails);
        
        ClinicalData clinicalData = clinicalDataRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clinical data not found with ID: " + id));
        
        clinicalData.setComponentName(clinicalDataDetails.getComponentName());
        clinicalData.setComponentValue(clinicalDataDetails.getComponentValue());
        clinicalData.setMeasuredDateTime(clinicalDataDetails.getMeasuredDateTime());
        
        return clinicalDataRepository.save(clinicalData);
    }

    /**
     * Delete clinical data by ID
     * @param id - Clinical Data ID to delete
     * @throws ResourceNotFoundException if clinical data not found
     */
    public void deleteClinicalData(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Clinical Data ID must be a positive number");
        }
        if (!clinicalDataRepository.existsById(id)) {
            throw new ResourceNotFoundException("Clinical data not found with ID: " + id);
        }
        clinicalDataRepository.deleteById(id);
    }

    /**
     * Create clinical data from DTO with patient ID
     * @param clinicalDataDTO - DTO containing clinical data and patient ID
     * @return Saved clinical data
     * @throws ResourceNotFoundException if patient not found
     */
    public ClinicalData saveClinicalDataByPatientId(ClinicalDataDTO clinicalDataDTO) {
        validateClinicalDataDTO(clinicalDataDTO);
        
        Patient patient = patientRepository.findById(clinicalDataDTO.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + clinicalDataDTO.getPatientId()));
        
        ClinicalData clinicalData = new ClinicalData();
        clinicalData.setComponentName(clinicalDataDTO.getComponentName());
        clinicalData.setComponentValue(clinicalDataDTO.getComponentValue());
        clinicalData.setPatient(patient);
        
        return clinicalDataRepository.save(clinicalData);
    }

    /**
     * Validate clinical data fields
     * @param clinicalData - ClinicalData to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateClinicalData(ClinicalData clinicalData) {
        if (clinicalData == null) {
            throw new IllegalArgumentException("Clinical data cannot be null");
        }
        
        if (clinicalData.getComponentName() == null || clinicalData.getComponentName().trim().isEmpty()) {
            throw new IllegalArgumentException("Component name is required and cannot be empty");
        }
        
        if (clinicalData.getComponentValue() == null || clinicalData.getComponentValue().trim().isEmpty()) {
            throw new IllegalArgumentException("Component value is required and cannot be empty");
        }
        
        if (clinicalData.getMeasuredDateTime() == null) {
            throw new IllegalArgumentException("Measured date time is required");
        }
    }

    /**
     * Validate clinical data DTO fields
     * @param clinicalDataDTO - ClinicalDataDTO to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateClinicalDataDTO(ClinicalDataDTO clinicalDataDTO) {
        if (clinicalDataDTO == null) {
            throw new IllegalArgumentException("Clinical data DTO cannot be null");
        }
        
        if (clinicalDataDTO.getComponentName() == null || clinicalDataDTO.getComponentName().trim().isEmpty()) {
            throw new IllegalArgumentException("Component name is required and cannot be empty");
        }
        
        if (clinicalDataDTO.getComponentValue() == null || clinicalDataDTO.getComponentValue().trim().isEmpty()) {
            throw new IllegalArgumentException("Component value is required and cannot be empty");
        }
        
        if (clinicalDataDTO.getPatientId() == null || clinicalDataDTO.getPatientId() <= 0) {
            throw new IllegalArgumentException("Patient ID must be a positive number");
        }
    }
}
