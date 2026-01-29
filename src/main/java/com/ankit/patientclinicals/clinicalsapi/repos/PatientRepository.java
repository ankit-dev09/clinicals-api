package com.ankit.patientclinicals.clinicalsapi.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ankit.patientclinicals.clinicalsapi.models.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

}
