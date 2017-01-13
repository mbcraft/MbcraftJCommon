/*
 * 
 *    Copyright MBCRAFT di Marco Bagnaresi - © 2015
 *    All rights reserved - Tutti i diritti riservati
 * 
 *    Mail : info [ at ] mbcraft [ dot ] it 
 *    Web : http://www.mbcraft.it
 * 
 */

package it.mbcraft.libraries.net.http;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Questa classe contiene alcuni metodi per effettuare semplici chiamate
 * http GET e POST, ritornando il risultato o lanciando un'eccezione
 * se la risposta non è 200 OK.
 *
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
public class EasyHttp {

    private static final Logger logger = LogManager.getLogger(EasyHttp.class);
    private CloseableHttpClient httpClient = HttpClients.createDefault();
    private CloseableHttpResponse lastResponse = null;
    private int lastStatusCode = -1;

    /**
     * Controlla che il client http sia utilizzabile.
     */
    private void checkNotClosed() {
        if (httpClient == null)
            throw new IllegalStateException("Il client è stato chiuso.");
    }

    public static String prepareQuery(Properties pt) {
        logger.info(""+pt.toString());
        String query = "";
        if (!pt.isEmpty()) {
            query = "?";
            for (String key : pt.stringPropertyNames()) {
                String param = "";

                try {
                    param += URLEncoder.encode(key, "UTF-8");
                    param += "=";
                    param += URLEncoder.encode(pt.getProperty(key), "UTF-8");
                    param += "&";
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                query += param;
            }
            query = query.substring(0, query.length() - 1);
        }
        return query;
    }

    /**
     * Esegue una get all'indirizzo specificato.
     *
     * @param address L'indirizzo a cui effettuare la get, contenente anche eventuali parametri
     * @return il codice della response
     * @throws it.mbcraft.libraries.net.http.HttpErrorException if the return status is not 200
     */
    public String doPostDownload(String address, Properties parameters, File saveDir) throws HttpErrorException,IOException {
        checkNotClosed();
        try {
            HttpPost post = new HttpPost(address);

            List<NameValuePair> postParams = new ArrayList<>();
            for (Object key : parameters.keySet()) {
                postParams.add(new BasicNameValuePair((String) key, (String) parameters.get(key)));
            }

            UrlEncodedFormEntity paramEntity = new UrlEncodedFormEntity(postParams);

            post.setEntity(paramEntity);

            post.setHeader("Connection", "close");

            lastResponse = httpClient.execute(post);
            lastStatusCode = lastResponse.getStatusLine().getStatusCode();

            Header cd = lastResponse.getFirstHeader("Content-Disposition");
            HeaderElement elems[] = cd.getElements();
            HeaderElement hfilename = elems[0];
            String filename = hfilename.getParameterByName("filename").getValue();

            //saving file
            File outputFile = new File(saveDir, filename);
            saveLastResponseToFile(outputFile);

            if (lastStatusCode == 200) {
                return filename;
            } else
                throw new HttpErrorException(lastStatusCode, "Error in downloading file from : " + address);
        } catch (IOException ex) {
            logger.error("Unable to postDownload to "+address+" : "+ex.getMessage());
            throw ex;
        }
    }

    /**
     * Esegue una get all'indirizzo specificato.
     *
     * @param address L'indirizzo a cui effettuare la get, contenente anche eventuali parametri
     * @return il codice della response
     * @throws it.mbcraft.libraries.net.http.HttpErrorException if the return status is not 200
     */
    public String doGet(String address, Properties pt) throws HttpErrorException, IOException {
        checkNotClosed();
        try {

            String query = prepareQuery(pt);

            HttpGet get = new HttpGet(address + query);
            get.setHeader("Connection", "close");

            lastResponse = httpClient.execute(get);
            lastStatusCode = lastResponse.getStatusLine().getStatusCode();
            String responseContent = getLastResponseAsString();
            if (lastStatusCode == 200) {
                return responseContent;
            } else
                throw new HttpErrorException(lastStatusCode, responseContent);
        } catch (IOException ex) {
            logger.error("Unable to get to "+address+" : "+ ex.getMessage());
            throw ex;
        }
    }

    /**
     * Esegue una post all'indirizzo specificato
     *
     * @param address    L'indirizzo a cui effettuare la post
     * @param parameters I parametri della richiesta
     * @return Il codice della response
     * @throws it.mbcraft.libraries.net.http.HttpErrorException if the return status is not 200
     */
    public String doPost(String address, Properties parameters) throws HttpErrorException,IOException {
        checkNotClosed();
        try {
            HttpPost post = new HttpPost(address);

            List<NameValuePair> postParams = new ArrayList<>();
            for (Object key : parameters.keySet()) {
                postParams.add(new BasicNameValuePair((String) key, (String) parameters.get(key)));
            }

            UrlEncodedFormEntity paramEntity = new UrlEncodedFormEntity(postParams);

            post.setEntity(paramEntity);

            post.setHeader("Connection", "close");

            lastResponse = httpClient.execute(post);
            lastStatusCode = lastResponse.getStatusLine().getStatusCode();
            String responseContent = getLastResponseAsString();
            if (lastStatusCode == 200) {
                return responseContent;
            } else
                throw new HttpErrorException(lastStatusCode, responseContent);

        } catch (IOException ex) {
            logger.error("Unable to post to "+address+" : "+ ex.getMessage());
            throw ex;
        }
    }

    /**
     * Ritorna la response come stringa.
     *
     * @return Il contenuto della response come stringa.
     */
    private String getLastResponseAsString() throws IOException {
        try {
            String result = EntityUtils.toString(lastResponse.getEntity());
            EntityUtils.consume(lastResponse.getEntity());
            lastResponse.close();
            lastResponse = null;
            return result;
        } catch (IOException ex) {
            logger.error("Unable to read response : "+ ex.getMessage());
            throw ex;
        }
    }

    /**
     * Salva la response in un file
     *
     * @param toSave Il file in cui salvare la response.
     */
    private void saveLastResponseToFile(File toSave) throws IOException {
        try {
            OutputStream out = new BufferedOutputStream(new FileOutputStream(toSave), 1024 * 1024);

            lastResponse.getEntity().writeTo(out);
            out.flush();
            out.close();

            //deallocating response
            lastResponse.close();
            lastResponse = null;
        } catch (IOException ex) {
            logger.error("Unable to save response as file "+toSave.getPath()+" : "+ex.getMessage());
            throw ex;
        }
    }

    /**
     * Ritorna la validità dell'ultima richiesta eseguita.
     *
     * @return true se è valida, false altrimenti
     */
    public boolean isLastResponseOK() {
        return lastStatusCode == 200;
    }

    /**
     * Dealloca il client http.
     */
    public void dispose() throws IOException {
        try {
            httpClient.close();
            httpClient = null;
        } catch (IOException ex) {
            logger.error("Unable to dispose http client : "+ ex.getMessage());
            throw ex;
        }
    }
}
