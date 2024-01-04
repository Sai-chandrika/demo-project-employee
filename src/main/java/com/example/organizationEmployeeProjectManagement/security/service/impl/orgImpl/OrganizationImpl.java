package com.example.organizationEmployeeProjectManagement.security.service.impl.orgImpl;

import com.example.organizationEmployeeProjectManagement.exception.*;
import com.example.organizationEmployeeProjectManagement.exception.NullPointerException;
import com.example.organizationEmployeeProjectManagement.security.config.JwtTokenUtils;
import com.example.organizationEmployeeProjectManagement.security.dto.genericResponce.GenericResponse;
import com.example.organizationEmployeeProjectManagement.security.dto.organizationDto.OrganizationDto;
import com.example.organizationEmployeeProjectManagement.security.dto.employeeDto.EmployeeDto;
import com.example.organizationEmployeeProjectManagement.security.entity.employee.Employee;
import com.example.organizationEmployeeProjectManagement.security.entity.organization.Organization;
import com.example.organizationEmployeeProjectManagement.security.enums.roletype.RoleType;
import com.example.organizationEmployeeProjectManagement.security.repo.employeeRepo.EmployeeRepo;
import com.example.organizationEmployeeProjectManagement.security.repo.organizationRepo.OrganizationRepo;
import com.example.organizationEmployeeProjectManagement.security.service.organizationService.OrganizationService;
import com.example.organizationEmployeeProjectManagement.validation.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author chandrika
 * @ProjectName Organization_Employee_ProjectManagement
 * @since 29-03-2023
 */
@Service
public class OrganizationImpl implements OrganizationService {
    @Autowired
    OrganizationRepo organizationRepo;
    @Autowired
    JwtTokenUtils jwtTokenUtils;
    @Autowired
    EmployeeRepo appUserRepo;



    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Override
    public GenericResponse save(OrganizationDto organizationDto) {
        Organization organization=convertOrganizationDtoToEntity(organizationDto);
        organizationRepo.save(organization);
        return new GenericResponse(HttpStatus.OK.value(),  "organization saved successfully by SuperAdmin :)");
    }



    @Override
    public GenericResponse update(OrganizationDto organizationDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Employee employee = (Employee) authentication.getPrincipal();
            if (employee != null) {
                if (employee.getRoleType().equals(RoleType.SUPER_ADMIN)) {
                    Optional<Organization> organization = organizationRepo.findById(organizationDto.getId());
                    if (organization.isPresent()) {
                        Organization o = organization.get();
                        Optional<Organization> optionalOrganization = organizationRepo.findByName(organizationDto.getName());
                        if (optionalOrganization.isEmpty() || optionalOrganization.get().getId().equals(organizationDto.getId())) {
                            o.setName(organizationDto.getName());
                        } else {
                            throw new DuplicateValueException("organization is already existed");
                        }
                        o.setLocation(organizationDto.getLocation());
                        organizationRepo.save(o);
                        return new GenericResponse(HttpStatus.OK.value(), "organization updated successfully by super_admin");
                    }
                    throw new NotFoundException("organization id is not found!!");
                }else {
                    throw new AuthenticationBasedException("not authorised");
                }
            }
        }
        return null;
    }


    public Employee authentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee employee = (Employee) authentication.getPrincipal();
        return employee;
    }



    @Override
    public GenericResponse delete(Long id) {
        Optional<Organization> organization = organizationRepo.findById(id);
        if (organization.isPresent()) {
            organizationRepo.deleteById(id);
            return new GenericResponse(HttpStatus.OK.value(), "organization delete successfully");
        }else
        throw  new NotFoundException("organization id is not found");
    }


    @Override
    public GenericResponse getAll(int offset, int pageNo) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee employee = (Employee) authentication.getPrincipal();
        if(employee.getRoleType().equals(RoleType.SUPER_ADMIN)) {
        Page<OrganizationDto> organisationDtos = organizationRepo.findAll(PageRequest.of(offset,pageNo)).map(this :: convertOrganizationEntityToDto);
        return new GenericResponse(HttpStatus.OK.value(), organisationDtos);
        }else
        if(employee.getRoleType().equals(RoleType.ADMIN)){
            Page<OrganizationDto> organisationDtos = organizationRepo.findById(employee.getOrganization().getId(),PageRequest.of(offset,pageNo)).map(this::convertOrganizationEntityToDto);
            return new GenericResponse(HttpStatus.OK.value(), organisationDtos);
        }else throw new AuthenticationBasedException("not authenticated ");
        }






    @Override
    public GenericResponse getAll() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee employee = (Employee) authentication.getPrincipal();
        if(employee.getRoleType().equals(RoleType.SUPER_ADMIN)) {
            List<OrganizationDto> organisationDtos = organizationRepo.findAll().stream().map(this :: convertOrganizationEntityToDto).collect(Collectors.toList());
            return new GenericResponse(HttpStatus.OK.value(), organisationDtos);
        }else
        if(employee.getRoleType().equals(RoleType.ADMIN)){
            List<OrganizationDto> organisationDtos = organizationRepo.findAll().stream().filter(a->a.getId().equals(employee.getOrganization().getId())).map(this :: convertOrganizationEntityToDto).collect(Collectors.toList());
            return new GenericResponse(HttpStatus.OK.value(), organisationDtos);
        }
        else throw new AuthenticationBasedException("not authenticated ");
    }




    @Override
    public GenericResponse getById(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Employee employee = (Employee) authentication.getPrincipal();
            if (employee.getRoleType().equals(RoleType.SUPER_ADMIN)) {
                Optional<Organization>optionalOrganisation = organizationRepo.findById(id);
                if(optionalOrganisation.isPresent()){
                    Organization organisationDtos= optionalOrganisation.get();
                    return new GenericResponse(HttpStatus.OK.value(), convertOrganizationEntityToDto(organisationDtos));
                }else {
                    throw  new NotFoundException("organization id is not found");
                }
            } else {
                return new GenericResponse(HttpStatus.UNAUTHORIZED.value(), "SUPER ADMIN can access to see all organisation details others do not accessible", "FAILED");
            }
        }else {
            return new GenericResponse(HttpStatus.BAD_REQUEST.value(), "not authenticated ","FAILED");
        }
    }




    public Organization convertOrganizationDtoToEntity(OrganizationDto organization) {
        Organization organization1 = new Organization();
        organization1.setId(organization.getId());
            Optional<Organization> optional = organizationRepo.findByNameAndLocation(organization.getName(), organization.getLocation());
            if (optional.isEmpty()) {
                organization1.setName(organization.getName());
            } else {
                throw new DuplicateValueException("organization is already existed!!");
            }
                organization1.setLocation(organization.getLocation());
                return organization1;
    }




    public Employee convertEmployeeDtoToEntity(EmployeeDto employeeDto){
        Employee employee= new Employee();
        employee.setId(employeeDto.getId());
        employee.setName(employeeDto.getName());
        employee.setContactNo(employeeDto.getContactNo());
        employee.setEmail(employeeDto.getEmail());
        employee.setRoleType(employeeDto.getRoleType());
        employee.setPassword(bCryptPasswordEncoder.encode(employeeDto.getPassword()));
        return employee;
    }



    public EmployeeDto convertEmployeeEntityToDto(Employee employee){
        EmployeeDto employeeDto=new EmployeeDto();
        employeeDto.setId(employee.getId());
        employeeDto.setName(employee.getName());
        employeeDto.setContactNo(employee.getContactNo());
        employeeDto.setEmail(employee.getEmail());
        employeeDto.setRoleType(employee.getRoleType());
        employeeDto.setDateOfJoining(employee.getDateOfJoining());
//        employeeDto.setPassword(bCryptPasswordEncoder.encode(employee.getPassword()));
        return employeeDto;
    }



    public OrganizationDto convertOrganizationEntityToDto(Organization organization) {
        OrganizationDto organizationDto=new OrganizationDto();
            organizationDto.setId(organization.getId());
            organizationDto.setName(organization.getName());
            organizationDto.setLocation(organization.getLocation());
        if(organization.getEmployees() !=null){
            organizationDto.setEmployees(organization.getEmployees().stream().map(this ::convertEmployeeEntityToDto).collect(Collectors.toList()));
        }
        return organizationDto;
    }
}
