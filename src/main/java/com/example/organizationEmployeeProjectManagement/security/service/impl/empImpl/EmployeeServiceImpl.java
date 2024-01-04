/**
 * Created By Sunil Verma
 * Date: 07/01/23
 * Time: 4:15 PM
 * Project Name: security
 */

package com.example.organizationEmployeeProjectManagement.security.service.impl.empImpl;

import com.example.organizationEmployeeProjectManagement.exception.*;
import com.example.organizationEmployeeProjectManagement.exception.NullPointerException;
import com.example.organizationEmployeeProjectManagement.security.config.JwtTokenUtils;
import com.example.organizationEmployeeProjectManagement.security.dto.EmployeeSearch;
import com.example.organizationEmployeeProjectManagement.security.dto.EmployeeSearchDto;
import com.example.organizationEmployeeProjectManagement.security.dto.changePassword.ChangePassWordDto;
import com.example.organizationEmployeeProjectManagement.security.dto.employeeDto.EmployeeDto;
import com.example.organizationEmployeeProjectManagement.security.dto.genericResponce.GenericResponse;
import com.example.organizationEmployeeProjectManagement.security.dto.organizationDto.OrganizationDto;
import com.example.organizationEmployeeProjectManagement.security.dto.searchDto.FilterDto;
import com.example.organizationEmployeeProjectManagement.security.dto.searchRequestDto.RequestDto;
import com.example.organizationEmployeeProjectManagement.security.dto.signInDto.SignInResponseDto;
import com.example.organizationEmployeeProjectManagement.security.entity.employee.Employee;
import com.example.organizationEmployeeProjectManagement.security.entity.organization.Organization;
import com.example.organizationEmployeeProjectManagement.security.enums.roletype.RoleType;
import com.example.organizationEmployeeProjectManagement.security.repo.employeeRepo.EmployeeRepo;
import com.example.organizationEmployeeProjectManagement.security.repo.organizationRepo.OrganizationRepo;
import com.example.organizationEmployeeProjectManagement.security.service.employeeservice.EmployeeService;
import com.example.organizationEmployeeProjectManagement.security.service.filterSpecification.FilterSpecification;
import com.nimbusds.jose.JOSEException;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    OrganizationRepo organizationRepo;
    @Autowired
    EmployeeRepo appUserRepo;
    @Autowired
    JwtTokenUtils jwtTokenUtils;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    FilterSpecification<Employee> filterSpecification;

    @Autowired
    EntityManager entityManager;

    @Override
    @SneakyThrows
    public GenericResponse signIn(EmployeeDto dto) {
        Employee user = appUserRepo.findByEmail(dto.getEmail());
        if (user == null) {
//            return new GenericResponse(HttpStatus.UNAUTHORIZED.value(), "Email id is wrong !!", "FAILED");
            throw new AuthenticationBasedException("Email id is wrong !!");
        }
        if (bCryptPasswordEncoder.matches(dto.getPassword(), user.getPassword())) {
            SignInResponseDto response = new SignInResponseDto(user);
            response.setToken(jwtTokenUtils.getToken(user));
            return new GenericResponse(HttpStatus.OK.value(), response);
        } else {
            return new GenericResponse(HttpStatus.UNAUTHORIZED.value(), "Password is wrong !!", "FAILED");
        }
    }



    @Override
    public GenericResponse changePassword(ChangePassWordDto changePassWordDto) {
        String validateRequest = changePassWordDto.validateEmail();
        if (!validateRequest.isEmpty())
            throw new BadAlertException(validateRequest);
        Optional<Employee> optionalEmployee = appUserRepo.getByEmail(changePassWordDto.getEmail().trim());
        if (optionalEmployee.isPresent()) {
            return new GenericResponse(HttpStatus.OK.value(), "employee details are matched");
        } else throw new AuthenticationBasedException("email is not match");
    }



    @Override
    public GenericResponse newPassword(ChangePassWordDto changePassWordDto) {
        String validate = changePassWordDto.validateRequest();
        if (!validate.isEmpty())
            throw new BadAlertException(validate);
        Optional<Employee> optionalEmployee = appUserRepo.getByEmail(changePassWordDto.getEmail());
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            if (changePassWordDto.getNewPassword().equals(changePassWordDto.getConfirmPassword())) {
                employee.setPassword(bCryptPasswordEncoder.encode(changePassWordDto.getNewPassword()));
                appUserRepo.save(employee);
                return new GenericResponse(HttpStatus.OK.value(), "password changed successfully");
            } else {
                return new GenericResponse(HttpStatus.OK.value(), "confirmPassword and new password  is not match");
            }
        }
        return new GenericResponse(HttpStatus.OK.value(), "employee not found");
    }


    @Override
    public GenericResponse signUp(EmployeeDto dto) throws JOSEException {
        Employee user = new Employee();
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setRoleType(dto.getRoleType());
        user.setContactNo(dto.getContactNo());
        user.setDateOfJoining(dto.getDateOfJoining());
        user.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        appUserRepo.save(user);
        return new GenericResponse(HttpStatus.OK.value(), "Registration is done :)", "SUCCESS");
    }


    @Override
    public Employee verifyUser(EmployeeDto dto) {
        Employee user = appUserRepo.findByEmail(dto.getEmail());
        if (user == null) {
            return null;
        }
        if (bCryptPasswordEncoder.matches(dto.getPassword(), user.getPassword())) {
            SignInResponseDto response = new SignInResponseDto(user);
            response.setToken(dto.getEmail() + ":" + dto.getPassword());
            return user;
        } else {
            return null;
        }
    }


    public GenericResponse superAdmin(EmployeeDto employeeDto) throws PatternNotMatchException {
        Employee employee1 = convertEmployeeDtoToEntity(employeeDto);
        if (employee1.getRoleType().equals(RoleType.ADMIN)) {
            Optional<Employee> employeeOptional = appUserRepo.findByRoleTypeAndOrganizationId(employeeDto.getRoleType(), employeeDto.getOrganization().getId());
            if (employeeOptional.isEmpty()) {
                appUserRepo.save(convertEmployeeDtoToEntity(employeeDto));
                return new GenericResponse(HttpStatus.OK.value(), "admin save successfully by superAdmin  :)");
            }
            throw new DuplicateValueException("admin is already saved this organization id !!");
        } else if (employee1.getRoleType().equals(RoleType.MANAGER) || employee1.getRoleType().equals(RoleType.EMPLOYEE) || employee1.getRoleType().equals(RoleType.ADMIN) && !employee1.getRoleType().equals(RoleType.SUPER_ADMIN)) {
            appUserRepo.save(employee1);
            return new GenericResponse(HttpStatus.OK.value(), "Employee saved successfully by superAdmin :)");
        }
        return null;
    }



    public GenericResponse admin(EmployeeDto employeeDto) throws PatternNotMatchException, DuplicateValueException {
        if (employeeDto.getRoleType().equals(RoleType.MANAGER) || employeeDto.getRoleType().equals(RoleType.EMPLOYEE)) {

            convertEmployeeEntityToDto(appUserRepo.save(convertEmployeeDtoToEntity(employeeDto)));
            return new GenericResponse(HttpStatus.OK.value(), "Employee saved successfully by Admin :)");

        }
        throw new AuthenticationBasedException("admin save only Managers and employees...");
    }



    public GenericResponse manager(EmployeeDto employeeDto) throws PatternNotMatchException {
        if (employeeDto.getRoleType().equals(RoleType.EMPLOYEE)) {

            convertEmployeeEntityToDto(appUserRepo.save(convertEmployeeDtoToEntity(employeeDto)));
            return new GenericResponse(HttpStatus.OK.value(), "Employee saved successfully by Manager :)");
        }
        return new GenericResponse(HttpStatus.OK.value(), "Manager saved only employees ...");
    }



    public GenericResponse saveEmployee(EmployeeDto employeeDto) throws PatternNotMatchException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Employee employee = (Employee) authentication.getPrincipal();
            if (employee != null) {
                if (employee.getRoleType().equals(RoleType.SUPER_ADMIN)) {
                    return superAdmin(employeeDto);
                } else if (employee.getRoleType().equals(RoleType.ADMIN) && employee.getOrganization().getId().equals(employeeDto.getOrganization().getId())) {
                    return admin(employeeDto);
                } else if (employee.getRoleType().equals(RoleType.MANAGER) && employee.getOrganization().getId().equals(employeeDto.getOrganization().getId())) {
                    return manager(employeeDto);
                } else {
                    throw new AuthenticationBasedException("Unauthorised !!");
                }
            }
        }
        return null;
    }




    public GenericResponse superAdminUpdate(List<EmployeeDto> employeeDto) {
        List<Employee> employees=new ArrayList<>();
        for(EmployeeDto e:employeeDto) {
//            if (employeeDto.size() <= 3) {
            Optional<Employee> optionalEmployee = appUserRepo.findById(e.getId());
            if (optionalEmployee.isPresent()) {
                Employee employee1 = optionalEmployee.get();
                employee1.setName(e.getName());
                if (e.getDateOfJoining().isBefore(LocalDate.now()) || e.getDateOfJoining().equals(LocalDate.now())) {
                    employee1.setDateOfJoining(e.getDateOfJoining());
                } else throw new BadAlertException("date is must be less than current date");
                employee1.setSearchKey(searchKey(employee1));
                Optional<Employee> optional = Optional.ofNullable(appUserRepo.findByEmail(e.getEmail()));
                if (optional.isEmpty() || optional.get().getId().equals(e.getId())) {
                    employee1.setEmail(e.getEmail());
                } else {
                    throw new DuplicateValueException("email is already existed!!");
                }
                Optional<Employee> optionalContact = appUserRepo.getByContactNo(e.getContactNo());
                if (optionalContact.isEmpty() || optionalContact.get().getId().equals(e.getId())) {
                    employee1.setContactNo(e.getContactNo());
                } else throw new DuplicateValueException(" mobile number is already existed");
                employee1.setRoleType(e.getRoleType());
                if (e.getOrganization() != null) {
                    Optional<Organization> organization = organizationRepo.findById(e.getOrganization().getId());
                    if (organization.isPresent()) {
                        employee1.setOrganization(organization.get());
                        employees.add(employee1);
                    } else
                        throw new NotFoundException("organization id is not found!!");
                }
            } else
                throw new NotFoundException(" employee  id is not found");
//        }else throw new NullPointerException("select max 3 values only");
        }
        appUserRepo.saveAll(employees);
        return new GenericResponse(HttpStatus.OK.value(), " employee  and org details updated successfully by SuperAdmin :) ", employees.stream().map(this::convertEmployeeEntityToDto).collect(Collectors.toList()));
    }






    public GenericResponse updateAdmin(List<EmployeeDto> employeeDto) {
        Employee employee=authentication();
        List<Employee> employees = new ArrayList<>();
        for (Employee e : employees) {
            Optional<Employee> employee1 = appUserRepo.findById(e.getId());
            if (employee1.isPresent()) {
                if(employee.getOrganization().getId().equals(employee1.get().getOrganization().getId())){
                Employee employee2 = employee1.get();
                employee2.setName(e.getName());
                if (e.getDateOfJoining().isBefore(LocalDate.now()) || e.getDateOfJoining().equals(LocalDate.now())) {
                    employee2.setDateOfJoining(e.getDateOfJoining());
                } else throw new BadAlertException("date is must be less than current date");
                employee2.setSearchKey(searchKey(employee2));
                Optional<Employee> optionalEmployee = appUserRepo.getByEmail(e.getEmail());
                if (optionalEmployee.isEmpty() || optionalEmployee.get().getId().equals(e.getId())) {
                    employee2.setEmail(e.getEmail());
                } else {
                    throw new DuplicateValueException("email id is already exist!!");
                }
                Optional<Employee> optional = appUserRepo.getByContactNo(e.getContactNo());
                if (optional.isEmpty()) {
                    employee2.setContactNo(e.getContactNo());
                } else throw new DuplicateValueException(" mobile number is already existed");
                employee2.setRoleType(e.getRoleType());
                if (employee2.getRoleType().equals(RoleType.EMPLOYEE) || employee2.getRoleType().equals(RoleType.MANAGER)) {
                    appUserRepo.save(employee2);
                    return new GenericResponse(HttpStatus.OK.value(), "updated  Employee Details   successfully by Admin :)");
                }
                throw new AuthenticationBasedException("Admin update only employee and manager details");
            }else throw new NotFoundException("admin not belong to this organization :(");
            }
            throw new NotFoundException("employee id is not found");
        }
        appUserRepo.saveAll(employees);
        return new GenericResponse(HttpStatus.OK.value(), " employee  and org details updated successfully by SuperAdmin :) ", employees.stream().map(this::convertEmployeeEntityToDto).collect(Collectors.toList()));

    }


@Override
public GenericResponse idList(List<Long> ids) {
    List<Employee> employeeOptional = appUserRepo.findAllByIdIn(ids);
    if (employeeOptional.isEmpty()) {
        throw new NotFoundException("id is not found");
    }        return new GenericResponse(HttpStatus.OK.value(), employeeOptional.stream().map(this :: convertEmployeeEntityToDto).collect(Collectors.toList()));

}



// Optional<Employee> employeeOptional = appUserRepo.findById(id);
//                    if (employeeOptional.isPresent()) {
//                        return new GenericResponse(HttpStatus.OK.value(), employeeOptional.stream().map(this::convertEmployeeEntityToDto).collect(Collectors.toList()));

    @Override
    public GenericResponse specification(RequestDto searchRequest) {
        Specification<Employee> specification=filterSpecification.getSearchSpecification(searchRequest.getSearchRequest(), searchRequest.getOperation());
        List<Employee> employees=appUserRepo.findAll(specification);
        return new GenericResponse(HttpStatus.OK.value(), employees.stream().map(this::convertEmployeeEntityToDto).collect(Collectors.toList()));
    }

    @Override
    public GenericResponse excelUploadForCreateUser(MultipartFile multipartFile) throws IOException {
        List<Employee> resourses=new ArrayList<>();
        XSSFWorkbook workbook=new XSSFWorkbook(multipartFile.getInputStream());
        XSSFSheet sheet=workbook.getSheet("employees");
        Long rowIndex=0L;
        for(Row row:sheet){
            if(rowIndex==0){
                rowIndex++;
                continue;
            }

            Iterator<Cell> cellIterator=row.iterator();
            int cellIndex=0;
            Employee resourse=new Employee();
            while(cellIterator.hasNext()){
                Cell cell=cellIterator.next();
                switch (cellIndex){
                    case 0->resourse.setId((long) cell.getNumericCellValue());
                    case 1-> resourse.setName(cell.getStringCellValue());
                    case 2-> resourse.setEmail(cell.getStringCellValue());
                    case 3->resourse.setContactNo(cell.getStringCellValue());
                    case 4->resourse.setPassword(cell.getStringCellValue());
                    case 5->resourse.setRoleType(RoleType.valueOf(cell.getStringCellValue()));
                    case 6->{
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate localDate = LocalDate.parse(cell.getStringCellValue(), formatter);
                        resourse.setDateOfJoining(localDate);
                    }
                    case 7->{
                        String organizationName = cell.getStringCellValue();
                        Organization org = new Organization();
                        Optional<Organization> organization=organizationRepo.findByName(organizationName);
                        if(organization.isPresent()){
                            org=organization.get();
                            resourse.setOrganization(org);
                        }
                        org.setName(organizationName);
                        resourse.setOrganization(org);
                    }
                    default -> {

                    }
                }
                cellIndex++;
            }
            resourses.add(resourse);
        }
        return new GenericResponse(HttpStatus.OK.value(), resourses);
    }



//    @Override
//    public GenericResponse specificationUsingOr(RequestDto searchRequest) {
//            Specification<Employee> specification = filterSpecification.getSearchSpecification(searchRequest.getSearchRequest());
//            List<Employee> employees=appUserRepo.findAll(specification);
//        return new GenericResponse(HttpStatus.OK.value() ,employees.stream().map(this::convertEmployeeEntityToDto).collect(Collectors.toList())) ;
//    }
//

    @Override
    public GenericResponse update(List<EmployeeDto> employeeDto) {
//        String request = employeeDto.requiredDataUpdate();
//        if (!request.isEmpty())
//            throw new BadAlertException(request);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Employee employee = (Employee) authentication.getPrincipal();
            if (employee != null) {
                if (employee.getRoleType().equals(RoleType.SUPER_ADMIN)) {
                    return superAdminUpdate(employeeDto);
                } else if (employee.getRoleType().equals(RoleType.ADMIN)) {
                    return updateAdmin(employeeDto);
                }
                throw new NotFoundException("admin could not update the organization details !!");
            }

        }
        return null;
    }





    public GenericResponse deleteById(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Employee employee = (Employee) authentication.getPrincipal();
            Optional<Employee> employeeOptional = appUserRepo.findById(id);
            if (employeeOptional.isPresent()) {
                Employee employee1 = employeeOptional.get();
                if (employee.getRoleType().equals(RoleType.SUPER_ADMIN)) {
                    appUserRepo.deleteById(id);
                } else if (employee.getRoleType().equals(RoleType.ADMIN) && employee.getOrganization().getId().equals(employeeOptional.get().getOrganization().getId())) {
                    appUserRepo.deleteById(id);
                    return new GenericResponse(HttpStatus.OK.value(), "delete successfully by admin");
                } else if (employee.getRoleType().equals(RoleType.MANAGER) && employee1.getRoleType().equals(RoleType.EMPLOYEE) && employee.getOrganization().getId().equals(employeeOptional.get().getOrganization().getId())) {
                    appUserRepo.deleteById(id);
                    return new GenericResponse(HttpStatus.OK.value(), "delete successfully by Manager");
                } else {
                    throw new AuthenticationBasedException("UNAUTHORISED");
                }
            } else {
                throw new NotFoundException("employee not found");
            }
        } else {
            throw new AuthenticationBasedException("Not Authenticated....");
        }
        return new GenericResponse(HttpStatus.OK.value(), "Deleted successfully....", "SUCCESS");
    }





    public GenericResponse filterBySuperAdmin(FilterDto searchDto) {
        Page<Employee> users = appUserRepo.findAll(PageRequest.of(searchDto.getOffset(), searchDto.getPageNo()));
        if (searchDto.getKey() != null   && searchDto.getId() != null  && searchDto.getFromDate() != null && searchDto.getToDate() != null &&  searchDto.getOffset() !=null  &&  searchDto.getPageNo() !=null && searchDto.getKey().length() >=3) {
            users = appUserRepo.findAllByDateOfJoiningBetweenAndOrganizationIdAndSearchKeyContainsIgnoreCase(searchDto.getFromDate(), searchDto.getToDate(), searchDto.getId(), searchDto.getKey(), PageRequest.of(searchDto.getOffset(), searchDto.getPageNo()));
        } else if (searchDto.getKey() != null && searchDto.getId() != null  &&  searchDto.getOffset() !=null  &&  searchDto.getPageNo() !=null&& searchDto.getKey().length() >=3) {
            users = appUserRepo.findAllByOrganizationIdAndSearchKeyContainsIgnoreCase(searchDto.getId(), searchDto.getKey(), PageRequest.of(searchDto.getOffset(), searchDto.getPageNo()));
        } else if (searchDto.getKey() != null  && searchDto.getFromDate() != null && searchDto.getToDate() != null &&  searchDto.getOffset() !=null  &&  searchDto.getPageNo() !=null&& searchDto.getKey().length() >=3) {
            users = appUserRepo.findAllByDateOfJoiningBetweenAndSearchKeyContainsIgnoreCase(searchDto.getFromDate(), searchDto.getToDate(), searchDto.getKey(), PageRequest.of(searchDto.getOffset(), searchDto.getPageNo()));
        }else if (searchDto.getKey() != null && searchDto.getFromDate() != null &&  searchDto.getOffset() !=null  &&  searchDto.getPageNo() !=null&& searchDto.getKey().length() >=3) {
            users = appUserRepo.findAllByDateOfJoiningGreaterThanEqualAndSearchKeyContainsIgnoreCase(searchDto.getFromDate(), searchDto.getKey(), PageRequest.of(searchDto.getOffset(), searchDto.getPageNo()));
        } else if (searchDto.getKey() != null && searchDto.getToDate() != null  &&  searchDto.getOffset() !=null  &&  searchDto.getPageNo() !=null&& searchDto.getKey().length() >=3 ) {
            users = appUserRepo.findAllByDateOfJoiningLessThanEqualAndSearchKeyContainsIgnoreCase(searchDto.getToDate(), searchDto.getKey(), PageRequest.of(searchDto.getOffset(), searchDto.getPageNo()));
        }  else if (searchDto.getKey() != null && searchDto.getId() != null && searchDto.getFromDate() != null  &&  searchDto.getOffset() !=null  &&  searchDto.getPageNo() !=null&& searchDto.getKey().length() >=3) {
            users = appUserRepo.findAllByOrganizationIdAndDateOfJoiningGreaterThanEqualAndSearchKeyContainsIgnoreCase(searchDto.getId(), searchDto.getFromDate(), searchDto.getKey(), PageRequest.of(searchDto.getOffset(), searchDto.getPageNo()));
        } else if (searchDto.getKey() != null && searchDto.getId() != null && searchDto.getToDate() != null  &&  searchDto.getOffset() !=null  &&  searchDto.getPageNo() !=null&& searchDto.getKey().length() >=3) {
            users = appUserRepo.findAllByOrganizationIdAndDateOfJoiningLessThanEqualAndSearchKeyContainsIgnoreCase(searchDto.getId(), searchDto.getToDate(), searchDto.getKey(), PageRequest.of(searchDto.getOffset(), searchDto.getPageNo()));
        } else  if (searchDto.getId() != null && searchDto.getFromDate() != null && searchDto.getToDate() != null &&  searchDto.getOffset() !=null  &&  searchDto.getPageNo() !=null ) {
            users = appUserRepo.findAllByDateOfJoiningBetweenAndOrganizationId(searchDto.getFromDate(), searchDto.getToDate(), searchDto.getId(), PageRequest.of(searchDto.getOffset(), searchDto.getPageNo()));
        }else if (searchDto.getId() != null && searchDto.getToDate() != null &&  searchDto.getOffset() !=null  &&  searchDto.getPageNo() !=null) {
            users = appUserRepo.findAllByOrganizationIdAndDateOfJoiningLessThanEqual(searchDto.getId(), searchDto.getToDate(), PageRequest.of(searchDto.getOffset(), searchDto.getPageNo()));
        }else if (searchDto.getId() != null && searchDto.getOffset() !=null  &&  searchDto.getPageNo() !=null) {
            users = appUserRepo.findAllByOrganizationId(searchDto.getId(), PageRequest.of(searchDto.getOffset(), searchDto.getPageNo()));
//        } else if (searchDto.getFromDate() != null &&  searchDto.getToDate() ==null && searchDto.getId() == null &&searchDto.getKey()==null &&  searchDto.getOffset() !=null  &&  searchDto.getPageNo() !=null) {
        }else if (searchDto.getFromDate() != null && searchDto.getToDate() != null &&   searchDto.getOffset() !=null  &&  searchDto.getPageNo() !=null) {
            users = appUserRepo.findAllByDateOfJoiningBetween(searchDto.getFromDate(), searchDto.getToDate(), PageRequest.of(searchDto.getOffset(), searchDto.getPageNo()));
        } else if (searchDto.getFromDate() != null && searchDto.getOffset() !=null  &&  searchDto.getPageNo() !=null) {
            users = appUserRepo.findAllByDateOfJoiningGreaterThanEqual(searchDto.getFromDate(), PageRequest.of(searchDto.getOffset(), searchDto.getPageNo()));
        } else if (searchDto.getToDate() != null &&  searchDto.getOffset() !=null  &&  searchDto.getPageNo() !=null) {
            users = appUserRepo.findAllByDateOfJoiningLessThanEqual(searchDto.getToDate(), PageRequest.of(searchDto.getOffset(), searchDto.getPageNo()));
        }  else if (searchDto.getId() != null && searchDto.getFromDate() != null   &&  searchDto.getOffset() !=null  &&  searchDto.getPageNo() !=null) {
            users = appUserRepo.findAllByOrganizationIdAndDateOfJoiningGreaterThanEqual(searchDto.getId(), searchDto.getFromDate(), PageRequest.of(searchDto.getOffset(), searchDto.getPageNo()));
        } else
        if (searchDto.getId() != null && searchDto.getFromDate() != null && searchDto.getToDate() != null &&  searchDto.getOffset() !=null  &&  searchDto.getPageNo() !=null ) {
            users = appUserRepo.findAllByDateOfJoiningBetweenAndOrganizationId(searchDto.getFromDate(), searchDto.getToDate(), searchDto.getId(), PageRequest.of(searchDto.getOffset(), searchDto.getPageNo()));
        }else
        if(searchDto.getKey() !=null&&  searchDto.getOffset() !=null  &&  searchDto.getPageNo() !=null&& searchDto.getKey().length() >=3) {
            users = appUserRepo.findAllBySearchKeyContainsIgnoreCase(searchDto.getKey(), PageRequest.of(searchDto.getOffset(), searchDto.getPageNo()));
        }else
        if (searchDto.getKey() != null   && searchDto.getFromDate() != null && searchDto.getToDate() != null &&  searchDto.getOffset() !=null  &&  searchDto.getPageNo() !=null && searchDto.getKey().length() >=3) {
            users = appUserRepo.findAllByDateOfJoiningBetweenAndOrganizationIdAndSearchKeyContainsIgnoreCase(searchDto.getFromDate(), searchDto.getToDate(), searchDto.getId(), searchDto.getKey(), PageRequest.of(searchDto.getOffset(), searchDto.getPageNo()));
        }
        else
        if(searchDto ==null  &&  searchDto.getOffset() !=null  &&  searchDto.getPageNo() !=null){
            getAllEmployeeDetails(searchDto.getOffset(), searchDto.getPageNo());
        }
            return new GenericResponse(HttpStatus.OK.value(), users.map(this::convertEmployeeEntityToDto));
    }






    public GenericResponse filterByAdmin(FilterDto searchDto) {
        Page<Employee> users = appUserRepo.findAll(PageRequest.of(searchDto.getOffset(), searchDto.getPageNo()));
        Employee employee=authentication();
        if(searchDto.getId() ==null){
searchDto.setId(employee.getOrganization().getId());
        }
        if(searchDto.getId().equals(employee.getOrganization().getId())) {
            if (searchDto.getKey() != null && searchDto.getKey().length() >= 3) {
                if (searchDto.getToDate() != null && searchDto.getFromDate() != null && searchDto.getOffset() != null && searchDto.getPageNo() != null) {
                    if (searchDto.getFromDate().isBefore(searchDto.getToDate())) {
                        users = appUserRepo.findAllByDateOfJoiningBetweenAndSearchKeyContainsIgnoreCase(searchDto.getFromDate(), searchDto.getToDate(), searchDto.getKey(), PageRequest.of(searchDto.getOffset(), searchDto.getPageNo()));
                    }else
                    throw new AuthenticationBasedException("FromDate should be always less than ToDate");
                } else if (searchDto.getFromDate() != null && searchDto.getOffset() != null && searchDto.getPageNo() != null) {
                    users = appUserRepo.findAllByDateOfJoiningGreaterThanEqualAndSearchKeyContainsIgnoreCase(searchDto.getFromDate(), searchDto.getKey(), PageRequest.of(searchDto.getOffset(), searchDto.getPageNo()));
                } else if (searchDto.getToDate() != null && searchDto.getOffset() != null && searchDto.getPageNo() != null) {
                    users = appUserRepo.findAllByDateOfJoiningLessThanEqualAndSearchKeyContainsIgnoreCase(searchDto.getToDate(), searchDto.getKey(), PageRequest.of(searchDto.getOffset(), searchDto.getPageNo()));
                } else if (searchDto.getKey() != null && searchDto.getOffset() != null && searchDto.getPageNo() != null) {
                    users = appUserRepo.findAllBySearchKeyContainsIgnoreCase(searchDto.getKey(), PageRequest.of(searchDto.getOffset(), searchDto.getPageNo()));
                }
            } else {
                if (searchDto.getFromDate() != null && searchDto.getToDate() != null && searchDto.getOffset() != null && searchDto.getPageNo() != null) {
                    if (searchDto.getFromDate().isBefore(searchDto.getToDate())) {
                        users = appUserRepo.findAllByDateOfJoiningBetweenAndOrganizationId(searchDto.getFromDate(), searchDto.getToDate(), searchDto.getId(), PageRequest.of(searchDto.getOffset(), searchDto.getPageNo()));
                    }else
                    throw new AuthenticationBasedException("FromDate should be always less than ToDate");
                } else if(searchDto.getToDate() !=null  && searchDto.getId() !=null&& searchDto.getOffset() !=null && searchDto.getPageNo()!=null){
                    users=appUserRepo.findAllByOrganizationIdAndDateOfJoiningLessThanEqual(searchDto.getId(),searchDto.getToDate(),PageRequest.of(searchDto.getOffset(), searchDto.getPageNo()));
                }else if (searchDto.getToDate() != null && searchDto.getOffset() != null && searchDto.getPageNo() != null) {
                    users = appUserRepo.findAllByDateOfJoiningLessThanEqual(searchDto.getToDate(), PageRequest.of(searchDto.getOffset(), searchDto.getPageNo()));
                } else if(searchDto.getFromDate()!=null && searchDto.getId() !=null && searchDto.getOffset() != null && searchDto.getPageNo() != null){
                    users=appUserRepo.findAllByOrganizationIdAndDateOfJoiningGreaterThanEqual(searchDto.getId(),searchDto.getFromDate(),PageRequest.of(searchDto.getOffset(), searchDto.getPageNo()));
                }
                else if (searchDto.getFromDate() != null && searchDto.getOffset() != null && searchDto.getPageNo() != null) {
                        users = appUserRepo.findAllByDateOfJoiningGreaterThanEqual(searchDto.getFromDate(), PageRequest.of(searchDto.getOffset(), searchDto.getPageNo()));
                }
                else if (searchDto.getOffset() != null && searchDto.getPageNo() != null) {
                    users = appUserRepo.findAllByOrganizationId(searchDto.getId(), PageRequest.of(searchDto.getOffset(), searchDto.getPageNo()));
                }
            }
        }else throw new AuthenticationBasedException("unauthorized");
return new GenericResponse(HttpStatus.OK.value(), users.map(this::convertEmployeeEntityToDto));
    }












public Employee authentication() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee employee = (Employee) authentication.getPrincipal();
        return employee;
    }




    @Override
    public GenericResponse searchList(FilterDto searchDto) {
        Employee employee=authentication();
        if (employee != null) {
                if (employee.getRoleType().equals(RoleType.SUPER_ADMIN)) {
                        return filterBySuperAdmin(searchDto);
                } else if(employee.getRoleType().equals(RoleType.ADMIN) || employee.getRoleType().equals(RoleType.MANAGER)) {
                        return filterByAdmin(searchDto);
                }
                return new GenericResponse(HttpStatus.OK.value(), "UNAUTHORISED");
            }
            return new GenericResponse(HttpStatus.OK.value(), "unauthorised");
        }





        @Override
        public GenericResponse getAllEmployeeDetails ( int offset, int pageNo){
            Page<EmployeeDto> employeeDtos = appUserRepo.findAll(PageRequest.of(offset, pageNo)).map(this::convertEmployeeEntityToDto);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                Employee employee = (Employee) authentication.getPrincipal();
                if (employee != null) {
                    if (employee.getRoleType().equals(RoleType.SUPER_ADMIN)) {
                        return new GenericResponse(HttpStatus.OK.value(), employeeDtos);
                    } else if (employee.getRoleType().equals(RoleType.ADMIN)) {
                        Page<Employee> adminGet = appUserRepo.findAllByOrganizationId(employee.getOrganization().getId(), PageRequest.of(offset, pageNo));
                        return new GenericResponse(HttpStatus.OK.value(), adminGet.map(this::convertEmployeeEntityToDto));
                    } else if (employee.getRoleType().equals(RoleType.MANAGER)) {
                        Page<Employee> adminGet = appUserRepo.findAllByOrganizationId(employee.getOrganization().getId(), PageRequest.of(offset, pageNo));
                        return new GenericResponse(HttpStatus.OK.value(), adminGet.map(this::convertEmployeeEntityToDto));
                    }
                }
            }
            return new GenericResponse(HttpStatus.OK.value(), "UNAUTHORISED");
        }





    @Override
    public GenericResponse getAllEmployeeDetails() {
        List<EmployeeDto> employeeDtos = appUserRepo.findAll().stream().map(this::convertEmployeeEntityToDto).collect(Collectors.toList());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Employee employee = (Employee) authentication.getPrincipal();
            if (employee != null) {
                if (employee.getRoleType().equals(RoleType.SUPER_ADMIN)) {
                    return new GenericResponse(HttpStatus.OK.value(), employeeDtos);
                } else if (employee.getRoleType().equals(RoleType.ADMIN)) {
                    List<Employee> adminGet = appUserRepo.findAllByOrganizationId(employee.getOrganization().getId());
                    return new GenericResponse(HttpStatus.OK.value(), adminGet.stream().map(this ::convertEmployeeEntityToDto).collect(Collectors.toList()));
                } else if (employee.getRoleType().equals(RoleType.MANAGER)) {
                    List<Employee> adminGet = appUserRepo.findAllByOrganizationId(employee.getOrganization().getId());
                    return new GenericResponse(HttpStatus.OK.value(), adminGet.stream().map(this::convertEmployeeEntityToDto).collect(Collectors.toList()));
                }
            }
        }
        return null;
    }




    public String searchKey(Employee dto){
        String searchKey="";
        if(dto.getName()!=null){
            searchKey=dto.getName();
        }
        if(dto.getEmail()!=null){
            searchKey+=dto.getEmail();
        }
        if(dto.getContactNo()!=null){
            searchKey+=dto.getContactNo();
        }
        if(dto.getRoleType()!=null){
            searchKey+=dto.getRoleType();
        }
        if(dto.getDateOfJoining() !=null){
            searchKey+=dto.getDateOfJoining();
        }
if(dto.getOrganization().getName() !=null){
    searchKey += dto.getOrganization().getName();
}
if(dto.getOrganization().getLocation() !=null){
    searchKey +=dto.getOrganization().getLocation();
}
        return searchKey;
    }







//    @Override
//    @Transactional
//    public  GenericResponse findBySearchKey(String key, int offset, int pageNo) {
//        List<Employee> list = appUserRepo.findAllBySearchKeyContainsIgnoreCase(key, PageRequest.of(offset, pageNo));
//        List<Employee> emplist=new ArrayList<>();
//        if (key != null ) {
//            if (key.length() >= 3) {
//                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//                if (authentication != null) {
//                    Employee employee = (Employee) authentication.getPrincipal();
//                    if (employee != null) {
//                        if (employee.getRoleType().equals(RoleType.SUPER_ADMIN)) {
//                            return new GenericResponse(HttpStatus.OK.value(), list.stream().map(this::convertEmployeeEntityToDto).toList());
//                        } else if(employee.getRoleType().equals(RoleType.ADMIN)){
//                            emplist=list.stream().filter(e->e.getRoleType().equals(RoleType.MANAGER) ||  e.getRoleType().equals(RoleType.EMPLOYEE) || e.getRoleType().equals(RoleType.ADMIN) && e.getOrganization().getId().equals(employee.getOrganization().getId())).toList();
//                            return new GenericResponse(HttpStatus.OK.value(), emplist.stream().map(this::convertEmployeeEntityToDto).toList());
//                        }else if(employee.getRoleType().equals(RoleType.MANAGER)){
//                            emplist=list.stream().filter(e->e.getRoleType().equals(RoleType.MANAGER) ||  e.getRoleType().equals(RoleType.EMPLOYEE) && e.getOrganization().getId().equals(employee.getOrganization().getId())).toList();
//                            return new GenericResponse(HttpStatus.OK.value(), emplist.stream().map(this::convertEmployeeEntityToDto).toList());
//                        }else if(employee.getRoleType().equals(RoleType.EMPLOYEE)){
//                            emplist=list.stream().filter(e-> e.getRoleType().equals(RoleType.EMPLOYEE) && e.getId().equals(employee.getId())).toList();
//                            return new GenericResponse(HttpStatus.OK.value(), emplist.stream().map(this::convertEmployeeEntityToDto).toList());
//                        }
//                    }
//                }
//            }else throw new NotFoundException("key must contain greater than 3 characters ");
//        }return  new GenericResponse(HttpStatus.OK.value(), "data is not available");
//    }







    @Override
    public GenericResponse getById(Long id) {
        List<EmployeeDto> employeeDtos = appUserRepo.findAll().stream().map(this::convertEmployeeEntityToDto).collect(Collectors.toList());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Employee employee = (Employee) authentication.getPrincipal();
            List<EmployeeDto> employees=new ArrayList<>();
            if (employee != null) {
                if (employee.getRoleType().equals(RoleType.SUPER_ADMIN)) {
                    Optional<Employee> employeeOptional = appUserRepo.findById(id);
                    if (employeeOptional.isPresent()) {
                        return new GenericResponse(HttpStatus.OK.value(), employeeOptional.stream().map(this::convertEmployeeEntityToDto).collect(Collectors.toList()));
                    }throw  new NotFoundException("employee id is not found");
                } else if (employee.getRoleType().equals(RoleType.ADMIN)) {
                    employees = employeeDtos.stream().filter(a -> a.getRoleType() != RoleType.SUPER_ADMIN && a.getRoleType() != RoleType.ADMIN && a.getId().equals(id) && a.getOrganization().getId().equals(employee.getOrganization().getId())).toList();
                        return new GenericResponse(HttpStatus.OK.value(), employees);
                        }else {
                    throw new AuthenticationBasedException("unauthorized");
                }
                    }
                }else throw new AuthenticationBasedException("Authentication failed");
        return null;
            }




    public Employee convertEmployeeDtoToEntity(EmployeeDto employeeDto) throws PatternNotMatchException {
        String request=employeeDto.requiredData();
        if (!request.isEmpty())
            throw new BadAlertException(request);
        Employee employee = new Employee();
        employee.setId(employeeDto.getId());
            employee.setName(employeeDto.getName());
            Optional<Employee> optionalEmployee = appUserRepo.getByContactNo(employeeDto.getContactNo());
            if (optionalEmployee.isEmpty() || optionalEmployee.get().getId().equals(employeeDto.getId())) {
                employee.setContactNo(employeeDto.getContactNo());
            } else throw new DuplicateValueException("mobile number is already existed");
            Optional<Employee> employeeOptional = appUserRepo.getByEmail(employeeDto.getEmail());
            if (employeeOptional.isEmpty() || employeeOptional.get().getId().equals(employeeDto.getId())) {
                employee.setEmail(employeeDto.getEmail());
            } else throw new DuplicateValueException("email id is already existed");
                employee.setRoleType(employeeDto.getRoleType());
                if(employeeDto.getDateOfJoining().isBefore(LocalDate.now() ) || employeeDto.getDateOfJoining().equals(LocalDate.now())) {
                    employee.setDateOfJoining(employeeDto.getDateOfJoining());
                }else throw new BadAlertException("date is must be less than current date");
                     employee.setPassword(bCryptPasswordEncoder.encode(employeeDto.getPassword()));
            if (employeeDto.getOrganization() != null) {
                Optional<Organization> organization = organizationRepo.findById(employeeDto.getOrganization().getId());
                if (organization.isPresent()) {
                    employee.setOrganization(organization.get());
                } else throw new NullPointerException("organization is not found");
            } else {
                throw new NullPointerException("organisation id is null");
            }
            employee.setSearchKey(searchKey(employee));
            return employee;
    }




    private Organization convertOrganizationDtoToEntity(OrganizationDto organization) {
        Organization organization1=new Organization();
        organization1.setId(organization.getId());
        organization1.setName(organization.getName());
        organization1.setLocation(organization.getLocation());
        return organization1;
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
        if(employee.getOrganization()!= null){
            Optional<Organization> organization=organizationRepo.findById(employee.getOrganization().getId());
            if(organization.isPresent()) {
                employeeDto.setOrganization(convertOrganizationEntityToDto(organization.get()));
            }
        }
return employeeDto;
    }
    @Override
    public List<EmployeeSearchDto> search(EmployeeSearch employeeSearch){
        List<EmployeeSearchDto> employeeSearchDtos=new ArrayList<>();
        List<Employee> employees=searchEmployee(employeeSearch);
        employees.stream().forEach(a->{
            EmployeeSearchDto employeeSearchDto=new EmployeeSearchDto();
            employeeSearchDto.setName(a.getName());
            employeeSearchDto.setId(a.getId());
            employeeSearchDto.setRoleType(a.getRoleType());
            employeeSearchDtos.add(employeeSearchDto);
        });
        return employeeSearchDtos;
    }



    public List<Employee> searchEmployee(EmployeeSearch employeeSearch) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Employee> query = criteriaBuilder.createQuery(Employee.class);
        Root<Employee> root = query.from(Employee.class);
        List<Predicate> predicates = new LinkedList<>();
        for(String  employee:employeeSearch.getName()){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + employee.toLowerCase() + "%"));
        }
        Predicate[] predicateArray = new Predicate[predicates.size()];
        query.where(predicates.toArray(predicateArray));
        EntityGraph<Employee> entityGraph = entityManager.createEntityGraph(Employee.class);
        return entityManager.createQuery(query).setHint("javax.persistence.fetchgraph", entityGraph).getResultList();
    }






    private OrganizationDto convertOrganizationEntityToDto(Organization organization) {
        OrganizationDto organizationDto=new OrganizationDto();
        organizationDto.setId(organization.getId());
        organizationDto.setName(organization.getName());
        organizationDto.setLocation(organization.getLocation());
        return organizationDto;
    }
}


