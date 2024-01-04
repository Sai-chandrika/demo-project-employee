package com.example.organizationEmployeeProjectManagement.security.repo.employeeProjectRepo;

import com.example.organizationEmployeeProjectManagement.security.dto.empProjectDto.EmployeeProjectDto;
import com.example.organizationEmployeeProjectManagement.security.entity.employeeProject.EmployeeProject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author chandrika
 * @ProjectName Organization_Employee_ProjectManagement
 * @since 30-03-2023
 */
@Repository
public interface  EmployeeProjectRepo extends JpaRepository<EmployeeProject, Long> {

    Page<EmployeeProject> findAllByProjectOrganizationId(Long id, PageRequest of);

    Optional<EmployeeProject> findByProjectId(Long id);
}