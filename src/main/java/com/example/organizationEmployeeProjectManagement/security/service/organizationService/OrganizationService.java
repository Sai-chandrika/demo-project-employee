package com.example.organizationEmployeeProjectManagement.security.service.organizationService;

import com.example.organizationEmployeeProjectManagement.security.dto.genericResponce.GenericResponse;
import com.example.organizationEmployeeProjectManagement.security.dto.organizationDto.OrganizationDto;

/**
 * @author chandrika
 * @ProjectName Organization_Employee_ProjectManagement
 * @since 29-03-2023
 */
public interface OrganizationService {
    GenericResponse save(OrganizationDto organizationDto);
    GenericResponse update(OrganizationDto organizationDto);

    GenericResponse delete(Long id);

    GenericResponse getAll(int offset,int pageNo);
    public GenericResponse getAll();

    GenericResponse getById(Long id);
}