package com.patient.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.patient.dto.DoctorDTO;
import com.patient.model.Patient;
import com.patient.repository.PatientRepository;


@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "http://localhost:3000/", allowedHeaders = "*", exposedHeaders = {"Access-Control-Allow-Origin"},  methods = {RequestMethod.OPTIONS, RequestMethod.PUT, RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE})
public class PatientController {

    //private static final String DOCTOR_SERVICE_URL = "http://localhost:8001/api/doctors/";
    private static final String DOCTOR_SERVICE_URL = "http://doctorapp:8001/api/doctors/";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PatientRepository patientRepository;

    @PostMapping
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
    	// Write code to create a new Patient and save to the database, return the
        // savedPatient along with ResponseEntity with HTTP status CREATED on successful
        // creation.
        // if there is no doctor with the specified id in patient object you must throw
        // IllegalArgumentException as "Doctor with ID {doctorId} not found."
        // Use restTemplate for service communication
        ResponseEntity<DoctorDTO> response = restTemplate.getForEntity(DOCTOR_SERVICE_URL + patient.getDoctorId(), DoctorDTO.class);
        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new IllegalArgumentException("Doctor with ID " + patient.getDoctorId() + " not found.");
        }

        Patient savedPatient = patientRepository.save(patient);
        return new ResponseEntity<>(savedPatient, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        // Write code to get a single patient from the database
        // if found return ResponseEntity with the patient object along with HTTP status
        // OK
        // if not found return ResponseEntity with empty object with HTTP status
        // NOT_FOUND
        Optional<Patient> patient = patientRepository.findById(id);
        if (patient.isPresent()) {
            return new ResponseEntity<>(patient.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
   
    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @RequestBody Patient updatedPatient) {
        // Write code to update a patient object using given patient id
        // if patient with given id is not found return ResponseEntity with empty object
        // with HTTP status NOT_FOUND
        // if found return ResponseEntity with updated patient object along with HTTP
        // status OK
        // if there is no doctor with the specified id in updated patient object you
        // must throw
        // IllegalArgumentException as "Doctor with ID {doctorId} not found."
    	System.out.println("patient data: "+updatedPatient);
        Optional<Patient> existingPatient = patientRepository.findById(id);
        if (!existingPatient.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        
        ResponseEntity<DoctorDTO> response = restTemplate.getForEntity(DOCTOR_SERVICE_URL + updatedPatient.getDoctorId(), DoctorDTO.class);
        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new IllegalArgumentException("Doctor with ID " + updatedPatient.getDoctorId() + " not found.");
        }

        Patient patient = existingPatient.get();
        patient.setName(updatedPatient.getName());
        patient.setAge(updatedPatient.getAge());
        patient.setEmergencyContactPhone(updatedPatient.getEmergencyContactPhone());
        patient.setDoctorId(updatedPatient.getDoctorId());
        Patient savedPatient = patientRepository.save(patient);
        return new ResponseEntity<>(savedPatient, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        // Write code to delete a patient object using given patient id
        // if patient with given id is not found return Void ResponseEntity with HTTP
        // status NOT_FOUND
        // if found return a Void ResponseEntity along with HTTP status NO_CONTENT
        Optional<Patient> existingPatient = patientRepository.findById(id);
        if (!existingPatient.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        patientRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
    	// Write code to get all patients data from the database and return them as a
        // ResponseEntity along with HTTP status OK
        List<Patient> patients = patientRepository.findAll();
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    @GetMapping("/doctor/id/{doctorId}")
    public ResponseEntity<List<Patient>> getPatientsByDoctorId(@PathVariable Long doctorId) {
        // Write code to get all patients data by doctorId from the database and return
        // them as a ResponseEntity along with HTTP status OK
        List<Patient> patients = patientRepository.findByDoctorId(doctorId);
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }
    //search by Doctor name and see all the patients list
    @GetMapping("/doctor/{doctorName}")
    public ResponseEntity<List<Patient>> getPatientsByDoctorName(@PathVariable String doctorName) {
    	 // Write code to get patients data by doctorName from the database and return them as a
        // ResponseEntity along with HTTP status OK
        // if patient with given doctor name is not found return empty ResponseEntity with HTTP
        // status NOT_FOUND
        // Find out the availability of doctorName from the doctor service url /api/doctors/searchByName?name=
        // Hint: Refer DoctorDTO class and Doctor model.
 
        System.out.println("Searching for doctors with name: " + doctorName);

        ResponseEntity<List<DoctorDTO>> response = restTemplate.exchange(
            DOCTOR_SERVICE_URL + "searchByName?name=" + doctorName,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<DoctorDTO>>() {}
        );

   
        System.out.println("Response from doctor service: " + response.getBody());

        List<DoctorDTO> doctors = response.getBody();
        if (doctors == null || doctors.isEmpty()) {
            System.out.println("No doctors found with name: " + doctorName);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Patient> patients = new ArrayList<>();
        for (DoctorDTO doctor : doctors) {
          
            System.out.println("Searching for patients with doctor ID: " + doctor.getId());
            patients.addAll(patientRepository.findByDoctorId(doctor.getId()));
        }

       
        System.out.println("Patients found: " + patients);

        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

}


