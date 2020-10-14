package me.wcy.express.http;

import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import me.wcy.express.application.ExpressApplication;
import me.wcy.express.model.SearchResult;
import me.wcy.express.model.SuggestionResult;

/**
 * Created by hzwangchenyan on 2017/3/22.
 */
public class HttpClient {
    private static final String BASE_URL = "https://www.kuaidi100.com";
    private static final String HEADER_REFERER = "Referer";

    private static RequestQueue sRequestQueue;

    static {
        FakeX509TrustManager.allowAllSSL();
        sRequestQueue = Volley.newRequestQueue(ExpressApplication.getInstance().getApplicationContext());
    }

    public static void query(String type, String postId, final HttpCallback<SearchResult> callback) {
        String action = "/query";
        Map<String, String> params = new HashMap<>();
        params.put("type", type);
        params.put("postid", postId);
        params.put("temp", String.valueOf(new Random().nextDouble()));
        String url = makeUrl(action, params);
        Map<String, String> headers = new HashMap<>();
        headers.put(HEADER_REFERER, BASE_URL);

        GsonRequest<SearchResult> request = new GsonRequest<>(
                url,
                headers,
                SearchResult.class,
                callback::onResponse,
                callback::onError);
        sRequestQueue.add(request);
    }

    public static void getSuggestion(final String postId, final HttpCallback<SuggestionResult> callback) {
        String action = "/autonumber/autoComNum";
        Map<String, String> params = new HashMap<>();
        params.put("text", postId);
        String url = makeUrl(action, params);
        Map<String, String> headers = new HashMap<>();
        headers.put(HEADER_REFERER, BASE_URL);

        GsonRequest<SuggestionResult> request = new GsonRequest<>(
                Request.Method.POST,
                url,
                headers,
                SuggestionResult.class,
                callback::onResponse,
                callback::onError);
        sRequestQueue.add(request);
    }

    public static String urlForLogo(String logo) {
        String action = "/images/all/" + logo;
        return makeUrl(action, null);
    }

    private static String makeUrl(String action, Map<String, String> params) {
        StringBuilder url = new StringBuilder(BASE_URL + action);
        if (params == null || params.isEmpty()) {
            return url.toString();
        }

        int i = 0;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (TextUtils.isEmpty(entry.getKey()) || TextUtils.isEmpty(entry.getValue())) {
                continue;
            }

            url.append((i == 0) ? "?" : "&")
                    .append(entry.getKey())
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue()));
            i++;
        }
        return url.toString();
    }
}
