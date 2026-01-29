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
import com.ankit.patientclinicals.clinicalsapi.repos.PatientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for PatientController class.
 * Tests all CRUD operations and HTTP endpoints for Patient management.
 */
@ExtendWith(MockitoExtension.class)
public class PatientControllerTest {

    @Mock
    private PatientRepository patientRepository;

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
        mockMvc = MockMvcBuilders.standaloneSetup(patientController).build();
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
     * 
     * @throws Exception if the MockMvc request fails
     */
    @Test
    public void testGetAllPatients_ReturnsListSuccessfully() throws Exception {
        List<Patient> patientList = Arrays.asList(testPatient, secondPatient);
        when(patientRepository.findAll()).thenReturn(patientList);

        mockMvc.perform(get("/api/patients")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Alice"))
                .andExpect(jsonPath("$[1].firstName").value("Bob"));

        verify(patientRepository, times(1)).findAll();
    }

    /**
     * Test GET /api/patients endpoint returns an empty list when no patients exist.
     * Verifies that the endpoint returns HTTP 200 (OK) with an empty array.
     * 
     * @throws Exception if the MockMvc request fails
     */
    @Test
    public void testGetAllPatients_ReturnsEmptyList() throws Exception {
        when(patientRepository.findAll()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/patients")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(patientRepository, times(1)).findAll();
    }

    /**
     * Test GET /api/patients/{id} endpoint returns a specific patient by ID.
     * Verifies that the endpoint returns HTTP 200 (OK) with the correct patient details.
     * 
     * @throws Exception if the MockMvc request fails
     */
    @Test
    public void testGetPatientById_PatientExists() throws Exception {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));

        mockMvc.perform(get("/api/patients/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Johnson"))
                .andExpect(jsonPath("$.age").value(28));

        verify(patientRepository, times(1)).findById(1L);
    }

    /**
     * Test GET /api/patients/{id} endpoint returns 404 when patient does not exist.
     * Verifies that the endpoint returns HTTP 404 (NOT FOUND) for a non-existent patient ID.
     * 
     * @throws Exception if the MockMvc request fails
     */
    @Test
    public void testGetPatientById_PatientNotFound() throws Exception {
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/patients/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(patientRepository, times(1)).findById(999L);
    }

    /**
     * Test POST /api/patients endpoint creates a new patient successfully.
     * Verifies that the endpoint returns HTTP 201 (CREATED) with the newly created patient's ID.
     * 
     * @throws Exception if the MockMvc request fails
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

        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPatient)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.firstName").value("Charlie"));

        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    /**
     * Test PUT /api/patients/{id} endpoint updates an existing patient successfully.
     * Verifies that the endpoint returns HTTP 200 (OK) with the updated patient details.
     * 
     * @throws Exception if the MockMvc request fails
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

        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(patientRepository.save(any(Patient.class))).thenReturn(modifiedPatient);

        mockMvc.perform(put("/api/patients/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.lastName").value("Williams"))
                .andExpect(jsonPath("$.age").value(29));

        verify(patientRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    /**
     * Test PUT /api/patients/{id} endpoint returns 404 when patient does not exist.
     * Verifies that the endpoint returns HTTP 404 (NOT FOUND) and does not save any data.
     * 
     * @throws Exception if the MockMvc request fails
     */
    @Test
    public void testUpdatePatient_PatientNotFound() throws Exception {
        Patient updatedDetails = new Patient();
        updatedDetails.setFirstName("Unknown");
        updatedDetails.setLastName("User");
        updatedDetails.setAge(50);

        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/patients/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isNotFound());

        verify(patientRepository, times(1)).findById(999L);
        verify(patientRepository, never()).save(any(Patient.class));
    }

    /**
     * Test DELETE /api/patients/{id} endpoint deletes an existing patient successfully.
     * Verifies that the endpoint returns HTTP 204 (NO CONTENT) and calls the delete method.
     * 
     * @throws Exception if the MockMvc request fails
     */
    @Test
    public void testDeletePatient_PatientExists() throws Exception {
        when(patientRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/patients/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(patientRepository, times(1)).existsById(1L);
        verify(patientRepository, times(1)).deleteById(1L);
    }

    /**
     * Test DELETE /api/patients/{id} endpoint returns 404 when patient does not exist.
     * Verifies that the endpoint returns HTTP 404 (NOT FOUND) and does not attempt to delete.
     * 
     * @throws Exception if the MockMvc request fails
     */
    @Test
    public void testDeletePatient_PatientNotFound() throws Exception {
        when(patientRepository.existsById(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/patients/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(patientRepository, times(1)).existsById(999L);
        verify(patientRepository, never()).deleteById(999L);
    }
}