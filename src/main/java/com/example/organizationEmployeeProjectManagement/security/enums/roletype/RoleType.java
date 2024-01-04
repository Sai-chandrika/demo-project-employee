package com.example.organizationEmployeeProjectManagement.security.enums.roletype;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@NotNull
@NotEmpty
public enum RoleType {
SUPER_ADMIN,
    ADMIN,//0
    MANAGER,//1
    EMPLOYEE;//2
}
