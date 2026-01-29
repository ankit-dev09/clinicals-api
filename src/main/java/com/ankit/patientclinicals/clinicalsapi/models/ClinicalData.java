
package com.ankit.patientclinicals.clinicalsapi.models;

import jakarta.persistence.*;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "clinicaldata")
public class ClinicalData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "component_name", nullable = false)
    private String componentName;

    @Column(name = "component_value", nullable = false)
    private String componentValue;

    @Column(name = "measured_date_time", nullable = false)
    @CreationTimestamp
    private Timestamp measuredDateTime;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnore
    private Patient patient;

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public ClinicalData() {
    }

    public ClinicalData(String componentName, String componentValue, Timestamp measuredDateTime) {
        this.componentName = componentName;
        this.componentValue = componentValue;
        this.measuredDateTime = measuredDateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Timestamp getMeasuredDateTime() {
        return measuredDateTime;
    }

    public void setMeasuredDateTime(Timestamp measuredDateTime) {
        this.measuredDateTime = measuredDateTime;
    }

}