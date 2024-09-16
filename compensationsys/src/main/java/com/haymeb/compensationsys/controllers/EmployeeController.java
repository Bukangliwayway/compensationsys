package com.haymeb.compensationsys.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.haymeb.compensationsys.models.Compensation;
import com.haymeb.compensationsys.models.Employee;
import com.haymeb.compensationsys.models.EmployeeDTO;
import com.haymeb.compensationsys.models.MonthlyNetCompensation;
import com.haymeb.compensationsys.services.CompensationRepository;
import com.haymeb.compensationsys.services.EmployeeRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

	@Autowired
	private EmployeeRepository repo;

	@Autowired
	private CompensationRepository compensationRepository;

	@GetMapping({ "", "/" })
	public String searchEmployees(Model model, @RequestParam(defaultValue = "") String firstName,
			@RequestParam(defaultValue = "") String lastName, @RequestParam(defaultValue = "") String position) {
		List<Employee> employees = repo.searchEmployees(firstName, lastName, position);
		model.addAttribute("employees", employees);
		return "employees/index";
	}

	@GetMapping({ "/create" })
	public String showCreateEmployees(Model model) {
		EmployeeDTO employeeDTO = new EmployeeDTO();
		model.addAttribute("employeeDTO", employeeDTO);
		return "employees/create";
	}

	@PostMapping({ "/create" })
	public String createEmployees(@Valid @ModelAttribute EmployeeDTO employeeDTO, BindingResult res, Model model) {
		Employee employee = new Employee();

		if (res.hasErrors()) {
			return "employees/create";
		}
		employee.setFirstName(employeeDTO.getFirstName());
		employee.setMiddleName(employeeDTO.getMiddleName());
		employee.setLastName(employeeDTO.getLastName());
		employee.setBirthDate(employeeDTO.getBirthDate());
		employee.setPosition(employeeDTO.getPosition());

		if (repo.duplicateEmployees(employee.getFirstName(), employee.getLastName(), employee.getBirthDate())) {
			model.addAttribute("errorMessage", "This Employee is Already in the System");
			return "employees/create";
		}
		repo.save(employee);
		return "redirect:/employees";
	}

	@GetMapping({ "/edit" })
	public String showCreateEmployees(Model model, @RequestParam int id) {
		Employee employee = repo.findById(id).get();
		model.addAttribute("employee", employee);

		EmployeeDTO employeeDTO = new EmployeeDTO();
		employeeDTO.setFirstName(employee.getFirstName());
		employeeDTO.setMiddleName(employee.getMiddleName());
		employeeDTO.setLastName(employee.getLastName());
		employeeDTO.setBirthDate(employee.getBirthDate());
		employeeDTO.setPosition(employee.getPosition());
		model.addAttribute("employeeDTO", employeeDTO);
		return "employees/edit";
	}

	@PostMapping({ "/edit" })
	public String updateEmployees(Model model, @RequestParam int id, @Valid @ModelAttribute EmployeeDTO employeeDTO,
			BindingResult res) {
		Employee employee = repo.findById(id).get();

		if (res.hasErrors()) {
			return "employees/edit";
		}

		employee.setFirstName(employeeDTO.getFirstName());
		employee.setMiddleName(employeeDTO.getMiddleName());
		employee.setLastName(employeeDTO.getLastName());
		employee.setBirthDate(employeeDTO.getBirthDate());
		employee.setPosition(employeeDTO.getPosition());

		repo.save(employee);
		return "redirect:/employees";
	}

	@GetMapping({ "/view" })
	public String viewEmployees(Model model, @RequestParam int id) {
		Employee employee = repo.findById(id).get();
		model.addAttribute("employee", employee);

		return "employees/view";
	}

	@GetMapping("/compensation")
	public String handleCompensation(@RequestParam int id, Model model,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

		Employee employee = repo.findById(id).get();
		model.addAttribute("employee", employee);

		if (startDate == null) {
			startDate = LocalDate.MIN; // Set to the earliest possible date
		}
		if (endDate == null) {
			endDate = LocalDate.MAX; // Set to the latest possible date
		}

		if (endDate.isBefore(startDate)) {
			model.addAttribute("dateError", "End date must be later than or equal to start date.");
			return "employees/view";
		}

		List<Compensation> compensations = compensationRepository.findByEmployeeIdAndDateBetween(employee.getId(),
				startDate, endDate);

		if (compensations.isEmpty()) {
			System.out.println("  No compensations found for this criteria.");
		} else {
			for (Compensation compensation : compensations) {
				System.out.println("  - ID: " + compensation.getId());
				System.out.println("    Employee: " + compensation.getEmployee().getFirstName() + " ("
						+ compensation.getEmployee().getId() + ")"); // Assuming Employee has a getName() method
				System.out.println("    Type: " + compensation.getType());
				System.out.println("    Amount: " + compensation.getAmount());
				System.out.println("    Description: " + compensation.getDescription());
				System.out.println("    Date: " + compensation.getDate());
			}
		}

		List<MonthlyNetCompensation> monthlyNetCompensations = processCompensations(compensations, id);

		// Now you can process the monthlyNetCompensations list
		for (MonthlyNetCompensation netComp : monthlyNetCompensations) {
			// Do something with the monthly net compensation data
			System.out.println("Employee ID: " + netComp.getEmployee_id());
			System.out.println("Year: " + netComp.getYear());
			System.out.println("Month: " + netComp.getMonth());
			System.out.println("Total: " + netComp.getTotal());
		}

		model.addAttribute("monthlyNetCompensations", monthlyNetCompensations);

		return "employees/monthlycompensations";
	}

	public List<MonthlyNetCompensation> processCompensations(List<Compensation> compensations, int employeeId) {
		Map<LocalDate, BigDecimal> monthlyTotals = new HashMap<>();

		// Calculate monthly totals
		for (Compensation compensation : compensations) {
			LocalDate date = compensation.getDate();
			int year = date.getYear();
			int month = date.getMonthValue();
			BigDecimal amount = compensation.getAmount();

			// Get or initialize monthly total for this month/year
			BigDecimal currentTotal = monthlyTotals.get(date);
			if (currentTotal == null) {
				currentTotal = BigDecimal.ZERO;
			}

			// Add compensation amount to monthly total
			currentTotal = currentTotal.add(amount);
			monthlyTotals.put(date, currentTotal);
		}

		// Convert monthly totals to MonthlyNetCompensation objects
		List<MonthlyNetCompensation> monthlyNetCompensations = new ArrayList<>();

		for (Map.Entry<LocalDate, BigDecimal> entry : monthlyTotals.entrySet()) {
			LocalDate date = entry.getKey();
			int year = date.getYear();
			int month = date.getMonthValue();
			BigDecimal total = entry.getValue();

			MonthlyNetCompensation netComp = new MonthlyNetCompensation();
			netComp.setEmployee_id(employeeId);
			netComp.setYear(year);
			netComp.setMonth(month);
			netComp.setTotal(total);

			monthlyNetCompensations.add(netComp);
		}

		// Sort by month and year descending
		monthlyNetCompensations.sort((c1, c2) -> {
			int yearComparison = Integer.compare(c2.getYear(), c1.getYear());
			if (yearComparison != 0) {
				return yearComparison;
			}
			return Integer.compare(c2.getMonth(), c1.getMonth());
		});

		return monthlyNetCompensations;
	}

	/*
	 * @GetMapping({ "", "/" }) public String viewEmployees(Model
	 * model, @RequestParam(defaultValue = "") int id) { if (id == 0) { return
	 * "redirect:employees/index"; }
	 * 
	 * Employee employee = repo.findById(id).get(); model.addAttribute("employee",
	 * employee); return "employees/view"; }
	 */
}