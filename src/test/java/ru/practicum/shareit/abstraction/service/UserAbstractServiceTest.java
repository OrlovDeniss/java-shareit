package ru.practicum.shareit.abstraction.service;

import ru.practicum.shareit.user.model.User;

import java.util.Map;

class UserAbstractServiceTest extends AbstractServiceTest<User> {

    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "name";
    private static final String UPDATED_USER_NAME = "updName";
    private static final String USER_EMAIL = "user@user.com";
    private static final String UPDATED_USER_EMAIL = "updUser@user.com";

    @Override
    protected User getEntity() {
        return User.builder()
                .id(USER_ID)
                .name(USER_NAME)
                .email(USER_EMAIL)
                .build();
    }

    @Override
    protected User getReference() {
        return User.builder()
                .id(USER_ID)
                .name(UPDATED_USER_NAME)
                .email(UPDATED_USER_EMAIL)
                .build();
    }

    @Override
    protected Map<String, Object> getNewFields() {
        return Map.of(
                "name", UPDATED_USER_NAME,
                "email", UPDATED_USER_EMAIL
        );
    }

}
