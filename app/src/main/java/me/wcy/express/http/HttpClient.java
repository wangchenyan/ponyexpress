package me.wcy.express.http;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import me.wcy.express.application.ExpressApplication;
import me.wcy.express.model.SearchResult;
import me.wcy.express.model.SuggestionResult;

/**
 * Created by hzwangchenyan on 2017/3/22.
 */
public class HttpClient {
    private static final String BASE_URL = "https://www.kuaidi100.com";
    private static final String HEADER_REFERER = "Referer";

    private static RequestQueue mRequestQueue;

    static {
        FakeX509TrustManager.allowAllSSL();
    }

    private static RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(ExpressApplication.getInstance().getApplicationContext());
        }
        return mRequestQueue;
    }

    public static void query(String type, String postId, final HttpCallback<SearchResult> callback) {
        String action = "/query";
        Map<String, String> params = new HashMap<>(2);
        params.put("type", type);
        params.put("postid", postId);
        String url = makeUrl(action, params);

        GsonRequest<SearchResult> request = new GsonRequest<SearchResult>(url, SearchResult.class,
                new Response.Listener<SearchResult>() {
                    @Override
                    public void onResponse(SearchResult searchResult) {
                        callback.onResponse(searchResult);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        callback.onError(volleyError);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put(HEADER_REFERER, BASE_URL);
                return headers;
            }
        };
        request.setShouldCache(false);
        getRequestQueue().add(request);
    }

    public static void getSuggestion(final String postId, final HttpCallback<SuggestionResult> callback) {
        String action = "/autonumber/autoComNum";
        Map<String, String> params = new HashMap<>(1);
        params.put("text", postId);
        String url = makeUrl(action, params);

        GsonRequest<SuggestionResult> request = new GsonRequest<SuggestionResult>(Request.Method.POST, url, SuggestionResult.class,
                new Response.Listener<SuggestionResult>() {
                    @Override
                    public void onResponse(SuggestionResult response) {
                        callback.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put(HEADER_REFERER, BASE_URL);
                return headers;
            }
        };
        request.setShouldCache(false);
        getRequestQueue().add(request);
    }

    public static String urlForLogo(String logo) {
        String action = "/images/all/" + logo;
        return makeUrl(action, null);
    }

    private static String makeUrl(String action, Map<String, String> params) {
        String url = BASE_URL + action;
        if (params == null || params.isEmpty()) {
            return url;
        }

        int i = 0;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (TextUtils.isEmpty(entry.getKey()) || TextUtils.isEmpty(entry.getValue())) {
                continue;
            }

            url += (i == 0) ? "?" : "&";
            url += (entry.getKey() + "=" + URLEncoder.encode(entry.getValue()));
            i++;
        }
        return url;
    }
}
