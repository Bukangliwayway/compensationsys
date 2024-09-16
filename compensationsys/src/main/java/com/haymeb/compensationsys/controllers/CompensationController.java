package com.haymeb.compensationsys.controllers;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.haymeb.compensationsys.models.Compensation;
import com.haymeb.compensationsys.models.CompensationDTO;
import com.haymeb.compensationsys.models.Employee;
import com.haymeb.compensationsys.services.CompensationRepository;
import com.haymeb.compensationsys.services.EmployeeRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/compensations")
public class CompensationController {

	@Autowired
	private EmployeeRepository repo;

	@Autowired
	private CompensationRepository compensationRepository;

	@GetMapping("/view")
	public String viewMonthlyCompensations(@RequestParam int id, Model model, @RequestParam int year,
			@RequestParam int month) {

		List<Compensation> compensations = compensationRepository.findByEmployeeIdAndMonth(id, year, month);
		model.addAttribute("compensations", compensations);

		return "compensations/view";
	}

	@GetMapping({ "/create" })
	public String showCreateCompensation(@RequestParam int id, Model model) {
		CompensationDTO compensationDTO = new CompensationDTO();
		model.addAttribute("compensationDTO", compensationDTO);
		model.addAttribute("employee_id", id);

		compensationDTO.setEmployee_id(id);
		return "compensations/create";
	}

	@PostMapping({ "/create" })
	public String createCompensation(@Valid @ModelAttribute CompensationDTO compensationDTO, BindingResult res,
			Model model) {
		Compensation compensation = new Compensation();
		if (res.hasErrors()) {
			return "compensations/create";
		}

		int employeeID = compensationDTO.getEmployee_id();
		int year = compensationDTO.getDate().getYear();
		int month = compensationDTO.getDate().getMonthValue();

		if (compensationDTO.getType().equalsIgnoreCase("Salary")
				&& compensationRepository.hasSalaryCompensationForMonth(employeeID, year, month) == 1) {
			model.addAttribute("errorMessage", "There should be only 1 Salary Type Compensation for this month!");
			return "compensations/create";
		}

		// Description is Required for these Types
		if ((compensationDTO.getType().equalsIgnoreCase("Commission")
				|| compensationDTO.getType().equalsIgnoreCase("Bonus")
				|| compensationDTO.getType().equalsIgnoreCase("Allowance")
				|| compensationDTO.getType().equalsIgnoreCase("Adjustment"))
				&& compensationDTO.getDescription() == "") {
			model.addAttribute("errorMessage", "Description is Required on this Type!");
			return "compensations/create";
		}

		if ((compensationDTO.getType().equalsIgnoreCase("bonus")
				|| compensationDTO.getType().equalsIgnoreCase("commission")
				|| compensationDTO.getType().equalsIgnoreCase("allowance"))
				&& compensationDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
			model.addAttribute("errorMessage", "Amount should never be Zero or Lower on this Types!");
			return "compensations/create";
		}

		if (compensationDTO.getType().equalsIgnoreCase("adjustment")
				&& compensationDTO.getAmount().compareTo(BigDecimal.ZERO) == 0) {
			model.addAttribute("errorMessage", "Amount should never be Zero on this Types!");
			return "compensations/create";
		}

		Employee employee = repo.findById(employeeID).get();
		compensation.setAmount(compensationDTO.getAmount());
		compensation.setDate(compensationDTO.getDate());
		compensation.setDescription(compensationDTO.getDescription());
		compensation.setType(compensationDTO.getType());
		compensation.setEmployee(employee);

		compensationRepository.save(compensation);
		return "redirect:/compensations/view?id=" + compensation.getEmployee().getId() + "&month="
				+ compensation.getDate().getMonthValue() + "&year=" + compensation.getDate().getYear();
	}

	@GetMapping({ "/edit" })
	public String updateCompensationPage(Model model, @RequestParam int id) {
		Compensation compensation = compensationRepository.findById(id).get();
		model.addAttribute("compensation", compensation);

		CompensationDTO compensationDTO = new CompensationDTO();
		compensationDTO.setDate(compensation.getDate());
		compensationDTO.setDescription(compensation.getDescription());
		compensationDTO.setType(compensation.getType());
		compensationDTO.setEmployee_id(compensation.getEmployee().getId());

		compensationDTO.setAmount(compensation.getAmount());
		model.addAttribute("compensationDTO", compensationDTO);
		return "compensations/edit";
	}

	@PostMapping({ "/edit" })
	public String updateCompensation(Model model, @RequestParam int id,
			@Valid @ModelAttribute CompensationDTO compensationDTO, BindingResult res) {
		Compensation compensation = compensationRepository.findById(id).get();

		if (res.hasErrors()) {
			return "compensations/edit";
		}

		if (res.hasErrors()) {
			return "compensations/create";
		}

		int employeeID = compensationDTO.getEmployee_id();
		int year = compensationDTO.getDate().getYear();
		int month = compensationDTO.getDate().getMonthValue();

		if (compensationDTO.getType().equalsIgnoreCase("Salary")
				&& compensationRepository.findSalaryCompensationIdForMonth(employeeID, year, month) != null
				&& compensationRepository.findSalaryCompensationIdForMonth(employeeID, year, month) != compensation
						.getId()) {
			model.addAttribute("errorMessage", "There should be only 1 Salary Type Compensation for this month!");
			return "compensations/create";
		}
		// Description is Required for these Types
		if ((compensationDTO.getType().equalsIgnoreCase("Commission")
				|| compensationDTO.getType().equalsIgnoreCase("Bonus")
				|| compensationDTO.getType().equalsIgnoreCase("Allowance")
				|| compensationDTO.getType().equalsIgnoreCase("Adjustment"))
				&& compensationDTO.getDescription() == "") {
			model.addAttribute("errorMessage", "Description is Required on this Type!");
			return "compensations/create";
		}

		if ((compensationDTO.getType().equalsIgnoreCase("bonus")
				|| compensationDTO.getType().equalsIgnoreCase("commission")
				|| compensationDTO.getType().equalsIgnoreCase("allowance"))
				&& compensationDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
			model.addAttribute("errorMessage", "Amount should never be Zero or Lower on this Types!");
			return "compensations/create";
		}

		if (compensationDTO.getType().equalsIgnoreCase("adjustment")
				&& compensationDTO.getAmount().compareTo(BigDecimal.ZERO) == 0) {
			model.addAttribute("errorMessage", "Amount should never be Zero on this Types!");
			return "compensations/create";
		}

		Employee employee = repo.findById(employeeID).get();
		compensation.setAmount(compensationDTO.getAmount());
		compensation.setDate(compensationDTO.getDate());
		compensation.setDescription(compensationDTO.getDescription());
		compensation.setType(compensationDTO.getType());
		compensation.setEmployee(employee);

		compensationRepository.save(compensation);
		return "redirect:/compensations/view?id=" + compensation.getEmployee().getId() + "&month="
				+ compensation.getDate().getMonthValue() + "&year=" + compensation.getDate().getYear();
	}
}
