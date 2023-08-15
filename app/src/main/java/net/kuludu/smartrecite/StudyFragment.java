package net.kuludu.smartrecite;

import android.app.Fragment; // 这是导入android.app.Fragment类，用于创建Fragment对象
import android.content.Context; // 这是导入android.content.Context类，用于获取应用程序的上下文
import android.content.Intent;
import android.content.SharedPreferences; // 这是导入android.content.SharedPreferences类，用于存储和读取配置信息
import android.os.Bundle; // 这是导入android.os.Bundle类，用于传递数据
import android.view.LayoutInflater; // 这是导入android.view.LayoutInflater类，用于加载布局文件
import android.view.View; // 这是导入android.view.View类，用于表示界面上的控件
import android.view.ViewGroup; // 这是导入android.view.ViewGroup类，用于表示控件的容器
import android.widget.Button;
import android.widget.TextView; // 这是导入android.widget.TextView类，用于创建文本显示控件

import com.example.assetsbasedata.AssetsDatabaseManager; // 这是导入com.example.assetsbasedata.AssetsDatabaseManager类，用于管理Assets目录下的数据库文件

import java.util.HashSet; // 这是导入java.util.HashSet类，用于创建不重复元素的集合
import java.util.Set; // 这是导入java.util.Set接口，用于定义集合的操作

public class StudyFragment extends Fragment {
    private TextView difficultyText, quoteEnglishText, quoteChinaText, alreadyStudyText, alreadyMasterText, wrongText; // 这是声明六个私有的TextView类型的变量，分别表示难度、英文名言、中文名言、已学习单词数、已掌握单词数和错误单词数的文本显示控件
    private QuoteHelper quoteHelper;
    private SharedPreferences sharedPreferences; //声明一个私有的SharedPreferences类型的变量，表示一个存储配置信息的对象
    private WordHelper wordHelper;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) { // 这是重写Fragment类中的onCreate方法，在Fragment被创建时调用
        super.onCreate(savedInstanceState); // 调用父类中的onCreate方法

        sharedPreferences = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE); // 通过getActivity方法获取当前Fragment所依附的Activity对象，并通过getSharedPreferences方法获取名为config的SharedPreferences对象，并指定访问模式为私有模式
        editor = sharedPreferences.edit();
    }

    @Override
    public void onStart() { // 这是重写Fragment类中的onStart方法，在Fragment被启动时调用
        super.onStart(); // 调用父类中的onStart方法
        setText(); // 用于设置文本显示控件的内容
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { // 这是重写Fragment类中的onCreateView方法，在Fragment被加载时调用
        View view = inflater.inflate(R.layout.frame_study, null); // 通过inflater对象（表示布局加载器）的inflate方法，加载名为frame_study的布局文件，并将其转换为View类型的对象，赋值给view变量
        initControl(view);// 调用initControl方法，传入view对象（表示Fragment的界面），用于初始化界面上的控件
        AssetsDatabaseManager.initManager(getActivity()); // 调用AssetsDatabaseManager类中的initManager静态方法，传入当前Fragment所依附的Activity对象，用于初始化数据库管理器

        return view; // 返回view对象，表示Fragment的界面
    }

    private void setText() { // 用于设置文本显示控件的内容
        Quote quote = quoteHelper.getRandQuote();
        String quoteChinese = quote.getChinese();
        String quoteEnglish = quote.getEnglish();
        quoteEnglishText.setText(quoteEnglish);
        quoteChinaText.setText(quoteChinese);

        Set<String> wrong = sharedPreferences.getStringSet("wrong", new HashSet<>()); // 通过sharedPreferences对象（表示存储配置信息的对象）的getStringSet方法，获取键为wrong的String类型的集合，并赋值给wrong变量。如果没有则默认为空集合
        int wrongCount = wrong.size(); // 通过wrong对象（表示错误单词集合）的size方法，获取集合中元素个数
        wrongText.setText(wrongCount + ""); // 通过wrongText对象（表示错误单词数的文本显示控件）的setText方法，设置文本内容为wrongCount变量转换为String类型的对象的值
        int rightCount = wordHelper.getLearnedWord().size(); // 通过wordHelper对象（表示单词帮助类的对象）的getLearnedWord方法，获取已学习单词集合，并调用其size方法，获取集合中元素个数，并赋值给rightCount变量
        alreadyMasterText.setText(rightCount + ""); // 通过alreadyMasterText对象（表示已掌握单词数的文本显示控件）的setText方法，设置文本内容为rightCount变量转换为String类型的对象的值

        String totalCount = String.valueOf(wrongCount + rightCount); // 通过String类中的valueOf静态方法，将wrongCount变量和rightCount变量相加后转换为String类型的对象，并赋值给totalCount变量

        alreadyStudyText.setText(totalCount); // 通过alreadyStudyText对象（表示已学习单词数的文本显示控件）的setText方法，设置文本内容为totalCount变量的值
        String level = sharedPreferences.getString("level", "cet_4"); // 通过sharedPreferences对象中存储的键为level的String值（如果没有则默认为cet_4），并赋值给level变量
        if (level.equals("cet_4")) {
            difficultyText.setText(getString(R.string.cet4));
        } else if (level.equals("cet_6")) {
            difficultyText.setText(getString(R.string.cet6));
        }
    }
    private void initControl(View view) { //表示Fragment的界面，用于初始化界面上的控件
        difficultyText = view.findViewById(R.id.difficulty_text);
        quoteEnglishText = view.findViewById(R.id.wisdom_english);
        quoteChinaText = view.findViewById(R.id.wisdom_china);
        alreadyStudyText = view.findViewById(R.id.already_study);
        alreadyMasterText = view.findViewById(R.id.already_mastered);
        wrongText = view.findViewById(R.id.wrong_text);
        wordHelper = new WordHelper(getActivity());
        quoteHelper = new QuoteHelper(getActivity());


        Button backButton = view.findViewById(R.id.back_screenlock_btn);
        // 为backButton对象设置点击监听器，传入一个匿名内部类的对象
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 重写onClick方法，在按钮被点击时调用
                // 创建一个Intent类型的对象，用于指定跳转的源Activity和目标Activity，这里假设目标Activity为MainActivity
                Intent intent = new Intent(getActivity(), MainActivity.class);
                // 调用当前Fragment所依附的Activity对象的startActivity方法，传入intent对象，执行跳转
                getActivity().startActivity(intent);
            }
        });



    }
}