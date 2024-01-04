package com.example.organizationEmployeeProjectManagement.security.config;

import com.example.organizationEmployeeProjectManagement.security.dto.genericResponce.GenericResponse;
import com.example.organizationEmployeeProjectManagement.security.dto.employeeDto.EmployeeDto;
import com.example.organizationEmployeeProjectManagement.security.entity.employee.Employee;
import com.example.organizationEmployeeProjectManagement.security.service.employeeservice.EmployeeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class CustomAuthFilter extends OncePerRequestFilter {

    private EmployeeService userService;

    CustomAuthFilter(EmployeeService userService) {
        this.userService = userService;
    }

    private static Logger logger = LoggerFactory.getLogger(CustomAuthFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filter)
            throws IOException {
        try {
            String authToken = req.getHeader("Authorization");
            if (authToken != null) {
                Employee user = userService.verifyUser(generateLoginDto(authToken.split(":")[0], authToken.split(":")[1]));
                System.out.println("AppUser === " + user);
                if (user != null) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, Arrays.asList(
                            new SimpleGrantedAuthority(user.getRoleType().name())));
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    logger.info("authenticated user " + authToken.split(":")[0] + ", setting security context");
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    filter.doFilter(req, res);
                } else {
                    generateUnauthorisedAccess(res);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            generateUnauthorisedAccess(res);
        }


    }

    public EmployeeDto generateLoginDto(String email, String password) {
        EmployeeDto dto = new EmployeeDto();
        dto.setEmail(email);
        dto.setPassword(password);
        return dto;
    }

    public void generateUnauthorisedAccess(HttpServletResponse res) throws JsonProcessingException, IOException {

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        GenericResponse resp = new GenericResponse(HttpStatus.UNAUTHORIZED.value(), "UNAUTORISED");
        String jsonRespString = ow.writeValueAsString(resp);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer = res.getWriter();
        writer.write(jsonRespString);
        System.out.println("===============================");

    }
}
