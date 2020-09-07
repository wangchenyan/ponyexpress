# 小马快递
![](https://raw.githubusercontent.com/wangchenyan/ponyexpress/master/app/src/main/res/drawable-xxhdpi/ic_launcher.png)

## 前言
这是我第一个独立完成的项目，时隔多年又把它拿出来重构了一下代码。

- 项目地址：https://github.com/wangchenyan/ponyexpress
- 有问题请提Issues
- 如果喜欢，欢迎Star！

## 简介
小马快递，您的好帮手。查询并跟踪快递，快递信息及时掌握。<br>
支持全国100多家快递公司，支持扫码查询，智能识别快递公司。<br>
附带生成二维码小工具，方便实用。体积小巧，无广告，无多余权限。

## 更新说明
`v 2.0`
- 全新UI，高仿“支付宝-我的快递”
- 新增智能识别快递公司
- 新增扫一扫

`v 1.5`
- 新增自动更新

`v 1.4`
- 修复无法查询快递的问题
- 支持Android 6.0
- 支持运单备注
- 优化单号扫描界面

## 下载地址
### 2.1
https://raw.githubusercontent.com/wangchenyan/ponyexpress/master/release/ponyexpress.apk

扫码下载

![](https://raw.githubusercontent.com/wangchenyan/ponyexpress/master/release/qrcode.png)

## 项目
### 公开API
- 快递查询：[快递100](http://www.kuaidi100.com/)（非公开，侵权删）

### 开源技术
- [ZXing](https://github.com/zxing/zxing)
- [Volley](https://developer.android.com/training/volley/index.html)
- [Gson](https://github.com/google/gson)
- [ormlite](https://github.com/j256/ormlite-android)
- [Glide](https://github.com/bumptech/glide)

### 关键代码
网络请求`Volley + Gson`
```
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
```
封装GsonRequest
```java
public class GsonRequest<T> extends Request<T> {
    private Class<T> mClass;
    private Response.Listener<T> mListener;
    private Gson mGson;

    public GsonRequest(int method, String url, Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mClass = clazz;
        mListener = listener;
        mGson = new Gson();
    }

    public GsonRequest(String url, Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this(Method.GET, url, clazz, listener, errorListener);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String jsonString;
        try {
            jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
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
```

## 截图
![](https://raw.githubusercontent.com/wangchenyan/ponyexpress/master/art/screenshot_01.jpg)
![](https://raw.githubusercontent.com/wangchenyan/ponyexpress/master/art/screenshot_02.jpg)
![](https://raw.githubusercontent.com/wangchenyan/ponyexpress/master/art/screenshot_03.jpg)
![](https://raw.githubusercontent.com/wangchenyan/ponyexpress/master/art/screenshot_04.jpg)
![](https://raw.githubusercontent.com/wangchenyan/ponyexpress/master/art/screenshot_05.jpg)

## 关于作者
掘金：https://juejin.im/user/2313028193754168<br>
微博：https://weibo.com/wangchenyan1993

## License

    Copyright 2016 wangchenyan

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
