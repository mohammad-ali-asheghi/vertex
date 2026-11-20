package com.vertex.eureka;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.cloud.compatibility-verifier.enabled=false")
class EurekaServerApplicationTests {

    @Test
    void contextLoads() {
    }

}
