package org.phial.baas.service.util;

import okhttp3.*;
import org.mayanjun.myrest.util.JSON;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

public class OkHttpUtils {
    private static OkHttpClient okHttpClient;

    public static void setOkHttpClient(OkHttpClient client) {
        okHttpClient = client;
    }

    /**
     * GET Method begin---------------------------------
     */

    public static <T> T get(String url, Class<T> clasz) {
        return get(url, null, null, clasz);
    }

    public static void get(String url, Callback callback) {
        get(url, null, null, callback);
    }

    public static <T> T get(String url, Map<String, String> queryParameter, Class<T> clasz) {
        return get(url, null, queryParameter, clasz);
    }

    public static void get(String url, Map<String, String> queryParameter, Callback callback) {
        get(url, null, queryParameter, callback);
    }

    public static <T> T get(String url, Map<String, String> headerParameter, Map<String, String> queryParameter, Class<T> clasz) {
        Request request = processGetParameter(url, headerParameter, queryParameter);

        try (Response resp = okHttpClient.newCall(request).execute();) {
            return processResponse(resp, clasz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void get(String url, Map<String, String> headerParameter, Map<String, String> queryParameter, Callback callback) {
        Request request = processGetParameter(url, headerParameter, queryParameter);
        okHttpClient.newCall(request).enqueue(callback);
    }

    private static Request processGetParameter(String url, Map<String, String> headerParameter, Map<String, String> queryParameter) {
        Request.Builder builder = new Request.Builder();
        if (!isEmptyMap(headerParameter)) {
            builder.headers(Headers.of(headerParameter));
        }
        if (isEmptyMap(queryParameter)) {
            builder.url(url);
        } else {
            boolean hasQuery = false;
            try {
                hasQuery = new URL(url).getQuery().isEmpty();
            } catch (MalformedURLException e) {
                throw new RuntimeException("url is illegal");
            }
            StringBuilder sb = new StringBuilder(url);
            if (!hasQuery) {
                sb.append("?1=1");
            }
            queryParameter.forEach((k, v) -> {
                sb.append("&").append(k).append("=").append(v);
            });
            builder.url(sb.toString());
        }
        return builder.build();
    }

    /**
     * POST Method With JSON begin---------------------------------
     */

    public static <T> T postJson(String url, Class<T> clasz) {
        return postJson(url, null, null, clasz);
    }

    public static void postJson(String url, Callback callback) {
        postJson(url, null, null, callback);
    }

    public static <T> T postJson(String url, Map<String, String> headerParameter, Class<T> clasz) {
        return postJson(url, headerParameter, null, clasz);
    }

    public static void postJson(String url, Map<String, String> headerParameter, Callback callback) {
        postJson(url, headerParameter, null, callback);
    }

    public static <T> T postJson(String url, Map<String, String> headerParameter, Object bodyObj, Class<T> clasz) {
        Request request = processPostJsonParameter(url, headerParameter, bodyObj);
        try (Response resp = okHttpClient.newCall(request).execute();) {
            return processResponse(resp, clasz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void postJson(String url, Map<String, String> headerParameter, Object bodyObj, Callback callback) {
        Request request = processPostJsonParameter(url, headerParameter, bodyObj);
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static String postJson(String url, Object bodyObj) {
        Request request = processPostJsonParameter(url, null, bodyObj);
        try (Response resp = okHttpClient.newCall(request).execute();) {
            return processResponse(resp, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static Request processPostJsonParameter(String url, Map<String, String> headerParameter, String bodyObj) {
        Request.Builder builder = new Request.Builder().addHeader("Connection", "close").url(url);
        if (!Objects.isNull(bodyObj)) {
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), bodyObj);
            builder.post(body);
        } else {
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "{}");
            builder.post(body);
        }
        if (!isEmptyMap(headerParameter)) {
            builder.headers(Headers.of(headerParameter));
        }
        return builder.build();
    }

    private static Request processPostJsonParameter(String url, Map<String, String> headerParameter, Object bodyObj) {
        String json = null;
        if (bodyObj != null) {
            if (!(bodyObj instanceof String)) {
                json = JSON.se(bodyObj);
            } else {
                json = (String) bodyObj;
            }
        }
        return processPostJsonParameter(url, headerParameter, json);
    }

    /**
     * POST Method With Form begin---------------------------------
     */

    public static <T> T postForm(String url, Class<T> clasz) {
        return postForm(url, null, null, clasz);
    }

    public static void postForm(String url, Callback callback) {
        postForm(url, null, null, callback);
    }

    public static <T> T postForm(String url, Map<String, String> headerParameter, Class<T> clasz) {
        return postForm(url, headerParameter, null, clasz);
    }

    public static void postForm(String url, Map<String, String> headerParameter, Callback callback) {
        postForm(url, headerParameter, null, callback);
    }

    public static <T> T postForm(String url, Map<String, String> headerParameter, Map<String, String> parameters, Class<T> clasz) {
        Request request = processPostFormParameter(url, headerParameter, parameters);
        try (Response resp = okHttpClient.newCall(request).execute();) {
            return processResponse(resp, clasz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void postForm(String url, Map<String, String> headerParameter, Map<String, String> parameters, Callback callback) {
        Request request = processPostFormParameter(url, headerParameter, parameters);
        okHttpClient.newCall(request).enqueue(callback);
    }

    private static Request processPostFormParameter(String url, Map<String, String> headerParameter, Map<String, String> parameters) {
        Request.Builder builder = new Request.Builder().addHeader("Connection", "close").url(url);
        if (!Objects.isNull(parameters)) {
            FormBody.Builder formBuilder = new FormBody.Builder();
            parameters.forEach((k, v) -> {
                formBuilder.add(k, v);
            });
            builder.post(formBuilder.build());
        }
        if (!isEmptyMap(headerParameter)) {
            builder.headers(Headers.of(headerParameter));
        }
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private static <T> T processResponse(Response resp, Class<T> clasz) throws IOException {
        if (resp.isSuccessful()) {
            if (Objects.equals(Void.class, clasz)) {
                return null;
            }
            String respStr = resp.body().string();
            if (Objects.equals(String.class, clasz)) {
                return (T) respStr;
            }
            return JSON.de(respStr, clasz);
        }
        return null;
    }

    private static boolean isEmptyMap(Map<? extends Object, ? extends Object> map) {
        return Objects.isNull(map) || map.isEmpty();
    }
}
