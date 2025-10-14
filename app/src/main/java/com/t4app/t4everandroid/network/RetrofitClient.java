package com.t4app.t4everandroid.network;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.t4app.t4everandroid.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String TAG = "RETROFIT-CLIENT";

    public static Retrofit getRetrofitClient() {
        String baseUrl = ApiConfig.BASE_URL;
        Log.d(TAG, "ENTRO EN GET RETROFIT CLIENT");
        SessionManager sessionManager = SessionManager.getInstance();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> {
            Log.d("RETRO", message);
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        Interceptor errorInterceptor = new Interceptor() {
            @NonNull
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + sessionManager.getTokenKey())
                        .build();

                Response response = chain.proceed(newRequest);

                String responseBody = response.body() != null ? response.peekBody(Long.MAX_VALUE).string() : "";

                if (!response.isSuccessful()) {
                    Log.e(TAG, "HTTP Error: " + response.code() + " - " + responseBody);
                    throw new IOException("HTTP Error: " + response.code() + " - " + responseBody);
                }

                return response;
            }
        };

        // TODO CHANGE THIS AND DELETE SSL TRUST MANAGER JUST FOR TESTING
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() { return new java.security.cert.X509Certificate[]{}; }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);

            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .addInterceptor(errorInterceptor)
                    .addInterceptor(logging)
                    .connectTimeout(90, TimeUnit.SECONDS)
                    .readTimeout(90, TimeUnit.SECONDS)
                    .writeTimeout(90, TimeUnit.SECONDS)
                    .build();

//            RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());


            return new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
//                    .addCallAdapterFactory(rxAdapter)
                    .client(client)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static Retrofit getRetrofitMapClient() {
//        String baseUrl = ApiConfig.BASE_MAP_URL;
//        Log.d(TAG, "ENTRO EN GET RETROFIT CLIENT");
//        SessionManager sessionManager = SessionManager.getInstance();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> {
            Log.d("RETRO", message);
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        Interceptor errorInterceptor = new Interceptor() {
            @NonNull
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .build();

                Response response = chain.proceed(newRequest);

                String responseBody = response.body() != null ? response.peekBody(Long.MAX_VALUE).string() : "";

                if (!response.isSuccessful()) {
                    Log.e(TAG, "HTTP Error: " + response.code() + " - " + responseBody);
                    throw new IOException("HTTP Error: " + response.code() + " - " + responseBody);
                }

                try {
                    JSONObject jsonResponse = new JSONObject(responseBody);

//                    if (jsonResponse.has("status") && !jsonResponse.getBoolean("status")) {
//                        String message = jsonResponse.optString("msg", "Unknown error");
//
//                        if (message.toUpperCase().contains("CREDENTIALS NOT FOUND")) {
//                            MessageUserUtils.showCredentialsError(sessionManager, T4DispatchMainActivity.getInstance());
//
//                        }
//                    }

                } catch (JSONException e) {
                    Log.e(TAG, "JSON Parsing Error", e);
                }

                return response;
            }
        };


        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(errorInterceptor)
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();


//        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());


        return new Retrofit.Builder()
//                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(rxAdapter)
                .client(client)
                .build();
    }


    public static Retrofit getRetrofitClientCustom() {
        String baseUrl = ApiConfig.BASE_URL;
//        Log.d(TAG, "ENTRO EN GET RETROFIT CLIENT");
        SessionManager sessionManager = SessionManager.getInstance();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> {
            Log.d("RETRO", message);
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        Interceptor errorInterceptor = new Interceptor() {
            @NonNull
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + sessionManager.getTokenKey())
                        .build();

                Response response = chain.proceed(newRequest);

                String responseBody = response.body() != null ? response.peekBody(Long.MAX_VALUE).string() : "";

                if (!response.isSuccessful()) {
                    Log.e(TAG, "HTTP Error: " + response.code() + " - " + responseBody);
                    throw new IOException("HTTP Error: " + response.code() + " - " + responseBody);
                }
                return response;
            }
        };


        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(errorInterceptor)
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();


//        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        Gson gson = new GsonBuilder()
                .create();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
//                .addCallAdapterFactory(rxAdapter)
                .client(client)
                .build();
    }
}
