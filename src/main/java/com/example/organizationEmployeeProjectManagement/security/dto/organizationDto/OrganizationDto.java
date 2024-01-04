package com.example.organizationEmployeeProjectManagement.security.dto.organizationDto;

import com.example.organizationEmployeeProjectManagement.exception.NullPointerException;
import com.example.organizationEmployeeProjectManagement.security.dto.projectDto.ProjectDto;
import com.example.organizationEmployeeProjectManagement.security.dto.employeeDto.EmployeeDto;
import com.example.organizationEmployeeProjectManagement.validation.Validation;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganizationDto {
    private Long id;
    private String name;
    private String  location;
    List<EmployeeDto> employees=new ArrayList<>();
    List<ProjectDto> projects=new ArrayList<>();

    public String validateRequest(){
        if (!Validation.isValid(this.getName())) {
            return "name must be provided"  ;
        }  else if (!Validation.isValid(this.getLocation())) {
            return "Location must be provided";
        }
        return " ";
    }
}
