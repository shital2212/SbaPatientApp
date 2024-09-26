package com.patient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DoctorDTO {

    private Long id;
    private String name;
    private String specialty;
    private String hospitalAffiliation;
    private String contactPhone;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSpecialty() {
		return specialty;
	}
	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}
	public String getHospitalAffiliation() {
		return hospitalAffiliation;
	}
	public void setHospitalAffiliation(String hospitalAffiliation) {
		this.hospitalAffiliation = hospitalAffiliation;
	}
	public String getContactPhone() {
		return contactPhone;
	}
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

}
