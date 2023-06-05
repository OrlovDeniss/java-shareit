package ru.practicum.shareit.abstraction.model;

import ru.practicum.shareit.user.model.User;

public interface UserObject extends Identified {

    User getUser();

    void setUser(User user);

}