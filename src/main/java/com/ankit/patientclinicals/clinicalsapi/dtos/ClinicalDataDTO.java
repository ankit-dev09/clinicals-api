package com.ankit.patientclinicals.clinicalsapi.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public class ClinicalDataDTO {

    @NotBlank(message = "Component name is required and cannot be empty")
    private String componentName;

    @NotBlank(message = "Component value is required and cannot be empty")
    private String componentValue;

    @NotNull(message = "Patient ID is required")
    @Min(value = 1, message = "Patient ID must be a positive number")
    private Long patientId;

    public ClinicalDataDTO() {
    }

    public ClinicalDataDTO(String componentName, String componentValue, Long patientId) {
        this.componentName = componentName;
        this.componentValue = componentValue;
        this.patientId = patientId;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getComponentValue() {
        return componentValue;
    }

    public void setComponentValue(String componentValue) {
        this.componentValue = componentValue;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }
}
