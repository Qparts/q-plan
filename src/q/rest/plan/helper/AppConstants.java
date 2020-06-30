package q.rest.plan.helper;

public class AppConstants {

    private final static String MESSAGING_SERVICE = SysProps.getValue("messagingService");
    public final static String INTERNAL_APP_SECRET = "INTERNAL_APP";
    public static final String SEND_SMS = MESSAGING_SERVICE + "sms";
    public static final String SEND_EMAIL = MESSAGING_SERVICE + "email";



    public static final String SMS_PURPOSE_SIGNUP = "signup";
}
