package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Boolean available;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany
    private List<Comment> comments;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    private Request request;

}