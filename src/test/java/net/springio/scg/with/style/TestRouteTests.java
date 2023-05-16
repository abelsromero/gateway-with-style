package net.springio.scg.with.style;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@ActiveProfiles("local")
@AutoConfigureWebTestClient
class TestRouteTests {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void testRouteIsAvailable() {
        webTestClient.get()
            .uri("/test/get")
            .exchange()
            .expectStatus()
            .isOk();
    }

}
