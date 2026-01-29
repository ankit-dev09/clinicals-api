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
import com.ankit.patientclinicals.clinicalsapi.controllers.ClinicalDataController;
import com.ankit.patientclinicals.clinicalsapi.services.ClinicalDataService;
import com.ankit.patientclinicals.clinicalsapi.exceptions.CustomExceptionHandler;
import com.ankit.patientclinicals.clinicalsapi.exceptions.ResourceNotFoundException;
import com.ankit.patientclinicals.clinicalsapi.dtos.ClinicalDataDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for ClinicalDataController class.
 * Tests all CRUD operations, validation, and HTTP endpoints for ClinicalData management.
 * Uses ClinicalDataService with mocked repository layer.
 */
@ExtendWith(MockitoExtension.class)
public class ClinicalDataControllerTest {

    @Mock
    private ClinicalDataService clinicalDataService;

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
        mockMvc = MockMvcBuilders.standaloneSetup(clinicalDataController)
                .setControllerAdvice(new CustomExceptionHandler())
                .build();
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
     */
    @Test
    public void testGetAllClinicalData_ReturnsListSuccessfully() throws Exception {
        List<ClinicalData> clinicalDataList = Arrays.asList(testClinicalData, secondClinicalData);
        when(clinicalDataService.getAllClinicalData()).thenReturn(clinicalDataList);

        mockMvc.perform(get("/api/clinicaldata")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].componentName").value("Blood Pressure"))
                .andExpect(jsonPath("$[1].componentName").value("Heart Rate"));

        verify(clinicalDataService, times(1)).getAllClinicalData();
    }

    /**
     * Test GET /api/clinicaldata endpoint returns an empty list when no clinical data exists.
     */
    @Test
    public void testGetAllClinicalData_ReturnsEmptyList() throws Exception {
        when(clinicalDataService.getAllClinicalData()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/clinicaldata")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(clinicalDataService, times(1)).getAllClinicalData();
    }

    /**
     * Test GET /api/clinicaldata/{id} endpoint returns a specific clinical data record by ID.
     */
    @Test
    public void testGetClinicalDataById_ClinicalDataExists() throws Exception {
        when(clinicalDataService.getClinicalDataById(1L)).thenReturn(testClinicalData);

        mockMvc.perform(get("/api/clinicaldata/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.componentName").value("Blood Pressure"))
                .andExpect(jsonPath("$.componentValue").value("120/80"));

        verify(clinicalDataService, times(1)).getClinicalDataById(1L);
    }

    /**
     * Test GET /api/clinicaldata/{id} endpoint returns 404 when clinical data does not exist.
     */
    @Test
    public void testGetClinicalDataById_ClinicalDataNotFound() throws Exception {
        when(clinicalDataService.getClinicalDataById(999L))
                .thenThrow(new ResourceNotFoundException("Clinical data not found with ID: 999"));

        mockMvc.perform(get("/api/clinicaldata/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));

        verify(clinicalDataService, times(1)).getClinicalDataById(999L);
    }

    /**
     * Test POST /api/clinicaldata endpoint creates a new clinical data record successfully.
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

        when(clinicalDataService.createClinicalData(any(ClinicalData.class))).thenReturn(savedClinicalData);

        mockMvc.perform(post("/api/clinicaldata")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newClinicalData)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.componentName").value("Temperature"));

        verify(clinicalDataService, times(1)).createClinicalData(any(ClinicalData.class));
    }

    /**
     * Test POST /api/clinicaldata endpoint fails with 400 when componentName is empty.
     */
    @Test
    public void testCreateClinicalData_InvalidComponentName_Empty() throws Exception {
        ClinicalData invalidData = new ClinicalData();
        invalidData.setComponentName("");
        invalidData.setComponentValue("37.5°C");
        invalidData.setMeasuredDateTime(new Timestamp(System.currentTimeMillis()));

        mockMvc.perform(post("/api/clinicaldata")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest());

        verify(clinicalDataService, never()).createClinicalData(any(ClinicalData.class));
    }

    /**
     * Test POST /api/clinicaldata endpoint fails with 400 when componentValue is empty.
     */
    @Test
    public void testCreateClinicalData_InvalidComponentValue_Empty() throws Exception {
        ClinicalData invalidData = new ClinicalData();
        invalidData.setComponentName("Temperature");
        invalidData.setComponentValue("");
        invalidData.setMeasuredDateTime(new Timestamp(System.currentTimeMillis()));

        mockMvc.perform(post("/api/clinicaldata")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest());

        verify(clinicalDataService, never()).createClinicalData(any(ClinicalData.class));
    }

    /**
     * Test PUT /api/clinicaldata/{id} endpoint updates an existing clinical data record successfully.
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

        when(clinicalDataService.updateClinicalData(eq(1L), any(ClinicalData.class))).thenReturn(modifiedClinicalData);

        mockMvc.perform(put("/api/clinicaldata/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.componentValue").value("130/85"));

        verify(clinicalDataService, times(1)).updateClinicalData(eq(1L), any(ClinicalData.class));
    }

    /**
     * Test PUT /api/clinicaldata/{id} endpoint returns 404 when clinical data does not exist.
     */
    @Test
    public void testUpdateClinicalData_ClinicalDataNotFound() throws Exception {
        ClinicalData updatedDetails = new ClinicalData();
        updatedDetails.setComponentName("Unknown");
        updatedDetails.setComponentValue("Unknown");
        updatedDetails.setMeasuredDateTime(new Timestamp(System.currentTimeMillis()));

        when(clinicalDataService.updateClinicalData(eq(999L), any(ClinicalData.class)))
                .thenThrow(new ResourceNotFoundException("Clinical data not found with ID: 999"));

        mockMvc.perform(put("/api/clinicaldata/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));

        verify(clinicalDataService, times(1)).updateClinicalData(eq(999L), any(ClinicalData.class));
    }

    /**
     * Test DELETE /api/clinicaldata/{id} endpoint deletes an existing clinical data record successfully.
     */
    @Test
    public void testDeleteClinicalData_ClinicalDataExists() throws Exception {
        doNothing().when(clinicalDataService).deleteClinicalData(1L);

        mockMvc.perform(delete("/api/clinicaldata/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(clinicalDataService, times(1)).deleteClinicalData(1L);
    }

    /**
     * Test DELETE /api/clinicaldata/{id} endpoint returns 404 when clinical data does not exist.
     */
    @Test
    public void testDeleteClinicalData_ClinicalDataNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Clinical data not found with ID: 999"))
                .when(clinicalDataService).deleteClinicalData(999L);

        mockMvc.perform(delete("/api/clinicaldata/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));

        verify(clinicalDataService, times(1)).deleteClinicalData(999L);
    }

    /**
     * Test POST /api/clinicaldata/save endpoint creates and saves clinical data for a specific patient.
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

        when(clinicalDataService.saveClinicalDataByPatientId(any(ClinicalDataDTO.class))).thenReturn(savedClinicalData);

        mockMvc.perform(post("/api/clinicaldata/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clinicalDataDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.componentName").value("Glucose"));

        verify(clinicalDataService, times(1)).saveClinicalDataByPatientId(any(ClinicalDataDTO.class));
    }

    /**
     * Test POST /api/clinicaldata/save endpoint returns 404 when specified patient does not exist.
     */
    @Test
    public void testSaveClinicalDataByPatientId_PatientNotFound() throws Exception {
        ClinicalDataDTO clinicalDataDTO = new ClinicalDataDTO();
        clinicalDataDTO.setPatientId(999L);
        clinicalDataDTO.setComponentName("Glucose");
        clinicalDataDTO.setComponentValue("110 mg/dL");

        when(clinicalDataService.saveClinicalDataByPatientId(any(ClinicalDataDTO.class)))
                .thenThrow(new ResourceNotFoundException("Patient not found with ID: 999"));

        mockMvc.perform(post("/api/clinicaldata/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clinicalDataDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));

        verify(clinicalDataService, times(1)).saveClinicalDataByPatientId(any(ClinicalDataDTO.class));
    }

    /**
     * Test POST /api/clinicaldata/save endpoint fails with 400 when componentName is empty.
     */
    @Test
    public void testSaveClinicalDataByPatientId_InvalidComponentName() throws Exception {
        ClinicalDataDTO clinicalDataDTO = new ClinicalDataDTO();
        clinicalDataDTO.setPatientId(1L);
        clinicalDataDTO.setComponentName("");
        clinicalDataDTO.setComponentValue("110 mg/dL");

        mockMvc.perform(post("/api/clinicaldata/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clinicalDataDTO)))
                .andExpect(status().isBadRequest());

        verify(clinicalDataService, never()).saveClinicalDataByPatientId(any(ClinicalDataDTO.class));
    }

    /**
     * Test POST /api/clinicaldata/save endpoint fails with 400 when patientId is invalid.
     */
    @Test
    public void testSaveClinicalDataByPatientId_InvalidPatientId() throws Exception {
        ClinicalDataDTO clinicalDataDTO = new ClinicalDataDTO();
        clinicalDataDTO.setPatientId(0L);
        clinicalDataDTO.setComponentName("Glucose");
        clinicalDataDTO.setComponentValue("110 mg/dL");

        mockMvc.perform(post("/api/clinicaldata/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clinicalDataDTO)))
                .andExpect(status().isBadRequest());

        verify(clinicalDataService, never()).saveClinicalDataByPatientId(any(ClinicalDataDTO.class));
    }
}