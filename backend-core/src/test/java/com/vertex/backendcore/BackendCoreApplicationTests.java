package com.vertex.backendcore;

import com.vertex.backendcore.security.CryptoUtil;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class BackendCoreApplicationTests {

    @Test
    void testCryptoUtilKeyPairGeneration() throws Exception {
        KeyPair keyPair = CryptoUtil.generateRsaKeyPair();
        assertNotNull(keyPair);
        assertNotNull(keyPair.getPublic());
        assertNotNull(keyPair.getPrivate());
    }

}
