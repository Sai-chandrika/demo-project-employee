package com.example.organizationEmployeeProjectManagement.security.dto.projectDto;

import com.example.organizationEmployeeProjectManagement.security.dto.employeeDto.EmployeeDto;
import com.example.organizationEmployeeProjectManagement.security.dto.organizationDto.OrganizationDto;
import com.example.organizationEmployeeProjectManagement.security.enums.projectStatus.ProjectStatus;
import com.example.organizationEmployeeProjectManagement.validation.Validation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chandrika
 * @ProjectName Organization_Employee_ProjectManagement
 * @since 28-03-2023
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto {
    private Long id;
    private  String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private ProjectStatus status;
    List<EmployeeDto> employee=new ArrayList<>();
    private OrganizationDto organization;

    public String validateRequest() {
        if (!Validation.isValid(this.getName())) {
            return "projectName is mandatory";
        }else
            if(!Validation.isValid(this.getStartDate())){
                return "start date is mandatory";
            }else if(!Validation.isValidProjectName(this.getName())){
                return " ProjectName Contains Only Alphabets";
            }
        return "";
    }
}
