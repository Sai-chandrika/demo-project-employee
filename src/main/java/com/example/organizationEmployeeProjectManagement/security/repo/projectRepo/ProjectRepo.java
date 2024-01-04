package com.example.organizationEmployeeProjectManagement.security.repo.projectRepo;

import com.example.organizationEmployeeProjectManagement.security.dto.projectDto.ProjectDto;
import com.example.organizationEmployeeProjectManagement.security.entity.employee.Employee;
import com.example.organizationEmployeeProjectManagement.security.entity.project.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author chandrika
 * @ProjectName Organization_Employee_ProjectManagement
 * @since 29-03-2023
 */
@Repository
public interface ProjectRepo extends JpaRepository<Project,Long> {
    Page<Project> findAllByOrganizationId(Long id, Pageable pageable);

    Optional<Project> findByName(String name);


    List<Project> findAllByOrganizationId(Long id);
}