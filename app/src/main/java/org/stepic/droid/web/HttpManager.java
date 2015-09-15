package org.stepic.droid.web;

import android.content.Context;
import android.os.Bundle;

import org.stepic.droid.base.MainApplication;
import org.stepic.droid.configuration.IConfig;
import com.google.gson.JsonObject;
import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.Proxy;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class HttpManager implements IHttpManager {


    @Inject
    IConfig mConfig;

    OkHttpClient mOkHttpClient = new OkHttpClient();
    private static final MediaType DEFAULT_MEDIA_TYPE
            = MediaType.parse("application/x-www-form-urlencoded");

    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    @Inject
    public HttpManager(Context context) {
        MainApplication.component(context).inject(this);
        mOkHttpClient.setAuthenticator(new Authenticator() {
            @Override
            public Request authenticate(Proxy proxy, Response response) throws IOException {
                String credential = Credentials.basic(mConfig.getOAuthClientId(), mConfig.getOAuthClientSecret());
                return response.request().newBuilder().header("Authorization", credential).build();
            }

            @Override
            public Request authenticateProxy(Proxy proxy, Response response) throws IOException {
                return null;
            }
        });
    }

    @Override
    public String post(String url, Bundle params) throws IOException {

        String query = makeQueryFromBundle(params);

        RequestBody body = RequestBody.create(DEFAULT_MEDIA_TYPE, query);
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();

        Response response = mOkHttpClient.newCall(request).execute();

        return response.body().string();
    }

    @Override
    public String postJson(String url, JsonObject jsonObject) throws IOException {

        String credential = Credentials.basic(mConfig.getOAuthClientId(), mConfig.getOAuthClientSecret());

        String jsonStr = jsonObject.toString();
        RequestBody requestBody = RequestBody.create(JSON_MEDIA_TYPE, jsonStr);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Response response = mOkHttpClient.newCall(request).execute();

        String respStr = response.body().toString();

        return "";

    }


    private String makeQueryFromBundle(Bundle params) {

        StringBuilder queryMaker = new StringBuilder();
        int i = 0;
        for (String key : params.keySet()) {
            i++;
            queryMaker.append(key);
            queryMaker.append('=');
            queryMaker.append(params.get(key));
            if (params.keySet().size() != i) {
                //if not last element
                queryMaker.append('&');
            }
        }
        return queryMaker.toString();
    }
}