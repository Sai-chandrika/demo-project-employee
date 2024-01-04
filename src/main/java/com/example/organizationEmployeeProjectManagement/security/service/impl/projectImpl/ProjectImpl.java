package com.example.organizationEmployeeProjectManagement.security.service.impl.projectImpl;

import com.example.organizationEmployeeProjectManagement.exception.AuthenticationBasedException;
import com.example.organizationEmployeeProjectManagement.exception.BadAlertException;
import com.example.organizationEmployeeProjectManagement.exception.DuplicateValueException;
import com.example.organizationEmployeeProjectManagement.exception.NotFoundException;
import com.example.organizationEmployeeProjectManagement.exception.NullPointerException;
import com.example.organizationEmployeeProjectManagement.security.config.JwtTokenUtils;
import com.example.organizationEmployeeProjectManagement.security.dto.empProjectDto.EmployeeProjectDto;
import com.example.organizationEmployeeProjectManagement.security.dto.genericResponce.GenericResponse;
import com.example.organizationEmployeeProjectManagement.security.dto.organizationDto.OrganizationDto;
import com.example.organizationEmployeeProjectManagement.security.dto.projectDto.ProjectDto;
import com.example.organizationEmployeeProjectManagement.security.dto.employeeDto.EmployeeDto;
import com.example.organizationEmployeeProjectManagement.security.entity.employee.Employee;
import com.example.organizationEmployeeProjectManagement.security.entity.employeeProject.EmployeeProject;
import com.example.organizationEmployeeProjectManagement.security.entity.organization.Organization;
import com.example.organizationEmployeeProjectManagement.security.entity.project.Project;
import com.example.organizationEmployeeProjectManagement.security.enums.roletype.RoleType;
import com.example.organizationEmployeeProjectManagement.security.repo.employeeRepo.EmployeeRepo;
import com.example.organizationEmployeeProjectManagement.security.repo.organizationRepo.OrganizationRepo;
import com.example.organizationEmployeeProjectManagement.security.repo.projectRepo.ProjectRepo;
import com.example.organizationEmployeeProjectManagement.security.service.projectService.ProjectService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author chandrika
 * @ProjectName Organization_Employee_ProjectManagement
 * @since 29-03-2023
 */
@Service
public class ProjectImpl implements ProjectService {
    @Autowired
    ProjectRepo projectRepo;

    @Autowired
    EmployeeRepo employeeRepo;
    @Autowired
    OrganizationRepo organizationRepo;
    @Autowired
    JwtTokenUtils jwtTokenUtils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;


    public void saveProject(ProjectDto projectDto){
            Project project = convertProjectDtoToEntity(projectDto);
            projectRepo.save(project);
        }




    @Override
    public GenericResponse save(ProjectDto projectDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Employee employee = (Employee) authentication.getPrincipal();
            if (employee != null) {
                if (employee.getRoleType().equals(RoleType.SUPER_ADMIN)) {
                    saveProject(projectDto);
                    return new GenericResponse(HttpStatus.OK.value(), "project is saved successfully by SuperAdmin");
                } else if (employee.getRoleType().equals(RoleType.ADMIN) && employee.getOrganization().getId().equals(projectDto.getOrganization().getId())) {
                    saveProject(projectDto);
                    return new GenericResponse(HttpStatus.OK.value(), "project is saved successfully by Admin");
                }
                    else
                    if(employee.getRoleType().equals(RoleType.MANAGER) && employee.getOrganization().getId().equals(projectDto.getOrganization().getId())){
                        saveProject(projectDto);
                        return new GenericResponse(HttpStatus.OK.value(), "project is saved successfully by manager");
                    }
                    else {
                        return new GenericResponse(HttpStatus.OK.value(), "Unauthorised");
                    }    }
            }
            return null;
    }





        @Transactional
        public GenericResponse employeeProject (Long empId, Long projectId){
            List<Project> projectList;
            Optional<Project> optionalProject = projectRepo.findById(projectId);
            Optional<Employee> optionalEmployee = employeeRepo.findById(empId);
            if (optionalProject.isPresent() && optionalEmployee.isPresent()) {
                Employee employee = optionalEmployee.get();
                return new GenericResponse(HttpStatus.OK.value(), employeeRepo.save(employee));
            } throw new NotFoundException("employee or project is not present");
        }
    @Override
    public GenericResponse update(ProjectDto projectDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Employee employee = (Employee) authentication.getPrincipal();
            if (employee != null) {
                if (employee.getRoleType().equals(RoleType.SUPER_ADMIN)) {
                    String request = projectDto.validateRequest();
                    if (!request.isEmpty())
                        throw new BadAlertException(request);
                    Optional<Project> optionalProject = projectRepo.findById(projectDto.getId());
                    if (optionalProject.isPresent()) {
                        Project project = optionalProject.get();
                        Optional<Project> projectOptional = projectRepo.findByName(projectDto.getName());
                        if (projectOptional.isEmpty() || projectOptional.get().getId().equals(projectDto.getId())) {
                            project.setName(projectDto.getName());
                        } else
                            throw new DuplicateValueException("project name is already existed!!");
                        project.setStartDate(projectDto.getStartDate());
                        project.setEndDate(projectDto.getEndDate());
                        project.setStatus(projectDto.getStatus());
                        if (projectDto.getOrganization() != null) {
                            Optional<Organization> organization = organizationRepo.findById(projectDto.getOrganization().getId());
                            if (organization.isPresent()) {
                                project.setOrganization(organization.get());
                                projectRepo.save(project);
                                return new GenericResponse(HttpStatus.OK.value(), " employee  and org details updated successfully by SuperAdmin :) ");
                            } else
                                throw new NotFoundException("organization id is not found!!");
                        }
                    }
                    throw new NotFoundException(" project  id is not found");
                }
            }
        }
       throw  new AuthenticationBasedException("Not Authenticated....");
    }






    public GenericResponse deleteById(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Employee employee = (Employee) authentication.getPrincipal();
                if (employee.getRoleType().equals(RoleType.SUPER_ADMIN)) {
                    Optional<Project> projectOptional = projectRepo.findById(id);
                    if (projectOptional.isPresent()) {
                    projectRepo.deleteById(id);
                }
                 else
                        throw new NotFoundException("id is not found");
                }
            else
                    throw new AuthenticationBasedException("UNAUTHORISED");
        }
        else
            throw new AuthenticationBasedException("Not Authenticated....");

        return new GenericResponse(HttpStatus.OK.value(), "Deleted successfully....", "SUCCESS");
    }



    @Override
    public GenericResponse getById(Long id) {
        List<Project> projects=projectRepo.findAll().stream().toList();
        List<Project> projectList=new ArrayList<>();
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null){
            Employee employee=(Employee) authentication.getPrincipal();
            if(employee!=null){
                if(employee.getRoleType().equals(RoleType.SUPER_ADMIN)){
                    Optional<Project> projectOptional=projectRepo.findById(id);
                    if(projectOptional.isPresent()){
                        return new GenericResponse(HttpStatus.OK.value(), projectOptional.stream().map(this::convertProjectEntityToDto).collect(Collectors.toList()));
                    }throw new NotFoundException("project id is not found");
                }else if(employee.getRoleType().equals(RoleType.ADMIN)){
                    projectList=projects.stream().filter(a->a.getOrganization().getId().equals(employee.getOrganization().getId())).collect(Collectors.toList());
                    return new GenericResponse(HttpStatus.OK.value(), projectList.stream().map(this::convertProjectEntityToDto).collect(Collectors.toList()));
                }else throw new AuthenticationBasedException("unauthorized");
            }
        }else
            throw new AuthenticationBasedException("Authentication failed");
        return null;
    }



    @Override
    public GenericResponse getByOrgId(Long id, int offset, int pageNo) {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null){
            Employee employee=(Employee) authentication.getPrincipal();
            if(employee!=null){
                if(employee.getRoleType().equals(RoleType.SUPER_ADMIN)){
                    Page<Project> projects = projectRepo.findAllByOrganizationId(id, PageRequest.of(offset, pageNo));
                    if(projects.isEmpty()) {
                        throw new NotFoundException(" org id is not found");
                    }  return new GenericResponse(HttpStatus.OK.value(), projects.map(this::convertProjectEntityToDto));
                }
                }else throw new AuthenticationBasedException("unauthorized");
        }else
            throw new AuthenticationBasedException("Authentication failed");
        return null;
    }



    @Override
    public GenericResponse getAll(int offset, int pageNo) {
                    Page<ProjectDto> projectDtos = projectRepo.findAll(PageRequest.of(offset,pageNo)).map(this ::convertProjectEntityToDto);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
                Employee employee = (Employee) authentication.getPrincipal();
                if (employee != null) {
                    if (employee.getRoleType().equals(RoleType.SUPER_ADMIN)) {
                        return new GenericResponse(HttpStatus.OK.value(), projectDtos);
                    } else if (employee.getRoleType().equals(RoleType.ADMIN)) {
                        Page<Project> adminGet = projectRepo.findAllByOrganizationId(employee.getOrganization().getId(), PageRequest.of(offset, pageNo));
                        return new GenericResponse(HttpStatus.OK.value(), adminGet.map(this::convertProjectEntityToDto));
                    } else if (employee.getRoleType().equals(RoleType.MANAGER)) {
                        Page<Project> adminGet = projectRepo.findAllByOrganizationId(employee.getOrganization().getId(), PageRequest.of(offset, pageNo));
                        return new GenericResponse(HttpStatus.OK.value(), adminGet.map(this::convertProjectEntityToDto));
                    }
                }
            }
            return new GenericResponse(HttpStatus.OK.value(), "UNAUTHORISED");
    }






    private OrganizationDto convertOrganizationEntityToDto(Organization organization) {
        OrganizationDto organizationDto=new OrganizationDto();
        organizationDto.setId(organization.getId());
        organizationDto.setName(organization.getName());
        organizationDto.setLocation(organization.getLocation());
//        if(organization.getEmployees() !=null){
//            organizationDto.setEmployees(organization.getEmployees().stream().map(this::convertEmployeeEntityToDto).collect(Collectors.toList()));
//        }
        return organizationDto;
    }
    private Organization convertOrganizationDtoToEntity(OrganizationDto organization) {
        Organization organization1=new Organization();
        organization1.setId(organization.getId());
        organization1.setName(organization.getName());
        organization1.setLocation(organization.getLocation());
        return organization1;
    }

    private ProjectDto convertProjectEntityToDto(Project entity) {
        ProjectDto projectDto=new ProjectDto();
        projectDto.setId(entity.getId());
        projectDto.setName(entity.getName());
        projectDto.setStartDate(entity.getStartDate());
        projectDto.setEndDate(entity.getEndDate());
        projectDto.setStatus(entity.getStatus());
     if(entity.getOrganization()!=null){
//         Optional<Organization> organization=organizationRepo.findById(entity.getOrganization().getId());
//         if(organization.isPresent()){
             projectDto.setOrganization(convertOrganizationEntityToDto(entity.getOrganization()));
//         }
     }

        return projectDto;
    }




    private Project convertProjectDtoToEntity(ProjectDto projectDto) {
        String request = projectDto.validateRequest();
        if (!request.isEmpty())
            throw new BadAlertException(request);
        Project project = new Project();
        project.setId(projectDto.getId());
        Optional<Project> projectOptional = projectRepo.findByName(projectDto.getName());
        if (projectOptional.isEmpty() || projectOptional.get().getId().equals(projectDto.getId())) {
            project.setName(projectDto.getName());
        }else
        throw new DuplicateValueException("project name is already existed!!");
        project.setStartDate(projectDto.getStartDate());
        project.setEndDate(projectDto.getEndDate());
        project.setStatus(projectDto.getStatus());
        if(projectDto.getOrganization()!=null){
            Optional<Organization> organization=organizationRepo.findById(projectDto.getOrganization().getId());
            if(organization.isPresent()){
                project.setOrganization(organization.get());
            }
        }
        return project;
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
}


