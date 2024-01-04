package com.example.organizationEmployeeProjectManagement.security.dto.searchDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author chandrika
 * @ProjectName Organization_Employee_ProjectManagement
 * @since 08-04-2023
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterDto {
    private Long id;
    private LocalDate fromDate;
    private  LocalDate toDate;
    private String key;
    private Integer offset;
    private Integer pageNo;
}