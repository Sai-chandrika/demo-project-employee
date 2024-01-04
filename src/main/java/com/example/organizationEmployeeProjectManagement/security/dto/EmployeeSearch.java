package com.example.organizationEmployeeProjectManagement.security.dto;

import com.example.organizationEmployeeProjectManagement.security.entity.organization.Organization;
import com.example.organizationEmployeeProjectManagement.security.enums.roletype.RoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author chandrika.g
 * user
 * @ProjectName demo-project-aprl-backend-2023
 * @since 04-01-2024
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class EmployeeSearch {
    private List<Long> id;
    private List<String> name;
    private RoleType roleType;
    private Long organization;

}
