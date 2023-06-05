package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.abstraction.model.Identified;

import javax.persistence.*;

@Data
@Entity
@Table(name = "usr")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Identified {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;

}