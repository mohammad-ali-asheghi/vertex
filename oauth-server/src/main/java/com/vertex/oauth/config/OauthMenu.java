package com.vertex.oauth.config;

public interface OauthMenu {

    /**
     * منو اصلی سامانه
     */
    @SuppressWarnings("unused")
    Long MAIN_MENU = 1L;

    /**
     * مدیریت سامانه ها
     */
    Long APPLICATION_MENU = 2L;

    /**
     * مدیریت نقش ها
     */
    Long ROLE_MENU = 3L;

    /**
     * مدیریت کاربران
     */
    Long USER_MENU = 4L;

    /**
     * مدیریت ثابت ها
     */
    Long CONSTANT_MENU = 5L;

    /**
     * مدیریت منو ها
     */
    Long OAUTH_MENU = 6L;

    /**
     * مدیریت مجوز ها
     */
    Long PERMISSION_MENU = 7L;
}
