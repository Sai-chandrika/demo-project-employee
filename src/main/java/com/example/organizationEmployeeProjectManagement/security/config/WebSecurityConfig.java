package com.example.organizationEmployeeProjectManagement.security.config;


import com.example.organizationEmployeeProjectManagement.security.repo.employeeRepo.EmployeeRepo;
import com.example.organizationEmployeeProjectManagement.security.service.employeeservice.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    private EmployeeRepo appUserRepo;

    @Autowired
    private EmployeeService userService;

    private String[] PUBLIC_RESOURCE_AND_URL = {"/",
            "/api/v1/employee/sign-up",
            "/api/v1/employee/sign-in",
            "/api/v1/employee/email_verification",
            "/api/v1/employee/new_password",
            "/api/v1/employee/excel-upload-for-create-user",
            "/api/v1/employee/search/employee",
            "/api/v1/save"
    };


    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        // We don't need CSRF for this example

//        http.cors().configurationSource(request -> {
//            String[] methods = { "POST"};
//            System.out.println("Enter 1");
//            CorsConfiguration config = new CorsConfiguration();
//            config.setAllowedHeaders(Collections.singletonList("*"));
//            config.setAllowedMethods(Arrays.asList(methods));
//            config.addAllowedOriginPattern("1.1.1.1");
//            config.setAllowCredentials(true);
//            return config;
//        });

        http.csrf()
                .disable()
                .authorizeHttpRequests()
                .anyRequest()
                .authenticated()
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler()).and().addFilterBefore(
//                        new CustomAuthFilter(userService), BasicAuthenticationFilter.class).
                new JwtAuthenticationFilter(jwtTokenUtils,appUserRepo), BasicAuthenticationFilter.class).
                addFilterBefore(new CustomCORSFilter(), ChannelProcessingFilter.class);

        return http.build();
    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.debug(true)
                .ignoring()
                .requestMatchers(PUBLIC_RESOURCE_AND_URL);
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }
}
