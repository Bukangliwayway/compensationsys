package com.haymeb.compensationsys.models;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

public class EmployeeDTO {

	@NotEmpty(message = "First Name Is Required!")
	private String firstName;

	private String middleName;

	@NotEmpty(message = "Last Name Is Required!")
	private String lastName;

	@NotNull(message = "Birth Date Is Required!")
	@Past(message = "Birth date must be in the past")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date birthDate;

	@NotEmpty(message = "Position Is Required!")
	private String position;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}
}
