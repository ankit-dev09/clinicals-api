package com.ankit.patientclinicals.clinicalsapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.ankit.patientclinicals.clinicalsapi.models.ClinicalData;
import com.ankit.patientclinicals.clinicalsapi.models.Patient;
import com.ankit.patientclinicals.clinicalsapi.repos.ClinicalDataRepository;
import com.ankit.patientclinicals.clinicalsapi.repos.PatientRepository;
import com.ankit.patientclinicals.clinicalsapi.controllers.ClinicalDataController;
import com.ankit.patientclinicals.clinicalsapi.dtos.ClinicalDataDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for ClinicalDataController class.
 * Tests all CRUD operations and HTTP endpoints for ClinicalData management.
 */
@ExtendWith(MockitoExtension.class)
public class ClinicalDataControllerTest {

    @Mock
    private ClinicalDataRepository clinicalDataRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private ClinicalDataController clinicalDataController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ClinicalData testClinicalData;
    private ClinicalData secondClinicalData;
    private Patient testPatient;

    /**
     * Initialize MockMvc, ObjectMapper, and test ClinicalData/Patient objects before each test.
     * Sets up test clinical data instances with different component names for use in test cases.
     */
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clinicalDataController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        testPatient = new Patient();
        testPatient.setId(1L);
        testPatient.setFirstName("Alice");
        testPatient.setLastName("Johnson");
        testPatient.setAge(28);

        testClinicalData = new ClinicalData();
        testClinicalData.setId(1L);
        testClinicalData.setComponentName("Blood Pressure");
        testClinicalData.setComponentValue("120/80");
        testClinicalData.setMeasuredDateTime(new Timestamp(System.currentTimeMillis()));
        testClinicalData.setPatient(testPatient);

        secondClinicalData = new ClinicalData();
        secondClinicalData.setId(2L);
        secondClinicalData.setComponentName("Heart Rate");
        secondClinicalData.setComponentValue("72 bpm");
        secondClinicalData.setMeasuredDateTime(new Timestamp(System.currentTimeMillis()));
        secondClinicalData.setPatient(testPatient);
    }

    /**
     * Test GET /api/clinicaldata endpoint returns a list of all clinical data records successfully.
     * Verifies that the endpoint returns HTTP 200 (OK) with a list containing 2 clinical data records.
     * 
     * @throws Exception if the MockMvc request fails
     */
    @Test
    public void testGetAllClinicalData_ReturnsListSuccessfully() throws Exception {
        List<ClinicalData> clinicalDataList = Arrays.asList(testClinicalData, secondClinicalData);
        when(clinicalDataRepository.findAll()).thenReturn(clinicalDataList);

        mockMvc.perform(get("/api/clinicaldata")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].componentName").value("Blood Pressure"))
                .andExpect(jsonPath("$[1].componentName").value("Heart Rate"));

        verify(clinicalDataRepository, times(1)).findAll();
    }

    /**
     * Test GET /api/clinicaldata endpoint returns an empty list when no clinical data exists.
     * Verifies that the endpoint returns HTTP 200 (OK) with an empty array.
     * 
     * @throws Exception if the MockMvc request fails
     */
    @Test
    public void testGetAllClinicalData_ReturnsEmptyList() throws Exception {
        when(clinicalDataRepository.findAll()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/clinicaldata")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(clinicalDataRepository, times(1)).findAll();
    }

    /**
     * Test GET /api/clinicaldata/{id} endpoint returns a specific clinical data record by ID.
     * Verifies that the endpoint returns HTTP 200 (OK) with the correct clinical data details.
     * 
     * @throws Exception if the MockMvc request fails
     */
    @Test
    public void testGetClinicalDataById_ClinicalDataExists() throws Exception {
        when(clinicalDataRepository.findById(1L)).thenReturn(Optional.of(testClinicalData));

        mockMvc.perform(get("/api/clinicaldata/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.componentName").value("Blood Pressure"))
                .andExpect(jsonPath("$.componentValue").value("120/80"));

        verify(clinicalDataRepository, times(1)).findById(1L);
    }

    /**
     * Test GET /api/clinicaldata/{id} endpoint returns 404 when clinical data does not exist.
     * Verifies that the endpoint returns HTTP 404 (NOT FOUND) for a non-existent clinical data ID.
     * 
     * @throws Exception if the MockMvc request fails
     */
    @Test
    public void testGetClinicalDataById_ClinicalDataNotFound() throws Exception {
        when(clinicalDataRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/clinicaldata/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(clinicalDataRepository, times(1)).findById(999L);
    }

    /**
     * Test POST /api/clinicaldata endpoint creates a new clinical data record successfully.
     * Verifies that the endpoint returns HTTP 201 (CREATED) with the newly created record's ID.
     * 
     * @throws Exception if the MockMvc request fails
     */
    @Test
    public void testCreateClinicalData_SuccessfulCreation() throws Exception {
        ClinicalData newClinicalData = new ClinicalData();
        newClinicalData.setComponentName("Temperature");
        newClinicalData.setComponentValue("37.5°C");
        newClinicalData.setMeasuredDateTime(new Timestamp(System.currentTimeMillis()));

        ClinicalData savedClinicalData = new ClinicalData();
        savedClinicalData.setId(3L);
        savedClinicalData.setComponentName("Temperature");
        savedClinicalData.setComponentValue("37.5°C");
        savedClinicalData.setMeasuredDateTime(new Timestamp(System.currentTimeMillis()));

        when(clinicalDataRepository.save(any(ClinicalData.class))).thenReturn(savedClinicalData);

        mockMvc.perform(post("/api/clinicaldata")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newClinicalData)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.componentName").value("Temperature"));

        verify(clinicalDataRepository, times(1)).save(any(ClinicalData.class));
    }

    /**
     * Test PUT /api/clinicaldata/{id} endpoint updates an existing clinical data record successfully.
     * Verifies that the endpoint returns HTTP 200 (OK) with the updated clinical data details.
     * 
     * @throws Exception if the MockMvc request fails
     */
    @Test
    public void testUpdateClinicalData_ClinicalDataExists() throws Exception {
        ClinicalData updatedDetails = new ClinicalData();
        updatedDetails.setComponentName("Blood Pressure");
        updatedDetails.setComponentValue("130/85");
        updatedDetails.setMeasuredDateTime(new Timestamp(System.currentTimeMillis()));

        ClinicalData modifiedClinicalData = new ClinicalData();
        modifiedClinicalData.setId(1L);
        modifiedClinicalData.setComponentName("Blood Pressure");
        modifiedClinicalData.setComponentValue("130/85");
        modifiedClinicalData.setMeasuredDateTime(new Timestamp(System.currentTimeMillis()));
        modifiedClinicalData.setPatient(testPatient);

        when(clinicalDataRepository.findById(1L)).thenReturn(Optional.of(testClinicalData));
        when(clinicalDataRepository.save(any(ClinicalData.class))).thenReturn(modifiedClinicalData);

        mockMvc.perform(put("/api/clinicaldata/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.componentValue").value("130/85"));

        verify(clinicalDataRepository, times(1)).findById(1L);
        verify(clinicalDataRepository, times(1)).save(any(ClinicalData.class));
    }

    /**
     * Test PUT /api/clinicaldata/{id} endpoint returns 404 when clinical data does not exist.
     * Verifies that the endpoint returns HTTP 404 (NOT FOUND) and does not save any data.
     * 
     * @throws Exception if the MockMvc request fails
     */
    @Test
    public void testUpdateClinicalData_ClinicalDataNotFound() throws Exception {
        ClinicalData updatedDetails = new ClinicalData();
        updatedDetails.setComponentName("Unknown");
        updatedDetails.setComponentValue("Unknown");
        updatedDetails.setMeasuredDateTime(new Timestamp(System.currentTimeMillis()));

        when(clinicalDataRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/clinicaldata/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isNotFound());

        verify(clinicalDataRepository, times(1)).findById(999L);
        verify(clinicalDataRepository, never()).save(any(ClinicalData.class));
    }

    /**
     * Test DELETE /api/clinicaldata/{id} endpoint deletes an existing clinical data record successfully.
     * Verifies that the endpoint returns HTTP 204 (NO CONTENT) and calls the delete method.
     * 
     * @throws Exception if the MockMvc request fails
     */
    @Test
    public void testDeleteClinicalData_ClinicalDataExists() throws Exception {
        when(clinicalDataRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/clinicaldata/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(clinicalDataRepository, times(1)).existsById(1L);
        verify(clinicalDataRepository, times(1)).deleteById(1L);
    }

    /**
     * Test DELETE /api/clinicaldata/{id} endpoint returns 404 when clinical data does not exist.
     * Verifies that the endpoint returns HTTP 404 (NOT FOUND) and does not attempt to delete.
     * 
     * @throws Exception if the MockMvc request fails
     */
    @Test
    public void testDeleteClinicalData_ClinicalDataNotFound() throws Exception {
        when(clinicalDataRepository.existsById(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/clinicaldata/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(clinicalDataRepository, times(1)).existsById(999L);
        verify(clinicalDataRepository, never()).deleteById(999L);
    }

    /**
     * Test POST /api/clinicaldata/save endpoint creates and saves clinical data for a specific patient.
     * Verifies that the endpoint returns HTTP 201 (CREATED) with the newly created clinical data record.
     * This endpoint accepts a DTO with patient ID and clinical data details.
     * 
     * @throws Exception if the MockMvc request fails
     */
    @Test
    public void testSaveClinicalDataByPatientId_SuccessfulCreation() throws Exception {
        ClinicalDataDTO clinicalDataDTO = new ClinicalDataDTO();
        clinicalDataDTO.setPatientId(1L);
        clinicalDataDTO.setComponentName("Glucose");
        clinicalDataDTO.setComponentValue("110 mg/dL");

        ClinicalData savedClinicalData = new ClinicalData();
        savedClinicalData.setId(4L);
        savedClinicalData.setComponentName("Glucose");
        savedClinicalData.setComponentValue("110 mg/dL");
        savedClinicalData.setPatient(testPatient);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(clinicalDataRepository.save(any(ClinicalData.class))).thenReturn(savedClinicalData);

        mockMvc.perform(post("/api/clinicaldata/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clinicalDataDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.componentName").value("Glucose"));

        verify(patientRepository, times(1)).findById(1L);
        verify(clinicalDataRepository, times(1)).save(any(ClinicalData.class));
    }

    /**
     * Test POST /api/clinicaldata/save endpoint returns 404 when specified patient does not exist.
     * Verifies that the endpoint returns HTTP 404 (NOT FOUND) with an error message and does not save any data.
     * Ensures referential integrity by validating patient existence before creating clinical data.
     * 
     * @throws Exception if the MockMvc request fails
     */
    @Test
    public void testSaveClinicalDataByPatientId_PatientNotFound() throws Exception {
        ClinicalDataDTO clinicalDataDTO = new ClinicalDataDTO();
        clinicalDataDTO.setPatientId(999L);
        clinicalDataDTO.setComponentName("Glucose");
        clinicalDataDTO.setComponentValue("110 mg/dL");

        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/clinicaldata/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clinicalDataDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Patient with ID 999 not found"));

        verify(patientRepository, times(1)).findById(999L);
        verify(clinicalDataRepository, never()).save(any(ClinicalData.class));
    }
}