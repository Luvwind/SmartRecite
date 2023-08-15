package net.kuludu.smartrecite;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.util.Map; // 导入Map类
import com.google.gson.Gson; // 导入Gson库
import org.json.JSONObject;
import org.json.JSONException;


public class LoginHelper {
    private String username; // 这一行是声明一个私有的String对象，用来存储用户名
    private String password; // 这一行是声明一个私有的String对象，用来存储密码
    private String token; // 这一行是声明一个私有的String对象，用来存储登录后返回的令牌
    private String server_url; // 这一行是声明一个私有的String对象，用来存储服务器的地址
    private Handler register_handler;
    private Handler login_handler; // 这一行是声明一个私有的Handler对象，用来处理登录相关的消息
    private Handler push_handler; // 这一行是声明一个私有的Handler对象，用来处理上传相关的消息
    private Handler fetch_handler; // 这一行是声明一个私有的Handler对象，用来处理下载相关的消息
    private String localWordFilePath; // 这一行是声明一个私有的String对象，用来存储本地单词数据库的路径

    public LoginHelper(Context context, String username, String password) {
        this.username = username;
        this.password = password;

        localWordFilePath = context.getFilesDir() + "/word.db";// 这一行是获取应用程序的文件目录，并拼接上"/word.db"，得到本地单词数据库文件的路径，并赋值给localWordFilePath属性
        SharedPreferences sharedPreferences = context.getSharedPreferences("config", MODE_PRIVATE);
        server_url = sharedPreferences.getString("server_url", "");// 这一行是从sharedPreferences对象中获取名为server_url的配置信息，并赋值给server_url属性，这个信息表示服务器的地址
        login_handler = new Handler() {// 创建一个匿名的Handler对象，并赋值给login_handler属性，这个对象用来处理登录相关的消息
            @Override
            public void handleMessage(Message msg) {//重写handleMessage方法，用来处理接收到的消息
                super.handleMessage(msg);// 调用父类的handleMessage方法，用来处理默认的消息

                if (msg.what == 0) {// 判断消息的类型是否为0，如果是，表示上传失败，执行以下代码
                    Toast.makeText(context, "Login failed!", Toast.LENGTH_SHORT).show();// 这一行是在屏幕上显示一个提示信息，表示上传失败
                } else {
                    token = (String) msg.obj;
                    Toast.makeText(context, "Login success!", Toast.LENGTH_SHORT).show();
                }
            }
        };
        register_handler = new Handler() {// 创建一个匿名的Handler对象，并赋值给login_handler属性，这个对象用来处理登录相关的消息
            @Override
            public void handleMessage(Message msg) {//重写handleMessage方法，用来处理接收到的消息
                super.handleMessage(msg);// 调用父类的handleMessage方法，用来处理默认的消息

                if (msg.what == 0) {// 判断消息的类型是否为0，如果是，表示注册失败，执行以下代码
                    Toast.makeText(context, "Register failed!", Toast.LENGTH_SHORT).show();// 这一行是在屏幕上显示一个提示信息，表示上传失败
                } else {
//                    token = (String) msg.obj;
                    Toast.makeText(context, "Register success!", Toast.LENGTH_SHORT).show();
                }
            }
        };
        push_handler = new Handler() {// 这一行是创建一个匿名的Handler对象，并赋值给push_handler属性，这个对象用来处理上传相关的消息
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                if (msg.what == 0) {
                    Toast.makeText(context, "Push failed!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Push success!", Toast.LENGTH_SHORT).show();
                }
            }
        };
        fetch_handler = new Handler() {// 这一行是创建一个匿名的Handler对象，并赋值给fetch_handler属性，这个对象用来处理下载相关的消息
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                if (msg.what == 0) {
                    Toast.makeText(context, "Fetch failed!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Fetch success!", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    public void login() {//声明一个login方法，用来实现登录功能
        try {
            final String login_entry = server_url + "/login"; // 将server_url属性和"/login"拼接起来，得到登录接口的地址，并赋值给login_entry变量
            URL url = new URL(login_entry);// 这一行是创建一个URL对象，并传入login_entry变量作为参数，并赋值给url变量，这个对象可以表示一个统一资源定位符
            OkHttpClient client = new OkHttpClient();// 这一行是创建一个OkHttpClient对象，并赋值给client变量，这个对象可以创建和发送HTTP请求
            RequestBody requestBody = new FormBody.Builder()// 这一行是创建一个FormBody对象，并使用Builder模式设置其属性，并赋值给requestBody变量，这个对象可以表示一个表单类型的请求体
                    .add("username", username)// 向FormBody对象中添加一个名为username的键值对，值为username属性
                    .add("password", password)// 创建一个Request对象，并使用Builder模式设置其属性，并赋值给request变量，这个对象可以封装一个HTTP请求的信息
                    .build();// 调用build方法生成一个FormBody对象
            Request request = new Request.Builder()// 这一行是创建一个Request对象，并使用Builder模式设置其属性，并赋值给request变量，这个对象可以封装一个HTTP请求的信息
                    .url(url)
                    .post(requestBody)
                    .build();// 调用build方法生成一个Request对象
            Call call = client.newCall(request); // 这一行是使用client对象的newCall方法，传入request对象作为参数，生成一个Call对象，并赋值给call变量，这个对象可以表示一个HTTP请求和响应的对话
            call.enqueue(new Callback() {// 这一行是使用call对象的enqueue方法，传入一个匿名的Callback对象作为参数，用来异步地发送请求并接收响应
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {// 这一行是重写onFailure方法，用来在请求失败时执行以下代码，传入两个参数，分别表示Call对象和IOException对象
                    Message message = new Message();
                    message.what = 0;// 这一行是设置message对象的类型为0，表示登录失败
                    login_handler.sendMessage(message); // 这一行是使用login_handler属性的sendMessage方法，传入message变量作为参数，向主线程发送消息
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException { // 这一行是重写onResponse方法，用来在请求成功时执行以下代码，传入两个参数，分别表示Call对象和Response对象
                    String resp = Objects.requireNonNull(response.body()).string(); // 这一行是从response对象中获取响应体，并使用Objects类的requireNonNull方法判断其是否为null，如果不为null，则调用string方法获取响应体的字符串，并赋值给resp变量
                    Message message = new Message(); // 这一行是创建一个Message对象，并赋值给message变量，这个对象可以封装要发送或接收的消息
                    if (!resp.equals("Bad authentication.")) {
                        message.obj = resp;// 这一行是向message对象中添加一个附加的对象，并赋值为resp变量，表示登录后返回的令牌
                        message.what = 1;
                    } else {
                        message.what = 0;
                    }
                    login_handler.sendMessage(message);
                }
            });
        } catch (MalformedURLException e) {// 这一行是捕获可能发生的MalformedURLException异常，并执行以下代码
            e.printStackTrace();// 这一行是打印异常的堆栈信息
        }
    }


    public void register() {//用来实现注册功能
        try {
            final String register_entry = server_url + "/register";
            URL url = new URL(register_entry);// 这一行是创建一个URL对象，并传入register_entry变量作为参数，并赋值给url变量，这个对象可以表示一个统一资源定位符
            OkHttpClient client = new OkHttpClient();// 这一行是创建一个OkHttpClient对象，并赋值给client变量，这个对象可以创建和发送HTTP请求
            RequestBody requestBody = new FormBody.Builder()// 这一行是创建一个FormBody对象，并使用Builder模式设置其属性，并赋值给requestBody变量，这个对象可以表示一个表单类型的请求体
                    .add("username", username)// 向FormBody对象中添加一个名为username的键值对，值为username属性
                    .add("password", password)// 向FormBody对象中添加一个名为password的键值对，值为password属性
                    .build();// 调用build方法生成一个FormBody对象
            Request request = new Request.Builder()//创建一个Request对象，这个对象可以封装一个HTTP请求的信息
                    .url(url)
                    .post(requestBody)
                    .build();// 调用build方法生成一个Request对象
            Call call = client.newCall(request); // 这一行是使用client对象的newCall方法，传入request对象作为参数，生成一个Call对象，并赋值给call变量，这个对象可以表示一个HTTP请求和响应的对话
            call.enqueue(new Callback() {// 这一行是使用call对象的enqueue方法，传入一个匿名的Callback对象作为参数，用来异步地发送请求并接收响应

            File file = new File("/data/data/net.kuludu.smartrecite/shared_prefs/config.xml");

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {// 这一行是重写onFailure方法，用来在请求失败时执行以下代码，传入两个参数，分别表示Call对象和IOException对象
                    Message message = new Message();
                    message.what = 0;// 这一行是设置message对象的类型为0，表示注册失败
                    register_handler.sendMessage(message); // 这一行是使用register_handler属性的sendMessage方法，传入message变量作为参数，向主线程发送消息
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException { // 这一行是重写onResponse方法，用来在请求成功时执行以下代码，传入两个参数，分别表示Call对象和Response对象
                    String resp = Objects.requireNonNull(response.body()).string(); // 这一行是从response对象中获取响应体，并使用Objects类的requireNonNull方法判断其是否为null，如果不为null，则调用string方法获取响应体的字符串，并赋值给resp变量
                    Message message = new Message(); // 这一行是创建一个Message对象，并赋值给message变量，这个对象可以封装要发送或接收的消息
                    if (resp.equals("Registration successful.")) {
                        file.delete();
                        message.what = 1;// 设置message对象的类型为1，表示注册成功
                    } else {
                        message.obj = resp;// 向message对象中添加一个附加的对象，并赋值为resp变量，这个对象表示注册失败时返回的信息
                        message.what = 0;
                    }
                    register_handler.sendMessage(message);
                }
            });
        } catch (MalformedURLException e) {//捕获可能发生的MalformedURLException异常，并执行以下代码
            e.printStackTrace();// 打印异常的堆栈信息
        }
    }



    public void upload(Context context) {//声明一个upload方法，用来实现上传单词数据库文件和sharedPreferences文件或内容的功能
        if (token == null) {//判断登录状态
            return;
        }
        try {
            final String push_entry = server_url + "/api/upload";
            File localWordFile = new File(localWordFilePath);
            URL url = new URL(push_entry);
            OkHttpClient client = new OkHttpClient();// 创建一个OkHttpClient对象，可以创建和发送HTTP请求
            MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);// 这一行是创建一个MultipartBody对象，并使用Builder模式设置其类型为表单类型，并赋值给requestBody变量，这个对象可以表示一个多部分类型的请求体

            File file = new File("/data/data/net.kuludu.smartrecite/shared_prefs/config.xml");

            requestBody.addFormDataPart("token", token)// 这一行是向MultipartBody对象中添加一个名为token的键值对，值为token属性
                    .addFormDataPart("file", file.getName(), RequestBody.create(file, MediaType.parse("application/octet-stream")));
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody.build())
                    .build();
            Call call = client.newCall(request);// 使用client对象的newCall方法，传入request对象作为参数，生成一个Call对象，并赋值给call变量，可以表示一个HTTP请求和响应的对话
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Message message = new Message();
                    message.what = 0;
                    push_handler.sendMessage(message);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String resp = Objects.requireNonNull(response.body()).string();
                    Message message = new Message();
                    if (resp.equals("Successfully uploaded.")) {
                        message.what = 1;
                    } else {
                        message.what = 0;
                    }
                    push_handler.sendMessage(message);
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }



    public void fetch(Context context) {
        if (token == null) {
            return;
        }
        try {
            final String fetch_entry = server_url + "/api/fetch";
            URL url = new URL(fetch_entry); //创建一个URL对象，并传入fetch_entry变量作为参数，并赋值给url变量，这个对象可以表示一个统一资源定位符
            OkHttpClient client = new OkHttpClient(); // 这一行是创建一个OkHttpClient对象，并赋值给client变量，这个对象可以创建和发送HTTP请求
            File file = new File("/data/data/net.kuludu.smartrecite/shared_prefs/config.xml");

            RequestBody requestBody = new FormBody.Builder() //创建一个FormBody对象，表示一个表单类型的请求体
                    .add("token", token)
                    .build();
            Request request = new Request.Builder()
                    .url(url+"?filename="+file.getName())
                    .post(requestBody)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Message message = new Message();
                    message.what = 0;
                    fetch_handler.sendMessage(message);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    byte[] resp = Objects.requireNonNull(response.body()).bytes();
                    Message message = new Message();
                    message.obj = resp; // 传递字节数组
                    fetch_handler.sendMessage(message); // 发送消息给handler
                }

                Handler fetch_handler = new Handler() {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
                        byte[] resp = (byte[]) msg.obj; // 获取字节数组
                        String respString = new String(resp);
                        if (!respString.equals("Bad authentication.")) {
                            try {
                                file.delete();
                                FileOutputStream fos = new FileOutputStream(file);
                                fos.write(resp);
                                fos.close();
                                msg.what = 1;

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            msg.what = 0;
                        }
                        if (msg.what == 0) {// 判断消息的类型是否为0，如果是，表示上传失败，执行以下代码
                            Toast.makeText(context, "Fetch failed!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Fetch success!", Toast.LENGTH_SHORT).show();
                        }
                    }
                };

            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}

