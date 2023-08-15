package net.kuludu.smartrecite;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

// 定义一个名为MainActivity的类，继承自AppCompatActivity类，实现了View.OnClickListener和RadioGroup.OnCheckedChangeListener两个接口
public class MainActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    // 声明一些私有的控件和变量，用于在本类中使用
    private TextView timeText, dateText, wordText, englishText; // 文本控件，分别用于显示时间、日期、单词和音标
    private RadioGroup radioGroup; // 单选按钮组，用于显示三个选项
    private RadioButton radioOne, radioTwo, radioThree; // 单选按钮，分别对应三个选项
    private WordHelper wordHelper; // 单词帮助类，用于操作数据库中的单词数据
    private SharedPreferences sharedPreferences; // SharedPreferences对象，用于存储一些配置信息
    private SharedPreferences.Editor editor; // SharedPreferences的编辑器对象，用于修改配置信息
    private LinearLayout linearLayout; // 线性布局控件，用于显示背景图片
    private KeyguardManager.KeyguardLock kl; // 锁屏管理器的锁对象，用于解锁屏幕
    private TextToSpeech textToSpeech; // 文本转语音对象，用于播放单词的发音

    int id; // 一个整型变量，用于存储当前显示的单词的id
    int wordCount; // 一个整型变量，用于存储还需要背多少个单词才能解锁屏幕
    float x1 = 0; // 一个浮点型变量，用于记录手指按下时的横坐标
    float y1 = 0; // 一个浮点型变量，用于记录手指按下时的纵坐标
    float x2 = 0; // 一个浮点型变量，用于记录手指抬起时的横坐标
    float y2 = 0; // 一个浮点型变量，用于记录手指抬起时的纵坐标

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置窗口的一些属性，使得在锁屏状态下也能显示，并且全屏显示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main); // 设置布局文件为activity_main.xml

        initDatabaseHelper(); // 调用自定义的方法，初始化数据库帮助类和SharedPreferences对象
        initControl(); // 调用自定义的方法，初始化控件和事件监听器
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 获取系统日历对象
        Calendar calendar = Calendar.getInstance();
        String mMonth = String.valueOf(calendar.get(Calendar.MONTH) + 1); // 获取月份，并转换为字符串类型
        String mDay = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)); // 获取日期，并转换为字符串类型
        String mWeek = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)); // 获取星期，并转换为字符串类型



        String mHours;
        if (calendar.get(Calendar.HOUR_OF_DAY) < 10) { // 如果小时数小于10，则在前面补0
            mHours = "0" + calendar.get(Calendar.HOUR_OF_DAY);
        } else { // 否则直接转换为字符串类型
            mHours = "" + calendar.get(Calendar.HOUR_OF_DAY);
        }

        String mMinute;
        if (calendar.get(Calendar.MINUTE) < 10) { // 如果分钟数小于10，则在前面补0
            mMinute = "0" + calendar.get(Calendar.MINUTE);
        } else { // 否则直接转换为字符串类型
            mMinute = "" + calendar.get(Calendar.MINUTE);
        }

        // 根据星期数转换为中文字符，并赋值给mWeek变量
        if ("1".equals(mWeek)) {
            mWeek = "天";
        } else if ("2".equals(mWeek)) {
            mWeek = "一";
        } else if ("3".equals(mWeek)) {
            mWeek = "二";
        } else if ("4".equals(mWeek)) {
            mWeek = "三";
        } else if ("5".equals(mWeek)) {
            mWeek = "四";
        } else if ("6".equals(mWeek)) {
            mWeek = "五";
        } else if ("7".equals(mWeek)) {
            mWeek = "六";
        }
        timeText.setText(mHours + ":" + mMinute); // 设置时间文本控件的内容为小时和分钟
        dateText.setText(mMonth + "月" + mDay + "日" + " " + "星期" + mWeek); // 设置日期文本控件的内容为月份、日期和星期
    }

    private void btnGetText(String msg, RadioButton btn) { // 用于判断用户选择的选项是否正确，并给出反馈
        Word word = wordHelper.getXWord(id); // 从数据库中获取当前显示的单词对象
        String right_chinese = word.getChinese(); // 获取单词对象的中文意思
        if (msg.equals(right_chinese)) { // 如果用户选择的选项和正确答案相同
            wordText.setTextColor(Color.GREEN); // 设置单词文本控件的颜色为绿色
            englishText.setTextColor(Color.GREEN); // 设置音标文本控件的颜色为绿色
            btn.setTextColor(Color.GREEN); // 设置单选按钮的颜色为绿色
            saveRight(word); // 调用自定义的方法，保存正确的单词到SharedPreferences中
        } else { // 如果用户选择的选项和正确答案不同
            wordText.setTextColor(Color.RED); // 设置单词文本控件的颜色为红色
            englishText.setTextColor(Color.RED); // 设置音标文本控件的颜色为红色
            btn.setTextColor(Color.RED); // 设置单选按钮的颜色为红色
            saveWrong(word); // 调用自定义的方法，保存错误的单词到SharedPreferences中
        }
    }

//    private void saveWrong(Word word) { // 定义一个私有的方法，用于保存错误的单词到SharedPreferences中
//        Set<String> wrong = sharedPreferences.getStringSet("wrong", new HashSet<>()); // 获取SharedPreferences中存储的错误单词集合，如果没有则返回一个空集合
//        wrong.add(word.getIndex().toString()); // 把错误单词的id添加到集合中
//        editor.putStringSet("wrong", wrong); // 把更新后的集合保存到SharedPreferences中
//        editor.apply(); // 提交修改
//    }
//直接修改了getStringSet返回的set对象，而这个对象是SharedPreferences内部存储的引用，所以当你再次调用putStringSet时，Android会认为你没有改变任何数据，因此不会保存。解决办法有两个3：
//可以在获取set对象后，重新创建一个新的set对象，然后把原来的set对象的元素复制到新的set对象中，再添加新的元素，最后保存新的set对象。
//也可以在保存set对象后，再保存一个简单的偏好值，比如一个整数或布尔值，只要保证每次保存的值不同，就可以强制更新SharedPreferences。

    private void saveWrong(Word word) { //保存错误的单词到SharedPreferences中
        Set<String> wrong = sharedPreferences.getStringSet("wrong", new HashSet<>());
        // 获取SharedPreferences中存储的错误单词集合，如果没有则返回一个空集合
        Set<String> newWrong = new HashSet<>(wrong); // 创建一个新的集合，把原来的集合的元素复制到新的集合中
        newWrong.add(word.getIndex().toString()); // 把错误单词的id添加到新的集合中
        editor.putStringSet("wrong", newWrong); // 把更新后的新集合保存到SharedPreferences中
        editor.apply(); // 提交修改
    }

    private void saveRight(Word word) { // 定义一个私有的方法，用于保存正确的单词到SharedPreferences中
        Set<String> right = sharedPreferences.getStringSet("right", new HashSet<>());
        // 获取SharedPreferences中存储的正确单词集合，如果没有则返回一个空集合
        Set<String> newRight = new HashSet<>(right); // 创建一个新的集合，把原来的集合的元素复制到新的集合中
        newRight.add(word.getIndex().toString()); // 把正确单词的id添加到集合中
        editor.putStringSet("right", newRight); // 把更新后的集合保存到SharedPreferences中
        editor.apply(); // 提交修改
        wordHelper.setLastReview(word.getLast_review() + 1, word.getIndex()); // 调用数据库帮助类的方法，更新单词对象在数据库中的复习次数字段，加一表示复习了一次
        word.setLast_review(word.getLast_review() + 1); // 更新单词对象在内存中的复习次数字段，加一表示复习了一次
    }

    private void getNextWord() { // 定义一个私有的方法，用于获取下一个要显示的单词，并更新界面上的内容
        wordCount--; // 单词计数器减一，表示还需要背一个单词才能解锁屏幕
        if (wordCount == -1) { // 如果单词计数器等于-1，表示已经背完了设定的单词数量
            unlock(); // 调用自定义的方法，解锁屏幕并跳转到主界面
            finish(); // 结束当前活动
        }
        initTextColor(); // 调用自定义的方法，初始化文本控件和单选按钮的颜色为白色
        Word nextWord; // 声明一个单词对象，用于存储下一个要显示的单词
        Word prevWord; // 声明一个单词对象，用于存储上一个要显示的单词

        List<Word> words = wordHelper.getRandXWords(1); // 调用数据库帮助类的方法，从数据库中随机获取一个单词对象，并存储到一个列表中
        Word w = words.get(0); // 从列表中取出第一个（也是唯一一个）单词对象，并赋值给w变量
        wordText.setText(w.getWord()); // 设置单词文本控件的内容为w对象的英文单词
        englishText.setText(w.getSoundmark()); // 设置音标文本控件的内容为w对象的音标
        id = w.getIndex(); // 获取w对象的id，并赋值给id变量

        if (id == 0) { // 如果id等于0，表示w对象是数据库中第一个单词
            nextWord = wordHelper.getXWord(id + 1); // 那么下一个要显示的单词就是数据库中第二个单词
            prevWord = wordHelper.getXWord(id + 2); // 那么上一个要显示的单词就是数据库中第三个单词
        } else if (id == wordHelper.getWordsCount() - 1) { // 如果id等于数据库中单词总数减一，表示w对象是数据库中最后一个单词
            nextWord = wordHelper.getXWord(id - 1); // 那么下一个要显示的单词就是数据库中倒数第二个单词
            prevWord = wordHelper.getXWord(id - 2); // 那么上一个要显示的单词就是数据库中倒数第三个单词
        } else { // 如果id既不是0也不是最后一个，表示w对象是数据库中中间某个单词
            nextWord = wordHelper.getXWord(id + 1); // 那么下一个要显示的单词就是数据库中w对象后面那个单词
            prevWord = wordHelper.getXWord(id - 1); // 那么上一个要显示的单词就是数据库中w对象前面那个单词
        }
        Random r = new Random(); // 创建一个随机数生成器对象
        int random = r.nextInt(3); // 生成一个0到2之间（包含0和2）的随机整数，并赋值给random变量
        if (random == 0) { // 如果random等于0，表示正确答案放在第一个选项上
            radioOne.setText("A:" + w.getChinese()); // 设置第一个单选按钮的内容为w对象的中文意思，并在前面加上A:
            radioTwo.setText("B:" + nextWord.getChinese()); // 设置第二个单选按钮的内容为nextWord对象的中文意思，并在前面加上B:
            radioThree.setText("C:" + prevWord.getChinese()); // 设置第三个单选按钮的内容为prevWord对象的中文意思，并在前面加上C:
        } else if (random == 1) { // 如果random等于1，表示正确答案放在第二个选项上
            radioOne.setText("A:" + nextWord.getChinese()); // 设置第一个单选按钮的内容为nextWord对象的中文意思，并在前面加上A:
            radioTwo.setText("B:" + w.getChinese()); // 设置第二个单选按钮的内容为w对象的中文意思，并在前面加上B:
            radioThree.setText("C:" + prevWord.getChinese()); // 设置第三个单选按钮的内容为prevWord对象的中文意思，并在前面加上C:
        } else if (random == 2) { // 如果random等于2，表示正确答案放在第三个选项上
            radioOne.setText("A:" + nextWord.getChinese()); // 设置第一个单选按钮的内容为nextWord对象的中文意思，并在前面加上A:
            radioTwo.setText("B:" + prevWord.getChinese()); // 设置第二个单选按钮的内容为prevWord对象的中文意思，并在前面加上B:
            radioThree.setText("C:" + w.getChinese()); // 设置第三个单选按钮的内容为w对象的中文意思，并在前面加上C:
        }
    }

    private void initDatabaseHelper() { // 定义一个私有的方法，用于初始化数据库帮助类和SharedPreferences对象
        wordHelper = new WordHelper(this); // 创建一个WordHelper对象，并传入当前活动的上下文，用于操作数据库中的单词数据
        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE); // 获取一个名为config的SharedPreferences对象，用于存储一些配置信息，如解锁屏幕需要背多少个单词等
        editor = sharedPreferences.edit(); // 获取SharedPreferences对象的编辑器对象，用于修改配置信息
    }

    private void initControl() { // 定义一个私有的方法，用于初始化控件和事件监听器
        timeText = findViewById(R.id.time_text); // 通过id找到时间文本控件，并赋值给timeText变量
        dateText = findViewById(R.id.date_text); // 通过id找到日期文本控件，并赋值给dateText变量
        wordText = findViewById(R.id.word_text); // 通过id找到单词文本控件，并赋值给wordText变量
        englishText = findViewById(R.id.english_text); // 通过id找到音标文本控件，并赋值给englishText变量
        ImageView playVioce = findViewById(R.id.play_voice); // 通过id找到播放声音的图片控件，并赋值给playVioce变量
        radioGroup = findViewById(R.id.choose_group); // 通过id找到单选按钮组控件，并赋值给radioGroup变量
        radioOne = findViewById(R.id.choose_btn_one); // 通过id找到第一个单选按钮控件，并赋值给radioOne变量
        radioTwo = findViewById(R.id.choose_btn_two); // 通过id找到第二个单选按钮控件，并赋值给radioTwo变量
        radioThree = findViewById(R.id.choose_btn_three); // 通过id找到第三个单选按钮控件，并赋值给radioThree变量
        linearLayout = findViewById(R.id.background); // 通过id找到线性布局控件，并赋值给linearLayout变量
        radioGroup.setOnCheckedChangeListener(this); // 给单选按钮组控件设置监听器，当其中某个单选按钮被选择时，会触发onCheckedChanged方法
        playVioce.setOnClickListener(this); // 给播放声音的图片控件设置监听器，当被点击时，会触发onClick方法

        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE); // 获取系统服务中的锁屏管理器对象，并赋值给km变量
        kl = km.newKeyguardLock("unlock"); // 通过锁屏管理器对象创建一个锁对象，并赋值给kl变量，用于解锁屏幕
        wordCount = Integer.parseInt(sharedPreferences.getString("unlock", "3")); // 从SharedPreferences中获取解锁屏幕需要背多少个单词的配置信息，并转换为整型，如果没有则默认为3个，并赋值给wordCount变量
        getNextWord(); // 调用自定义的方法，获取下一个要显示的单词，并更新界面上的内容
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() { // 创建一个文本转语音对象，并传入当前活动的上下文和一个初始化监听器，用于播放单词的发音
            @Override
            public void onInit(int i) { // 定义初始化监听器的onInit方法，当文本转语音对象初始化完成时会调用这个方法
                if (i == TextToSpeech.SUCCESS) { // 如果初始化成功
                    textToSpeech.setLanguage(Locale.US); // 设置文本转语音对象的语言为美式英语
                    textToSpeech.setSpeechRate(1.0f); // 设置文本转语音对象的语速为正常速度
                } else { // 如果初始化失败
                    Toast.makeText(MainActivity.this, "语言功能初始化失败", Toast.LENGTH_SHORT).show(); // 弹出一个短暂的提示信息，告诉用户语言功能初始化失败
                }
            }
        });
    }

    @Override
    public void onClick(View view) { // 实现View.OnClickListener接口的onClick方法，当被监听的控件被点击时会调用这个方法
        switch (view.getId()) { // 根据被点击控件的id进行判断
            case R.id.play_voice: // 如果是播放声音的图片控件被点击
                String content = wordText.getText().toString(); // 获取单词文本控件的内容，并转换为字符串类型，赋值给content变量
                textToSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null); // 调用文本转语音对象的speak方法，传入content变量作为要播放的文本，QUEUE_FLUSH表示清空当前队列并立即播放，null表示不需要额外的参数
                break; // 跳出switch语句
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) { // 实现RadioGroup.OnCheckedChangeListener接口的onCheckedChanged方法，当被监听的单选按钮组中某个单选按钮被选择时会调用这个方法
        radioGroup.setClickable(false); // 设置单选按钮组控件不可点击，避免重复选择
        radioOne.setClickable(false); // 设置第一个单选按钮控件不可点击，避免重复选择
        radioTwo.setClickable(false); // 设置第二个单选按钮控件不可点击，避免重复选择
        radioThree.setClickable(false); // 设置第三个单选按钮控件不可点击，避免重复选择
        switch (checkedId) { // 根据被选择的单选按钮的id进行判断
            case R.id.choose_btn_one: // 如果是第一个单选按钮被选择
                String msg1 = radioOne.getText().toString().substring(2).trim(); // 获取第一个单选按钮控件的内容，并去掉前面的A:和空格，转换为字符串类型，赋值给msg1变量
                btnGetText(msg1, radioOne); // 调用自定义的方法，传入msg1变量和第一个单选按钮控件作为参数，判断用户选择的选项是否正确，并给出反馈
                break; // 跳出switch语句
            case R.id.choose_btn_two: // 如果是第二个单选按钮被选择
                String msg2 = radioTwo.getText().toString().substring(2).trim(); // 获取第二个单选按钮控件的内容，并去掉前面的B:和空格，转换为字符串类型，赋值给msg2变量
                btnGetText(msg2, radioTwo); // 调用自定义的方法，传入msg2变量和第二个单选按钮控件作为参数，判断用户选择的选项是否正确，并给出反馈
                break; // 跳出switch语句
            case R.id.choose_btn_three: // 如果是第三个单选按钮被选择
                String msg3 = radioThree.getText().toString().substring(2).trim(); // 获取第三个单选按钮控件的内容，并去掉前面的C:和空格，转换为字符串类型，赋值给msg3变量
                btnGetText(msg3, radioThree); // 调用自定义的方法，传入msg3变量和第三个单选按钮控件作为参数，判断用户选择的选项是否正确，并给出反馈
                break; // 跳出switch语句
        }
    }

    private void initTextColor() { // 定义一个私有的方法，用于初始化文本控件和单选按钮的颜色为白色
        radioOne.setChecked(false); // 取消第一个单选按钮的选择状态
        radioTwo.setChecked(false); // 取消第二个单选按钮的选择状态
        radioThree.setChecked(false); // 取消第三个单选按钮的选择状态
        radioOne.setClickable(true); // 设置第一个单选按钮控件可点击
        radioTwo.setClickable(true); // 设置第二个单选按钮控件可点击
        radioThree.setClickable(true); // 设置第三个单选按钮控件可点击
        radioGroup.setClickable(true); // 设置单选按钮组控件可点击

        radioOne.setTextColor(Color.WHITE); // 设置第一个单选按钮控件的颜色为白色
        radioTwo.setTextColor(Color.WHITE); // 设置第二个单选按钮控件的颜色为白色
        radioThree.setTextColor(Color.WHITE); // 设置第三个单选按钮控件的颜色为白色
        wordText.setTextColor(Color.WHITE); // 设置单词文本控件的颜色为白色
        englishText.setTextColor(Color.WHITE); // 设置音标文本控件的颜色为白色
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
                Toast.makeText(this, getString(R.string.mastered), Toast.LENGTH_SHORT).show(); // 弹出一个短暂的提示信息，告诉用户已经掌握了这个单词
                getNextWord(); // 调用自定义的方法，获取下一个要显示的单词，并更新界面上的内容
            } else if (x1 - x2 > 200) { // 如果横向滑动的距离小于-200像素，表示向左滑动
                Toast.makeText(this, getString(R.string.unlocked), Toast.LENGTH_SHORT).show(); // 弹出一个短暂的提示信息，告诉用户已经解锁了屏幕
                unlock(); // 调用自定义的方法，解锁屏幕并跳转到主界面
            } else if (y1 - y2 > 200) { // 如果纵向滑动的距离小于-200像素，表示向上滑动
                changeBackground(); // 调用自定义的方法，随机更换背景图片
            }
        }
        return super.onTouchEvent(event); // 返回父类的onTouchEvent方法的结果
    }

    private void unlock() { // 定义一个私有的方法，用于解锁屏幕并跳转到主界面
        Intent intent = new Intent(this, HomeActivity.class);
        // 创建一个意图对象，并传入当前活动的上下文和要跳转到的活动类，用于启动主界面活动
        startActivity(intent); // 调用startActivity方法，传入意图对象，启动主界面活动
        kl.disableKeyguard(); // 调用锁对象的disableKeyguard方法，解锁屏幕
        finish(); // 结束当前活动
        editor.putBoolean("tf", false); // 把SharedPreferences中存储的tf字段（表示是否在锁屏状态下）修改为false，并保存到SharedPreferences中
        editor.apply(); // 提交修改
    }

    private void changeBackground() { // 定义一个方法，用于随机更换背景图片
        Random random = new Random(); // 创建一个随机数生成器对象
        int index = random.nextInt(5); // 生成一个0到4之间（包含0和4）的随机整数，并赋值给index变量
        List<Integer> backgrounds = new ArrayList<>(); // 创建一个整型列表对象，用于存储背景图片的资源id
        backgrounds.add(R.mipmap.background_1); // 把第一张背景图片的资源id添加到列表中
        backgrounds.add(R.mipmap.background_2); // 把第二张背景图片的资源id添加到列表中
        backgrounds.add(R.mipmap.background_3); // 把第三张背景图片的资源id添加到列表中
        backgrounds.add(R.mipmap.background_4); // 把第四张背景图片的资源id添加到列表中
        backgrounds.add(R.mipmap.background_5); // 把第五张背景图片的资源id添加到列表中
        linearLayout.setBackgroundResource(backgrounds.get(index)); // 根据index变量从列表中取出对应的背景图片资源id，并设置给线性布局控件作为背景图片
    }
}