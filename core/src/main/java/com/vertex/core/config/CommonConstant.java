package com.vertex.core.config;

@SuppressWarnings("unused")
public class CommonConstant {

    //PAGEABLE
    public static final int PAGE_NUMBER = 0;
    public static final int PAGE_SIZE = 500;
    //PAGEABLE

    public static final String RSA = "RSA";
    public static final Integer KEY_GEN_INIT = 2048;
    public static final String PUBLIC_KEY = "migration:hash:key:public_key";
    public static final String PRIVATE_KEY = "migration:hash:key:private_key";
    public static final String PERMISSIONS = "migration:hash";
    public static final String INSTANCE_KEY = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
}
