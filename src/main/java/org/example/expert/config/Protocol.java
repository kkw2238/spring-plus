package org.example.expert.config;

/**
 * 한 곳에서 사용하는 것이 아닌 여러 곳에서 쓰는
 * String과 같은 데이터를 정리해두는 Protocol Class
 */
public class Protocol {
    // Attribute 관련 Protocol
    public static final String EMAIL = "email";
    public static final String USER_ID = "userId";
    public static final String USER_ROLE = "userRole";
    public static final String NICKNAME = "nickname";

    // Time 관련 Protocol
    public static final String FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_BEGIN_VALUE = "BEGIN_DATE";
    public static final String DEFAULT_END_VALUE = "END_DATE";

}
