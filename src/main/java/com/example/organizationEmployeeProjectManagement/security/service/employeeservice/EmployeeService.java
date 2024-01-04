/**
 * Created By Sunil Verma
 * Date: 07/01/23
 * Time: 4:13 PM
 * Project Name: security
 */

package com.example.organizationEmployeeProjectManagement.security.service.employeeservice;

import com.example.organizationEmployeeProjectManagement.exception.PatternNotMatchException;
import com.example.organizationEmployeeProjectManagement.security.dto.EmployeeSearch;
import com.example.organizationEmployeeProjectManagement.security.dto.EmployeeSearchDto;
import com.example.organizationEmployeeProjectManagement.security.dto.changePassword.ChangePassWordDto;
import com.example.organizationEmployeeProjectManagement.security.dto.genericResponce.GenericResponse;
import com.example.organizationEmployeeProjectManagement.security.dto.employeeDto.EmployeeDto;
import com.example.organizationEmployeeProjectManagement.security.dto.searchDto.FilterDto;
import com.example.organizationEmployeeProjectManagement.security.dto.searchRequestDto.RequestDto;
import com.example.organizationEmployeeProjectManagement.security.dto.searchRequestDto.SearchRequest;
import com.example.organizationEmployeeProjectManagement.security.entity.employee.Employee;
import com.nimbusds.jose.JOSEException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface EmployeeService {

   

    GenericResponse signIn(EmployeeDto dto) throws JOSEException;
    GenericResponse signUp(EmployeeDto dto) throws JOSEException;
    Employee verifyUser(EmployeeDto dto);

    GenericResponse saveEmployee(EmployeeDto employeeDto) throws PatternNotMatchException;
    GenericResponse update(List<EmployeeDto> employeeDto);

    public GenericResponse getAllEmployeeDetails(int offset, int pageNo);
    GenericResponse getById(Long id);
 GenericResponse getAllEmployeeDetails() ;

        GenericResponse deleteById(Long id);
     GenericResponse newPassword(ChangePassWordDto changePassWordDto);
    GenericResponse searchList(FilterDto searchDto);
     GenericResponse changePassword(ChangePassWordDto changePassWordDto);
    GenericResponse idList(List<Long>  ids);
    GenericResponse specification(RequestDto searchRequest);

    GenericResponse excelUploadForCreateUser(MultipartFile multipartFile) throws IOException;

    List<EmployeeSearchDto> search(EmployeeSearch employeeSearch);

    List<Employee> searchEmployee(EmployeeSearch employeeSearch);
//    GenericResponse specificationUsingOr(RequestDto searchRequest);
//    GenericResponse findBySearchKey(String key,int offset, int pageNo);
}
