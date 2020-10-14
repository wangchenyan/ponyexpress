package me.wcy.express.http;

import android.text.format.DateUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wcy on 2015/7/31.
 */
public class GsonRequest<T> extends Request<T> {
    private Class<T> mClass;
    private Response.Listener<T> mListener;
    private Gson mGson;
    private Map<String, String> mHeaders = new HashMap<>();

    private static RetryPolicy sRetryPolicy = new DefaultRetryPolicy() {
        @Override
        public int getCurrentTimeout() {
            return (int) (15 * DateUtils.SECOND_IN_MILLIS);
        }
    };

    public GsonRequest(String url, Map<String, String> headers, Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this(Method.GET, url, headers, clazz, listener, errorListener);
    }

    public GsonRequest(int method, String url, Map<String, String> headers, Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mClass = clazz;
        mListener = listener;
        mGson = new Gson();
        mHeaders.putAll(headers);
        setShouldCache(false);
        setRetryPolicy(sRetryPolicy);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(mGson.fromJson(jsonString, mClass), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }
}
