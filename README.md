# 小马快递
![](https://raw.githubusercontent.com/ChanWong21/PonyExpress/master/app/src/main/res/drawable-xxhdpi/ic_launcher.png)

## 前言
这是我第一个独立完成的项目，时隔多年又把它拿出来重构了一下代码。

**注意：fir的key我没有提交，可直接忽略。**

* **开源不易，希望能给个Star鼓励**
* 项目地址：https://github.com/ChanWong21/PonyExpress
* 有问题请提Issues

## 简介
小马快递，您的好帮手。查询并跟踪快递，快递信息及时掌握。<br>
支持全国100多家快递公司，支持扫码查询，查询记录自动保存。<br>
附带生成二维码小工具，方便实用。体积小巧，无广告，无多余权限。<br>
简约，而不简单。

## 更新说明
`v 1.4`
* 修复无法查询快递的问题
* 支持Android 6.0
* 支持运单备注
* 优化单号扫描界面

## 下载地址
fir.im：http://fir.im/ponyexpress<br>
360手机助手：http://zhushou.360.cn/detail/index/soft_id/2876860<br>
百度应用：http://shouji.baidu.com/soft/item?docid=9102402<br>
应用宝：http://android.myapp.com/myapp/detail.htm?apkName=me.wcy.express

## 项目
### 公开API
* 快递查询：[快递100](http://www.kuaidi100.com/)（自己抓包拿到的接口^_^）

### 开源技术
* 条码扫描：[ZXing](https://github.com/zxing/zxing)
* 网络请求：[Volley](https://developer.android.com/training/volley/index.html)
* Json解析：[Gson](https://github.com/google/gson)
* 数据存储：[ormlite](https://github.com/j256/ormlite-android)

### 关键代码
网络请求`Volley+Gson`
```java
private void query() {
    GsonRequest<QueryResult> request = new GsonRequest<QueryResult>(Utils.getQueryUrl(mExpressInfo),
            QueryResult.class, new Listener<QueryResult>() {
        @Override
        public void onResponse(QueryResult queryResult) {
            Log.i("Query", queryResult.getMessage());
            if (queryResult.getStatus().equals("200")) {
                onQuerySuccess(queryResult);
            } else {
                onQueryFailure(queryResult);
            }
        }
    }, new ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Log.e("Query", volleyError.getMessage(), volleyError);
        }
    }) {
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = new HashMap<>();
            headers.put(Utils.HEADER_REFERER, Utils.REFERER);
            return headers;
        }
    };
    request.setShouldCache(false);
    mRequestQueue.add(request);
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
![](https://raw.githubusercontent.com/ChanWong21/PonyExpress/master/art/screenshot_01.jpg)
![](https://raw.githubusercontent.com/ChanWong21/PonyExpress/master/art/screenshot_02.jpg)
![](https://raw.githubusercontent.com/ChanWong21/PonyExpress/master/art/screenshot_03.jpg)
![](https://raw.githubusercontent.com/ChanWong21/PonyExpress/master/art/screenshot_04.jpg)

## 关于作者
简书：http://www.jianshu.com/users/3231579893ac<br>
微博：http://weibo.com/wangchenyan1993

## License

    Copyright 2016 Chay Wong

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
