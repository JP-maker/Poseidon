package com.nnk.poseidon.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "Id")
    private Integer id;

    @NotBlank(message = "Username is mandatory")
    @Column(name = "username", nullable = true, length = 125)
    private String username;

    @NotBlank(message = "Password is mandatory")
    @Column(name = "password", nullable = true, length = 125)
    private String password;

    @NotBlank(message = "FullName is mandatory")
    @Column(name = "fullname", nullable = true, length = 125)
    private String fullname;

    @NotBlank(message = "Role is mandatory")
    @Column(name = "role", nullable = true, length = 125)
    private String role;
}
