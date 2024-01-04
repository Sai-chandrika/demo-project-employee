/**
 * Created By Sunil Verma
 * Date: 07/01/23
 * Time: 4:09 PM
 * Project Name: security
 */

package com.example.organizationEmployeeProjectManagement.security.repo.employeeRepo;

import com.example.organizationEmployeeProjectManagement.security.dto.employeeDto.EmployeeDto;
import com.example.organizationEmployeeProjectManagement.security.dto.organizationDto.OrganizationDto;
import com.example.organizationEmployeeProjectManagement.security.entity.employee.Employee;
import com.example.organizationEmployeeProjectManagement.security.enums.roletype.RoleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, Long> , JpaSpecificationExecutor<Employee> {

    Employee
    findByEmail(String email);
//    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByRoleTypeAndOrganizationId(RoleType roleType, Long id);

    Page<Employee> findAllByOrganizationId(Long id, Pageable pageable);
List<Employee> findAllByOrganizationId(Long id);
    Optional<Employee> getByEmail(String email);
    List<Employee> findAll();

//    List<Employee> findAllByNameOrEmailOrContactNoOrRoleTypeOrOrganizationNameOrOrganizationLocation(String name, String email, String contactNo, RoleType roleType, String oName, String location );
    Optional<Employee> getByContactNo(String contactNo);


    Page<Employee> findAllBySearchKeyContainsIgnoreCase(String key, Pageable pageable);

//    Page<Employee> findAllByNameOrEmailOrContactNoOrRoleTypeStartsWith(String name, String email, String contactNo, RoleType roleType);
//
//    Optional<Employee> getByName(String name);
//
//    List<Employee> findByOrganizationId(Long id);
//
//    Object findByContactNo(String contactNo);


    Page<Employee> findAllByDateOfJoiningGreaterThanEqual(LocalDate fromDate, Pageable pageable);

    Page<Employee> findAllByDateOfJoiningLessThanEqual(LocalDate toDate, Pageable pageable);


    Page<Employee> findAllByDateOfJoiningBetween(LocalDate fromDate, LocalDate toDate, Pageable pageable);

//    List<Employee> findAllByDateOfJoiningAndDateOfJoiningAndOrganizationId(LocalDate fromDate, LocalDate toDate, Long organization, Pageable pageable);

    Page<Employee> findAllByOrganizationIdAndDateOfJoiningGreaterThanEqual(Long organization, LocalDate fromDate, Pageable pageable);

    Page<Employee> findAllByOrganizationIdAndDateOfJoiningLessThanEqual(Long organization, LocalDate toDate, Pageable pageable);

    Page<Employee> findAllByDateOfJoiningBetweenAndOrganizationId(LocalDate fromDate, LocalDate toDate,  Long id, Pageable pageable);

Page<Employee> findAllByDateOfJoiningBetweenAndOrganizationIdAndSearchKeyContainsIgnoreCase(LocalDate fromDate, LocalDate toDate, Long id, String key, PageRequest of);

    Page<Employee> findAllByOrganizationIdAndSearchKeyContainsIgnoreCase(Long id, String key, PageRequest of);

    Page<Employee> findAllByDateOfJoiningGreaterThanEqualAndSearchKeyContainsIgnoreCase(LocalDate fromDate, String key, PageRequest of);

    Page<Employee> findAllByDateOfJoiningLessThanEqualAndSearchKeyContainsIgnoreCase(LocalDate toDate, String key, PageRequest of);

    Page<Employee> findAllByDateOfJoiningBetweenAndSearchKeyContainsIgnoreCase(LocalDate fromDate, LocalDate toDate, String key, PageRequest of);

    Page<Employee> findAllByOrganizationIdAndDateOfJoiningGreaterThanEqualAndSearchKeyContainsIgnoreCase(Long id, LocalDate fromDate, String key, PageRequest of);

    Page<Employee> findAllByOrganizationIdAndDateOfJoiningLessThanEqualAndSearchKeyContainsIgnoreCase(Long id, LocalDate toDate, String key, PageRequest of);

    List<Employee> findByIdIn(List<Long> employeeDtos);

    List<Employee> findAllByIdIn(List<Long> ids);

    List<Employee> getAllByIdIn(List<Long> ids);

    List<Employee> getByIdIn(List<String> ids);
}
