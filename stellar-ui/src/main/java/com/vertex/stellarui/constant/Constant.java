package com.vertex.stellarui.constant;

import org.springframework.data.domain.Sort;

@SuppressWarnings("unused")
public class Constant {

    //default value constant
    public static final int DEFAULT_PAGE_INDEX = 0;
    public static final int DEFAULT_PAGE_SIZE = 200;
    public static final int MAX_PAGE_SIZE = 500;
    public static final int MIN_PAGE_SIZE = 10;
    public static final Sort NULL_SORT = null;

    //oauth token key in session
    public static final String AUTH_TOKEN = "authToken";
    public static final String AUTH_MENU = "authMenu";
}
