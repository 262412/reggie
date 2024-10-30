package com.example.reggie.cache_demo.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class UserCache implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private int age;

    private String address;

}
