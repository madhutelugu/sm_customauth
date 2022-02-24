package org.customauth;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.netegrity.policyserver.smapi.APIContext;
import com.netegrity.policyserver.smapi.AppSpecificContext;
import com.netegrity.policyserver.smapi.SmAuthQueryCode;
import com.netegrity.policyserver.smapi.SmAuthQueryResponse;
import com.netegrity.policyserver.smapi.SmAuthScheme;
import com.netegrity.policyserver.smapi.SmAuthStatus;
import com.netegrity.policyserver.smapi.SmAuthenticationContext;
import com.netegrity.policyserver.smapi.SmAuthenticationResult;
import com.netegrity.policyserver.smapi.SmJavaApiException;
import com.netegrity.policyserver.smapi.UserContext;
import com.netegrity.policyserver.smapi.UserCredentialsContext;
import org.customauth.restutils.ForgeRockAPIService;

/**
 * A simple SiteMinder Java Authentication Scheme.
 */

public class OTPApi implements SmAuthScheme
{
    private static final int SCHEME_VERSION = SmAuthQueryResponse.SMAUTH_API_VERSION_V3;
    private static final String SCHEME_DESCRIPTION = "SiteMinder(tm) Example Java authentication scheme, (c) Netegrity 2003";
    private static Logger theLogger =
            Logger.getLogger(OTPApi.class.getName());


    public static void  logInJavaUtilLogger(String msg) {
        //Log message into JavaUtil Logger
        theLogger.fine("OTPAPIAuthApiSample::FileLogger::"+ msg);
    }

    /**
     * Returns information about the authentication scheme.
     *
     * @param parameter - The parameter string as specified for the authentication scheme.
     * @param secret - The secret string as specified for the authentication scheme.
     * @param request - The request code, SMAUTH_QUERY_DESCRIPTION or SMAUTH_QUERY_CREDENTIALS_REQ.
     * @param response - Holds methods by which query() returns the requested information.
     * @return: If successful returns SMAUTH_SUCCESS, otherwise returns SMAUTH_FAILURE
     */

    public SmAuthStatus
    query(String parameter,
          String secret,
          SmAuthQueryCode request,
          SmAuthQueryResponse response)
    {


        logInJavaUtilLogger("OTPAPI: SmAuthStatus  query:"+parameter);
        logInJavaUtilLogger("OTPAPI: SmAuthStatus  query:"+secret);
        logInJavaUtilLogger("OTPAPI: SmAuthStatus  query:"+request);
        logInJavaUtilLogger("OTPAPI: SmAuthStatus  query:"+response);

        if (null == response)
        {
            return SmAuthStatus.SMAUTH_FAILURE;
        }

        if (SmAuthQueryCode.SMAUTH_QUERY_DESCRIPTION == request)
        {
            response.setResponseBuffer(SCHEME_DESCRIPTION);
            response.setResponseCode(SCHEME_VERSION);
        }
        else if (SmAuthQueryCode.SMAUTH_QUERY_CREDENTIALS_REQ == request)
        {
            String[] tokens = parameter.split("=##=");
            String fccURL = tokens[0];
            String propsFilePath = null;
            if(tokens.length > 1)
                propsFilePath = tokens[1];

            logInJavaUtilLogger("OTPAPI: FCCURL  query:"+fccURL);
            logInJavaUtilLogger("OTPAPI: FilePath  query:"+propsFilePath);
            //response.setResponseCode(SmAuthQueryResponse.SMAUTH_CRED_BASIC);
            //response.setResponseCode(SmAuthQueryResponse.SMAUTH_CRED_BASIC);
            response.setResponseCode(SmAuthQueryResponse.SMAUTH_CRED_FORM_REQUIRED);
            response.setResponseBuffer(getRedirectURL(fccURL));
        }
        else
        {
            return SmAuthStatus.SMAUTH_FAILURE;
        }

        return SmAuthStatus.SMAUTH_SUCCESS;
    }


    /**
     * SiteMinder invokes this method so the authentication scheme can
     * perform its own initialization procedure. This method is invoked once
     * for each authentication scheme instance when it is first loaded.
     *
     * @param parameter - The parameter string as specified for the authentication scheme.
     * @param secret - The secret string as specified for the authentication scheme.
     * @return: If successful returns SMAUTH_SUCCESS, otherwise returns SMAUTH_FAILURE
     */

    public SmAuthStatus
    init(String parameter,
         String secret)
    {
        logInJavaUtilLogger("OTPAPI: SmAuthStatus  init:"+parameter);
        logInJavaUtilLogger("OTPAPI: SmAuthStatus  init:"+secret);

        return SmAuthStatus.SMAUTH_SUCCESS;
    }


    /**
     * SiteMinder invokes this method during shutdown so the authentication
     * scheme can perform its own rundown procedure. This method is invoked
     * once for each authentication scheme instance during SiteMinder shutdown.
     *
     * @param parameter - The parameter string as specified for the authentication scheme.
     * @param secret - The secret string as specified for the authentication scheme.
     * @return: If successful returns SMAUTH_SUCCESS, otherwise returns SMAUTH_FAILURE
     */

    public SmAuthStatus
    release(String parameter,
            String secret)
    {
        return SmAuthStatus.SMAUTH_SUCCESS;
    }


    void logInPSTrace(SmAuthenticationContext context, String msg) {
        //Log message into Policy Server Trace Log
        context.getAPIContext().trace(getClass().getSimpleName(), "AuthApiSample:: ['" + msg +"']");
    }

    /**
     * SiteMinder invokes this method to authenticate user credentials.
     *
     * @param parameter - The parameter string as specified for the authentication scheme.
     * @param secret - The secret string as specified for the authentication scheme.
     * @param challengeReason - The reason for the original authentication challenge, or 0 if unknown.
     * @param context - Contains request context and methods to return message buffers.
     *
     * @return: an SmAuthenticationResult object.
     */

    public SmAuthenticationResult
    authenticate(String parameter,
                 String secret,
                 int challengeReason,
                 SmAuthenticationContext context)
    {

        logInJavaUtilLogger("OTPAPI: SmAuthStatus  authenticate1:"+parameter);
        logInJavaUtilLogger("OTPAPI: SmAuthStatus  authenticate2:"+secret);
        logInJavaUtilLogger("OTPAPI: SmAuthStatus  authenticate3:"+challengeReason);
        logInJavaUtilLogger("OTPAPI: SmAuthStatus  authenticate4:"+context);

        String[] tokens = parameter.split("=##=");
        String fccURL = tokens[0];
        String propsFilePath = null;
        if(tokens.length > 1)
            propsFilePath = tokens[1];

        logInJavaUtilLogger("OTPAPI: FCCURL  query:"+fccURL);
        logInJavaUtilLogger("OTPAPI: FilePath  query:"+propsFilePath);

        // cannot do authentication without the authentication context
        if (null == context)
        {
            return
                    new SmAuthenticationResult(SmAuthStatus.SMAUTH_NO_USER_CONTEXT, SmAuthenticationResult.REASON_NONE);
        }

        // If the scheme is designed not to disambiguate users, it should return SmAuthApi_NoUserContext.
        UserContext theUserContext = context.getUserContext();

        if ((null == theUserContext) || !theUserContext.isUserContext())
        {
            return
                    new SmAuthenticationResult(SmAuthStatus.SMAUTH_NO_USER_CONTEXT, SmAuthenticationResult.REASON_NONE);
        }

        // Reject the user if the password is not entered.
        UserCredentialsContext theUserCredentialsContext = context.getUserCredentialsContext();

        logInJavaUtilLogger("User Context : " + theUserCredentialsContext.getUserName() + " " + System.currentTimeMillis());
        //System.out.println("User Context : " + theUserCredentialsContext.getPassword() + " " + System.currentTimeMillis());

        if (null == theUserCredentialsContext)
        {
            return
                    new SmAuthenticationResult(SmAuthStatus.SMAUTH_REJECT, SmAuthenticationResult.REASON_NONE);
        }

        // String thePassword = theUserCredentialsContext.getPassword();

        Map<String,String> paramMaps = parsePassword(theUserCredentialsContext.getPassword());
        String theOTP =  paramMaps.get("OTP");
        String requestBase64Payload = paramMaps.get("REQUEST_PAYLOAD");
        logInJavaUtilLogger("tempCode :"+theOTP);
        logInJavaUtilLogger("Payload :"+requestBase64Payload);
        if(requestBase64Payload == null) {
            logInJavaUtilLogger("No OTP Payload redirect to otp.fcc");
            requestBase64Payload.getBytes();
        }

        //byte[] base64Content = Base64.getDecoder().decode(requestBase64Payload.getBytes());

        //String base64ContentStr = new String(base64Content);
        //logInJavaUtilLogger("Decoded Payload :"+base64Content);

        logInJavaUtilLogger("OTPAPI: Authenticate  authenticate5:"+theOTP);
        //System.out.println("Department :"+paramMaps.get("pin"));
        //logInJavaUtilLogger("OTPAPI: Authenticate  authenticate6:"+paramMaps.get("pin"));

        //String thePin = paramMaps.get("pin");

      /* String thePassword = theUserCredentialsContext.getPassword();

        if (thePassword.length() <= 0)
        {
            return
                    new SmAuthenticationResult(SmAuthStatus.SMAUTH_REJECT, SmAuthenticationResult.REASON_NONE);
        }*/


        // Check if the user account is disabled.
        try
        {
            if (0 != Integer.parseInt(theUserContext.getProp("disabled")))
            {
                context.setUserText("User account is disabled.");

                return
                        new SmAuthenticationResult(SmAuthStatus.SMAUTH_REJECT, SmAuthenticationResult.REASON_USER_DISABLED);
            }
        }
        catch (NumberFormatException exc)
        {
            // Do nothing -- the user is not disabled
        }

        // authenticate the user
        String authUserText;

        try
        {
            // authUserText = theUserContext.authenticateUser(thePassword);

            authUserText = theUserCredentialsContext.getUserName();

            ForgeRockAPIService.init(propsFilePath);
            String payload =  ForgeRockAPIService.step3withPayload(authUserText,requestBase64Payload,theOTP);
            logInJavaUtilLogger("Step 3 Response : " + payload);


            int code = ForgeRockAPIService.isMFASuccessfull(payload);

            //User Invalid OTP
            if(code == 1) {
                String encPayload = ForgeRockAPIService.encrypt(payload);
                context.setErrorText("Unable to verify MFA for user " + theUserContext.getUserName());
                context.setUserText("OTP_VALIDATION_FAILED=#="  +
                            theUserCredentialsContext.getUserName() + "=#=" +
                             encPayload);

                logInJavaUtilLogger("Unable to verify MFA for user " + theUserContext.getUserName());

                //throw new Exception("Exit here..!");

                return
                       new SmAuthenticationResult(SmAuthStatus.SMAUTH_FAILURE, SmAuthenticationResult.REASON_NONE);
            }
            else if(code == 2)  //Locked User
            {
            context.setErrorText("User failed OTP Attempts, account is Locked " + theUserContext.getUserName());
            context.setUserText("OTP_LOCKED=#="+theUserCredentialsContext.getUserName());

            logInJavaUtilLogger("User failed OTP Attempts, account is Locked " + theUserContext.getUserName());


            return
                    new SmAuthenticationResult(SmAuthStatus.SMAUTH_FAILURE, SmAuthenticationResult.REASON_NONE);
        }

            //ForgeRockAPIService.step3("msitestuser4@mailinator.com",thePassword);

            // invokeHttpService(authUserText,thePin);
        }
        catch (Throwable exc)
        {
            // insure subsequent code knows the authentication attempt failed
            authUserText = null;
        }

        if (null == authUserText)
        {
            context.setErrorText("Unable to authenticate user " + theUserContext.getUserName());
            //authUserText.toString();

           return
                    new SmAuthenticationResult(SmAuthStatus.SMAUTH_REJECT, SmAuthenticationResult.REASON_NONE);
        }

        // Set the time stamp of the user's last authentication.
        // For demonstration purposes, we will store the last login
        // in the "PIN" user property as a printable date&time string.

        String timeString = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.MEDIUM).format(new Date());

        // put single quotes around the string if the user directory is "ODBC:"
        String theNameSpace = theUserContext.getDirNameSpace();

        if ((theNameSpace != null) && theNameSpace.equals("ODBC:"))
        {
            timeString = "'" + timeString + "'" ;
        }

        if (0 != theUserContext.setProp("pin", timeString))
        {
            context.setUserText("Failed to set the time stamp for user's profile attribute " + parameter);
        }

        // If a parameter is supplied, set it as app specific data

        try
        {
            APIContext apiContext = context.getAPIContext();
            AppSpecificContext appContext = apiContext.getAppSpecificContext();
            //appContext.setData(parameter.getBytes());
            byte[] data = appContext.getData();
            if (data != null)
            {
                logInJavaUtilLogger("OTPAPI: Context Data : " + new String(data));
                //return  new String(data);
            } else {
                logInJavaUtilLogger("OTPAPI: Context Data : null" );
            }
        }
        catch (NullPointerException exc)
        {
            context.setUserText("Failed to modify application specific context");
        }
        catch (SmJavaApiException exc)
        {
            context.setUserText("Failed to modify application specific context");
        }


        return
                new SmAuthenticationResult(SmAuthStatus.SMAUTH_ACCEPT, SmAuthenticationResult.REASON_NONE);
    }

    /***
     * The redirectURL is exepcted to be first parameter in the Auth scheme definition
     * @param parameter
     * @return
     */
    String getRedirectURL(String parameter){
        String redirectURL = parameter;
        logInJavaUtilLogger("parameter :"+redirectURL);
        if (parameter.indexOf(';') != -1)
        {
            String[] params = parameter.split(";");
            redirectURL = params[0];
        }
        return redirectURL;
    }

    String invokeHttpService(String username, String pin) {

        try {
            System.out.println("Username: " + username + " | pin : " + pin);
            URL obj = new URL("http://localhost:8080/email?username="+username+"&pin="+pin);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            System.out.println("GET Response Code :: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // print result
                System.out.println(response.toString());
                return response.toString();
            } else {
                System.out.println("GET request not worked");
            }
        } catch(Exception e) {

        }
        return null;
    }

    Map<String,String> parsePassword(String param)
    {
        logInJavaUtilLogger("Inside parsePassword param is :"+param);
        Map<String, String> map = new HashMap<String, String>();


        String[] parts = param.split("#&#");

        for (String keypair : parts) {
            String[] keyval = keypair.split("#=#");
            try {
                //map.put(keyval[0], URLDecoder.decode(keyval[1], "UTF-8"));
                map.put(keyval[0], keyval[1]);
           // } catch (UnsupportedEncodingException e) {
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                logInJavaUtilLogger("Exception : " + e.getMessage());
            }

        }

        return map;

    }
}

// EOF AuthApiSample.java

