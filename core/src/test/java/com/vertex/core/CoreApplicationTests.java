package com.vertex.core;

import com.vertex.core.util.CharacterUtil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CoreApplicationTests {

    @Test
    void testCoreCharacterUtil() {
        assertEquals("علی", CharacterUtil.handleSpecialPersianChars("علي"));
    }

}
