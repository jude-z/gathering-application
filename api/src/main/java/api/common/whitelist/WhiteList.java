package api.common.whitelist;

public abstract class WhiteList {
    public static String[] getWhiteList = {"/connect/**","/gatherings","/gathering/*","/gathering","/gathering/*/meetings",
            "/gathering/*/meeting/*","/image/*","/gathering/*/image","/gathering/*/boards","/auth/user/*","/gathering/participated/*","/health"};
    public static String[] postWhiteList = {"/auth/id-check","/auth/generateToken","auth/nickname-check",
            "/auth/email-certification","/auth/check-certification","/auth/sign-in","/auth/sign-up"};
}
