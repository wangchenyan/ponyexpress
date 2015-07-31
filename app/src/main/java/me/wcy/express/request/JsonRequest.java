package me.wcy.express.request;

import com.alibaba.fastjson.JSON;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;

/**
 * Created by wcy on 2015/7/31.
 */
public class JsonRequest<T> extends Request<T> {
    private Class<T> mClass;
    private Response.Listener<T> mListener;

    public JsonRequest(int method, String url, Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mClass = clazz;
        this.mListener = listener;
    }

    public JsonRequest(String url, Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this(Method.GET, url, clazz, listener, errorListener);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String jsonString;
        try {
            jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(JSON.parseObject(jsonString, mClass), HttpHeaderParser.parseCacheHeaders(response));
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
