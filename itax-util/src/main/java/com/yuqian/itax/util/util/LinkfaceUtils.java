package com.yuqian.itax.util.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
@Slf4j
public class LinkfaceUtils {

    public static final String API_ID = "cc43014cc9494cdd846d4ee49adf0a0b";
    public static final String API_SECRET = "2216b2c0bec3460d9e7939917b54f007";
    public static final String POST_URL_HUOTI = "https://cloudapi.linkface.cn/identity/liveness_idnumber_verification";
    public static final String POST_URL_3AUTH = "https://cloudapi.linkface.cn/data/verify_id_name_phone";
    public static final String POST_URL_4AUTH = "https://cloudapi.linkface.cn/data/verify_id_name_bankcard_phone";
    public static final String POST_URL_2AUTH = "https://cloudapi.linkface.cn/data/verify_id_name";

    /**
     * LINKFACE活体验证接口
     * @param name1
     * @param idNumber1
     * @param livenessDataUrl1
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String HttpClientPost(String name1,String idNumber1,String livenessDataUrl1,String postUrlHuoti, String apiSecretLinkface,String appIdLinkface) throws ClientProtocolException, IOException {
        HttpClient httpclient =null;
        try{
            httpclient= new DefaultHttpClient();
            HttpPost post = new HttpPost(postUrlHuoti);
            StringBody id = new StringBody(appIdLinkface);
            StringBody secret = new StringBody(apiSecretLinkface);
            StringBody name = new StringBody(name1, Charset.forName("UTF-8"));
            StringBody number = new StringBody(idNumber1);
            StringBody livenessDataUrl = new StringBody(livenessDataUrl1);

            MultipartEntity entity = new MultipartEntity();

            entity.addPart("api_id", id);
            entity.addPart("api_secret", secret);
            entity.addPart("name", name);
            entity.addPart("id_number", number);
            entity.addPart("liveness_data_url",livenessDataUrl );
            log.info(name.getFilename());
            post.setEntity(entity);

            HttpResponse response = httpclient.execute(post);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entitys = response.getEntity();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(entitys.getContent()));
                String line = reader.readLine();
                log.info(line);
                httpclient.getConnectionManager().shutdown();
                return line;
            }else{
                HttpEntity r_entity = response.getEntity();
                String responseString = EntityUtils.toString(r_entity);
                log.info("错误码是："+response.getStatusLine().getStatusCode()+"  "+response.getStatusLine().getReasonPhrase());
                log.info("出错原因是："+responseString);
                //你需要根据出错的原因判断错误信息，并修改
                httpclient.getConnectionManager().shutdown();
                return responseString;
            }
        }catch (Exception e){
            log.debug(e.getMessage());
        }finally {
            if(httpclient!=null){
                ((DefaultHttpClient) httpclient).close();
            }
        }
        return "";
    }

    /***
     *LINKFACE运营商三要素
     * @param name
     * @param idNumber
     * @param phoneNumber
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */

    public static String Auth3Linkface(String name,String idNumber,String phoneNumber,String postUrl3authLinkface, String apiSecretLinkface,String appIdLinkface)  {
        HttpClient httpclient =null;
        try{
            httpclient= new DefaultHttpClient();
            HttpPost post = new HttpPost(postUrl3authLinkface);
            StringBody id = new StringBody(appIdLinkface);
            StringBody secret = new StringBody(apiSecretLinkface);
            StringBody nameBody = new StringBody(name, Charset.forName("UTF-8"));
            StringBody number = new StringBody(idNumber);
            StringBody phone = new StringBody(phoneNumber);

            MultipartEntity entity = new MultipartEntity();

            entity.addPart("api_id", id);
            entity.addPart("api_secret", secret);
            entity.addPart("name", nameBody);
            entity.addPart("id_number", number);
            entity.addPart("phone_number",phone );
            log.info(name);
            post.setEntity(entity);

            HttpResponse response = httpclient.execute(post);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entitys = response.getEntity();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(entitys.getContent()));
                String line = reader.readLine();
                log.info(line);
                httpclient.getConnectionManager().shutdown();
                return line;
            }else{
                HttpEntity r_entity = response.getEntity();
                String responseString = EntityUtils.toString(r_entity);
                log.info("错误码是："+response.getStatusLine().getStatusCode()+"  "+response.getStatusLine().getReasonPhrase());
                log.info("出错原因是："+responseString);
                //你需要根据出错的原因判断错误信息，并修改
                httpclient.getConnectionManager().shutdown();
                return responseString;
            }
        }catch (Exception e){
            log.debug(e.getMessage());
        }finally {
            if(httpclient!=null){
                ((DefaultHttpClient) httpclient).close();
            }
        }
        return "";
    }

    /***
     * LINKFACE银行卡4要素
     * @param name
     * @param idNumber
     * @param cardNumber
     * @param phoneNumber
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */

    public static String Auth4Linkface(String name,String idNumber,String cardNumber,String phoneNumber,String postUrl4authLinkface, String apiSecretLinkface,String appIdLinkface)  {
        HttpClient httpclient = null;
        try{
            httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(postUrl4authLinkface);
            StringBody id = new StringBody(appIdLinkface);
            StringBody secret = new StringBody(apiSecretLinkface);
            StringBody nameBody = new StringBody(name, Charset.forName("UTF-8"));
            StringBody number = new StringBody(idNumber);
            StringBody card = new StringBody(cardNumber);
            StringBody phone = new StringBody(phoneNumber);

            MultipartEntity entity = new MultipartEntity();

            entity.addPart("api_id", id);
            entity.addPart("api_secret", secret);
            entity.addPart("name", nameBody);
            entity.addPart("id_number", number);
            entity.addPart("card_number", card);
            entity.addPart("phone_number",phone );
            log.info(name);
            post.setEntity(entity);

            HttpResponse response = httpclient.execute(post);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entitys = response.getEntity();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(entitys.getContent()));
                String line = reader.readLine();
                log.info(line);
                httpclient.getConnectionManager().shutdown();
                return line;
            }else{
                HttpEntity r_entity = response.getEntity();
                String responseString = EntityUtils.toString(r_entity);
                log.info("错误码是："+response.getStatusLine().getStatusCode()+"  "+response.getStatusLine().getReasonPhrase());
                log.info("出错原因是："+responseString);
                //你需要根据出错的原因判断错误信息，并修改
                httpclient.getConnectionManager().shutdown();
                return responseString;
            }
        }catch (Exception e){
            log.debug(e.getMessage());
        }finally {
            if(httpclient!=null){
                ((DefaultHttpClient) httpclient).close();
            }
        }
        return "";
    }


    /*public static void HttpClientPost2() throws ClientProtocolException, IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost post = new HttpPost(POST_URL2);
        StringBody id = new StringBody(api_id);
        StringBody secret = new StringBody(api_secret);
        StringBody name = new StringBody(username,Charset.forName("UTF-8"));
        StringBody number = new StringBody(id_number);
        FileBody fileBody = new FileBody(new File(filepath));
        MultipartEntity entity = new MultipartEntity();

        entity.addPart("api_id", id);
        entity.addPart("api_secret", secret);
        entity.addPart("name", name);
        entity.addPart("id_number", number);
        entity.addPart("liveness_data_file", fileBody);
        post.setEntity(entity);

        HttpResponse response = httpclient.execute(post);
        if (response.getStatusLine().getStatusCode() == 200) {
            HttpEntity entitys = response.getEntity();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(entitys.getContent()));
            String line = reader.readLine();
            log.info(line);
        }else{
            HttpEntity r_entity = response.getEntity();
            String responseString = EntityUtils.toString(r_entity);
            log.info("错误码是："+response.getStatusLine().getStatusCode()+"  "+response.getStatusLine().getReasonPhrase());
            log.info("出错原因是："+responseString);
            //你需要根据出错的原因判断错误信息，并修改
        }

        httpclient.getConnectionManager().shutdown();
    }*/

    /**
     *
     * @param args
     * @throws ClientProtocolException
     * @throws IOException
     */


    public static void main(String[] args){
        try {
            Auth4Linkface("尹易春", "433130198511141511", "", "18688962791", POST_URL_4AUTH, API_SECRET, API_ID);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /***
     * LINKFACE身份证2要素验证
     * @param name
     * @param idNumber
     * @return String
     * @throws ClientProtocolException
     * @throws IOException
     * @author Kaven
     */

    public static String Auth2Linkface(String name,String idNumber,String postUrl2authLinkface, String apiSecretLinkface,String appIdLinkface)  {
        HttpClient httpclient = null;
        try{
            httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(postUrl2authLinkface);
            StringBody id = new StringBody(appIdLinkface);
            StringBody secret = new StringBody(apiSecretLinkface);
            StringBody nameBody = new StringBody(name, Charset.forName("UTF-8"));
            StringBody number = new StringBody(idNumber);

            MultipartEntity entity = new MultipartEntity();

            entity.addPart("api_id", id);
            entity.addPart("api_secret", secret);
            entity.addPart("name", nameBody);
            entity.addPart("id_number", number);
            log.info(name);
            post.setEntity(entity);

            HttpResponse response = httpclient.execute(post);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entitys = response.getEntity();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(entitys.getContent()));
                String line = reader.readLine();
                log.info(line);
                httpclient.getConnectionManager().shutdown();
                return line;
            }else{
                HttpEntity r_entity = response.getEntity();
                String responseString = EntityUtils.toString(r_entity);
                log.info("错误码是：" + response.getStatusLine().getStatusCode() + "  " + response.getStatusLine().getReasonPhrase());
                log.info("出错原因是：" + responseString);
                //你需要根据出错的原因判断错误信息，并修改
                httpclient.getConnectionManager().shutdown();
                return responseString;
            }
        }catch (Exception e){
            log.debug(e.getMessage());
        }finally {
            if(httpclient!=null){
                ((DefaultHttpClient) httpclient).close();
            }
        }
        return "";
    }
}
