package com.ra.patientservice.service.impl;

import com.ra.patientservice.dto.request.PatientRequestDTO;
import com.ra.patientservice.dto.response.PatientResponseDTO;
import com.ra.patientservice.entity.Patient;
import com.ra.patientservice.repository.PatientRepository;
import com.ra.patientservice.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public PatientResponseDTO createPatient(PatientRequestDTO request) {
        Patient patient = Patient.builder()
                .fullName(request.getFullName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .medicalHistory(request.getMedicalHistory())
                .build();

        Patient savedPatient = patientRepository.save(patient);

        return PatientResponseDTO.builder()
                .id(savedPatient.getId())
                .fullName(savedPatient.getFullName())
                .dateOfBirth(savedPatient.getDateOfBirth())
                .gender(savedPatient.getGender())
                .phoneNumber(savedPatient.getPhoneNumber())
                .address(savedPatient.getAddress())
                .medicalHistory(savedPatient.getMedicalHistory())
                .build();
    }

    @Override
    public PatientResponseDTO getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy bệnh nhân với ID: " + id
                ));

        return mapToResponseDTO(patient);
    }

    private PatientResponseDTO mapToResponseDTO(Patient patient) {
        return PatientResponseDTO.builder()
                .id(patient.getId())
                .fullName(patient.getFullName())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .phoneNumber(patient.getPhoneNumber())
                .address(patient.getAddress())
                .medicalHistory(patient.getMedicalHistory())
                .build();
    }
}