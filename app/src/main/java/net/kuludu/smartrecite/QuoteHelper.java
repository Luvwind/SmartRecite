package net.kuludu.smartrecite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.Random;

// 定义一个名为QuoteHelper的类，用于操作名言警句数据库的数据
public class QuoteHelper {
    private File localQuoteFile; // 声明一个私有的文件对象，用于存储名言警句数据库文件的路径

    public QuoteHelper(Context context) { // 定义一个公共的构造方法，用于创建QuoteHelper对象，并传入一个上下文参数
        String localQuoteFilePath = context.getApplicationContext().getFilesDir() + "/quote.db"; // 获取应用程序的文件目录，并拼接上名言警句数据库文件的名称，赋值给localQuoteFilePath变量
        localQuoteFile = new File(localQuoteFilePath); // 创建一个文件对象，并传入localQuoteFilePath变量作为文件路径，赋值给localQuoteFile变量
    }

    public boolean isQuoteExists() { // 定义一个公共的方法，用于判断名言警句数据库文件是否存在
        return localQuoteFile.exists(); // 调用文件对象的exists方法，返回一个布尔值，表示文件是否存在
    }

    private SQLiteDatabase openDatabase() { // 定义一个私有的方法，用于打开名言警句数据库文件，并返回一个SQLiteDatabase对象
        if (isQuoteExists()) { // 如果名言警句数据库文件存在
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(localQuoteFile, null); // 调用SQLiteDatabase类的静态方法openOrCreateDatabase，传入文件对象和null作为参数，创建或打开一个SQLiteDatabase对象，并赋值给db变量

            return db; // 返回db变量
        } else { // 如果名言警句数据库文件不存在
            Log.e("QuoteHelper", "Database not found!"); // 调用Log类的静态方法e，传入"QuoteHelper"和"Database not found!"作为参数，打印一条错误日志，表示数据库文件没有找到
        }

        return null; // 返回null值
    }

    public Quote getRandQuote() { // 用于从名言警句数据库中随机获取一条名言警句，并返回一个Quote对象
        SQLiteDatabase db = openDatabase(); // 调用自定义的方法，打开名言警句数据库，并赋值给db变量
        assert db != null; // 如果为空则抛出异常

        Random random = new Random(); // 创建一个随机数生成器对象

        Cursor cursor = db.query("quote", new String[]{"COUNT(*)"}, null, null, null, null, null);
        // 调用SQLiteDatabase对象的query方法，传入"quote"作为表名，new String[]{"COUNT(*)"}作为要查询的列名（表示统计表中有多少条数据），其他参数都为null（表示不需要条件、分组、排序等），返回一个Cursor对象，并赋值给cursor变量
        cursor.moveToFirst(); // 调用Cursor对象的moveToFirst方法，将游标移动到第一行数据（也是唯一一行数据）
        int index = random.nextInt(cursor.getInt(0));
        // 调用随机数生成器对象的nextInt方法，传入cursor.getInt(0)作为参数（表示表中有多少条数据），生成一个0到表中数据总数之间（不包含总数）的随机整数，并赋值给index变量
        cursor = db.query("quote", null, "`index`=?", new String[]{Integer.toString(index)}, null, null, null); // 调用SQLiteDatabase对象的query方法，传入"quote"作为表名，null作为要查询的列名（表示查询所有列），"`index`=?"作为查询条件（表示根据id查找），new String[]{Integer.toString(index)}作为查询条件的参数（表示id等于index变量），其他参数都为null（表示不需要分组、排序等），返回一个Cursor对象，并赋值给cursor变量
        cursor.moveToFirst(); // 调用Cursor对象的moveToFirst方法，将游标移动到第一行数据（也是唯一一行数据）
        Quote quote = new Quote(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
        // 创建一个Quote对象，并传入cursor.getInt(0)作为id，cursor.getString(1)作为英文内容，cursor.getString(2)作为中文内容，赋值给quote变量
        cursor.close(); // 关闭游标，释放资源
        db.close();

        return quote; // 返回quote变量
    }
}

