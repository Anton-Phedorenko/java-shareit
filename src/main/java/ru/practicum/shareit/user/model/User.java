package ru.practicum.shareit.user.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * TODO Sprint add-controllers.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true, length = 300)
    private String email;

    @Column(name = "name", length = 300)
    private String name;
}
