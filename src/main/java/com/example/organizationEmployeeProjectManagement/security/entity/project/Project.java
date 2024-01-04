package com.example.organizationEmployeeProjectManagement.security.entity.project;

import com.example.organizationEmployeeProjectManagement.security.entity.employeeProject.EmployeeProject;
import com.example.organizationEmployeeProjectManagement.security.entity.organization.Organization;
import com.example.organizationEmployeeProjectManagement.security.enums.projectStatus.ProjectStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chandrika
 * @ProjectName Organization_Employee_ProjectManagement
 * @since 28-03-2023
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"name"})})
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", unique = true)
    private  String name;
    @Column(name = "startDate", nullable = false)
    private LocalDate startDate;
    @Column(name = "endDate")
    private LocalDate endDate;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProjectStatus status;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "project")
    List<EmployeeProject> employeeProjects=new ArrayList<>();
    @ManyToOne(fetch = FetchType.EAGER,cascade = {CascadeType.MERGE})
    @JoinColumn(name ="org_id", referencedColumnName = "id")
    private Organization organization;
}

