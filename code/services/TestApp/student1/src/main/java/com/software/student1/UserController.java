package com.software.student1;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Statement;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final JdbcTemplate jdbc;

    public UserController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping
    public List<User> getUsers() {
        return jdbc.query(
                "SELECT id, name FROM student_test_users ORDER BY id",
                (row, index) -> new User(row.getLong("id"), row.getString("name"))
        );
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody CreateUser request) {

        System.out.println("hello");
        String name = request.name();
        if (name == null || name.isBlank() || name.length() > 255) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Name must contain between 1 and 255 characters"
            );
        }

        GeneratedKeyHolder key = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            var statement = connection.prepareStatement(
                    "INSERT INTO student_test_users (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, name);
            return statement;
        }, key);

        Number id = key.getKey();
        if (id == null) {
            throw new IllegalStateException("The database did not return a user ID");
        }
        return new User(id.longValue(), name);
    }

    public record CreateUser(String name) {}

    public record User(long id, String name) {}
}

@RestController
class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "hello tester";
    }
}
