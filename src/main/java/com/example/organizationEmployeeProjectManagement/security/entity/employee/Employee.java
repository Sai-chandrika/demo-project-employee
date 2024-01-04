package com.example.organizationEmployeeProjectManagement.security.entity.employee;

import com.example.organizationEmployeeProjectManagement.security.entity.employeeProject.EmployeeProject;
import com.example.organizationEmployeeProjectManagement.security.entity.organization.Organization;
import com.example.organizationEmployeeProjectManagement.security.enums.roletype.RoleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"email"}), @UniqueConstraint(columnNames = {"contactNo"})})
public class Employee {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
 @Column(name="contactNo", length = 20, unique = true)
 @Size(min = 10 ,max = 12,message = "invalid contact number (min 10 and max 12) please check once")
    private  String contactNo;
    @Column(name = "email", unique = true)
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", message = "invalid email input please check once")
    private String email;
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private RoleType roleType;
    @Column(name = "name", nullable = false)
    private String name;
    private  String searchKey;
    private String password;
    @Column(name = "dateOfJoining")
    private LocalDate dateOfJoining;
    @ManyToOne(fetch = FetchType.LAZY,cascade = {CascadeType.MERGE})
    @JoinColumn(name ="org_id", referencedColumnName = "id")
    private Organization organization;
 @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "employee")
 List<EmployeeProject> employeeProjects=new ArrayList<>();

   public Organization getOrganization() {
      return organization;
   }

   public void setOrganization(Organization organization) {
      this.organization = organization;
   }
}

