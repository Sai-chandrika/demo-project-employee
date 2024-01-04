package com.example.organizationEmployeeProjectManagement.security.dto.empProjectDto;

import com.example.organizationEmployeeProjectManagement.security.dto.projectDto.ProjectDto;
import com.example.organizationEmployeeProjectManagement.security.dto.employeeDto.EmployeeDto;
import com.example.organizationEmployeeProjectManagement.security.enums.empProjectStatus.EmployeeProjectStatus;
import com.example.organizationEmployeeProjectManagement.validation.Validation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * @author chandrika
 * @ProjectName Organization_Employee_ProjectManagement
 * @since 30-03-2023
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeProjectDto {
    private  Long id;
    private ProjectDto project=new ProjectDto();
    private EmployeeDto employee=new EmployeeDto();
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

    public String validateRequest() {
        if (!Validation.isValid(this.getStartDate())) {
            return "start date is mandatory";
        }else
            if(!Validation.isValid(this.getStatus())){
                return "status is mandatory";
            }
        return "";
    }
}