package net.kuludu.smartrecite;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.view.View;

public class ClassroomActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ClassroomAdapter adapter;
    private List<ClassroomItem> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

        // 初始化RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 初始化Adapter
        adapter = new ClassroomAdapter();
        recyclerView.setAdapter(adapter);
        // 初始化数据
        data = new ArrayList<>();
        // 从服务端获取数据
        getData();

        // 在Activity中为Button控件设置一个点击事件监听器
        ImageButton btn_home = findViewById(R.id.back_btn);
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建一个Intent对象，指定要跳转到的主页Activity
                Intent intent = new Intent();
                intent.setClass(ClassroomActivity.this,HomeActivity.class);
                // 启动Intent对象
                startActivity(intent);
            }
        });

    }

    // 从服务端获取数据的方法
    private void getData() {
        // 创建一个请求队列
        RequestQueue queue = Volley.newRequestQueue(this);
        // 创建一个请求地址
        // 创建一个sharedPreferences对象
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        // 从sharedPreferences对象中获取名为server_url的配置信息
        String server_url = sp.getString("server_url", "");
        String url = server_url + "/classroom";
        // 创建一个JsonArrayRequest对象
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // 解析返回的json数组，转换为ClassroomItem对象，并添加到数据列表中
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONArray array = response.getJSONArray(i);
                                String title = array.getString(0); // 获取第一个值，即title
                                String text = array.getString(1); // 获取第二个值，即content
                                String image = server_url+array.optString(2); // 获取第三个值，即image
                                ClassroomItem item = new ClassroomItem(title,text, image);
                                data.add(item);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        // 通知Adapter更新数据
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // 处理请求错误的情况
                Toast.makeText(ClassroomActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
            }
        });
        // 将请求添加到队列中
        queue.add(request);
    }

    // 定义一个内部类，表示小课堂的每一条数据
    private static class ClassroomItem {
        private String title;//标题内容
        private String text; // 文本内容
        private String image; // 图片地址

        public ClassroomItem(String title,String text, String image) {
            this.title =title;
            this.text = text;
            this.image = image;
        }
        public String getTitle(){
            return title;
        }
        public String getText() {
            return text;
        }

        public String getImage() {
            return image;
        }
    }

    // 定义一个内部类，继承自RecyclerView.Adapter，用于绑定数据和视图
    private class ClassroomAdapter extends RecyclerView.Adapter<ClassroomAdapter.ClassroomViewHolder> {

        @NonNull
        @Override
        public ClassroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // 根据布局文件创建视图
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_classroom, parent, false);
            return new ClassroomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ClassroomViewHolder holder, int position) {
            // 获取当前位置的数据对象
            ClassroomItem item = data.get(position);
            // 设置视图中的文本内容
            holder.titleView.setText(item.getTitle());
            holder.textView.setText(item.getText());
            // 判断是否有图片地址，如果有则加载图片，如果没有则隐藏图片视图
            if (item.getImage() != null && !item.getImage().isEmpty()) {
                holder.imageView.setVisibility(View.VISIBLE);
                Glide.with(ClassroomActivity.this).load(item.getImage()).into(holder.imageView);
            } else {
                holder.imageView.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        // 定义一个内部类，继承自RecyclerView.ViewHolder，用于缓存视图
        private class ClassroomViewHolder extends RecyclerView.ViewHolder {
            private TextView titleView;//标题视图
            private TextView textView; // 文本视图
            private ImageView imageView; // 图片视图

            public ClassroomViewHolder(@NonNull View itemView) {
                super(itemView);
                titleView = itemView.findViewById(R.id.title_view);
                textView = itemView.findViewById(R.id.text_view);
                imageView = itemView.findViewById(R.id.image_view);
            }
        }
    }
}
