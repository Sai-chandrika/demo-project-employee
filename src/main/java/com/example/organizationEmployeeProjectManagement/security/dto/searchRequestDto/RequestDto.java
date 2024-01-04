package com.example.organizationEmployeeProjectManagement.security.dto.searchRequestDto;

import com.example.organizationEmployeeProjectManagement.security.enums.specification.Operation;
import com.example.organizationEmployeeProjectManagement.security.enums.specification.Operator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author chandrika
 * @ProjectName demo-project-aprl-backend-2023
 * @since 25-04-2023
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {
    private SearchRequest  request;
    private List<SearchRequest> searchRequest;
    private Operation operation;
}