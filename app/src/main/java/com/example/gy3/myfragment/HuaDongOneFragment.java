package com.example.gy3.myfragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.eventbus.MessageEventMainActivity;
import com.example.gy3.R;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import androidx.fragment.app.Fragment;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * 第一页的Fragment
 */
public class HuaDongOneFragment extends Fragment {
    
    private ImageView iv_logo;
    private TextView tv_content;
    private TextView tv_link;
    private String myjson;
    private String mydata;
    private String mycontent;
    private String mylink;
    private String mylogo;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View mView = inflater.inflate(
            R.layout.fragment_hua_dong_one, container, false);
        
        iv_logo = mView.findViewById(R.id.hua_dong_one_iv_logo);
        tv_content = mView.findViewById(R.id.hua_dong_one_tv_content);
        tv_link = mView.findViewById(R.id.hua_dong_one_tv_link);
        
        getData();//获取数据
        
        touch_link();//点击网页链接，跳转网页
        
        return mView;
    }
    
    /**
     * 获取数据
     */
    private void getData(){
        getService()
            .DownLoad("5f8d20de-8c6c-4508-8ad0-7a1d8779b138","ch","Android")
            .subscribeOn(Schedulers.newThread())//Schedulers.io内部封装了线程池,网络操作要在子线程中执行
            .observeOn(AndroidSchedulers.mainThread())//在主线程中控制UI
            .subscribe(new Observer<ResponseBody>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {
        
                }
    
                @Override
                public void onNext(@NonNull ResponseBody responseBody) {
                    try {
                        myjson = responseBody.string();//获取json字符串
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        //本次获取的json算是一个嵌套的,一个大的json，嵌套一个名为data的json
                        JSONObject jsonObject_json = new JSONObject(myjson);
                        mydata = jsonObject_json.getString("data");
                        JSONObject jsonObject_data = new JSONObject(mydata);
                        mycontent = jsonObject_data.getString("content");//获取介绍
                        mylink = jsonObject_data.getString("link");//获取网址
                        mylogo = jsonObject_data.getString("logo");//获取logo的url
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    tv_content.setText(mycontent);
                    tv_link.setText(mylink);
                    Glide
                        .with(HuaDongOneFragment.this)
                        .load(mylogo)
                        .into(iv_logo);//显示logo
                }
    
                @Override
                public void onError(@NonNull Throwable e) {
        
                }
    
                @Override
                public void onComplete() {
        
                }
            });
    }
    
    private interface ServiceApi {
        @GET("v2/GET/getAppSynopsis")
        Observable<ResponseBody> DownLoad(@Query("key") String key,
                                          @Query("language") String language,
                                          @Query("type") String type);
        //Retrofit和Rxjava结合时，Call改为Observable
    }
    
    private HuaDongOneFragment.ServiceApi getService(){
        Retrofit retrofit = new Builder()
            .baseUrl("http://192.168.3.202/highnet/")//服务器地址
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())//添加Rxjava
            //.addConverterFactory(GsonConverterFactory.create())//添加Gson
            .build();
        
        return retrofit.create(HuaDongOneFragment.ServiceApi.class);
    }
    
    /**
     * 点击链接
     */
    private void touch_link(){
        tv_link.setOnClickListener(v -> {
            tv_link.setTextColor(Color.BLUE);//设置点击后网址变蓝色，表示已点击过该链接
            Uri uri = Uri.parse(mylink);//设置跳转网页
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            startActivity(intent);
        });
    }
    
    /**
     * Fragment可交互时发出消息
     */
    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new MessageEventMainActivity(1));
    }
}