package com.vertex.stellarui;

import com.vertex.stellarui.constant.Routing;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StellarUiApplicationTests {

    @Test
    void testRoutingConstants() {
        assertEquals("login", Routing.LOGIN);
        assertEquals("", Routing.DASHBOARD);
        assertEquals("users", Routing.USERS);
    }

}
