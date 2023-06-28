package ru.practicum.shareit.user.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.util.BaseClient;

import java.util.Map;

@Service
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> get(Long userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> post(UserDto userDto) {
        return post("", userDto);
    }

    public ResponseEntity<Object> put(UserDto userDto) {
        return put("", userDto);
    }

    public ResponseEntity<Object> patch(Long userId, Map<String, Object> fields) {
        return patch("/" + userId, fields);
    }

    public ResponseEntity<Object> getAll() {
        return get("");
    }

    public void delete(Long userId) {
        delete("/" + userId);
    }
}