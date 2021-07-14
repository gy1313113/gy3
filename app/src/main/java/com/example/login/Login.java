package com.example.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.TextView;
import com.example.gy3.MainActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 获取登录key并绑定工厂
 */
public class Login {
    private static final String TAG = "Login";
    public static final String FILE_NAME = "share_data";//保存在手机内的文件名
    private final TextView mTextView;
    private final Context mContext;
    private String myjson_uin;
    private String myuin;
    private String myjson_key;
    private String mykey_data;
    private String mykey;
    private String mybind;
    private String mybind_msg;
    
    public Login(TextView textView ,Context context) {
        this.mTextView = textView;
        this.mContext = context;
    }
    
    /**
     * 获取随机码（其中嵌套获取登录key）
     * @param phonenumber 电话号码
     * @param password 密码
     */
    public void getUin_getKey(String phonenumber, String password) {
        getUin_Service()
            .getPhoneNumber(phonenumber)
            .subscribeOn(Schedulers.io())//Schedulers.io内部封装了线程池,网络操作要在子线程中执行
            .observeOn(AndroidSchedulers.mainThread())//在主线程中控制UI
            .subscribe(new Observer<ResponseBody>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {
                }
    
                @Override
                public void onNext(@NonNull ResponseBody responseBody) {
                    try {
                        myjson_uin = responseBody.string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        JSONObject jsonObject_json = new JSONObject(myjson_uin);
                        String mydata_uin = jsonObject_json
                            .getString("data")
                            .replace("[", "")
                            .replace("]", "");//这算是歪门邪道
                        JSONObject jsonObject_data = new JSONObject(mydata_uin);
                        myuin = jsonObject_data.getString("uin");//获取随机码
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    MD5 md5 = new MD5();//MD5加密
                    String mypassword_md5 = md5.getMD5(password);//密码加密
                    String mypassword_uin_md5 = md5.getMD5(mypassword_md5 + myuin);//密码二次加密
                    getLoginKey(myuin,phonenumber,mypassword_uin_md5);//执行获取登录key
                }
    
                @Override
                public void onError(@NonNull Throwable e) {
                }
    
                @Override
                public void onComplete() {
                }
            });
    }
    
    private interface Uin_ServiceApi {
        @GET("v2/GET/RandomCode")
        Observable<ResponseBody> getPhoneNumber(@Query("PhoneNumber") String PhoneNumber);//GET请求参数
    }
    
    private Uin_ServiceApi getUin_Service() {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.3.202/highnet/")
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())//添加Rxjava
            .build();
        return retrofit.create(Uin_ServiceApi.class);
    }
    
    /**
     * 获取登录key（嵌套绑定工厂）
     * @param uin 随机码
     * @param PhoneNumber 电话号码
     * @param PassWord 加密后的密码
     */
    public void getLoginKey(String uin, String PhoneNumber, String PassWord) {
        getKey_Service()
            .getKey("5f8d20de-8c6c-4508-8ad0-7a1d8779b138", "ch", "Android", uin, PhoneNumber, PassWord,PhoneNumber)
            .subscribeOn(Schedulers.io())//Schedulers.io内部封装了线程池,网络操作要在子线程中执行
            .observeOn(AndroidSchedulers.mainThread())//在主线程中控制UI
            .subscribe(new Observer<ResponseBody>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {
                }
                @Override
                public void onNext(@NonNull ResponseBody responseBody) {
                    try {
                        myjson_key = responseBody.string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        JSONObject jsonObject_json = new JSONObject(myjson_key);
                        mykey_data = jsonObject_json
                            .getString("data")
                            .replace("[","")
                            .replace("]","");
                        JSONObject jsonObject_data = new JSONObject(mykey_data);
                        mykey = jsonObject_data.getString("key");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getbindFactory(mykey);//用登录key绑定工厂
                }
                @Override
                public void onError(@NonNull Throwable e) {
                }
                @Override
                public void onComplete() {
                }
            });
    }
    
    private interface Key_ServiceApi {
        @FormUrlEncoded
        @POST("v2/POST/Login")
        Observable<ResponseBody> getKey(@Field("key") String key,
                                        @Field("language") String language,
                                        @Field("type") String type,
                                        @Field("LoginCode") String uin,
                                        @Field("PhoneNumber") String PhoneNumber,
                                        @Field("PassWord") String PassWord,
                                        @Field("localphone") String localphone);
    }
    
    private Key_ServiceApi getKey_Service() {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.3.202/highnet/")
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())//添加Rxjava
            .addConverterFactory(GsonConverterFactory.create())//添加Gson
            .build();
        return retrofit.create(Key_ServiceApi.class);
    }
    
    /**
     * 绑定工厂
     * @param key 登录key
     */
    public void getbindFactory(String key) {
        binding_Service()
            .bindFactory(key,"2c9280846468765a016468fbe11b0121","Android")
            .subscribeOn(Schedulers.io())//Schedulers.io内部封装了线程池,网络操作要在子线程中执行
            .observeOn(AndroidSchedulers.mainThread())//在主线程中控制UI
            .subscribe(new Observer<ResponseBody>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {
                }
                @SuppressLint("SetTextI18n")
                @Override
                public void onNext(@NonNull ResponseBody responseBody) {
                    try {
                        mybind = responseBody.string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        JSONObject jsonObject_json = new JSONObject(mybind);
                        mybind_msg = jsonObject_json.getString("msg");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(mybind_msg.equals("success")){
                        mTextView.setText("Key:" + mykey + "\n" + "登陆成功");
                        /*
                        使用sharedPreferences存储小数据
                         */
                        SharedPreferences keyInfo = mContext.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
                        Editor editor = keyInfo.edit();//获取Editor
                        //得到Editor后，写入需要保存的数据
                        editor.putString("login_key", mykey);//在存储时，相同的key值对应的value会被覆盖，不用担心数据会累积
                        editor.apply();//异步保存
                        /*
                        页面跳转
                         */
                        Intent intent = new Intent();
                        intent.setClass(mContext, MainActivity.class);
                        mContext.startActivity(intent);
                    }
                    else {
                        mTextView.setText("登录失败");
                    }
                }
                @Override
                public void onError(@NonNull Throwable e) {
                }
                @Override
                public void onComplete() {
                }
            });
    }
    
    private interface binding_ServiceApi {
        @FormUrlEncoded
        @POST("v2/POST/setCurrentConnectFac")
        Observable<ResponseBody>  bindFactory(@Field("key") String key,
                                              @Field("factoryId") String factoryId,
                                              @Field("type") String type);
    }
    
    private binding_ServiceApi binding_Service() {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.3.202/highnet/")
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())//添加Rxjava
            .build();
        return retrofit.create(binding_ServiceApi.class);
    }
}
    
    
