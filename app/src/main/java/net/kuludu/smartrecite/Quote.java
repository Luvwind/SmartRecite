package net.kuludu.smartrecite;

// 定义一个名为Quote的类，用于封装名言警句的数据
public class Quote {
    private Integer index; // 声明一个私有的整型变量，用于存储名言警句的id
    private String english; // 声明一个私有的字符串变量，用于存储名言警句的英文内容
    private String chinese; // 声明一个私有的字符串变量，用于存储名言警句的中文内容

    public Quote(Integer index, String english, String chinese) { // 定义一个公共的构造方法，用于创建Quote对象，并传入三个参数
        this.index = index; // 把传入的index参数赋值给当前对象的index变量
        this.english = english; // 把传入的english参数赋值给当前对象的english变量
        this.chinese = chinese; // 把传入的chinese参数赋值给当前对象的chinese变量
    }

    public Integer getIndex() { // 定义一个公共的方法，用于获取当前对象的index变量的值
        return index; // 返回index变量的值
    }

    public String getEnglish() { // 定义一个公共的方法，用于获取当前对象的english变量的值
        return english; // 返回english变量的值
    }

    public String getChinese() { // 定义一个公共的方法，用于获取当前对象的chinese变量的值
        return chinese; // 返回chinese变量的值
    }
}

