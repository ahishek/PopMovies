package com.solo.nair.popmovies.network;

import android.support.v4.util.ArrayMap;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.ParseError;
import com.android.volley.request.JsonRequest;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by abhisheknair on 19/06/16.
 */
public class JsonParamRequest<T> extends JsonRequest<T> {

    private Map<String, String> params;
    private final Class<T> clazz;
    private final Response.Listener<T> listener;

    public JsonParamRequest(int method, String url, Class<T> clazz,
                            ArrayMap<String,String> params,
                            Response.Listener<T> listener,
                            Response.ErrorListener errorListener) {
        super(method, url, "", listener, errorListener);
        this.clazz = clazz;
        this.listener = listener;
        this.params = params;
    }


    @Override
    public Map<String, String> getParams() throws AuthFailureError {
        return params != null ? params : super.getHeaders();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(
                    new Gson().fromJson(json, clazz), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}
