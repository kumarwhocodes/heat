package com.zerobee.heat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "customers")
public class Customer {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    private Integer age;
    private String address;
    private String phone;
    private String email;
    
}
