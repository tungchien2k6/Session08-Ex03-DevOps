package com.ra.appointmentservice.service.impl;

import com.ra.appointmentservice.dto.request.AppointmentRequestDTO;
import com.ra.appointmentservice.dto.response.AppointmentResponseDTO;
import com.ra.appointmentservice.entity.Appointment;
import com.ra.appointmentservice.repository.AppointmentRepository;
import com.ra.appointmentservice.service.AppointmentService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final RestTemplate restTemplate;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, RestTemplate restTemplate) {
        this.appointmentRepository = appointmentRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    @Retry(name = "patientRetry")
    @CircuitBreaker(name = "doctorServiceCB", fallbackMethod = "fallbackCreateAppointment")
    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO request) {
        try {
            String patientUrl = "http://patient-service/api/v1/patients/" + request.getPatientId();
            restTemplate.getForObject(patientUrl, Object.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bệnh nhân với ID: " + request.getPatientId());
        }

        try {
            String doctorUrl = "http://doctor-service/api/v1/doctors/" + request.getDoctorId();
            restTemplate.getForObject(doctorUrl, Object.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bác sĩ với ID: " + request.getDoctorId());
        }

        Appointment appointment = Appointment.builder()
                .patientId(request.getPatientId())
                .doctorId(request.getDoctorId())
                .appointmentDate(request.getAppointmentDate())
                .reason(request.getReason())
                .status("PENDING")
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return AppointmentResponseDTO.builder()
                .id(savedAppointment.getId())
                .patientId(savedAppointment.getPatientId())
                .doctorId(savedAppointment.getDoctorId())
                .appointmentDate(savedAppointment.getAppointmentDate())
                .reason(savedAppointment.getReason())
                .status(savedAppointment.getStatus())
                .build();
    }

    public AppointmentResponseDTO fallbackCreateAppointment(AppointmentRequestDTO request, Exception e) {
        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Hiện tại không thể kiểm tra thông tin bác sĩ, vui lòng thử lại sau vài giây"
        );
    }
}