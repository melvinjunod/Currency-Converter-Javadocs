package com.github.blaxk3.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.*;
import java.util.Properties;

/**
 * Kelas utama program konversi kurs mata uang berdasarkan API
 * API berasal dari ExchangeRate-API
 * @author BLAXK3
 */

public class CurrencyRateAPI {

    /**
     * Logger yang digunakan program, yang meng-log segala macam output
     */
    private static final Logger logger = LoggerFactory.getLogger(CurrencyRateAPI.class);

    /**
     * Mendapatkan API_KEY dari config.properties
     * @return API key
     */
    public String getApiKeyService() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                logger.error("Unable to find config.properties");
                return null;
            }
            properties.load(input);
            return properties.getProperty("API_KEY");
        } catch (IOException e) {
            logger.error("Error loading config.properties", e);
            return null;
        }
    }

    /**
     * Mendapatkan URL API yang akan digunakan dengan GET request nanti
     * @return URL api
     */

    public String getURL() {
        return "https://v6.exchangerate-api.com/v6/" + getApiKeyService();
    }

    /**
     * Mendapatkan data dari API menggunakan GET request, dalam bentuk Json
     * @return json object dari API
     */
    public JsonObject getJsonObject(URL url) {
        try {
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.setRequestMethod("GET");

            int responseCode = request.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                JsonElement root = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent()));
                return root.getAsJsonObject();
            } else {
                logger.error("GET request failed with response code: {}", responseCode);
            }
        } catch (Exception e) {
            logger.error("An error occurred during the API request", e);
        }
        return null;
    }

    /**
     * Mendapatkan daftar kode mata uang
     * @return String array dengan daftar kode mata uang
     */

    public String[] getCurrencyCode() throws MalformedURLException, URISyntaxException {
        JsonObject jsonObject = getJsonObject(new URI(getURL() + "/latest/" + "USD").toURL());
        if (jsonObject != null && jsonObject.has("conversion_rates")) {
            JsonObject code = jsonObject.getAsJsonObject("conversion_rates");
            if (code != null && !code.keySet().isEmpty()) {
                return code.keySet().toArray(new String[0]);
            }
        }
        return null;
    }

    /**
     * Mendapatkan hasil konversi kurs mata uang dengan menggunakan {@link #getJsonObject(URL) getJsonObject}
     * @param foreignCurrency1 kode mata uang awal
     * @param foreignCurrency2 kode mata uang tujuan konversi
     * @param amount jumlah mata uang awal yang akan dikonversikan
     * @return String array dengan daftar kode mata uang
     */

    public String convert(String foreignCurrency1, String foreignCurrency2, BigDecimal amount) throws MalformedURLException, URISyntaxException {
        return getJsonObject(new URI(getURL() + "/pair/" + foreignCurrency1 + "/" + foreignCurrency2 + "/" + amount).toURL()).get("conversion_result").toString();
    }
}
