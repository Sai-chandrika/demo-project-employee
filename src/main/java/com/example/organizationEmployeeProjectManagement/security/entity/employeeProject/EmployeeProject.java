package com.example.organizationEmployeeProjectManagement.security.entity.employeeProject;
import com.example.organizationEmployeeProjectManagement.security.entity.employee.Employee;
import com.example.organizationEmployeeProjectManagement.security.entity.project.Project;
import com.example.organizationEmployeeProjectManagement.security.enums.empProjectStatus.EmployeeProjectStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * @author chandrika
 * @ProjectName Organization_Employee_ProjectManagement
 * @since 30-03-2023
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"e_id", "p_id"})})
public class EmployeeProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    @ManyToOne(fetch = FetchType.LAZY,cascade = {CascadeType.MERGE})
    @JoinColumn(name ="p_id", referencedColumnName = "id")
    private Project project=new Project();
    @ManyToOne(fetch = FetchType.LAZY,cascade = {CascadeType.MERGE})
    @JoinColumn(name ="e_id", referencedColumnName = "id")
    private Employee employee=new Employee();
    private LocalDate startDate;
    private LocalDate endDate;
    @Enumerated(EnumType.STRING)
    @Column(name = "emp_pro_status", nullable = false)
    private EmployeeProjectStatus status;
}
