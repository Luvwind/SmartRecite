// 这个文件是一个Java类，表示一个单词对象。它在整个项目中起到的作用和功能是：
//
//封装单词的属性，如索引、英文、音标、中文和最后复习时间
package net.kuludu.smartrecite; // 这是包名，表示这个文件属于net.kuludu.smartrecite这个包

public class Word { // 这是声明一个名为Word的公开类，表示一个单词对象
    private Integer index; // 这是声明一个私有的Integer类型的变量，表示单词的索引
    private String word; // 这是声明一个私有的String类型的变量，表示单词的英文
    private String soundmark; // 这是声明一个私有的String类型的变量，表示单词的音标
    private String chinese; // 这是声明一个私有的String类型的变量，表示单词的中文
    private Integer last_review; // 这是声明一个私有的Integer类型的变量，表示单词的最后复习时间

    public Word(Integer index, String word, String soundmark, String chinese, Integer last_review) { // 这是声明一个公开的构造函数，接收五个参数：一个Integer类型的对象（表示单词的索引），一个String类型的对象（表示单词的英文），一个String类型的对象（表示单词的音标），一个String类型的对象（表示单词的中文），一个Integer类型的对象（表示单词的最后复习时间）
        this.index = index; // 将index参数赋值给当前对象中的index变量
        this.word = word; // 将word参数赋值给当前对象中的word变量
        this.soundmark = soundmark; // 将soundmark参数赋值给当前对象中的soundmark变量
        this.chinese = chinese; // 将chinese参数赋值给当前对象中的chinese变量
        this.last_review = last_review; // 将last_review参数赋值给当前对象中的last_review变量
    }

    public Integer getIndex() { // 这是声明一个公开的Integer类型的方法，用于获取当前对象中index变量的值
        return index; // 返回index变量的值
    }

    public String getWord() { // 这是声明一个公开的String类型的方法，用于获取当前对象中word变量的值
        return word; // 返回word变量的值
    }

    public String getSoundmark() { // 这是声明一个公开的String类型的方法，用于获取当前对象中soundmark变量的值
        return soundmark; // 返回soundmark变量的值
    }

    public String getChinese() {// 这是声明一个公开的String类型的方法，用于获取当前对象中chinese变量的值
        return chinese; // 返回chinese变量的值
    }

    public Integer getLast_review() { // 这是声明一个公开的Integer类型的方法，用于获取当前对象中last_review变量的值
        return last_review; // 返回last_review变量的值
    }

    public void setLast_review(Integer last_review) { // 这是声明一个公开的void类型的方法，接收一个Integer类型的参数，表示单词的最后复习时间，用于设置当前对象中last_review变量的值
        this.last_review = last_review; // 将last_review参数赋值给当前对象中的last_review变量
    }
}