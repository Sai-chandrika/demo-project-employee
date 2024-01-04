package com.example.organizationEmployeeProjectManagement.security.dto.employeeDto;

import com.example.organizationEmployeeProjectManagement.security.dto.empProjectDto.EmployeeProjectDto;
import com.example.organizationEmployeeProjectManagement.security.dto.organizationDto.OrganizationDto;
import com.example.organizationEmployeeProjectManagement.security.enums.roletype.RoleType;
import com.example.organizationEmployeeProjectManagement.validation.Validation;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeDto {
    private Long id;
    private  String contactNo;
    private String email;
    private RoleType roleType;
    private LocalDate dateOfJoining;
    private String name;
    private  String searchKey;
    private String password;
    private OrganizationDto organization;
    List<EmployeeProjectDto> employeeProjects=new ArrayList<>();

    public String requiredDataUpdate(){
        if (!Validation.isValid(this.getName())) {
            return "name is mandatory !!";
        }else
        if (!Validation.isValid(this.getContactNo())) {
            return "contactNo is mandatory !!";
        }else
        if (!Validation.isValid(this.getEmail())) {
            return "email is mandatory !!";
        }else
        if(!Validation.isValidMobileNumber(this.getContactNo())){
            return "mobile pattern is no match :(";
        }else if(!Validation.isValidEmailPattern(this.getEmail())){
            return "email pattern is no match :(";
        }else if(!Validation.isValid(this.getDateOfJoining())){
            return "dateOfJoining is mandatory !!";
        }
        return "";
    }




    public String requiredData(){
        if (!Validation.isValid(this.getName())) {
            return "name is mandatory !!";
        }else
        if (!Validation.isValid(this.getContactNo())) {
            return "contactNo is mandatory !!";
        }else
        if (!Validation.isValid(this.getEmail())) {
            return "email is mandatory !!";
        }else
        if (!Validation.isValid(this.getPassword())) {
            return "password is mandatory !!";
        }else
        if (!Validation.isValidPassword(this.getPassword())) {
            return "password pattern is no match  please check once :(";
        }
    else if(!Validation.isValid(this.getDateOfJoining())){
        return "dateOfJoining is mandatory !!";
    }else
            if(!Validation.isValidMobileNumber(this.getContactNo())){
                return "mobile pattern is no match :(";
            }else if(!Validation.isValidEmailPattern(this.getEmail())){
                return "email pattern is no match :(";
            }
        return "";
    }
}
