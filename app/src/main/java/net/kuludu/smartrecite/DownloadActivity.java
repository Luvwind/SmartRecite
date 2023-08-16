package net.kuludu.smartrecite;

import android.content.Intent; // 这一行是导入Intent类，用来在不同的活动之间传递数据
import android.content.SharedPreferences; // 这一行是导入SharedPreferences类，用来存储和读取应用程序的配置信息
import android.os.Bundle; // 这一行是导入Bundle类，用来保存和恢复活动的状态
import android.os.Handler; // 这一行是导入Handler类，用来在不同的线程之间发送和处理消息
import android.os.Looper; // 这一行是导入Looper类，用来管理一个线程的消息队列
import android.os.Message; // 这一行是导入Message类，用来封装要发送或接收的消息
import android.util.Log;
import android.widget.Toast; // 这一行是导入Toast类，用来显示短暂的提示信息

import androidx.appcompat.app.AppCompatActivity; // 这一行是导入AppCompatActivity类，用来支持兼容性的活动

import org.jetbrains.annotations.NotNull; // 这一行是导入NotNull注解，用来标记一个参数或返回值不能为null

import java.io.FileOutputStream; // 这一行是导入FileOutputStream类，用来向文件中写入数据
import java.io.IOException; // 这一行是导入IOException类，用来处理输入输出异常
import java.net.MalformedURLException; // 这一行是导入MalformedURLException类，用来处理错误的URL格式异常
import java.net.URL; // 这一行是导入URL类，用来表示一个统一资源定位符
import java.util.Objects; // 这一行是导入Objects类，用来提供一些对象操作的工具方法

import okhttp3.Call; // 这一行是导入Call类，用来表示一个HTTP请求和响应的对话
import okhttp3.Callback; // 这一行是导入Callback接口，用来定义请求成功或失败时的回调方法
import okhttp3.OkHttpClient; // 这一行是导入OkHttpClient类，用来创建和发送HTTP请求
import okhttp3.Request; // 这一行是导入Request类，用来封装一个HTTP请求的信息
import okhttp3.Response; // 这一行是导入Response类，用来封装一个HTTP响应的信息

public class DownloadActivity extends AppCompatActivity { // 这一行是声明一个名为DownloadActivity的公开类，并继承了AppCompatActivity类
    private SharedPreferences sharedPreferences; // 这一行是声明一个私有的SharedPreferences对象，用来存储和读取应用程序的配置信息
    private SharedPreferences.Editor editor; // 这一行是声明一个私有的SharedPreferences.Editor对象，用来编辑和提交配置信息
    private WordHelper wordHelper; // 这一行是声明一个私有的WordHelper对象，用来操作单词数据库
    private QuoteHelper quoteHelper; // 这一行是声明一个私有的QuoteHelper对象，用来操作名言数据库
    private String remoteWordFilePath; // 这一行是声明一个私有的String对象，用来存储远程单词数据库文件的路径
    private String remoteQuoteFilePath; // 这一行是声明一个私有的String对象，用来存储远程名言数据库文件的路径
    private String localWordFilePath; // 这一行是声明一个私有的String对象，用来存储本地单词数据库文件的路径
    private String localQuoteFilePath; // 这一行是声明一个私有的String对象，用来存储本地名言数据库文件
    private Handler handler;// 这一行是声明一个私有的Handler对象，用来在不同的线程之间发送和处理消息

    @Override
    protected void onCreate(Bundle savedInstanceState) {//重写onCreate方法，用来在活动创建时执行一些初始化操作
        super.onCreate(savedInstanceState); // 这一行是调用父类的onCreate方法，用来恢复活动的状态
        setContentView(R.layout.activity_download);

        handler = new Handler() {
            Integer count = 0;// 这一行是声明一个Integer对象，用来记录下载完成的文件数量

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                count++;

                if (count >= 2) {// 这一行是判断count变量是否大于等于2，如果是，表示两个文件都下载完成了
                    Toast.makeText(DownloadActivity.this, "Download complete!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DownloadActivity.this, MainActivity.class);// 这一行是声明一个私有的Handler对象，用来在不同的线程之间发送和处理消息
                    startActivity(intent);// 这一行是启动这个Intent对象，实现跳转
                    finish();
                }
            }
        };

        initDatabase();
        checkDatabase();
    }

    private void initDatabase() {
        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (sharedPreferences.getString("server_url", null) == null) {
            editor.putString("server_url", "http://123.123.123.123:5000");
        }
        if (sharedPreferences.getString("level", null) == null) {
            editor.putString("level", "cet_4");
        }
        editor.apply();

        remoteWordFilePath = sharedPreferences.getString("server_url", "") + "/word";
        remoteQuoteFilePath = sharedPreferences.getString("server_url", "") + "/quote";
        localWordFilePath = getApplicationContext().getFilesDir() + "/word.db";
        localQuoteFilePath = getApplicationContext().getFilesDir() + "/quote.db";
        wordHelper = new WordHelper(this);
        quoteHelper = new QuoteHelper(this);
        if (!wordHelper.isDatabaseExists()) {
            fetch(remoteWordFilePath, localWordFilePath);
        }
        if (!quoteHelper.isQuoteExists()) {
            fetch(remoteQuoteFilePath, localQuoteFilePath);
        }
    }

    private void checkDatabase() {
        boolean isFileValid = true;

        if (!wordHelper.isDatabaseExists()) {
            Toast.makeText(this, getString(R.string.wait_for_db_download), Toast.LENGTH_SHORT).show();

            isFileValid = false;
        }

        if (!quoteHelper.isQuoteExists()) {
            Toast.makeText(this, getString(R.string.wait_for_quote_download), Toast.LENGTH_SHORT).show();

            isFileValid = false;
        }

        if (!isFileValid) {
            Log.w("DB", "Database file invalid.");
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    private void fetch(String remote_path, String local_path) {
        try {
            URL url = new URL(remote_path);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Looper.prepare();
                    Toast.makeText(DownloadActivity.this, "Download failed", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    byte[] buf = Objects.requireNonNull(response.body()).bytes();
                    FileOutputStream fos = new FileOutputStream(local_path);
                    fos.write(buf, 0, buf.length);

                    fos.close();

                    handler.sendEmptyMessage(0);
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
