package com.example.organizationEmployeeProjectManagement.security.service.empProjectService;

import com.example.organizationEmployeeProjectManagement.security.dto.empProjectDto.EmployeeProjectDto;
import com.example.organizationEmployeeProjectManagement.security.dto.genericResponce.GenericResponse;

/**
 * @author chandrika
 * @ProjectName Organization_Employee_ProjectManagement
 * @since 29-03-2023
 */
public interface EmployeeProjectService {
    GenericResponse save(EmployeeProjectDto employeeProjectDto);

    GenericResponse update(EmployeeProjectDto employeeProjectDto);

    GenericResponse getAll(int offset, int pageNo);
}