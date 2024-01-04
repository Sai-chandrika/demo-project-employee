package com.example.organizationEmployeeProjectManagement.security.controller.projectController;

import com.example.organizationEmployeeProjectManagement.security.dto.genericResponce.GenericResponse;
import com.example.organizationEmployeeProjectManagement.security.dto.projectDto.ProjectDto;
import com.example.organizationEmployeeProjectManagement.security.service.projectService.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

/**
 * @author chandrika
 * @ProjectName Organization_Employee_ProjectManagement
 * @since 29-03-2023
 */
@RestController
@RequestMapping("/api/v1/project")
public class ProjectController {
    @Autowired
    ProjectService projectService;
    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'MANAGER','ADMIN')")
    public GenericResponse save(@RequestBody ProjectDto projectDto)
    {
        return projectService.save(projectDto);
    }
    @PostMapping("/project_employee/save/{empId}/{projectId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    GenericResponse employeeProject(@PathVariable  Long empId, @PathVariable Long projectId){
        return projectService.employeeProject(empId,projectId);
    }
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    GenericResponse update(@RequestBody ProjectDto projectDto){
        return projectService.update(projectDto);
    }
    @GetMapping("/getAll/{offset}/{pageNo}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    GenericResponse getAll(@PathVariable int offset,@PathVariable int pageNo ){
        return projectService.getAll(offset, pageNo);
    }
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    GenericResponse deleteById(@PathVariable Long id){
        return projectService.deleteById(id);
    }
    @GetMapping("/get_by_project_id/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','ADMIN')")
    public GenericResponse getByProjectId(@PathVariable Long id){
        return  projectService.getById(id);
    }
    @GetMapping("/get_by_org_id/{orgId}/{offset}/{pageNo}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public GenericResponse getByOrgId(@PathVariable Long orgId,@PathVariable int offset,@PathVariable int pageNo){
        return  projectService.getByOrgId(orgId,offset,pageNo);
    }

}
