package com.example.organizationEmployeeProjectManagement.security.service.projectService;

import com.example.organizationEmployeeProjectManagement.security.dto.genericResponce.GenericResponse;
import com.example.organizationEmployeeProjectManagement.security.dto.projectDto.ProjectDto;

/**
 * @author chandrika
 * @ProjectName Organization_Employee_ProjectManagement
 * @since 29-03-2023
 */
public interface ProjectService {
    GenericResponse save(ProjectDto projectDto);
    GenericResponse employeeProject(Long empId, Long projectId);

    GenericResponse update(ProjectDto projectDto);

    GenericResponse getAll(int offset, int pageNo);
    GenericResponse deleteById(Long id);

    GenericResponse getById(Long id);

    GenericResponse getByOrgId(Long id,int offset,int pageNo);
}