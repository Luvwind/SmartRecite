package net.kuludu.smartrecite;


import android.content.ContentValues; // 导入类，用于存储键值对
import android.content.Context; // 导入类，用于获取应用上下文
import android.content.SharedPreferences; // 导入类，用于获取用户偏好设置
import android.database.Cursor; // 导入类，用于操作数据库结果集
import android.database.sqlite.SQLiteDatabase; // 导入类，用于操作数据库
import android.util.Log; // 导入类，用于打印日志
import java.io.File; // 导入类，用于操作文件
import java.util.ArrayList; // 导入类，用于存储列表
import java.util.HashSet; // 导入类，用于存储集合
import java.util.List; // 导入类，用于存储列表
import java.util.Random; // 导入类，用于生成随机数
import java.util.Set; // 导入类，用于存储集合

public class WordHelper {
    private File localDatabaseFile; // 定义一个私有的文件对象，用于存储本地数据库文件的引用
    private SharedPreferences sharedPreferences; // 存储用户的偏好设置

    public WordHelper(Context context) { // 定义一个公共的构造函数，接收一个上下文参数
        sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE); // 从上下文中获取名为config的偏好设置对象
        String localWordFilePath = context.getApplicationContext().getFilesDir() + "/word.db"; // 拼接本地数据库文件的路径
        localDatabaseFile = new File(localWordFilePath); // 创建一个文件对象，指向本地数据库文件
    }

    public boolean isDatabaseExists() { // 定义一个公共的方法，判断本地数据库文件是否存在，返回一个布尔值
        return localDatabaseFile.exists(); // 调用文件对象的exists方法，判断文件是否存在
    }

    private SQLiteDatabase openDatabase() { // 定义一个私有的方法，打开本地数据库文件，返回一个SQLiteDatabase对象
        if (isDatabaseExists()) { // 如果数据库文件存在
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(localDatabaseFile, null); // 调用SQLiteDatabase的静态方法，打开或创建数据库文件，并返回一个SQLiteDatabase对象
            return db; // 返回SQLiteDatabase对象
        } else { // 如果数据库文件不存在
            Log.e("WordHelper", "Database not found!"); // 打印一条错误日志，提示数据库未找到
        }
        return null; // 返回null值
    }

    public Integer getWordsCount() { // 定义一个公共的方法，获取当前单词等级的单词总数，返回一个整数值
        SQLiteDatabase db = openDatabase(); // 调用openDatabase方法，打开本地数据库文件，并赋值给一个SQLiteDatabase变量db
        assert db != null; // 断言db不为空，如果为空则抛出异常
        String level = sharedPreferences.getString("level", "cet_4"); // 从偏好设置对象中获取键为level的字符串值，并赋值给一个字符串变量level。如果没有该键，则默认为cet_4（四级单词）
        Cursor cursor = db.query(level, new String[]{"COUNT(*)"}, null, null, null, null, null); // 调用db对象的query方法，查询当前单词等级表中的所有行数，并返回一个Cursor对象（结果集）
        cursor.moveToFirst(); // 调用Cursor对象的moveToFirst方法，将游标移动到第一行（结果集只有一行）
        Integer wordCount = cursor.getInt(0); // 调用Cursor对象的getInt方法，获取第一列（索引为0）的整数值，并赋值给一个整数变量wordCount（行数

        cursor.close(); // 调用Cursor对象的close方法，关闭结果集，释放资源
        db.close(); // 调用db对象的close方法，关闭数据库，释放资源
        return wordCount; // 返回wordCount变量（单词总数）
    }


    public List<Word> getWords() { // 定义一个公共的方法，获取当前单词等级的所有单词，返回一个Word对象的列表
        List<Word> result = new ArrayList<>(); // 创建一个空的ArrayList对象，并赋值给一个List变量result（结果列表）
        SQLiteDatabase db = openDatabase(); // 调用openDatabase方法，打开本地数据库文件，并赋值给一个SQLiteDatabase变量db
        assert db != null; // 断言db不为空，如果为空则抛出异常
        String level = sharedPreferences.getString("level", "cet_4"); // 从偏好设置对象中获取键为level的字符串值，并赋值给一个字符串变量level。如果没有该键，则默认为cet_4（四级单词）
        Cursor cursor = db.query(level, null, null, null, null, null, null); // 调用db对象的query方法，查询当前单词等级表中的所有字段和所有行，并返回一个Cursor对象（结果集）
        if (cursor.getCount() > 0) { // 如果结果集不为空（有数据）
            cursor.moveToFirst(); // 调用Cursor对象的moveToFirst方法，将游标移动到第一行
            Word word = new Word(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4)); // 根据Cursor对象的各个字段值，创建一个Word对象，并赋值给一个Word变量word
            result.add(word); // 将word变量添加到result列表中
        }
        while (cursor.moveToNext()) { // 循环遍历结果集，直到游标移动到最后一行之后
            Word word = new Word(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4)); // 根据Cursor对象的各个字段值，创建一个Word对象，并赋值给一个Word变量word
            result.add(word); // 将word变量添加到result列表中
        }
        cursor.close(); // 调用Cursor对象的close方法，关闭结果集，释放资源
        db.close(); // 调用db对象的close方法，关闭数据库，释放资源
        return result; // 返回result列表（所有单词）
    }

    public List<Word> getRandXWords(int requireNum) { // 定义一个公共的方法，随机获取指定数量的单词，返回一个Word对象的列表
        List<Word> result = new ArrayList<>(); // 创建一个空的ArrayList对象，并赋值给一个List变量result（结果列表）
        SQLiteDatabase db = openDatabase(); // 调用openDatabase方法，打开本地数据库文件，并赋值给一个SQLiteDatabase变量db
        assert db != null; // 断言db不为空，如果为空则抛出异常
        Cursor cursor; // 定义一个Cursor变量cursor（结果集）

        String level = sharedPreferences.getString("level", "cet_4"); // 从偏好设置对象中获取键为level的字符串值，并赋值给一个字符串变量level。如果没有该键，则默认为cet_4（四级单词）
        int totalWordCount = getWordsCount(); // 调用getWordsCount方法，获取当前单词等级的单词总数，并赋值给一个整数变量totalWordCount
        boolean isWordCountExceed = false; // 定义一个布尔变量isWordCountExceed，并赋值为false（表示指定数量是否超过单词总数）
        if (totalWordCount < requireNum) { // 如果单词总数小于指定数量
            isWordCountExceed = true;//CountExceed变量赋值为true（表示指定数量超过单词总数）

            Log.w("WordHelper", "Word count exceed!");// 打印一条警告日志，提示单词数量超过
        }

        Set<Integer> wordIndex = new HashSet<>(); // 创建一个空的HashSet对象，并赋值给一个Set变量wordIndex（用于存储随机生成的索引）
        while (!isWordCountExceed && wordIndex.size() < requireNum) { // 循环生成随机索引，直到指定数量超过单词总数或者索引集合的大小等于指定数量
            Random random = new Random(); // 创建一个Random对象，并赋值给一个Random变量random（用于生成随机数）
            wordIndex.add(random.nextInt(totalWordCount)); // 调用random对象的nextInt方法，生成一个范围在0到单词总数之间的随机整数，并添加到wordIndex集合中
        }
        for (Integer index : wordIndex) { // 遍历wordIndex集合中的每个索引
            cursor = db.query(level, null, "`index`=?", new String[]{index.toString()}, null, null, null, null); // 调用db对象的query方法，根据索引查询当前单词等级表中的对应单词，并返回一个Cursor对象（结果集）
            cursor.moveToFirst(); // 调用Cursor对象的moveToFirst方法，将游标移动到第一行（结果集只有一行）
            Word word = new Word(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4)); // 根据Cursor对象的各个字段值，创建一个Word对象，并赋值给一个Word变量word
            cursor.close(); // 调用Cursor对象的close方法，关闭结果集，释放资源
            result.add(word); // 将word变量添加到result列表中
        }
        db.close(); // 调用db对象的close方法，关闭数据库，释放资源
        return result; // 返回result列表（随机单词）
    }

    public Word getXWord(Integer index) { // 定义一个公共的方法，根据索引获取指定的单词，返回一个Word对象
        SQLiteDatabase db = openDatabase(); // 调用openDatabase方法，打开本地数据库文件，并赋值给一个SQLiteDatabase变量db
        assert db != null; // 判断db不为空，如果为空则抛出异常
        String level = sharedPreferences.getString("level", "cet_4"); // 从偏好设置对象中获取键为level的字符串值，并赋值给一个字符串变量level。如果没有该键，则默认为cet_4（四级单词）
        Cursor cursor = db.query(level, null, "`index`=?", new String[]{index.toString()}, null, null, null); // 调用db对象的query方法，根据索引查询当前单词等级表中的对应单词，并返回一个Cursor对象（结果集）

        if (cursor.getCount() > 0) { // 如果结果集不为空（有数据）
            cursor.moveToFirst(); // 调用Cursor对象的moveToFirst方法，将游标移动到第一行（结果集只有一行）
            Word word = new Word(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4)); // 根据Cursor对象的各个字段值，创建一个Word对象，并赋值给一个Word变量word
            cursor.close(); // 调用Cursor对象的close方法，关闭结果集，释放资源
            db.close(); // 调用db对象的close方法，关闭数据库，释放资源
            return word; // 返回word变量（指定单词）

        }

        cursor.close();// 调用Cursor对象的close方法，关闭结果集，释放资源
        db.close();

        return null;// 返回null值（表示索引不存在）
    }

    public int setLastReview(Integer lastReview, Integer id) { // 定义一个公共的方法，根据索引更新单词的最后复习时间，返回一个整数值表示更新状态
        SQLiteDatabase db = openDatabase(); // 调用openDatabase方法，打开本地数据库文件，并赋值给一个SQLiteDatabase变量db

        assert db != null; // 断言db不为空，如果为空则抛出异常
        String level = sharedPreferences.getString("level", "cet_4"); // 从偏好设置对象中获取键为level的字符串值，并赋值给一个字符串变量level。如果没有该键，则默认为cet_4（四级单词）
        ContentValues cv = new ContentValues(); // 创建一个ContentValues对象，并赋值给一个ContentValues变量cv（用于存储键值对）
        cv.put("last_review", lastReview); // 调用cv对象的put方法，将键为last_review，值为lastReview的键值对添加到cv对象中
        int state = db.update(level, cv, "`index`=?", new String[]{id.toString()}); // 调用db对象的update方法，根据索引更新当前单词等级表中的最后复习时间字段，并返回一个整数值表示更新状态
        db.close(); // 调用db对象的close方法，关闭数据库，释放资源

        return state; // 返回state变量（更新状态）
    }

    public List<Word> getLearnedWord() { // 定义一个公共的方法，获取已经复习过的单词，返回一个Word对象的列表
        List<Word> result = new ArrayList<>(); // 创建一个空的ArrayList对象，并赋值给一个List变量result（结果列表）
        SQLiteDatabase db = openDatabase(); // 调用openDatabase方法，打开本地数据库文件，并赋值给一个SQLiteDatabase变量db
        assert db != null; // 断言db不为空，如果为空则抛出异常
        String level = sharedPreferences.getString("level", "cet_4"); // 从偏好设置对象中获取键为level的字符串值，并赋值给一个字符串变量level。如果没有该键，则默认为cet_4（四级单词）
        Cursor cursor = db.query(level, null, "last_review IS NOT NULL", null, "last_review", null, null, null); // 调用db对象的query方法，查询当前单词等级表中最后复习时间不为空的所有字段和所有行，并按照最后复习时间升序排列，并返回一个Cursor对象（结果集）
        if (cursor.getCount() > 0) { // 如果结果集不为空（有数据）
            cursor.moveToFirst(); // 调用Cursor对象的moveToFirst方法，将游标移动到第一行
            Word word = new Word(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4)); // 根据Cursor对象的各个字段值，创建一个Word对象，并赋值给一个Word变量word
            result.add(word); // 将word变量添加到result列表中
        }
        while (cursor.moveToNext()) { // 循环遍历结果集，直到游标移动到最后一行之后
            Word word = new Word(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4)); // 根据Cursor对象的各个字段值，创建一个Word对象，并赋值给一个Word变量word
            result.add(word); // 将word变量添加到result列表中
        }

        cursor.close(); // 调用Cursor对象的close方法，关闭结果集，释放资源
        db.close(); // 调用db对象的close方法，关闭数据库，释放资源

        return result; // 返回result列表（已复习单词）
    }
}