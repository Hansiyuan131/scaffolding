package com.yuanstack.sca.service.graphql.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hansiyuan
 */
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 5857637197019533387L;
    private Long id;
    private Long organizationId;
    private Long departmentId;
    private String name;
    private int age;
    private String position;
    private int salary;
}
