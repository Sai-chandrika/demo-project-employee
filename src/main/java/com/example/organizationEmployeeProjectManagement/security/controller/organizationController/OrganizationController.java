package com.example.organizationEmployeeProjectManagement.security.controller.organizationController;

import com.example.organizationEmployeeProjectManagement.security.dto.genericResponce.GenericResponse;
import com.example.organizationEmployeeProjectManagement.security.dto.organizationDto.OrganizationDto;
import com.example.organizationEmployeeProjectManagement.security.service.organizationService.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author chandrika
 * @ProjectName Organization_Employee_ProjectManagement
 * @since 29-03-2023
 */
@RestController
@RequestMapping("/api/v1/organization")
public class OrganizationController {
    @Autowired
    OrganizationService organizationService;
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public GenericResponse save(@RequestBody OrganizationDto organizationDto){
        return organizationService.save(organizationDto);
    }
@PutMapping("/update")
@PreAuthorize("hasAuthority('SUPER_ADMIN')")
GenericResponse update(@RequestBody OrganizationDto organizationDto){
        return organizationService.update(organizationDto);
    }
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public GenericResponse delete(@PathVariable Long id){
        return organizationService.delete(id);
    }
@GetMapping("/getAll/{offset}/{pageNo}")
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN','ADMIN')")
    public GenericResponse getAll(@PathVariable int offset,@PathVariable int pageNo){
        return organizationService.getAll(offset,pageNo);
    }
    @GetMapping("/getAll")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','ADMIN')")
    public GenericResponse getAll(){
        return organizationService.getAll();
    }
    @GetMapping("/get-by-id/{id}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public GenericResponse getById(@PathVariable Long id){
        return organizationService.getById(id);
    }
}

