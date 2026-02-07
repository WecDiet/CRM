package com.CRM.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee extends Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "employee_id")
    private User user;

    @Column(name = "employee_code", length = 50, nullable = false, unique = true)
    private String employeeCode;

    @Column(name = "hire_date", nullable = false)
    private Date hireDate;

    @Column(name = "salary")
    private BigDecimal salary;

    @Column(name = "position", length = 100)
    private String position;

    @Column(name = "department", length = 100)
    private String department;
}
