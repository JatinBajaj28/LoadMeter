package com.software.student1;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserController {

    @GetMapping("/api/users")
    public String getUsers() {

        return "Hello from Student Application";
    }
    @PostMapping(
            value = "/api/users",
            consumes = "application/json"
    )
    public String createUser(
            @RequestBody Map<String, String> user
    ) {
        return "Created user: " + user.get("name");
    }
}