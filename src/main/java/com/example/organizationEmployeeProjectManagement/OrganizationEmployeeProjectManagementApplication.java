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

		System.out.println("*****************************************");
		Integer a=100;
		Integer b=100;
		Integer c=500;
		Integer d=500;

		if(a==b){
			System.out.println("a=b is correct");
		}else {
			System.out.println("a=b is  not correct");
		}

		if(c==d){
			System.out.println("c=d is correct");
		}else {
			System.out.println("c=d is  not correct");
		}

		System.out.println("***********************************************************************************");

		if(a.equals(b)){
			System.out.println("a=b is correct");
		}else {
			System.out.println("a=b is  not correct");
		}
		if(c.equals(d)){
			System.out.println("c=d is correct");
		}else {
			System.out.println("c=d is  not correct");
		}
	}
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder(){
		return new BCryptPasswordEncoder();
	}



}

