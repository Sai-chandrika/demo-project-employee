package com.example.organizationEmployeeProjectManagement.security.controller.empProjectController;

import com.example.organizationEmployeeProjectManagement.security.dto.empProjectDto.EmployeeProjectDto;
import com.example.organizationEmployeeProjectManagement.security.dto.genericResponce.GenericResponse;
import com.example.organizationEmployeeProjectManagement.security.service.empProjectService.EmployeeProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author chandrika
 * @ProjectName Organization_Employee_ProjectManagement
 * @since 30-03-2023
 */
@RestController
@RequestMapping("/api/v1/employee_project")
public class EmployeeProjectController {
    @Autowired
    EmployeeProjectService employeeProjectService;
@PostMapping("/save")
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN','MANAGER')")
   public GenericResponse save(@RequestBody EmployeeProjectDto employeeProjectDto){
    return employeeProjectService.save(employeeProjectDto);
   }
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('ADMIN')")
    GenericResponse update(@RequestBody EmployeeProjectDto employeeProjectDto){
        return employeeProjectService.update(employeeProjectDto);
    }
    @GetMapping("/getAll/{offset}/{pageNo}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
   public GenericResponse getAll(@PathVariable int offset,@PathVariable int pageNo){
    return  employeeProjectService.getAll(offset,pageNo);
    }
}
