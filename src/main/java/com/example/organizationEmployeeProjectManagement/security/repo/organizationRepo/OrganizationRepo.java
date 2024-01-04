package com.example.organizationEmployeeProjectManagement.security.repo.organizationRepo;

import com.example.organizationEmployeeProjectManagement.security.entity.organization.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author chandrika
 * @ProjectName Organization_Employee_ProjectManagement
 * @since 29-03-2023
 */
@Repository
public interface OrganizationRepo extends JpaRepository<Organization, Long> {
    Optional<Organization> findByName(String name);

    Optional<Organization> findByLocation(String location);
Page<Organization> findById(Long id, Pageable pageable);
    Optional<Organization> findByNameAndLocation(String name, String location);
}