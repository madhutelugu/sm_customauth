package org.customauth.restutils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class HttpUtil {

    static Properties properties = null;

    public static void init(Properties propertiesInt){
        properties = propertiesInt;
    }

    public static String httpPost(String apiUrl,
                                  Map<String,String> headers,
                                  Map<String,String> params,
                                  String payload,
                                  String API) throws Exception
    {
        long startTime = System.currentTimeMillis();
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");

        if(headers != null && !headers.isEmpty()) {
            Iterator<String> iterator = headers.keySet().iterator();
            while(iterator.hasNext()){
                String key= iterator.next();
                conn.setRequestProperty(key,headers.get(key));
            }
        }

        OutputStream os = conn.getOutputStream();
        os.write(payload.getBytes());
        os.flush();

        long timeTaken = System.currentTimeMillis() - startTime;
        System.out.println("Time taken to complete the request: " + timeTaken);

        StringBuilder builder = new StringBuilder();
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            System.out.println("HTTP response code (200) : " + conn.getResponseCode());

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            System.out.println(API+ " ::: Success from Server .... \n");
            while ((output = br.readLine()) != null) {
                builder.append(output).append("\r\n");
            }
        } else {
                System.out.println("HTTP response code : " + conn.getResponseCode());
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getErrorStream())));

                String output;
                System.out.println(API+ " ::: Error from Server .... \n");
                while ((output = br.readLine()) != null) {
                    builder.append(output).append("\r\n");
                }
        }



        System.out.println(builder.toString());

        conn.disconnect();

       /* if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        }*/

        return  builder.toString();
    }


}
