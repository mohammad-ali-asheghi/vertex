package com.vertex.server;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.jupiter.api.Test;

class ConfigServerApplicationTests {

    @Test
    void testDecrypt() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword("Lh7^hy(d)T#)j5(*0)a@!f**&_usOp]0}x");
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);

        try {
            System.out.println("secret-key: " + encryptor.decrypt("wbbdvgV+bFb4FFwPmXUvy/xxidvNRoB7Gnjo3r9q4wWVcp8dFMukhQsQtjdQF+KiYSGL2WmbxaKVYKXYiI5j3khp/D/IN1AQY888nVcIi4c="));
            System.out.println("encode-key: " + encryptor.decrypt("5V9PvdKtdUE3I9LpjM4bF0FOmOc54Q1NfrAm1U4Kyn1AFtqENq/oGCgTYSY4QBUIUxyBHT1di4yz/hcYj9Yt7ZFNxQCBDpNfrC3fSVTtHck="));
            System.out.println("username: " + encryptor.decrypt("51b6rtMW9PgUYigkO/AGMHI5/eSk7Rp+TbhVqnRRPfDp/siH5tLRoF190uvY0fk1"));
            System.out.println("password: " + encryptor.decrypt("FlrNgnSwafzLwT1cNWRkXchHpiBNIYQmSWd9gMXh2Dk/O7S1QOw/5P4Z000OblqSLiQH4eT5gxuUSDUmMsOASKG36zx1+WjXisWwh/+Q7a0="));
        } catch (Exception e) {
            System.out.println("Error decrypting: " + e.getMessage());
        }
    }

}
