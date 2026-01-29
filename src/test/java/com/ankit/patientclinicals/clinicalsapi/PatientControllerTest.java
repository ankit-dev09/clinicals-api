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

import com.ankit.patientclinicals.clinicalsapi.controllers.PatientController;
import com.ankit.patientclinicals.clinicalsapi.models.Patient;
import com.ankit.patientclinicals.clinicalsapi.services.PatientService;
import com.ankit.patientclinicals.clinicalsapi.exceptions.CustomExceptionHandler;
import com.ankit.patientclinicals.clinicalsapi.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for PatientController class.
 * Tests all CRUD operations, validation, and HTTP endpoints for Patient management.
 * Uses PatientService with mocked repository layer.
 */
@ExtendWith(MockitoExtension.class)
public class PatientControllerTest {

    @Mock
    private PatientService patientService;

    @InjectMocks
    private PatientController patientController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Patient testPatient;
    private Patient secondPatient;

    /**
     * Initialize MockMvc, ObjectMapper, and test Patient objects before each test.
     * Sets up two test patient instances with different data for use in test cases.
     */
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(patientController)
                .setControllerAdvice(new CustomExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        
        testPatient = new Patient();
        testPatient.setId(1L);
        testPatient.setFirstName("Alice");
        testPatient.setLastName("Johnson");
        testPatient.setAge(28);

        secondPatient = new Patient();
        secondPatient.setId(2L);
        secondPatient.setFirstName("Bob");
        secondPatient.setLastName("Smith");
        secondPatient.setAge(45);
    }

    /**
     * Test GET /api/patients endpoint returns a list of all patients successfully.
     * Verifies that the endpoint returns HTTP 200 (OK) with a list containing 2 patients.
     */
    @Test
    public void testGetAllPatients_ReturnsListSuccessfully() throws Exception {
        List<Patient> patientList = Arrays.asList(testPatient, secondPatient);
        when(patientService.getAllPatients()).thenReturn(patientList);

        mockMvc.perform(get("/api/patients")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Alice"))
                .andExpect(jsonPath("$[1].firstName").value("Bob"));

        verify(patientService, times(1)).getAllPatients();
    }

    /**
     * Test GET /api/patients endpoint returns an empty list when no patients exist.
     */
    @Test
    public void testGetAllPatients_ReturnsEmptyList() throws Exception {
        when(patientService.getAllPatients()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/patients")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(patientService, times(1)).getAllPatients();
    }

    /**
     * Test GET /api/patients/{id} endpoint returns a specific patient by ID.
     */
    @Test
    public void testGetPatientById_PatientExists() throws Exception {
        when(patientService.getPatientById(1L)).thenReturn(testPatient);

        mockMvc.perform(get("/api/patients/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Johnson"))
                .andExpect(jsonPath("$.age").value(28));

        verify(patientService, times(1)).getPatientById(1L);
    }

    /**
     * Test GET /api/patients/{id} endpoint returns 404 when patient does not exist.
     */
    @Test
    public void testGetPatientById_PatientNotFound() throws Exception {
        when(patientService.getPatientById(999L))
                .thenThrow(new ResourceNotFoundException("Patient not found with ID: 999"));

        mockMvc.perform(get("/api/patients/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Patient not found with ID: 999"));

        verify(patientService, times(1)).getPatientById(999L);
    }

    /**
     * Test POST /api/patients endpoint creates a new patient successfully.
     */
    @Test
    public void testCreatePatient_SuccessfulCreation() throws Exception {
        Patient newPatient = new Patient();
        newPatient.setFirstName("Charlie");
        newPatient.setLastName("Davis");
        newPatient.setAge(35);

        Patient savedPatient = new Patient();
        savedPatient.setId(3L);
        savedPatient.setFirstName("Charlie");
        savedPatient.setLastName("Davis");
        savedPatient.setAge(35);

        when(patientService.createPatient(any(Patient.class))).thenReturn(savedPatient);

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPatient)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.firstName").value("Charlie"));

        verify(patientService, times(1)).createPatient(any(Patient.class));
    }

    /**
     * Test POST /api/patients endpoint fails with 400 when firstName is empty.
     */
    @Test
    public void testCreatePatient_InvalidFirstName_Empty() throws Exception {
        Patient invalidPatient = new Patient();
        invalidPatient.setFirstName("");
        invalidPatient.setLastName("Davis");
        invalidPatient.setAge(35);

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPatient)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));

        verify(patientService, never()).createPatient(any(Patient.class));
    }

    /**
     * Test POST /api/patients endpoint fails with 400 when age is negative.
     */
    @Test
    public void testCreatePatient_InvalidAge_Negative() throws Exception {
        Patient invalidPatient = new Patient();
        invalidPatient.setFirstName("Charlie");
        invalidPatient.setLastName("Davis");
        invalidPatient.setAge(-5);

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPatient)))
                .andExpect(status().isBadRequest());

        verify(patientService, never()).createPatient(any(Patient.class));
    }

    /**
     * Test POST /api/patients endpoint fails with 400 when age exceeds 150.
     */
    @Test
    public void testCreatePatient_InvalidAge_TooHigh() throws Exception {
        Patient invalidPatient = new Patient();
        invalidPatient.setFirstName("Charlie");
        invalidPatient.setLastName("Davis");
        invalidPatient.setAge(200);

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPatient)))
                .andExpect(status().isBadRequest());

        verify(patientService, never()).createPatient(any(Patient.class));
    }

    /**
     * Test PUT /api/patients/{id} endpoint updates an existing patient successfully.
     */
    @Test
    public void testUpdatePatient_PatientExists() throws Exception {
        Patient updatedDetails = new Patient();
        updatedDetails.setFirstName("Alice");
        updatedDetails.setLastName("Williams");
        updatedDetails.setAge(29);

        Patient modifiedPatient = new Patient();
        modifiedPatient.setId(1L);
        modifiedPatient.setFirstName("Alice");
        modifiedPatient.setLastName("Williams");
        modifiedPatient.setAge(29);

        when(patientService.updatePatient(eq(1L), any(Patient.class))).thenReturn(modifiedPatient);

        mockMvc.perform(put("/api/patients/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.lastName").value("Williams"))
                .andExpect(jsonPath("$.age").value(29));

        verify(patientService, times(1)).updatePatient(eq(1L), any(Patient.class));
    }

    /**
     * Test PUT /api/patients/{id} endpoint returns 404 when patient does not exist.
     */
    @Test
    public void testUpdatePatient_PatientNotFound() throws Exception {
        Patient updatedDetails = new Patient();
        updatedDetails.setFirstName("Unknown");
        updatedDetails.setLastName("User");
        updatedDetails.setAge(50);

        when(patientService.updatePatient(eq(999L), any(Patient.class)))
                .thenThrow(new ResourceNotFoundException("Patient not found with ID: 999"));

        mockMvc.perform(put("/api/patients/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));

        verify(patientService, times(1)).updatePatient(eq(999L), any(Patient.class));
    }

    /**
     * Test DELETE /api/patients/{id} endpoint deletes an existing patient successfully.
     */
    @Test
    public void testDeletePatient_PatientExists() throws Exception {
        doNothing().when(patientService).deletePatient(1L);

        mockMvc.perform(delete("/api/patients/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(patientService, times(1)).deletePatient(1L);
    }

    /**
     * Test DELETE /api/patients/{id} endpoint returns 404 when patient does not exist.
     */
    @Test
    public void testDeletePatient_PatientNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Patient not found with ID: 999"))
                .when(patientService).deletePatient(999L);

        mockMvc.perform(delete("/api/patients/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));

        verify(patientService, times(1)).deletePatient(999L);
    }

    /**
     * Test that invalid ID (non-positive) throws IllegalArgumentException.
     */
    @Test
    public void testGetPatientById_InvalidId() throws Exception {
        when(patientService.getPatientById(0L))
                .thenThrow(new IllegalArgumentException("Patient ID must be a positive number"));

        mockMvc.perform(get("/api/patients/{id}", 0L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}