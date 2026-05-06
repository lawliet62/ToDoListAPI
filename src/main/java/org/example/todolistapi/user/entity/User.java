package org.example.todolistapi.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    @JsonIgnore
    private String encodedPassword;

    public User(@NotBlank String name, @Email String email, @NotBlank String encodedPassword) {
        this.name = name;
        this.email = email;
        this.encodedPassword = encodedPassword;
    }
}
