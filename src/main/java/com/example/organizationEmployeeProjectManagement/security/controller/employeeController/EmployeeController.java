/**
 * Created By Sunil Verma
 * Date: 07/01/23
 * Time: 4:11 PM
 * Project Name: security
 */

package com.example.organizationEmployeeProjectManagement.security.controller.employeeController;

import com.example.organizationEmployeeProjectManagement.exception.PatternNotMatchException;
import com.example.organizationEmployeeProjectManagement.security.dto.EmployeeSearch;
import com.example.organizationEmployeeProjectManagement.security.dto.EmployeeSearchDto;
import com.example.organizationEmployeeProjectManagement.security.dto.changePassword.ChangePassWordDto;
import com.example.organizationEmployeeProjectManagement.security.dto.genericResponce.GenericResponse;
import com.example.organizationEmployeeProjectManagement.security.dto.employeeDto.EmployeeDto;
import com.example.organizationEmployeeProjectManagement.security.dto.searchDto.FilterDto;
import com.example.organizationEmployeeProjectManagement.security.dto.searchRequestDto.RequestDto;
import com.example.organizationEmployeeProjectManagement.security.entity.employee.Employee;
import com.example.organizationEmployeeProjectManagement.security.repo.employeeRepo.EmployeeRepo;
import com.example.organizationEmployeeProjectManagement.security.service.employeeservice.EmployeeService;
import com.example.organizationEmployeeProjectManagement.security.service.filterSpecification.FilterSpecification;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employee")
@CrossOrigin
public class EmployeeController {
    @Autowired
    EmployeeService userService;


    @Autowired
    EmployeeRepo employeeRepo;
    @Autowired
    FilterSpecification<Employee> employeeFilterSpecification;

    @PostMapping(value = "/sign-in")
    public GenericResponse signIn(@RequestBody EmployeeDto dto) throws JOSEException {
        return userService.signIn(dto);
    }

    @PostMapping(value = "/email_verification")
    public GenericResponse changePassword(@RequestBody ChangePassWordDto dto) throws JOSEException {
        return userService.changePassword(dto);
    }
    @PostMapping(value = "/new_password")
    public GenericResponse newPassword(@RequestBody ChangePassWordDto dto) throws JOSEException {
        return userService.newPassword(dto);
    }

    @PostMapping(value = "/sign-up")
    public GenericResponse signUp(@RequestBody EmployeeDto dto) throws JOSEException {
        return userService.signUp(dto);
    }


    @PostMapping("/save")
//    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','ADMIN','MANAGER')")
    public GenericResponse save(@RequestBody EmployeeDto employeeDto) throws PatternNotMatchException {
        return userService.saveEmployee(employeeDto);
    }


    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','ADMIN')")
    GenericResponse update(@RequestBody List<EmployeeDto> employeeDto) {
        return userService.update(employeeDto);
    }


    @GetMapping("/getAll/{offset}/{pageNo}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    public GenericResponse geAllEmployeeDetails(@PathVariable int offset, @PathVariable int pageNo) {
        return userService.getAllEmployeeDetails( offset,pageNo);
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    public GenericResponse geAllEmployeeDetails() {
        return userService.getAllEmployeeDetails();
    }

    @GetMapping("/get-by-id/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public GenericResponse getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @DeleteMapping("/delete-by-id/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public GenericResponse deleteById(@PathVariable Long id) {
        return userService.deleteById(id);
    }

@PostMapping("/search_filter")
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
        public GenericResponse search (@RequestBody FilterDto searchDto) {
        return  userService.searchList(searchDto);
        }

//    @GetMapping("/search-key/{key}/{offset}/{pageNo}")
//    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','ADMIN','MANAGER','EMPLOYEE')")
//    GenericResponse findBySearchKey(@PathVariable String key,@PathVariable int offset,@PathVariable int pageNo){
//        return  userService.findBySearchKey(key,offset,pageNo);
//    }

    @PostMapping("/id_list")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    GenericResponse idList(@RequestBody List<Long>  id){
        return userService.idList(id);
    }

    @PostMapping("/specification")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
public GenericResponse getEmployees(@RequestBody RequestDto requestDto){
        return userService.specification(requestDto);
    }

    @PostMapping(value = "/excel-upload-for-create-user")
    public GenericResponse excelUploadForCreateUser(MultipartFile multipartFile) throws IOException {
        GenericResponse apiResponse = userService.excelUploadForCreateUser(multipartFile);
        return new GenericResponse( HttpStatus.OK.value(), apiResponse);
    }

    @PostMapping("/search/employee")
    public List<EmployeeSearchDto> search(@RequestBody EmployeeSearch employeeSearch){
        List<EmployeeSearchDto> genericResponse=userService.search(employeeSearch);
        return genericResponse;
    }


//    @PostMapping("/specification_or")
//    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
//    public GenericResponse getDataUsingOr(@RequestBody RequestDto requestDto){
//        return userService.specificationUsingOr(requestDto);
//    }
}


