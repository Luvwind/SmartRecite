package net.kuludu.smartrecite;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;

// 定义一个接口，名为ScreenStateListener，用于监听屏幕的状态变化
interface ScreenStateListener {
    void onScreenOn(); // 用于处理屏幕亮起的事件

    void onScreenOff(); //用于处理屏幕关闭的事件

    void onUnLock(); // 用于处理屏幕解锁的事件
}

// 定义一个类，名为ScreenListener，用于注册和注销屏幕状态的广播接收器
public class ScreenListener {
    private Context context; // 声明一个私有的上下文对象，用于获取系统服务和注册广播接收器
    private ScreenBroadcastReceiver mScreenReceiver; // 声明一个私有的屏幕广播接收器对象，用于接收和处理屏幕状态的广播
    private ScreenStateListener mScreenStateListener; // 声明一个私有的屏幕状态监听器对象，用于回调屏幕状态变化的方法

    public ScreenListener(Context context) { // 用于创建ScreenListener对象，并传入一个上下文参数
        this.context = context; // 把传入的上下文参数赋值给当前对象的context变量
        mScreenReceiver = new ScreenBroadcastReceiver(); // 创建一个屏幕广播接收器对象，并赋值给当前对象的mScreenReceiver变量
    }

    /* Listener begin Listening */
    public void begin(ScreenStateListener listener) { // 用于开始监听屏幕状态，并传入一个屏幕状态监听器参数
        mScreenStateListener = listener; // 把传入的监听器参数赋值给当前对象的mScreenStateListener变量
        registerListener(); // 调用自定义的方法，注册屏幕状态的广播接收器
        getScreenState(); // 调用自定义的方法，获取当前屏幕的状态
    }

    /* Require screen's state */
    private void getScreenState() { // 定义一个私有的方法，用于获取当前屏幕的状态，并根据状态调用相应的监听器方法
        PowerManager manager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        // 获取系统服务中的电源管理器对象，并赋值给manager变量，用于判断屏幕是否亮起
        if (manager.isScreenOn()) { // 如果电源管理器对象判断屏幕是亮起的
            if (mScreenStateListener != null) { // 如果当前对象的mScreenStateListener变量不为空
                mScreenStateListener.onScreenOn(); // 调用mScreenStateListener变量的onScreenOn方法，处理屏幕亮起的事件
            }
        } else { // 如果电源管理器对象判断屏幕是关闭的
            if (mScreenStateListener != null) { // 如果当前对象的mScreenStateListener变量不为空
                mScreenStateListener.onScreenOff(); // 调用mScreenStateListener变量的onScreenOff方法，处理屏幕关闭的事件
            }
        }
    }

    /* Ongoing Listening  */
    public void registerListener() { // 用于注册屏幕状态的广播接收器，并设置要接收的广播类型
        IntentFilter filter = new IntentFilter(); // 创建一个意图过滤器对象，并赋值给filter变量，用于指定要接收哪些类型的广播
        filter.addAction(Intent.ACTION_SCREEN_ON); // 调用意图过滤器对象的addAction方法，传入Intent.ACTION_SCREEN_ON作为参数，表示要接收屏幕亮起的广播
        filter.addAction(Intent.ACTION_SCREEN_OFF); // 调用意图过滤器对象的addAction方法，传入Intent.ACTION_SCREEN_OFF作为参数，表示要接收屏幕关闭的广播
        filter.addAction(Intent.ACTION_USER_PRESENT); // 调用意图过滤器对象的addAction方法，传入Intent.ACTION_USER_PRESENT作为参数，表示要接收屏幕解锁的广播
        context.registerReceiver(mScreenReceiver, filter); // 调用上下文对象的registerReceiver方法，传入当前对象的mScreenReceiver变量和filter变量作为参数，注册屏幕状态的广播接收器
    }

    /* Destroy listener */
    public void unregisterListener() { // 定义一个公共的方法，用于注销屏幕状态的广播接收器
        context.unregisterReceiver(mScreenReceiver); // 调用上下文对象的unregisterReceiver方法，传入当前对象的mScreenReceiver变量作为参数，注销屏幕状态的广播接收器
    }

    class ScreenBroadcastReceiver extends BroadcastReceiver {
        // 定义一个内部类，继承自BroadcastReceiver类，用于接收和处理屏幕状态的广播
        /*
         * The BroadcastReceiver is for listening Screen's state
         * if Received message is SCREEN_ON,the get into MainActivity
         * else if Received message is SCREEN_OFF,change flag to true
         * else if Received message is UNLOCK,restore flag is false;
         */
        private String action = null; // 声明字符串变量，用于存储接收到的广播的类型


        @Override
        public void onReceive(Context context, Intent intent) { // 重写广播接收器类的onReceive方法，当接收到广播时会调用这个方法
            action = intent.getAction(); // 获取意图对象中携带的广播类型，并赋值给action变量
            if (Intent.ACTION_SCREEN_ON.equals(action)) { // 如果广播类型是屏幕亮起
            //当系统检测到屏幕亮起时，会发送一个包含这个类型的广播，通知其他应用程序屏幕的状态变化。如果一个广播接收器注册了这个类型的广播，就可以在onReceive方法中处理屏幕亮起的事件。
                mScreenStateListener.onScreenOn(); // 调用当前对象的mScreenStateListener变量的onScreenOn方法，处理屏幕亮起的事件
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 如果广播类型是屏幕关闭
                mScreenStateListener.onScreenOff(); // 调用当前对象的mScreenStateListener变量的onScreenOff方法，处理屏幕关闭的事件
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 如果广播类型是屏幕解锁
                mScreenStateListener.onUnLock(); // 调用当前对象的mScreenStateListener变量的onUnLock方法，处理屏幕解锁的事件
            }
        }
    }

}