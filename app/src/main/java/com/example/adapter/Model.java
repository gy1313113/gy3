package com.example.adapter;

import android.content.Context;

/**
 * RecycleView的数据实体类
 */
public class Model {
    private String title;
    private String content;
    private String value;
    //private Context mContext;
    
    public void setTitle(String inputTitle){
        this.title = inputTitle;
    }
    
    public void setContent(String inputContent){
        this.content = inputContent;
    }
    
    public void setValue(String inputValue) {this.value = inputValue; }
    
    //public void setContext(Context inputContext){
        //this.mContext = inputContext;
    //}
    public String getTitle(){
        return title;
    }
    public String getContent(){
        return content;
    }
    public String getValue() {return value; }
    //public Context getContext(){
        //return mContext;
    //}
}

