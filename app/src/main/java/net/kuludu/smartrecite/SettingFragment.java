package net.kuludu.smartrecite;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.assetsbasedata.AssetsDatabaseManager;

public class SettingFragment extends Fragment implements View.OnClickListener { // 这是声明一个名为SettingFragment的公开类，继承自Fragment类，并实现View.OnClickListener接口，表示这个类可以响应点击事件
    private Spinner difficulty, allNumber; // 这是声明两个私有的Spinner类型的变量，分别表示难度和单词数量的下拉框控件
    private EditText et_serverUrl, et_username, et_password; // 这是声明三个私有的EditText类型的变量，分别表示服务器地址、用户名和密码的文本输入框控件
    private LoginHelper loginHelper; // 这是声明一个私有的LoginHelper类型的变量，表示一个登录帮助类的对象
    private SharedPreferences sharedPreferences; // 这是声明一个私有的SharedPreferences类型的变量，表示一个存储配置信息的对象
    private SharedPreferences.Editor editor; // 这是声明一个私有的SharedPreferences.Editor类型的变量，表示一个编辑配置信息的对象

    @Override
    public void onCreate(Bundle savedInstanceState) { // 这是重写Fragment类中的onCreate方法，在Fragment被创建时调用
        super.onCreate(savedInstanceState); // 调用父类中的onCreate方法

        sharedPreferences = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE); // 通过getActivity方法获取当前Fragment所依附的Activity对象，并通过getSharedPreferences方法获取名为config的SharedPreferences对象，并指定访问模式为私有模式
        editor = sharedPreferences.edit(); // 通过edit方法获取SharedPreferences对象对应的Editor对象，用于编辑配置信息
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { // 这是重写Fragment类中的onCreateView方法，在Fragment被加载时调用
        View view = inflater.inflate(R.layout.frame_setting, null); // 通过inflater对象的inflate方法，加载名为frame_setting的布局文件，并将其转换为View类型的对象，赋值给view变量
        initView(view); // 调用initView方法，传入view对象，用于初始化界面上的控件
        AssetsDatabaseManager.initManager(getActivity()); // 调用AssetsDatabaseManager类中的initManager静态方法，传入当前Fragment所依附的Activity对象，用于初始化数据库管理器
        new SpinnerActivity().initAdapter(); // 创建一个SpinnerActivity类型的对象，并调用其initAdapter方法，用于初始化下拉框控件的适配器

        return view; // 返回view对象，表示Fragment的界面
    }

    private void initView(View view) { //表示Fragment的界面
        Switch onLockScreen = view.findViewById(R.id.on_lock_screen);
        difficulty = view.findViewById(R.id.spinner_difficulty);
        allNumber = view.findViewById(R.id.spinner_all_number);
        et_serverUrl = view.findViewById(R.id.et_server_url);
        et_username = view.findViewById(R.id.et_username);
        et_password = view.findViewById(R.id.et_password);
        Button btn_saveServerUrl = view.findViewById(R.id.save_server_url);
        Button btn_saveUserPwd = view.findViewById(R.id.save_user_pwd);
        Button btn_login = view.findViewById(R.id.login);
        Button btn_fetch = view.findViewById(R.id.fetch);
        Button btn_upload = view.findViewById(R.id.upload);
        Button btn_register= view.findViewById(R.id.register);
        int version= 0;
        onLockScreen.setOnCheckedChangeListener((buttonView, isChecked) -> { // 通过onLockScreen对象的setOnCheckedChangeListener方法，设置一个开关状态改变监听器，接收两个参数：一个View类型的对象，表示开关控件；一个boolean类型的值，表示开关状态
            editor.putBoolean("btnTf", isChecked); // 通过editor对象的putBoolean方法，存储一个键值对：键为btnTf，值为isChecked
            editor.putInt("version", version + 1); // 通过editor对象的putInt方法，存储一个键值对：键为version，值为version变量加一，这里假设version是一个整数变量
            editor.apply();
        });

        btn_saveServerUrl.setOnClickListener(this);
        btn_saveUserPwd.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        btn_fetch.setOnClickListener(this);
        btn_upload.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        onLockScreen.setChecked(sharedPreferences.getBoolean("btnTf", false)); // 设置开关状态为sharedPreferences对象中存储的键为btnTf的boolean值，如果没有则默认为false

        et_serverUrl.setText(sharedPreferences.getString("server_url", "")); //设置文本内容为sharedPreferences对象中存储的键为server_url的String值，如果没有则默认为空字符串
        et_username.setText(sharedPreferences.getString("username", "")); // 设置文本内容为sharedPreferences对象中存储的键为username的String值，如果没有则默认为空字符串
        et_password.setText(sharedPreferences.getString("password", "")); //设置文本内容为sharedPreferences对象中存储的键为password的String值，如果没有则默认为空字符串
    }

    @Override
    public void onClick(View v) { // 这是重写View.OnClickListener接口中的onClick方法，在控件被点击时调用
        int version=0;
        switch (v.getId()) { // 通过v对象（表示被点击的控件）的getId方法获取控件的id，并用switch语句进行判断
            case R.id.save_server_url:
                editor.putString("server_url", et_serverUrl.getText().toString()); // 通过editor对象的putString方法，存储一个键值对：键为server_url，值为et_serverUrl对象中获取的文本内容
                editor.putInt("version", version + 1); // 通过editor对象的putInt方法，存储一个键值对：键为version，值为version变量加一，这里假设version是一个整数变量
                editor.apply();

                break;
            case R.id.save_user_pwd:
                editor.putString("username", et_username.getText().toString()); // 通过editor对象的putString方法，存储一个键值对：键为username，值为et_username对象中获取的文本内容
                editor.putString("password", et_password.getText().toString()); // 通过editor对象的putString方法，存储一个键值对：键为password，值为et_password对象中获取的文本内容
                editor.putInt("version", version + 1); // 通过editor对象的putInt方法，存储一个键值对：键为version，值为version变量加一，这里假设version是一个整数变量
                editor.apply();
                break;
            case R.id.login:
                loginHelper = new LoginHelper(getContext(), // 创建一个LoginHelper类型的对象，并赋值给loginHelper变量。构造函数接收三个参数：一个Context类型的对象（表示上下文），一个String类型的对象（表示用户名），一个String类型的对象（表示密码）
                        et_username.getText().toString(),
                        et_password.getText().toString());
                loginHelper.login(); // 调用loginHelper对象中的login方法，进行登录操作
                break;
            case R.id.register:
                loginHelper = new LoginHelper(getContext(), // 创建一个LoginHelper类型的对象，并赋值给loginHelper变量。构造函数接收三个参数：一个Context类型的对象（表示上下文），一个String类型的对象（表示用户名），一个String类型的对象（表示密码）
                        et_username.getText().toString(),
                        et_password.getText().toString());
                loginHelper.register(); // 调用loginHelper对象中的register方法，进行注册操作
                break;
            case R.id.fetch:
                if (loginHelper == null) { // 如果loginHelper变量为空（表示没有创建LoginHelper对象）
                    Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show(); // 调用Toast类中的makeText静态方法创建一个Toast类型的对象，并调用其show方法显示提示信息。makeText方法接收三个参数：一个Context类型的对象（表示上下文），一个CharSequence类型的对象（表示提示内容），一个int类型的值（表示提示时长）
                    break;
                }
                loginHelper.fetch(getActivity()); // 调用loginHelper对象中的fetch方法，进行获取单词数据操作
//                //创建一个Intent对象，指定要启动的Activity为你的当前Activity
//                Intent intent = new Intent(getActivity(), MainActivity.class);
//
//                //设置Intent的标志位为FLAG_ACTIVITY_CLEAR_TOP和FLAG_ACTIVITY_NEW_TASK
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                //调用startActivity方法，传入你创建的Intent对象
//                startActivity(intent);
//
//                //调用finish方法，结束当前Activity
//                getActivity().finish();
                break;
            case R.id.upload:
                if (loginHelper == null) { // 如果loginHelper变量为空（表示没有创建LoginHelper对象）
                    Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show(); // 调用Toast类中的makeText静态方法创建一个Toast类型的对象，并调用其show方法显示提示信息。makeText方法接收三个参数：一个Context类型的对象（表示上下文），一个CharSequence类型的对象（表示提示内容），一个int类型的值（表示提示时长）
                    break;
                }
                loginHelper.upload(getActivity()); // 调用loginHelper对象中的upload方法，进行上传单词数据操作
                break;
        }

    }
    private class SpinnerActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener { // 这是声明一个私有的内部类，名为SpinnerActivity，继承自AppCompatActivity类，并实现AdapterView.OnItemSelectedListener接口，表示这个类可以响应下拉框控件的选择事件
        public void initAdapter() { //用于初始化下拉框控件的适配器
            String[] arr_difficulty = {"cet_4", "cet_6"}; // 创建一个String类型的数组，并赋值给arr_difficulty变量，表示难度等级的选项
            String[] arr_allNumber = {"3", "5", "9", "12"}; // 创建一个String类型的数组，并赋值给arr_allNumber变量，表示单词数量的选项

            ArrayAdapter<String> adapter_difficulty = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, arr_difficulty); // 创建一个ArrayAdapter类型的对象，并赋值给adapter_difficulty变量，表示难度等级下拉框控件的适配器。构造函数接收三个参数：一个Context类型的对象（表示上下文），一个int类型的值（表示列表项布局文件），一个String类型的数组（表示数据源）
            ArrayAdapter<String> adapter_allNumber = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, arr_allNumber); // 创建一个ArrayAdapter类型的对象，并赋值给adapter_allNumber变量，表示单词数量下拉框控件的适配器。构造函数接收三个参数：一个Context类型的对象（表示上下文），一个int类型的值（表示列表项布局文件），一个String类型的数组（表示数据源）

            difficulty.setAdapter(adapter_difficulty); // 通过difficulty对象的setAdapter方法，设置其适配器为adapter_difficulty
            allNumber.setAdapter(adapter_allNumber); // 通过allNumber对象的setAdapter方法，设置其适配器为adapter_allNumber

            difficulty.setOnItemSelectedListener(this); // 通过difficulty对象的setOnItemSelectedListener方法，设置一个选择事件监听器，传入this，表示当前SpinnerActivity对象
            allNumber.setOnItemSelectedListener(this); // 通过allNumber对象的setOnItemSelectedListener方法，设置一个选择事件监听器，传入this，表示当前SpinnerActivity对象

            setSpinnerItemSelectedByValue(difficulty, sharedPreferences.getString("level", "cet_4")); // 调用setSpinnerItemSelectedByValue方法，传入两个参数：一个Spinner类型的对象（表示难度等级下拉框控件），一个String类型的对象（表示默认选中项）。该方法用于根据值设置下拉框控件选中项
            setSpinnerItemSelectedByValue(allNumber, sharedPreferences.getString("unlock", "3")); // 调用setSpinnerItemSelectedByValue方法，传入两个参数：一个Spinner类型的对象（表示单词数量下拉框控件），一个String类型的对象（表示默认选中项）。该方法用于根据值设置下拉框控件选中项
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) { // 这是重写AdapterView.OnItemSelectedListener接口中的onItemSelected方法，在下拉框控件被选择时调用
            String msg = adapterView.getSelectedItem().toString();// 通过adapterView对象（表示被选择的下拉框控件）的getSelectedItem方法获取选中项，并转换为String类型的对象，赋值给msg变量
            switch (adapterView.getId()) { // 通过adapterView对象的getId方法获取控件的id，并用switch语句进行判断
                case R.id.spinner_difficulty: // 如果id为spinner_difficulty
                    if (!sharedPreferences.getString("level", "cet_4").equals(msg)) { // 如果sharedPreferences对象中存储的键为level的String值（如果没有则默认为cet_4）不等于msg变量的值
                        editor.remove("right"); // 通过editor对象的remove方法，删除键为right的键值对
                        editor.remove("wrong"); // 通过editor对象的remove方法，删除键为wrong的键值对
                    }
                    editor.putString("level", msg); // 通过editor对象的putString方法，存储一个键值对：键为level，值为msg变量的值
                    break; // 跳出switch语句
                case R.id.spinner_all_number: // 如果id为spinner_all_number
                    editor.putString("unlock", msg); // 通过editor对象的putString方法，存储一个键值对：键为unlock，值为msg变量的值
                    break; // 跳出switch语句
            }
            editor.apply(); // 通过editor对象的apply方法，提交修改
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) { // 这是重写AdapterView.OnItemSelectedListener接口中的onNothingSelected方法，在下拉框控件没有被选择时调用

        }

        public void setSpinnerItemSelectedByValue(Spinner spinner, String value) { // 这是声明一个公开的void类型的方法，接收两个参数：一个Spinner类型的对象，表示下拉框控件；一个String类型的对象，表示要选中的值
            SpinnerAdapter spinnerAdapter = spinner.getAdapter(); // 通过spinner对象的getAdapter方法获取其适配器，并赋值给spinnerAdapter变量
            int k = spinnerAdapter.getCount(); // 通过spinnerAdapter对象的getCount方法获取其数据项个数，并赋值给k变量
            for (int i = 0; i < k; i++) { // 用for循环遍历数据项，i表示当前索引
                if (value.equals(spinnerAdapter.getItem(i).toString())) { // 如果value变量的值等于spinnerAdapter对象中索引为i的数据项转换为String类型的对象的值
                    spinner.setSelection(i, true); // 通过spinner对象的setSelection方法，设置选中项为索引为i的数据项，并指定是否触发选择事件为true
                }
            }
        }
    }
}