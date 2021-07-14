package com.example.gy3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.adapter.Model;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adapter.Mold;
import com.example.adapter.Mold.getMoldData;
import com.example.adapter.MyRecyclerViewAdapter;
import java.util.ArrayList;
import java.util.List;
/**
 * RecyclerView为主的页面
 */
public class RVActivity extends AppCompatActivity {
    
    private RecyclerView rv_1;
    private Spinner mSpinner;
    private TextView rv_sr_tv;
    private TextView text;
    private EditText search_et;
    private Button search_btn;
    private MyRecyclerViewAdapter mMyRecyclerViewAdapter;
    private static String login_key;//登录key
    private static String search_word;//输入的搜索值
    private static boolean search_flag = false;//判断现在是否处于搜索结果状态
    private static int flag_position = 0;//当前月份
    private final String[] month = new String[]{"1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"};
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rv);
        
        rv_1 = findViewById(R.id.RV_1);
        rv_sr_tv = findViewById(R.id.RV_Sr_tv);
        search_et = findViewById(R.id.RV_search_et);
        search_btn = findViewById(R.id.RV_search_btn);
        mSpinner = findViewById(R.id.RV_Sr);
        text = findViewById(R.id.text_tv);
    
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/count_word.ttf");
        search_btn.setTypeface(typeface);//添加字体
    
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_list_item_multiple_choice, month);
        mSpinner.setAdapter(adapter);//生成列表选项框
        
        getKeyInfo();//获取登录key
        
        getValue(flag_position);//获取模具参数并显示,此时默认为1月
    
        SpinnerChange();//列表选项框改变选择
    
        getSearch();//搜索功能
    }
    
    /**
     * 读取存储在SharedPreferences中的登录key
     */
    private void getKeyInfo(){
        SharedPreferences keyInfo = getSharedPreferences("share_data", MODE_PRIVATE);//该name为文件名
        login_key = keyInfo.getString("login_key", null);//读取key
    }
    
    /**
     * 生成原始数据和RecycleView
     */
    private void create_DataAndView(List<Model> value){
        List<Model> data = new ArrayList<>();//RecycleView的数据
        Model model;
        for (int i = 0; i < value.toArray().length; i++) {
            model = new Model();
            model.setTitle(value.get(i).getTitle());
            model.setContent("id:" + value.get(i).getContent());
            model.setValue("产量:" + value.get(i).getValue());
            data.add(model);
        }
        create_view(data);//生成RecycleView
    }
    
    /**
     * 创建RecycleView
     * @param value_data 要显示的数据
     */
    private void create_view(List<Model> value_data){
        //创建布局管理
        LinearLayoutManager layoutManager = new LinearLayoutManager(RVActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_1.setLayoutManager(layoutManager);
    
        mMyRecyclerViewAdapter = new MyRecyclerViewAdapter(R.layout.item_rv,value_data);//创建适配器
        rv_1.setAdapter(mMyRecyclerViewAdapter);//给RecyclerView上适配器
    
        item_click();//添加点击事件
        item_long_click();//添加长按点击事件
        item_child_click();//添加子item的点击事件
        item_animation();//添加item动画
    }
    
    /**
     * 回调接口，获取模具参数
     */
    public void getValue(int position){
        Mold mold;
        mold = new Mold();
        mold.getMold(login_key,position, new getMoldData() {
            @Override
            public void ongetMoldName(List<Model> value) {
                if(!search_flag){
                    create_DataAndView(value);//生成原始数据和RecycleView
                }
                else{
                    Search(value);//搜索功能
                }
            }
        });
    }
    
    /**
     * 列表选项框改变选择
     */
    public void SpinnerChange(){
        mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rv_sr_tv.setText(month[position]);//显示已选择的月份
                flag_position = position;//获取当前选择月份
                getValue(position);//获取模具参数,此时为选择的月份
            }
    
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
        
            }
        });
    }
    
    /**
     * 点击搜索按钮
     */
    public void getSearch(){
        search_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                search_word = search_et.getText().toString();//获取输入框结果
                /*
                输入为空时处于全部数据状态；输入不为空时处于搜索结果状态
                 */
                search_flag = !search_word.equals("");
                getValue(flag_position);
                search_et.setText("");
            }
        });
    }
    
    /**
     * 搜索逻辑及显示
     */
    public void Search(List<Model> value_data){
        List<Model> data_SearchResult = new ArrayList<>();//RecycleView的搜索数据
        Model model;
        /*
        第1个长度判断是为了防止输入字符串长度大于模具名时闪退
        */
        for(int i=0;i<value_data.toArray().length;i++){
            if(search_word.length()<=(value_data.get(i).getTitle().length())){
                for(int j=0;j<=(value_data.get(i).getTitle().length()-search_word.length());j++){
                    if(search_word.equals(value_data.get(i).getTitle().substring(j,search_word.length()+j))){
                        model = new Model();
                        model.setTitle(value_data.get(i).getTitle());//内容中已有模具产量等标题，不用重复添加
                        model.setContent("id:" + value_data.get(i).getContent());
                        model.setValue("产量:" + value_data.get(i).getValue());
                        data_SearchResult.add(model);
                    }
                }
                
            }
        }
        create_view(data_SearchResult);//生成RecycleView
    }
    
    /**
     * item的点击事件
     */
    private void item_click(){
        mMyRecyclerViewAdapter.setOnItemClickListener((adapter, view, position) -> {
            Toast.makeText(RVActivity.this,"点击了第"+position+"条",Toast.LENGTH_SHORT).show();
        });
    }
    /**
     * item的长按点击事件
     */
    private void item_long_click(){
        mMyRecyclerViewAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            Toast.makeText(RVActivity.this,"长按了第"+position+"条",Toast.LENGTH_SHORT).show();
            return false;
        });
    }
    /**
     * 子item的点击事件
     */
    private void item_child_click(){
        mMyRecyclerViewAdapter.addChildClickViewIds(R.id.rv_tv_content,R.id.rv_tv_title);//注册子控件id
        mMyRecyclerViewAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            Toast.makeText(RVActivity.this,"点击了第"+position+"条内的子item",Toast.LENGTH_SHORT).show();
        });
    }
    /**
     * 设置item动画（我这里自定义了）
     */
    private void item_animation(){
        mMyRecyclerViewAdapter.setAnimationEnable(true);
        mMyRecyclerViewAdapter.setAdapterAnimation(view -> new Animator[]{
            ObjectAnimator.ofFloat(view, "scaleY", 1, 1.5f, 1),
            ObjectAnimator.ofFloat(view, "scaleX", 1, 1.5f, 1)
        });
        //设置item先放大再缩小
        mMyRecyclerViewAdapter.setAnimationFirstOnly(false);//设置动画不只使用一次
    }
    /**
     * 设置头布局
     */
    @SuppressLint("InflateParams")
    private void head(){
        mMyRecyclerViewAdapter.addHeaderView(LayoutInflater
            .from(this)
            .inflate(R.layout.item_head,null));
    }
    
    /**
     * 在生命周期中消去back键的影响
     */
    @Override
    protected void onPause() {
        super.onPause();
        search_flag = false;
        flag_position = 0;
    }
}