package com.example.organizationEmployeeProjectManagement.security.config;

import com.example.organizationEmployeeProjectManagement.security.dto.genericResponce.GenericResponse;
import com.example.organizationEmployeeProjectManagement.security.entity.employee.Employee;
import com.example.organizationEmployeeProjectManagement.security.repo.employeeRepo.EmployeeRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
import java.util.Date;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtTokenUtils jwtTokenUtil;

    private EmployeeRepo appUserRepo;

    JwtAuthenticationFilter(JwtTokenUtils jwtTokenUtil, EmployeeRepo appUserRepo) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.appUserRepo = appUserRepo;
    }

    private static Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filter)
            throws ServletException, IOException {
        try {
            String authToken = extractAuthToken(req.getHeader("Authorization"));
            String username = jwtTokenUtil.parseToken(authToken);
            Employee user = appUserRepo.findByEmail(username);
            if (user != null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, Arrays.asList(
                        new SimpleGrantedAuthority(user.getRoleType().name())));
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                logger.info("authenticated user " + username + ", setting security context");
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filter.doFilter(req, res);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            GenericResponse resp = new GenericResponse(HttpStatus.UNAUTHORIZED.value(), "TOKEN  TIME IS EXPIRED");
            String jsonRespString = ow.writeValueAsString(resp);
            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            PrintWriter writer = res.getWriter();
            writer.write(jsonRespString);
            System.out.println("===============================");
        }
    }

    private String extractAuthToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
            return authorizationHeader.substring(7); // Will be useful with Swagger
        }
        return authorizationHeader; // Token not found or header value format is incorrect
    }
}



