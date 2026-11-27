package com.software.student1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final RequestCounterService requestCounterService;

    public UserController(RequestCounterService requestCounterService) {
        this.requestCounterService = requestCounterService;
    }

    @GetMapping("/api/test")
    public RequestCounts getTest() {
        return requestCounterService.recordGetRequest();
    }

    @PostMapping("/api/test")
    public RequestCounts postTest() {
        return requestCounterService.recordPostRequest();
    }

    @GetMapping("/api/counts")
    public RequestCounts getCounts() {
        return requestCounterService.getCurrentCounts();
    }
}