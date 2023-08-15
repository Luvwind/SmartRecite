package net.kuludu.smartrecite;

import android.annotation.SuppressLint;
import android.app.Fragment; // 这一行是导入Fragment类，用来表示一个可重用的用户界面片段
import android.app.FragmentTransaction; // 这一行是导入FragmentTransaction类，用来表示一个对Fragment进行添加，移除，替换等操作的事务
import android.content.Intent; // 这一行是导入Intent类，用来在不同的活动之间传递数据
import android.content.SharedPreferences; // 这一行是导入SharedPreferences类，用来存储和读取应用程序的配置信息
import android.os.Bundle; // 这一行是导入Bundle类，用来保存和恢复活动的状态
import android.view.View; // 这一行是导入View类，用来表示一个用户界面组件

import androidx.appcompat.app.AppCompatActivity; // 这一行是导入AppCompatActivity类，用来支持兼容性的活动

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private StudyFragment studyFragment; // 声明一个私有的StudyFragment对象，用来表示学习界面的片段
    private SettingFragment settingFragment; // 声明一个私有的SettingFragment对象，用来表示设置界面的片段
    private ScreenListener screenListener; // 声明一个私有的ScreenListener对象，用来监听屏幕状态的变化
    private SharedPreferences sharedPreferences; // 声明一个私有的SharedPreferences对象，用来存储和读取应用程序的配置信息
    private SharedPreferences.Editor editor; // 这一行是声明一个私有的SharedPreferences.Editor对象，用来编辑和提交配置信息

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);// 设置活动的布局文件为activity_home.xml
        initControl();
        review(getWindow().getDecorView());// 调用review方法，传入当前窗口的根视图作为参数，用来显示学习界面的片段
        findViewById(R.id.btn_classroom).setOnClickListener(this);
    }

    private void initControl() {// 用来初始化控件和变量
        studyFragment = new StudyFragment();
        settingFragment = new SettingFragment();
        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        int version = 0;
        screenListener = new ScreenListener(this);
        screenListener.begin(new ScreenStateListener() {
            @Override
            public void onScreenOn() {// 这一行是重写onScreenOn方法，用来在屏幕亮起时执行以下代码
                if (sharedPreferences.getBoolean("btnTf", false)) {
                    //从sharedPreferences对象中获取名为btnTf的配置信息，并判断其是否为true，如果是，则执行以下代码，这个配置信息表示是否开启了锁屏复习功能
                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);//创建一个Intent对象，用来指定从HomeActivity跳转到MainActivity
                        startActivity(intent);// 启动这个Intent对象，实现跳转

                }
            }

            @Override
            public void onScreenOff() {// 重写onScreenOff方法，用来在屏幕关闭时执行以下代码
                editor.putBoolean("tf", true);//存入sharepreferenced当前屏幕状态
                editor.putInt("version", version + 1);
                // 通过editor对象的putInt方法，存储一个键值对：键为version，值为version变量加一，这里假设version是一个整数变量
                editor.apply();// 提交editor编辑器中的修改
            }

            @Override
            public void onUnLock() {// 重写onUnLock方法，用来在解锁时执行以下代码
                editor.putBoolean("tf", false);//存入sharepreferenced当前屏幕状态
                editor.putInt("version", version + 1);
                // 通过editor对象的putInt方法，存储一个键值对：键为version，值为version变量加一，这里假设version是一个整数变量
                editor.apply();
            }
        });
    }

    @Override
    protected void onDestroy() {// 重写onDestroy方法，用来在活动销毁时执行以下代码
        super.onDestroy();// 调用父类的onDestroy方法，用来释放活动的资源
        screenListener.unregisterListener(); // 使用screenListener对象的unregisterListener方法，取消监听屏幕状态
    }

    public void setStudyFragment(Fragment fragment) {//声明一个公开的setStudyFragment方法，传入一个Fragment类型的参数，用来替换当前显示的界面片段
        FragmentTransaction transaction = getFragmentManager().beginTransaction();// 获取活动的碎片管理器，并开始一个事务，并赋值给transaction变量，这个变量可以对碎片进行操作
        transaction.replace(R.id.frame_layout, fragment);// 使用transaction变量的replace方法，传入两个参数，分别表示要替换的布局容器的id和要替换的碎片对象，将当前显示的碎片替换为传入的碎片
        transaction.commit();// 提交transaction变量中的操作，使之生效
    }

    public void setting(View v) {//传入一个View类型的参数，用来响应设置按钮的点击事件
        setStudyFragment(settingFragment);//调用setStudyFragment方法，传入settingFragment对象作为参数，用来显示设置界面的片段
    }

    public void review(View v) {//传入一个View类型的参数，用来响应复习单词按钮的点击事件
        setStudyFragment(studyFragment);// 这一行是调用setStudyFragment方法，传入studyFragment对象作为参数，用来显示学习界面的片段
    }

    public void wrong(View v) {
        Intent intent = new Intent(this, WrongActivity.class);
        startActivity(intent);
    }

    public void reviewWord(View v) {
        Intent intent = new Intent(this, RightActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, ClassroomActivity.class);
        startActivity(intent);
    }
}