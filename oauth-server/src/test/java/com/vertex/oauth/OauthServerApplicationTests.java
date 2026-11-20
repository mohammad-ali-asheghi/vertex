package com.vertex.oauth;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OauthServerApplicationTests {

    @Test
    void verifiesModularStructure() {
        ApplicationModules modules = ApplicationModules.of(OauthServerApplication.class);
        assertNotNull(modules);
        System.out.println("Discovered Spring Modulith modules: " + modules);
    }

}
