package com.software.student1;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:h2:mem:student1test;MODE=MySQL",
        "spring.datasource.username=sa",
        "spring.datasource.password="
})
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
class Student1ApplicationTests {

    @LocalServerPort
    private int port;

    @Test
    void createsUsersWithGeneratedIdsAndReadsThemBack() {
        var requests = new SimpleClientHttpRequestFactory();
        requests.setConnectTimeout(5_000);
        requests.setReadTimeout(5_000);
        RestClient client = RestClient.builder()
                .baseUrl("http://127.0.0.1:" + port)
                .requestFactory(requests)
                .build();

        var first = client.post().uri("/api/users")
                .body(Map.of("name", "Aarav Sharma"))
                .retrieve().toEntity(UserController.User.class);
        var second = client.post().uri("/api/users")
                .body(Map.of("name", "Aarav Sharma"))
                .retrieve().toEntity(UserController.User.class);

        assertEquals(HttpStatus.CREATED, first.getStatusCode());
        assertEquals(HttpStatus.CREATED, second.getStatusCode());
        assertNotNull(first.getBody());
        assertNotNull(second.getBody());
        assertTrue(first.getBody().id() > 0);
        assertTrue(second.getBody().id() > first.getBody().id());
        assertEquals("Aarav Sharma", first.getBody().name());

        assertThrows(HttpClientErrorException.BadRequest.class, () ->
                client.post().uri("/api/users")
                        .body(Map.of("name", " "))
                        .retrieve().toBodilessEntity()
        );

        var users = client.get().uri("/api/users")
                .retrieve().body(UserController.User[].class);
        assertNotNull(users);
        assertEquals(2, users.length);
        assertTrue(Arrays.asList(users).containsAll(java.util.List.of(first.getBody(), second.getBody())));
    }
}
