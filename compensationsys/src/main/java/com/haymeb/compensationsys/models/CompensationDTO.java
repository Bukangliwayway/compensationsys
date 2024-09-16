package com.haymeb.compensationsys.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

public class CompensationDTO {

	private int employee_id;

	@NotEmpty(message = "Type is Required!")
	private String type;

	@NotNull(message = "Amount is Required!")
	private BigDecimal amount;

	private String description;

	@NotNull(message = "Date Is Required!")
	@Past(message = "Date must be in the past")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;

	public int getEmployee_id() {
		return employee_id;
	}

	public void setEmployee_id(int employee_id) {
		this.employee_id = employee_id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

}
