package com.example.organizationEmployeeProjectManagement.security.dto.searchRequestDto;

import com.example.organizationEmployeeProjectManagement.security.enums.specification.Operation;
import com.example.organizationEmployeeProjectManagement.security.enums.specification.Operator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author chandrika
 * @ProjectName demo-project-aprl-backend-2023
 * @since 24-04-2023
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchRequest {
    private String column;
    private String value;
    private String joinTable;
    private Operator operator;
}
