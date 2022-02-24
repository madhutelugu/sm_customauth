package org.customauth.restutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.customauth.model.ForgeRockAPIObjectModel;
import org.customauth.util.AESUtil;


import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ForgeRockAPIService {

    static String FORGE_ROCK_SERVICE_URL = "";

    static String SHARED_SECRET = "";

    static Properties properties = null;

    public static final String SALT =  "zyxwvut12345abcd";

    static final String algorithm = "AES";


    public static void init(String filePath) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
            properties = new Properties();
            properties.load(fis);
            HttpUtil.init(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void addHeaders(Map<String,String> headers){
        String configuredHeaders = properties!=null?properties.getProperty("HEADERS"):null;
        if(configuredHeaders != null){
            String[] tokens = configuredHeaders.split("\\$\\$");
            for(String token: tokens){
                String[] keyValue = token.split("=##=");
                headers.put(keyValue[0],keyValue[1]);
            }
        }
    }

    public static String step1(String username) {
        try {

            if(properties == null){
                return null;
            }

            Map<String, String> headers = new HashMap<String, String>();

            headers.put("Content-Type", "application/json");
            headers.put("X-OpenAM-Username", username);
            headers.put("X-OpenAM-SharedSecret", properties.getProperty("SHARED_SECRET"));
            headers.put("Accept-API-Version", "resource=2.0, protocol=1.0");

            addHeaders(headers);
            FORGE_ROCK_SERVICE_URL = properties.getProperty("FORGE_ROCK_SERVICE_URL");

            String input = "{}";

            int retryConfigured = properties.getProperty("retry")==null?3:Integer.parseInt(properties.getProperty("http.connection.retries"));
            System.out.println("Configured retry: " + retryConfigured);

            boolean retry = true;
            String payload = null;

            int i = 0;
            while(retry){
                try {
                    payload=HttpUtil.httpPost(FORGE_ROCK_SERVICE_URL, headers, null, input,"AuthApi");
                    retry = false;
                }catch(Exception e){
                    e.printStackTrace();
                    System.out.println("Attempt #: " + i);
                    payload =  "Error while authenticating user against forgerock(1). Error Message : "+e.getMessage();
                    i++;
                }
                if(i>retryConfigured){
                    retry = false;
                    System.out.println("Exiting retry as match attempts reached!");

                }
            }
           return  payload;

        }  catch (Exception e) {

            e.printStackTrace();
             return  "Error while authenticating user against forgerock(1). Error Message : "+e.getMessage();
        }
     }

    public static List<String> getChoicesFromJsonPayload(String jsonPayload) {

        List<JSONArray> choices = JsonPath.read(jsonPayload,
                                        "$.callbacks[?(@.type==\"ChoiceCallback\")].output[?(@.name==\"choices\")].value");

        List<String> transChoices = new ArrayList<String>();

        for(JSONArray eachItem : choices){

            Iterator  iterator = eachItem.iterator();

            while(iterator.hasNext()) {
                transChoices.add(iterator.next().toString());
            }

        }
        System.out.println("Choices: " + transChoices);

        return transChoices;

    }

    public static String step2(String username,String payload, String choice) {
        try {

            Map<String, String> headers = new HashMap<String, String>();

            headers.put("Content-Type", "application/json");
           // headers.put("X-OpenAM-Username", username);
          //  headers.put("X-OpenAM-SharedSecret", SHARED_SECRET);
            headers.put("Accept-API-Version", "resource=2.0, protocol=1.0");

            addHeaders(headers);
            FORGE_ROCK_SERVICE_URL = properties.getProperty("FORGE_ROCK_SERVICE_URL");

            int choiceNum = 0;

            if(choice.startsWith("Text/SMS")){
                choiceNum = 0;
            } else if (choice.startsWith("Call")){
                choiceNum = 1;
            } else if (choice.startsWith("Email")){
                choiceNum = 2;
            }

            String  withChoice = "\"input\":[{\"name\":\"IDToken2\",\"value\":"+choiceNum+"}]}";

            //String temp = "\"input\":[{\"name\":\"IDToken2\",\"value\":0}]}";

            payload = payload.replace("\"input\":[{\"name\":\"IDToken2\",\"value\":0}]}",withChoice);

            System.out.println("Payload after replacement: " + payload);

            int retryConfigured = properties.getProperty("retry")==null?
                                3:Integer.parseInt(properties.getProperty("http.connection.retries"));

            System.out.println("Configured retry: " + retryConfigured);

            boolean retry = true;

            String content = null;

            int i = 0;
            while(retry){
                try {
                    content=HttpUtil.httpPost(FORGE_ROCK_SERVICE_URL, headers, null, payload,"AuthApi");
                    retry = false;
                }catch(Exception e){
                    e.printStackTrace();
                    retry = true;
                    System.out.println("Attempt #: " + i);
                    content = "Error while authenticating user against forgerock(2). Error Message : "+e.getMessage();
                    i++;
                }

                if(i > retryConfigured) {
                    retry = false;
                    System.out.println("Exiting retry as match attempts reached!");
                }
            }

            String cipherText = encrypt(content);

            String plainText = decrypt(cipherText);

            System.out.println("Clear text: " + plainText);

            return cipherText;

        }  catch (Exception e) {

            e.printStackTrace();
            System.out.println("Step 2 Exception : " + e.getMessage());
            return "Error while authenticating user against forgerock(2). Error Message : "+e.getMessage();

        }
    }



    public static String encrypt(String plainText) throws Exception {
        String password = properties.getProperty("secret.key");
        //System.out.println("Key : " + key.getEncoded());
        SecretKey key = AESUtil.getKeyFromPassword(password,SALT);
        String cipherText = AESUtil.encrypt(algorithm, plainText, key);
        System.out.println("Cipher Text: " + cipherText);
        return cipherText;
    }


    public static String decrypt(String cipherText) throws Exception {
        String password = properties.getProperty("secret.key");
       // System.out.println("Key : " + key.getEncoded());
        SecretKey key =AESUtil.getKeyFromPassword(password,SALT);
        String plainText = AESUtil.decrypt(algorithm, cipherText, key);
        System.out.println("Plain Text: " + plainText);
        return plainText;
    }

  /*  public static String step1AndStep2(String username){
        String payload = step1(username);

        step2(username,payload,"");

        return payload;
    }*/

    public static int isMFASuccessfull(String payload){
        if(payload != null && payload.contains("User has completed MFA successfully")){
            System.out.println("MFA Successful");
            return 0; //success
        } if(payload != null && payload.contains("Account locked due to invalid OTP attempts")){
            return 2; //Locked due invalid otp attemtps
        }if(payload != null && payload.contains("Your account is locked")){
            return 2; //Locked due invalid otp attemtps
        }if(payload != null && payload.contains("You do not have access")){
            return 3; //Locked due invalid otp attemtps
        }

        return 1;//Invalid OTP
    }
    public static String step3withPayload(String username, String payload, String otp) {
        try {

            System.out.println("Step3: " + username);

            Map<String, String> headers = new HashMap<String, String>();

            headers.put("Content-Type", "application/json");
            //   headers.put("X-OpenAM-Username", username);
            // headers.put("X-OpenAM-SharedSecret", SHARED_SECRET);
            headers.put("Accept-API-Version", "resource=2.0, protocol=1.0");

            addHeaders(headers);
            FORGE_ROCK_SERVICE_URL = properties.getProperty("FORGE_ROCK_SERVICE_URL");


            payload = decrypt(payload);

            System.out.println("OTPAPI token info: " + payload);

            //String  withOtp ="\"input\":[{\"name\":\"IDToken3\",\"value\":\"\"}]";
            String  withOtp = "\"input\":[{\"name\":\"IDToken3\",\"value\":\""+otp+"\"}]";

            System.out.println("Text to replace : " + withOtp);

            payload = payload.replace("\"input\":[{\"name\":\"IDToken3\",\"value\":\"\"}]",withOtp);

            System.out.println("OTPAPI token info with otp: " + payload);

            int retryConfigured = properties.getProperty("retry")==null?3:Integer.parseInt(properties.getProperty("http.connection.retries"));
            System.out.println("Configured retires: " + retryConfigured);
            boolean retry = true;
            String content = null;
            int i = 0;
            while(retry){
                try {
                    content=HttpUtil.httpPost(FORGE_ROCK_SERVICE_URL, headers, null, payload,"OTPApi");
                    retry = false;
                }catch(Exception e){
                    e.printStackTrace();
                    System.out.println("Attempt #:" + i);
                    content = "Error while authenticating user against forgerock(3s). Error Message : "+e.getMessage();
                    i++;
                }
                if(i>retryConfigured){
                    retry = false;
                    System.out.println("Exiting retry as match attempts reached!");
                }
            }
            return  content;

        } catch (Exception e) {

            e.printStackTrace();
            System.out.println("Step3 Exception : " + e.getMessage());

        }
        return null;
    }


    public static String getProperty(String propName) {
        return properties.getProperty(propName);
    }



    /*public static String step3(String username, String otp) {
        try {

            System.out.println("Step3: " + username);

            Map<String, String> headers = new HashMap<String, String>();

            headers.put("Content-Type", "application/json");
         //   headers.put("X-OpenAM-Username", username);
           // headers.put("X-OpenAM-SharedSecret", SHARED_SECRET);
            headers.put("Accept-API-Version", "resource=2.0, protocol=1.0");


            byte[] encoded = Files.readAllBytes(Paths.get("payload_"+username));
            String actual =  new String(encoded);

            System.out.println("OTPAPI token info: " + actual);

            String  withOtp = "\"input\":[{\"name\":\"IDToken1\",\"value\":\""+otp+"\"}]}";

            actual = actual.replace("\"input\":[{\"name\":\"IDToken1\",\"value\":\"\"}]}",withOtp);

            System.out.println("OTPAPI token info with otp: " + actual);

            return  HttpUtil.httpPost(FORGE_ROCK_SERVICE_URL, headers, null, actual,"OTPApi");

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (Exception e) {

            e.printStackTrace();

        }
        return null;
    }



        public static void main(String[] args) throws Exception {

          init("C:\\siteminder\\config\\forgerock_custom.properties");

           String payload = step1("msitestuser4@mailinator.com");

            ObjectMapper objectMapper = new ObjectMapper();

           ForgeRockAPIObjectModel forgeRockAPIObjectModel =  objectMapper.readValue(payload, ForgeRockAPIObjectModel.class);

           System.out.println(" AuthID : " + forgeRockAPIObjectModel.getAuthId());

            System.out.println(" Type: " + forgeRockAPIObjectModel.getCallbacks().get(0).getOutput());


           getChoicesFromJsonPayload(payload);

        }*/


}
