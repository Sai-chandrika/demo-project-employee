package com.example.organizationEmployeeProjectManagement;

import com.example.organizationEmployeeProjectManagement.security.dto.genericResponce.GenericResponse;
import com.example.organizationEmployeeProjectManagement.security.enums.roletype.RoleType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class OrganizationEmployeeProjectManagementApplication {
	public   void s(){
		String role="admin";
		boolean matchFound = false;
		for(RoleType roleType:RoleType.values()){
			if(roleType.name().equalsIgnoreCase(role)){
				matchFound = true;
				System.out.println(role+"**********************  true **********************");
				break;
			}
		}
		if (!matchFound) {
			throw new NullPointerException("null");
		}
	}
	public static void main(String[] args) {
		SpringApplication.run(OrganizationEmployeeProjectManagementApplication.class, args);
		OrganizationEmployeeProjectManagementApplication o=new OrganizationEmployeeProjectManagementApplication();
		o.s();
	}
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder(){
		return new BCryptPasswordEncoder();
	}



}

