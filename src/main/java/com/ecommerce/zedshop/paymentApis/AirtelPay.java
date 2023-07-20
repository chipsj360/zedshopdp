package com.ecommerce.zedshop.paymentApis;

import okhttp3.CertificatePinner;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class AirtelPay {

    private static String x_country = "ZM";
    private static String x_currency = "ZMW";
    // Check if sandbox or production
    // Choose either staging or production
    private static String environment_mode = "staging";
    // pin
    private static String disbursement_pin = "disbursement_pin?";

    // Configure keys
    private static String client_id = "235210fa-7789-4f06-a0a2-9272871bf7ab";
    private static String client_secret = "0df1e288-cd51-47e0-a8b5-83bbc75a28e6";
    // Airtel money bearer token

    // Link check
    private static String url_prefix = "https://openapiuat.airtel.africa";
    static {
        if (environment_mode.equals("production")) {
            url_prefix = "https://openapi.airtel.africa";
        }
        System.out.println(url_prefix);
    }

    public static JSONObject token() throws IOException {
        String url = url_prefix + "/auth/oauth2/token";
        String payload = new JSONObject()
                .put("client_id", client_id)
                .put("client_secret", client_secret)
                .put("grant_type", "client_credentials")
                .toString();

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] input = payload.getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String jsonData = bufferedReader.readLine();
                return new JSONObject(jsonData);
            }
        } else {
            throw new IOException("Failed to get token. Response code: " + responseCode);
        }
    }

    // Request to pay
    public static JSONObject pay(String phoneNumber, Double price, String currency, String country, String txn) throws IOException {
        String url = url_prefix + "/merchant/v1/payments/";

//        String slicedphone = phoneNumber.substring(1);
        String transactionId = UUID.randomUUID().toString();

        JSONObject requestBody = new JSONObject()
                .put("reference", "Reference for transactions")
                .put("transactionId",transactionId)
                .put("subscriber", new JSONObject()
                        .put("country", country)
                        .put("currency", currency)
                        .put("msisdn", phoneNumber))
                .put("transaction", new JSONObject()
                        .put("price", price)
                        .put("country", country)
                        .put("currency", currency)
                        .put("id", txn));

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("X-Country", country);
        connection.setRequestProperty("X-Currency", currency);
        connection.setRequestProperty("Accept", "*/*");
        connection.setRequestProperty("Authorization", "Bearer " + token().getString("access_token"));
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {


            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String jsonData = bufferedReader.readLine();
                JSONObject context = new JSONObject();
                context.put("status", responseCode);
                context.put("jsondata", new JSONObject(jsonData));
                return context;
            }
        } else {
            throw new IOException("Failed to make payment. Response code: " + responseCode);
        }
    }

    // Verify transaction
    private static JSONObject verify_transaction(String txn) throws IOException {
        String url = url_prefix + "/standard/v1/payments/" + txn;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("X-Country", x_country);
        connection.setRequestProperty("X-Currency", x_currency);
        connection.setRequestProperty("Accept", "*/*");
        connection.setRequestProperty("Authorization", "Bearer " + token().getString("access_token"));
        connection.setRequestProperty("Content-Type", "application/json");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String jsonData = bufferedReader.readLine();
                JSONObject context = new JSONObject();
                context.put("status", responseCode);
                context.put("jsondata", new JSONObject(jsonData));
                return context;
            }
        } else {
            throw new IOException("Failed to verify transaction. Response code: " + responseCode);
        }
    }

    // Airtel money balance
    private static JSONObject airtelbalance() throws IOException {
        String url = url_prefix + "/standard/v1/users/balance";

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("X-Country", x_country);
        connection.setRequestProperty("X-Currency", x_currency);
        connection.setRequestProperty("Accept", "*/*");
        connection.setRequestProperty("Authorization", "Bearer " + token().getString("access_token"));
        connection.setRequestProperty("Cookie", "SERVERID=s116");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String jsonData = bufferedReader.readLine();
                return new JSONObject(jsonData);
            }
        } else {
            throw new IOException("Failed to get Airtel money balance. Response code: " + responseCode);
        }
    }

//    private static JSONObject transfermoney(String phone_number, String amount) throws IOException {
//        String pin = CertificatePinner.Pin.gen_pin(disbursement_pin).get("pin").toString();
//        String uuidgen = UUID.randomUUID().toString().substring(0, 20);
//        String url = url_prefix + "/standard/v1/disbursements/";
//
//        JSONObject requestBody = new JSONObject()
//                .put("charges", new JSONObject()
//                        .put("service", 1))
//                .put("payee", new JSONObject()
//                        .put("msisdn", phone_number))
//                .put("reference", "Pay")
//                .put("pin", pin)
//                .put("transaction", new JSONObject()
//                        .put("charge", 1)
//                        .put("amount", amount)
//                        .put("id", uuidgen));
//
//        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
//        connection.setRequestMethod("POST");
//        connection.setRequestProperty("X-Country", x_country);
//        connection.setRequestProperty("X-Currency", x_currency);
//        connection.setRequestProperty("Accept", "*/*");
//        connection.setRequestProperty("Authorization", "Bearer " + token().getString("access_token"));
//        connection.setRequestProperty("Content-Type", "application/json");
//        connection.setRequestProperty("Cookie", "SERVERID=s116");
//        connection.setDoOutput(true);
//
//        try (OutputStream outputStream = connection.getOutputStream()) {
//            byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
//            outputStream.write(input, 0, input.length);
//        }
//
//        int responseCode = connection.getResponseCode();
//        if (responseCode == HttpURLConnection.HTTP_OK) {
//            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
//                String jsonData = bufferedReader.readLine();
//                JSONObject data = new JSONObject();
//                data.put("data", new JSONObject(jsonData));
//                data.put("txn", uuidgen);
//                return data;
//            }
//        } else {
//            throw new IOException("Failed to transfer money. Response code: " + responseCode);
//        }
//    }
}
