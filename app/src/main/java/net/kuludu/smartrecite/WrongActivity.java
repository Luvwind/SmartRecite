package net.kuludu.smartrecite;

import android.content.SharedPreferences; // 导入类，用于获取用户偏好设置
import android.os.Bundle; // 导入类，用于存储活动状态
import android.speech.tts.TextToSpeech; // 导入类，用于实现语音合成
import android.view.MotionEvent; // 导入类，用于处理触摸事件
import android.view.View; // 导入类，用于表示视图
import android.widget.Button; // 导入类，用于表示按钮
import android.widget.ImageButton; // 导入类，用于表示图片按钮
import android.widget.ImageView; // 导入类，用于表示图片视图
import android.widget.TextView; // 导入类，用于表示文本视图
import android.widget.Toast; // 导入类，用于显示提示信息

import androidx.appcompat.app.AppCompatActivity; // 导入类，用于继承活动

import java.util.Iterator; // 导入类，用于遍历集合
import java.util.LinkedHashSet; // 导入类，用于存储有序的集合
import java.util.Locale; // 导入类，用于设置语言区域
import java.util.Set; // 导入类，用于存储集合

public class WrongActivity extends AppCompatActivity {
    private TextView chinaText, wordText, englishText; // 定义三个私有的文本视图对象，分别用于显示中文、英文和音标
    private WordHelper wordHelper; // 定义一个私有的WordHelper对象，用于操作本地数据库
    private ImageView playVoice; // 定义一个私有的图片视图对象，用于播放语音
    private TextToSpeech textToSpeech; // 定义一个私有的TextToSpeech对象，用于实现语音合成
    Iterator it; // 定义一个Iterator对象，用于遍历错题集合
    float x1 = 0; // 定义一个浮点变量x1，用于存储触摸事件的起始横坐标
    float y1 = 0; // 定义一个浮点变量y1，用于存储触摸事件的起始纵坐标
    float x2 = 0; // 定义一个浮点变量x2，用于存储触摸事件的结束横坐标
    float y2 = 0; // 定义一个浮点变量y2，用于存储触摸事件的结束纵坐标

    @Override
    protected void onCreate(Bundle savedInstanceState) { // 重写onCreate方法，在活动创建时执行
        super.onCreate(savedInstanceState); // 调用父类的onCreate方法，传递保存状态的Bundle对象
        setContentView(R.layout.activity_wrong); // 调用setContentView方法，设置活动的布局为activity_wrong.xml文件中定义的布局
        initControl(); // 调用initControl方法，初始化控件和数据
        nextWrong(); // 调用nextWrong方法，显示下一个错题
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) { // 重写onTouchEvent方法，在触摸事件发生时执行
        if (event.getAction() == MotionEvent.ACTION_DOWN) { // 如果触摸事件的动作是按下（ACTION_DOWN）
            x1 = event.getX(); // 调用event对象的getX方法，获取按下时的横坐标
            y1 = event.getY(); // 调用event对象的getY方法，获取按下时的纵坐标
        }
        if (event.getAction() == MotionEvent.ACTION_UP) { // 如果触摸事件的动作是抬起（ACTION_UP）
            x2 = event.getX(); // 调用event对象的getX方法，获取抬起时的横坐标
            y2 = event.getY(); // 调用event对象的getY方法，获取抬起时的纵坐标
            if (x2 - x1 > 200) { // 如果横向滑动距离大于200像素（从右向左滑动）
                nextWrong(); // 调用nextWrong方法，显示下一个错题
            }
        }
        return super.onTouchEvent(event); // 调用父类的onTouchEvent方法，传递触摸事件对象，并返回一个布尔值
    }

    private void initControl() { // 初始化控件和数据
        chinaText = findViewById(R.id.china_text);
        wordText = findViewById(R.id.word_text);
        englishText = findViewById(R.id.english_text);

        Button nextWrong = findViewById(R.id.i_know_btn);

        ImageButton backBtn = findViewById(R.id.back_btn);
        playVoice = findViewById(R.id.play_voice);
        nextWrong.setVisibility(View.VISIBLE); // 调用nextWrong对象的setVisibility方法，设置其可见性为可见（View.VISIBLE）

        wordHelper = new WordHelper(this); // 创建一个WordHelper对象，并传递当前活动的上下文参数，赋值给wordHelper变量
        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE); // 调用getSharedPreferences方法，获取名为config的偏好设置对象，并赋值给sharedPreferences变量
        Set<String> wrong = sharedPreferences.getStringSet("wrong", new LinkedHashSet<>()); // 从sharedPreferences对象中获取键为wrong的字符串集合，并赋值给wrong变量。如果没有该键，则默认为空集合（new LinkedHashSet<>()）
        it = wrong.iterator(); // 调用wrong集合的iterator方法，获取一个迭代器对象，并赋值给it变量
        nextWrong.setOnClickListener(view -> { // 给nextWrong按钮设置一个点击事件监听器，当用户点击时执行以下代码
            try { // 尝试执行以下代码
                it.remove(); // 调用it对象的remove方法，从错题集合中移除当前单词
                nextWrong(); // 调用nextWrong方法，显示下一个错题
            } catch (IllegalStateException e) { // 如果捕获到非法状态异常（表示没有下一个单词）
                setFinalText(); // 调用setFinalText方法，显示复习完毕的提示信息
            }
        });
        backBtn.setOnClickListener(view -> finish()); // 给backBtn图片按钮设置一个点击事件监听器，当用户点击时执行finish方法，结束当前活动

        textToSpeech = new TextToSpeech(this, i -> { // 创建一个TextToSpeech对象，并传递当前活动的上下文参数和一个初始化监听器参数，赋值给textToSpeech变量。当初始化完成时执行以下代码
            if (i == TextToSpeech.SUCCESS) { // 如果初始化状态为成功（TextToSpeech.SUCCESS）
                textToSpeech.setLanguage(Locale.US); //调用textToSpeech对象的setLanguage方法，设置语言为美式英语（Locale.US）
                textToSpeech.setSpeechRate(0.5f); // 调用textToSpeech对象的setSpeechRate方法，设置语速为x倍
            } else { // 如果初始化状态不为成功
                Toast.makeText(WrongActivity.this, "语言功能初始化失败", Toast.LENGTH_SHORT).show(); // 调用Toast类的静态方法makeText，创建一个提示信息对象，并传递当前活动的上下文参数、提示内容和显示时长参数。然后调用show方法，显示提示信息
            }
        });
        playVoice.setOnClickListener(view -> { // 给playVoice图片视图设置一个点击事件监听器，当用户点击时执行以下代码
            String content = wordText.getText().toString(); // 调用wordText对象的getText方法，获取其文本内容，并转换为字符串，赋值给content变量
            textToSpeech.speak(content, TextToSpeech.QUEUE_ADD, null); // 调用textToSpeech对象的speak方法，将content变量的内容合成语音，并添加到播放队列中，传递null值作为额外参数
        });
    }

    private void setText(Word word) { //根据Word对象设置文本视图的内容
        chinaText.setText(word.getChinese()); // 调用chinaText对象的setText方法，设置其文本内容为Word对象的中文属性（调用word对象的getChinese方法获取）
        wordText.setText(word.getWord()); // 调用wordText对象的setText方法，设置其文本内容为Word对象的英文属性（调用word对象的getWord方法获取）
        englishText.setText(word.getSoundmark()); // 调用englishText对象的setText方法，设置其文本内容为Word对象的音标属性（调用word对象的getSoundmark方法获取）
        playVoice.setVisibility(View.VISIBLE); // 调用playVoice对象的setVisibility方法，设置其可见性为可见（View.VISIBLE）
    }

    private void setFinalText() { //设置复习完毕的提示信息
        chinaText.setText("复习错题完毕！");
        wordText.setText(""); // 调用wordText对象的setText方法，设置其文本内容为空
        englishText.setText(""); // 调用englishText对象的setText方法，设置其文本内容为空
        playVoice.setVisibility(View.INVISIBLE); // 调用playVoice对象的setVisibility方法，设置其可见性为不可见（View.INVISIBLE）
    }

    private void nextWrong() { // 显示下一个错题
        if (it.hasNext()) { // 如果迭代器有下一个元素（还有错题）
            String wordIndex = (String) it.next(); // 调用迭代器对象的next方法，获取下一个元素，并转换为字符串，赋值给wordIndex变量（错题索引）
            Word word = wordHelper.getXWord(Integer.parseInt(wordIndex)); // 调用wordHelper对象的getXWord方法，根据索引获取对应的单词，并赋值给一个Word变量word
            setText(word); // 调用setText方法，根据word变量设置文本视图的内容
        } else { // 如果迭代器没有下一个元素（没有错题）
            setFinalText(); // 调用setFinalText方法，显示复习完毕的提示信息
        }
    }
}