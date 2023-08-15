package net.kuludu.smartrecite;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
// 定义一个名为RightActivity的类，继承自AppCompatActivity类，用于显示已经掌握的单词
public class RightActivity extends AppCompatActivity {
    private TextView chinaText, wordText, englishText; // 声明一些私有的文本控件，分别用于显示中文意思、英文单词和音标
    private ImageView playVoice; // 声明一个私有的图片控件，用于播放单词的发音
    private TextToSpeech textToSpeech; // 声明一个私有的文本转语音对象，用于播放单词的发音
    Iterator it; // 声明一个私有的迭代器对象，用于遍历已经掌握的单词列表
    float x1 = 0; // 声明一个私有的浮点型变量，用于记录手指按下时的横坐标
    float y1 = 0; // 声明一个私有的浮点型变量，用于记录手指按下时的纵坐标
    float x2 = 0; // 声明一个私有的浮点型变量，用于记录手指抬起时的横坐标
    float y2 = 0; // 声明一个私有的浮点型变量，用于记录手指抬起时的纵坐标

    @Override
    protected void onCreate(Bundle savedInstanceState) { // 重写活动类的onCreate方法，当活动被创建时会调用这个方法
        super.onCreate(savedInstanceState); // 调用父类的onCreate方法，传入保存状态的Bundle对象
        setContentView(R.layout.activity_wrong); // 设置布局文件为activity_wrong.xml（这里复用了错误单词界面的布局文件）
        initControl(); // 调用自定义的方法，初始化控件和事件监听器
        nextRight(); // 调用自定义的方法，显示下一个已经掌握的单词，并更新界面上的内容
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) { // 重写活动类的onTouchEvent方法，当屏幕被触摸时会调用这个方法
        if (event.getAction() == MotionEvent.ACTION_DOWN) { // 如果触摸事件是按下动作
            x1 = event.getX(); // 获取按下时的横坐标，并赋值给x1变量
            y1 = event.getY(); // 获取按下时的纵坐标，并赋值给y1变量
        }
        if (event.getAction() == MotionEvent.ACTION_UP) { // 如果触摸事件是抬起动作
            x2 = event.getX(); // 获取抬起时的横坐标，并赋值给x2变量
            y2 = event.getY(); // 获取抬起时的纵坐标，并赋值给y2变量
            if (x2 - x1 > 200) { // 如果横向滑动的距离大于200像素，表示向右滑动
                nextRight(); // 调用自定义的方法，显示下一个已经掌握的单词，并更新界面上的内容
            }
        }
        return super.onTouchEvent(event); // 返回父类的onTouchEvent方法的结果
    }

    private void initControl() { // 定义一个私有的方法，用于初始化控件和事件监听器
        chinaText = findViewById(R.id.china_text); // 通过id找到中文意思文本控件，并赋值给chinaText变量
        wordText = findViewById(R.id.word_text); // 通过id找到英文单词文本控件，并赋值给wordText变量
        englishText = findViewById(R.id.english_text); // 通过id找到音标文本控件，并赋值给englishText变量
        Button nextWrong = findViewById(R.id.i_know_btn); // 通过id找到我知道了按钮控件，并赋值给nextWrong变量
        ImageButton backBtn = findViewById(R.id.back_btn); // 通过id找到返回按钮控件，并赋值给backBtn变量
        playVoice = findViewById(R.id.play_voice); // 通过id找到播放声音的图片控件，并赋值给playVoice变量
        WordHelper wordHelper = new WordHelper(this); // 创建一个WordHelper对象，并传入当前活动的上下文，用于操作数据库中的单词数据

        nextWrong.setVisibility(View.INVISIBLE); // 设置我知道了按钮控件的可见性为不可见，因为这里不需要这个按钮
        List<Word> right = wordHelper.getLearnedWord();
        // 调用WordHelper对象的getLearnedWord方法，获取已经掌握的单词列表，并赋值给right变量
        it = right.iterator();
        // 调用列表对象的iterator方法，获取一个迭代器对象，并赋值给it变量，用于遍历列表中的单词
        backBtn.setOnClickListener(view -> finish()); // 给返回按钮控件设置监听器，当被点击时，调用finish方法，结束当前活动

        textToSpeech = new TextToSpeech(this, i -> { // 创建一个文本转语音对象，并传入当前活动的上下文和一个初始化监听器，用于播放单词的发音
            if (i == TextToSpeech.SUCCESS) { // 如果初始化成功
                textToSpeech.setLanguage(Locale.US); // 设置文本转语音对象的语言为美式英语
                textToSpeech.setSpeechRate(0.5f); // 设置文本转语音对象的语速速度
            } else { // 如果初始化失败
                Toast.makeText(RightActivity.this, "语言功能初始化失败", Toast.LENGTH_SHORT).show(); // 弹出一个短暂的提示信息，告诉用户语言功能初始化失败
            }
        });
        playVoice.setOnClickListener(view -> { // 给播放声音的图片控件设置监听器，当被点击时，会触发以下代码
            String content = wordText.getText().toString(); // 获取单词文本控件的内容，并转换为字符串类型，赋值给content变量
            textToSpeech.speak(content, TextToSpeech.QUEUE_ADD, null); // 调用文本转语音对象的speak方法，传入content变量作为要播放的文本，QUEUE_ADD表示把这个文本添加到当前队列中等待播放，null表示不需要额外的参数
        });
    }

    private void setText(Word word) { //用于设置文本控件和图片控件的内容，并传入一个Word对象作为参数
        chinaText.setText(word.getChinese()); // 设置中文意思文本控件的内容为Word对象的中文意思
        wordText.setText(word.getWord()); // 设置英文单词文本控件的内容为Word对象的英文单词
        englishText.setText(word.getSoundmark()); // 设置音标文本控件的内容为Word对象的音标
        playVoice.setVisibility(View.VISIBLE); // 设置播放声音的图片控件的可见性为可见
    }

    private void setFinalText() { // 用于设置最后一条提示信息，并隐藏不需要的控件
        chinaText.setText("复习完毕！"); // 设置中文意思文本控件的内容为"复习完毕！"
        wordText.setText(""); // 设置英文单词文本控件的内容为空字符串
        englishText.setText(""); // 设置音标文本控件的内容为空字符串
        playVoice.setVisibility(View.INVISIBLE); // 设置播放声音的图片控件的可见性为不可见
    }

    private void nextRight() { // 定义一个私有的方法，用于显示下一个已经掌握的单词，并更新界面上的内容
        if (it.hasNext()) { // 如果迭代器对象还有下一个元素，表示还有未复习的单词
            Word word = (Word) it.next(); // 调用迭代器对象的next方法，获取下一个元素，并转换为Word类型，赋值给word变量
            setText(word); // 调用自定义的方法，设置文本控件和图片控件的内容，并传入word变量作为参数
        } else { // 如果迭代器对象没有下一个元素，表示已经复习完所有单词
            setFinalText(); // 调用自定义的方法，设置最后一条提示信息，并隐藏不需要的控件
        }
    }
}