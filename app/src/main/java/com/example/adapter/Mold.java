package com.example.adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 获取模具产量
 */
public class Mold {
    
    private String myjson_mold;
    List<Model> mold_value = new ArrayList<>();//模具名称
    /**
     * 输入key,获取模具数据
     * @param key 登录key
     */
    public void getMold(String key,int postion,getMoldData getMoldData) {
        getMold_Service()
            .getMold(key,"ch","Android")
            .subscribeOn(Schedulers.io())//Schedulers.io内部封装了线程池,网络操作要在子线程中执行
            .observeOn(AndroidSchedulers.mainThread())//在主线程中控制UI
            .subscribe(new Observer<ResponseBody>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {
                }
                
                @Override
                public void onNext(@NonNull ResponseBody responseBody) {
                    try {
                        myjson_mold = responseBody.string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        JSONObject jsonObject_json = new JSONObject(myjson_mold);
                        String myjson_data = jsonObject_json.getString("data");
                        JSONObject jsonObject_data = new JSONObject(myjson_data);
                        String myjson_reportData = jsonObject_data.getString("reportData");
                        JSONArray jsonArray_reportData = new JSONArray(myjson_reportData);//获取数组
                        Model model;
                        for(int i=0;i<jsonArray_reportData.length();i++){
                            JSONObject jsonObject_data_value = new JSONObject(jsonArray_reportData.getString(i));
                            JSONArray jsonArray_value = new JSONArray(jsonObject_data_value.getString("data"));
                            JSONObject jsonObject_value = new JSONObject(jsonArray_value.getString(postion));
                            model =new Model();
                            model.setTitle(jsonObject_data_value.getString("name"));
                            model.setContent(jsonObject_data_value.getString("rId"));
                            model.setValue(jsonObject_value.getString("value"));
                            mold_value.add(model);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getMoldData.ongetMoldName(mold_value);//放入回调接口,内部需写入显示模具名字的方法体
                }
                
                @Override
                public void onError(@NonNull Throwable e) {
                }
                
                @Override
                public void onComplete() {
                }
            });
    }
    
    private interface Mold_ServiceApi {
        @GET("v2/GET/getMoldOutput")
        Observable<ResponseBody> getMold(@Query("key") String key,
                                         @Query("language") String language,
                                         @Query("type") String type);//GET请求参数
    }
    
    private Mold_ServiceApi getMold_Service() {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.3.202/highnet/")
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())//添加Rxjava
            .build();
        return retrofit.create(Mold_ServiceApi.class);
    }
    
    /**
     * 模具数据回调接口
     */
    public interface getMoldData{
        /**
         * 模具名字
         */
        public void ongetMoldName(List<Model> name);
    }
}
