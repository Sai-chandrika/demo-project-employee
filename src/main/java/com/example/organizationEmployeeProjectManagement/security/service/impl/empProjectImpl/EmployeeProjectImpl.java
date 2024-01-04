package com.example.organizationEmployeeProjectManagement.security.service.impl.empProjectImpl;

import com.example.organizationEmployeeProjectManagement.exception.BadAlertException;
import com.example.organizationEmployeeProjectManagement.exception.NotFoundException;
import com.example.organizationEmployeeProjectManagement.security.dto.empProjectDto.EmployeeProjectDto;
import com.example.organizationEmployeeProjectManagement.security.dto.genericResponce.GenericResponse;
import com.example.organizationEmployeeProjectManagement.security.dto.organizationDto.OrganizationDto;
import com.example.organizationEmployeeProjectManagement.security.dto.projectDto.ProjectDto;
import com.example.organizationEmployeeProjectManagement.security.dto.employeeDto.EmployeeDto;
import com.example.organizationEmployeeProjectManagement.security.entity.employee.Employee;
import com.example.organizationEmployeeProjectManagement.security.entity.employeeProject.EmployeeProject;
import com.example.organizationEmployeeProjectManagement.security.entity.organization.Organization;
import com.example.organizationEmployeeProjectManagement.security.entity.project.Project;
import com.example.organizationEmployeeProjectManagement.security.enums.empProjectStatus.EmployeeProjectStatus;
import com.example.organizationEmployeeProjectManagement.security.enums.roletype.RoleType;
import com.example.organizationEmployeeProjectManagement.security.repo.employeeProjectRepo.EmployeeProjectRepo;
import com.example.organizationEmployeeProjectManagement.security.repo.employeeRepo.EmployeeRepo;
import com.example.organizationEmployeeProjectManagement.security.repo.projectRepo.ProjectRepo;
import com.example.organizationEmployeeProjectManagement.security.service.empProjectService.EmployeeProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author chandrika
 * @ProjectName Organization_Employee_ProjectManagement
 * @since 29-03-2023
 */



@Service
public class EmployeeProjectImpl implements EmployeeProjectService {
    @Autowired
    EmployeeRepo employeeRepo;
    @Autowired
    ProjectRepo projectRepo;
    @Autowired
    EmployeeProjectRepo employeeProjectRepo;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Override





    public GenericResponse save(EmployeeProjectDto employeeProjectDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Employee employee = (Employee) authentication.getPrincipal();
            if (employee != null) {
                if (employee.getRoleType().equals(RoleType.ADMIN) && employee.getOrganization().getId().equals(employeeProjectDto.getProject().getOrganization().getId())) {
                    EmployeeProject employeeProject = convertEmployeeProjectDtoToEntity(employeeProjectDto);
                    employeeProjectRepo.save(employeeProject);
                    return new GenericResponse(HttpStatus.OK.value(), "Projects are  assigned employees successfully by Admin");
                } else if (employee.getRoleType().equals(RoleType.SUPER_ADMIN) ) {
                    EmployeeProject employeeProject = convertEmployeeProjectDtoToEntity(employeeProjectDto);
                            employeeProjectRepo.save(employeeProject);
                            return new GenericResponse(HttpStatus.OK.value(), "Projects are  assigned employees successfully by SuperAdmin");
                        }
                else
                    return new GenericResponse(HttpStatus.OK.value(), "Unauthorised");
            }else throw new  BadCredentialsException("employee is null!!");
        }
        return new GenericResponse(HttpStatus.OK.value(), "Unauthorised");
    }





    @Override
    public GenericResponse update(EmployeeProjectDto employeeProjectDto) {
        Optional<EmployeeProject> employeeProject = employeeProjectRepo.findById(employeeProjectDto.getId());
        if (employeeProject.isPresent()) {
            EmployeeProject employeeProject1 = employeeProject.get();
            employeeProject1.setId(employeeProjectDto.getId());
            employeeProject1.setStatus(EmployeeProjectStatus.valueOf(employeeProjectDto.getStatus()));
            employeeProject1.setStartDate(employeeProjectDto.getStartDate());
            employeeProject1.setEndDate(employeeProjectDto.getEndDate());
            employeeProject1.setEmployee(convertEmployeeDtoToEntity(employeeProjectDto.getEmployee()));
            employeeProject1.setProject(convertProjectDtoToEntity(employeeProjectDto.getProject()));
            employeeProjectRepo.save(employeeProject1);
            return new GenericResponse(HttpStatus.OK.value(), convertEmployeeProjectEntityToDto(employeeProject1));
        }
        return null;
    }




public GenericResponse getAll(int offset, int pageNo){
        Page<EmployeeProject> employeeProjectPage=employeeProjectRepo.findAll(PageRequest.of(offset, pageNo));
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null) {
            Employee employee=(Employee) authentication.getPrincipal();
            if(employee!=null){
                if(employee.getRoleType().equals(RoleType.SUPER_ADMIN)){
                    return new GenericResponse(HttpStatus.OK.value(),employeeProjectPage.map(this::convertEmployeeProjectEntityToDto));
                }else if(employee.getRoleType().equals(RoleType.ADMIN)){
                    Page<EmployeeProject> adminPage=employeeProjectRepo.findAllByProjectOrganizationId(employee.getOrganization().getId(), PageRequest.of(offset,pageNo));
                    return new GenericResponse(HttpStatus.OK.value(), adminPage.map(this::convertEmployeeProjectEntityToDto));
                }
            }  return new GenericResponse(HttpStatus.OK.value(), "Unauthorised");
         }
    return new GenericResponse(HttpStatus.BAD_REQUEST.value(), "not authenticated ","FAILED");
    }





//    @Override
//    public GenericResponse getAll() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null) {
//            Employee employee = (Employee) authentication.getPrincipal();
//            if (employee != null) {
//                if (employee.getRoleType().equals(RoleType.SUPER_ADMIN)) {
//                    Page<EmployeeProjectDto> employeeProjectDtoList = employeeProjectRepo.findAll().stream().map(this::convertEmployeeProjectEntityToDto).collect(Collectors.toList());
//                    return new GenericResponse(HttpStatus.OK.value(), employeeProjectDtoList);
//                } else if (employee.getRoleType().equals(RoleType.ADMIN)) {
//                    List<EmployeeProject> adminGet = employeeProjectRepo.findAllByEmployeeOrganizationIdAndProjectOrganizationId(employee.getOrganization().getId(), employee.getOrganization().getId());
//                    return new GenericResponse(HttpStatus.OK.value(), adminGet.stream().map(this::convertEmployeeProjectEntityToDto).collect(Collectors.toList()));
//                } else if (employee.getRoleType().equals(RoleType.MANAGER)) {
//                    List<EmployeeProject> adminGet = employeeProjectRepo.findAllByEmployeeOrganizationIdAndProjectOrganizationId(employee.getOrganization().getId(), employee.getOrganization().getId());
//                    for (EmployeeProject i : adminGet) {
//                        if (i.getEmployee().getRoleType().equals(RoleType.EMPLOYEE) || i.getEmployee().getRoleType().equals(RoleType.MANAGER))
//                            return new GenericResponse(HttpStatus.OK.value(), convertEmployeeProjectEntityToDto(i));
//                    }
//
//                } else if (employee.getRoleType().equals(RoleType.EMPLOYEE)) {
//                    List<EmployeeProject> adminGet = employeeProjectRepo.findAllByEmployeeOrganizationIdAndProjectOrganizationId(employee.getOrganization().getId(), employee.getOrganization().getId());
//                    for (EmployeeProject i : adminGet) {
//                        if (i.getEmployee().getEmail().equals(employee.getEmail())) {
//                            return new GenericResponse(HttpStatus.OK.value(), convertEmployeeProjectEntityToDto(i));
//
//                        }
//                    }
//                }
//            }
//        }
//        return null;
//    }

    private EmployeeProjectDto convertEmployeeProjectEntityToDto(EmployeeProject entity) {
        EmployeeProjectDto employeeProjectDto=new EmployeeProjectDto();
        employeeProjectDto.setId(entity.getId());
        employeeProjectDto.setStatus(entity.getStatus().name());
                employeeProjectDto.setStartDate(entity.getStartDate());
         employeeProjectDto.setEndDate(entity.getEndDate());
        if(entity.getEmployee()!=null){
            Optional<Employee> employeeOptional=employeeRepo.findById(entity.getEmployee().getId());
            if(employeeOptional.isPresent()) {
                if (entity.getProject() != null) {
                    Optional<Project> project = projectRepo.findById(entity.getProject().getId());
                    if (project.isPresent()) {
                        if(Objects.equals(employeeOptional.get().getOrganization().getId(), project.get().getOrganization().getId())) {
                            employeeProjectDto.setEmployee(convertEmployeeEntityToDto(employeeOptional.get()));
                            employeeProjectDto.setProject(convertProjectEntityToDto(project.get()));
                            return employeeProjectDto;
                        }else throw new BadCredentialsException("Project belongs another Organisation !!");
                    }else throw new BadCredentialsException("Project not found with given id!!");
                }else throw new BadCredentialsException("Project id must be provided !!");
            }else throw new BadCredentialsException("Employee not found with given id!!");
        }else throw new BadCredentialsException("Employee id must be provided !!");
    }





    private EmployeeDto convertEmployeeEntityToDto(Employee employee){
        EmployeeDto employeeDto=new EmployeeDto();
        employeeDto.setId(employee.getId());
        employeeDto.setName(employee.getName());
        employeeDto.setContactNo(employee.getContactNo());
        employeeDto.setEmail(employee.getEmail());
        employeeDto.setRoleType(employee.getRoleType());
        employeeDto.setPassword(bCryptPasswordEncoder.encode(employee.getPassword()));
        return employeeDto;
    }



    private ProjectDto convertProjectEntityToDto(Project entity) {
        ProjectDto projectDto=new ProjectDto();
        projectDto.setId(entity.getId());
        projectDto.setName(entity.getName());
        projectDto.setStartDate(entity.getStartDate());
        projectDto.setEndDate(entity.getEndDate());
        projectDto.setStatus(entity.getStatus());
        projectDto.setOrganization(convertOrganizationEntityToDto(entity.getOrganization()));
        return projectDto;
    }



    public Organization convertOrganizationDtoToEntity(OrganizationDto organization) {
        Organization organization1=new Organization();
        organization1.setId(organization.getId());
        organization1.setName(organization.getName());
        organization1.setLocation(organization.getLocation());
      /*  if(organization.getEmployees()!=null) {
            organization1.setEmployees(organization.getEmployees().stream().map(this::convertEmployeeDtoToEntity).collect(Collectors.toList()));
        }*/
        return organization1;
    }



    public OrganizationDto convertOrganizationEntityToDto(Organization organization) {
        OrganizationDto organizationDto=new OrganizationDto();
        organizationDto.setId(organization.getId());
        organizationDto.setName(organization.getName());
        organizationDto.setLocation(organization.getLocation());
    /*    if(organization.getEmployees() !=null){
            organizationDto.setEmployees(organization.getEmployees().stream().map(this ::convertEmployeeEntityToDto).collect(Collectors.toList()));
        }*/
        return organizationDto;
    }





    private Project convertProjectDtoToEntity(ProjectDto projectDto) {
        Project project=new Project();
        project.setId(projectDto.getId());
        project.setName(projectDto.getName());
        project.setStartDate(projectDto.getStartDate());
        project.setEndDate(projectDto.getEndDate());
        project.setStatus(projectDto.getStatus());
        project.setOrganization(convertOrganizationDtoToEntity(projectDto.getOrganization()));
        return project;
    }





    private EmployeeProject convertEmployeeProjectDtoToEntity(EmployeeProjectDto employeeProjectDto) {
        String request = employeeProjectDto.validateRequest();
        if (!request.isEmpty())
            throw new BadAlertException(request);
        EmployeeProject employeeProject = new EmployeeProject();
        employeeProject.setId(employeeProjectDto.getId());
        employeeProject.setStatus(EmployeeProjectStatus.valueOf(employeeProjectDto.getStatus()));
        Optional<EmployeeProject> employeeProject1 = employeeProjectRepo.findByProjectId(employeeProjectDto.getProject().getId());
        if(employeeProject1.isPresent()) {
            if (employeeProjectDto.getStartDate().isAfter(employeeProject1.get().getStartDate()) && employeeProjectDto.getStartDate().isBefore(LocalDate.now())) {
                employeeProject.setStartDate(employeeProjectDto.getStartDate());
            } else {
                throw new BadCredentialsException("please check once startDate is between project startDate and endDate");
            }
        }else throw new NotFoundException("id is not found");
        employeeProject.setEndDate(employeeProjectDto.getEndDate());
        if(employeeProjectDto.getEmployee()!=null){
            Optional<Employee> employeeOptional=employeeRepo.findById(employeeProjectDto.getEmployee().getId());
            if(employeeOptional.isPresent()) {
                if (employeeProjectDto.getProject() != null) {
                    Optional<Project> project = projectRepo.findById(employeeProjectDto.getProject().getId());
                    if (project.isPresent()) {
                        if(Objects.equals(employeeOptional.get().getOrganization().getId(), project.get().getOrganization().getId())) {
                            employeeProject.setEmployee(employeeOptional.get());
                            employeeProject.setProject(project.get());
                            return employeeProject;
                        }else throw new BadCredentialsException("Project belongs another Organisation !!");
                    }else throw new BadCredentialsException("Project not found with given id!!");
                }else throw new BadCredentialsException("Project id must be provided !!");
            }else throw new BadCredentialsException("Employee not found with given id!!");
        }else throw new BadCredentialsException("Employee id must be provided !!");
    }









    private Employee convertEmployeeDtoToEntity(EmployeeDto employeeDto){
        Employee employee= new Employee();
        employee.setId(employeeDto.getId());
        employee.setName(employeeDto.getName());
        employee.setContactNo(employeeDto.getContactNo());
        employee.setEmail(employeeDto.getEmail());
        employee.setRoleType(employeeDto.getRoleType());
        employee.setPassword(bCryptPasswordEncoder.encode(employeeDto.getPassword()));
        return employee;
    }

}