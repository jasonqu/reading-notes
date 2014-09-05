sid 获取

http://samsungapps.csdn.net/text.html?arcid=304625


如何检索Android设备的唯一ID
关于本文档
Android的开发者在一些特定情况下都需要知道手机中的唯一设备ID。例如，跟踪应用程序的安装，生成用于复制保护的DRM时需要使用设备的唯一ID。在本文档结尾处提供了作为参考的示例代码片段。
范围
本文提供有关如何读取各种Android设备的 ID的介绍，用以使用标识号。本文假定用户已经安装了Android以及开发应用程序必要的工具。并且，本文假定用户已了解Android的基本知识。
简介在搭载Android操作系统的设备中，已经存在好几种类型的设备标识号。先前的所有Android设备都具有电话功能，因此查找每部设备硬件唯一的IMEI，MEID，或ESN也很容易。但仅能使用Wifi的设备或音乐播放器没有电话硬件，所以没有这种类型的唯一标识号。本文阐述了如何读取不同Android设备的标识号。检索Android设备ID各种方式

以下是Android设备不同类型的识别设备ID。
· 唯一编号（IMEI，MEID，ESN，IMSI）
· MAC地址
· 序列号
· ANDROID_ID
 
唯一编号（IMEI，MEID，ESN，IMSI）
说明在以前，当Android设备均作为电话使用时，寻找唯一标识号比较简单：（）可用于找到（取决于网络技术）手机硬件唯一的IMEI，MEID，ESN和IMSI编号。
TelephonyManager.getDeviceId
IMEI，MEID，ESN，IMSI的定义如下：
•IMEI（国际移动设备识别码）唯一编号，用于识别 GSM，WCDMA手机以及一些卫星电话（移动设备识别码）全球唯一编号，用于识别CDMA移动电台设备的物理硬件，MEID出现的目的是取代ESN号段（电子序列号）（电子序列号）唯一编号，用于识别CDMA手机（国际移动用户识别码）与所有GSM和UMTS网络手机用户相关联的唯一识别编号如需要检索设备的ID，在项目中要使用以下代码：

•MEID
•ESN
•IMSI

import android.telephony.TelephonyManager;   
import android.content.Context;                                                     
 
String   imeistring = null;                                                        
String   imsistring = null;                                                                                     
{                                                                                   
    TelephonyManager    telephonyManager;                                            
                                                                                    
    telephonyManager =
         ( TelephonyManager )getSystemService( Context.TELEPHONY_SERVICE );
                    
    /*
      * getDeviceId() function Returns the unique device ID.
     * for example,the IMEI for GSM and the MEID or ESN for CDMA phones.
     */                                                              
    imeistring = telephonyManager.getDeviceId();
  
   /*
    * getSubscriberId() function Returns the unique subscriber ID,
 * for example, the IMSI for a GSM phone.
 */
   imsistring = telephonyManager.getSubscriberId();  
}

如要只读取手机的状态，则需添加READ_PHONE_STATE许可到AndroidManifest.xml文件中。
<uses-permission
  android:name="android.permission.READ_PHONE_STATE" >
</uses-permission>
缺点
•Android设备要具有电话功能
•其工作不是很可靠
•序列号
•当其工作时，该值保留了设备的重置信息（“恢复出厂设置”），从而可以消除当客户删除自己设备上的信息，并把设备转另一个人时发生的错误。
Mac地址
说明
可通过检索找到设备的Wi - Fi或蓝牙硬件的Mac地址。但是，不推荐使用Mac地址作为唯一的标识号。
缺点设备要具备Wi – Fi功能（并非所有的设备都有Wi – Fi功能）如果设备目前正在使用Wi - Fi，则不能报告Mac地址
序列号
从Android 2.3（“姜饼”）开始，通过android.os.Build.SERIAL方法序列号可被使用。没有电话功能的设备也都需要上给出唯一的设备ID;  某些手机也可以需要这样做。序列号可以用于识别MID（移动互联网设备）或PMP（便携式媒体播放器），这两种设备都没有电话功能。通过读取系统属性值“ro.serialno”的方法，可以使用序列号作为设备ID 。如检索序列号并作为设备ID使用，请参考下面的代码示例。

import java.lang.reflect.Method;                                 
 
String serialnum = null;                                                                                                                                        
try {                                                           
 Class<?> c = Class.forName("android.os.SystemProperties"); 
 Method get = c.getMethod("get", String.class, String.class );     
 serialnum = (String)(   get.invoke(c, "ro.serialno", "unknown" )  );   
}                                                                                
catch (Exception ignored)                                                        
{                              
}
 
缺点
序列号无法在所有Android设备上使用。
ANDROID_ID
 
说明
更具体地说，Settings.Secure.ANDROID_ID 是一串64位的编码（十六进制的字符串），是随机生成的设备的第一个引导，其记录着一个固定值，通过它可以知道设备的寿命（在设备恢复出厂设置后，该值可能会改变）。 ANDROID_ID也可视为作为唯一设备标识号的一个好选择。如要检索用于设备ID 的ANDROID_ID，请参阅下面的示例代码
String androidId = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
 
缺点
• 对于Android 2.2（“Froyo”）之前的设备不是100％的可靠
• 此外，在主流制造商的畅销手机中至少存在一个众所周知的错误，每一个实例都具有相同的ANDROID_ID。 
 
结论

对于绝大多数应用来说，只需识别特定的安装配置，而不需要识别物理设备。所幸是，这样做就省去了麻烦。
下面是部分使用设备ID的最佳途径：
•支持各种设备类型的另一种方法是使用getDeviceID（）API和ro.serialno的组合
•有许多值得参考的原因，来提醒开发者避免试图识别特定的设备。对于那些想做一下这方面尝试的用户， 最好的办法可能是使用ANDROID_ID，并在一些传统设备上做尝试。
示例代码
下面是用于追踪Android设置的示例代码
类: ReadDeviceID.java
package com.deviceid;
 
import java.lang.reflect.Method;
 
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
 
public class ReadDeviceID extends Activity {
 
 Button bt;
 TextView idView;
 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
 
        bt=(Button)findViewById(R.id.button1);
        idView=(TextView)findViewById(R.id.textView1);      
        bt.setOnClickListener(new OnClickListener() {  
 @Override
 public void onClick(View v) {
   String imeistring=null;   
              String imsistring=null;
  
 TelephonyManager   telephonyManager = 
( TelephonyManager)getSystemService( Context.TELEPHONY_SERVICE );
      
            /*
 * getDeviceId() function Returns the unique device ID.
 * for example,the IMEI for GSM and the MEID or ESN for CDMA phones.
 */      
 imeistring = telephonyManager.getDeviceId();
 idView.append("IMEI No : "+imeistring+"\n");    
            /*
             * getSubscriberId() function Returns the unique subscriber ID,
             * for example, the IMSI for a GSM phone.
             */                                                                                         
  imsistring = telephonyManager.getSubscriberId();               
  idView.append("IMSI No : "+imsistring+"\n");
       
            /*
  * System Property ro.serialno returns the serial number as unique number
  * Works for Android 2.3 and above        
  */
       
  String hwID = android.os.SystemProperties.get("ro.serialno", "unknown");
  idView.append( "hwID : " + hwID + "\n" );
      String serialnum = null;     
  try {        
    Class<?> c = Class.forName("android.os.SystemProperties");                          
    Method get = c.getMethod("get", String.class, String.class );                
               serialnum = (String)(   get.invoke(c, "ro.serialno", "unknown" )  );
     idView.append( "serial : " + serialnum + "\n" );
         } catch (Exception ignored) {      
           }
 String serialnum2 = null;
           try {
 Class myclass = Class.forName( "android.os.SystemProperties" );
        Method[] methods = myclass.getMethods();
        Object[] params = new Object[] { new String( "ro.serialno" ) , new String( 
              "Unknown" ) };         
         serialnum2 = (String)(methods[2].invoke( myclass, params ));         
            idView.append( "serial2 : " + serialnum2 + "\n" );
           }catch (Exception ignored)
 {       
 }  
   /*
    * Settings.Secure.ANDROID_ID returns the unique DeviceID
    * Works for Android 2.2 and above        
    */
 String androidId = Settings.Secure.getString(getContentResolver(), 
                                                    Settings.Secure.ANDROID_ID);         
            idView.append( "AndroidID : " + androidId + "\n" );   
         }
    });
       
    }
}
 
类: SystemProperties.java
 
package android.os;
 
/**
 * Gives access to the system properties store. The system properties
 * store contains a list of string key-value pairs.
 *
 * {@hide}
 */
public class SystemProperties
{
    public static final int PROP_NAME_MAX = 31;
    public static final int PROP_VALUE_MAX = 91;
    private static native String native_get(String key);
    private static native String native_get(String key, String def);
    private static native int native_get_int(String key, int def);
    private static native long native_get_long(String key, long def);
    private static native boolean native_get_boolean(String key, boolean def);
    private static native void native_set(String key, String def);
 
    /**
     * Get the value for the given key.
     * @return an empty string if the key isn't found
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static String get(String key) {
        if (key.length() > PROP_NAME_MAX) {
            throw new IllegalArgumentException("key.length > " + PROP_NAME_MAX);
        }
        return native_get(key);
    }
 
    /**
     * Get the value for the given key.
     * @return if the key isn't found, return def if it isn't null, or an empty string otherwise
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static String get(String key, String def) {
        if (key.length() > PROP_NAME_MAX) {
            throw new IllegalArgumentException("key.length > " + PROP_NAME_MAX);
        }
        return native_get(key, def);
    }
 
    /**
     * Get the value for the given key, and return as an integer.
     * @param key the key to lookup
     * @param def a default value to return
     * @return the key parsed as an integer, or def if the key isn't found or
     *         cannot be parsed
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static int getInt(String key, int def) {
        if (key.length() > PROP_NAME_MAX) {
            throw new IllegalArgumentException("key.length > " + PROP_NAME_MAX);
        }
        return native_get_int(key, def);
    }
 
    /**
     * Get the value for the given key, and return as a long.
     * @param key the key to lookup
     * @param def a default value to return
     * @return the key parsed as a long, or def if the key isn't found or
     *         cannot be parsed
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static long getLong(String key, long def) {
        if (key.length() > PROP_NAME_MAX) {
            throw new IllegalArgumentException("key.length > " + PROP_NAME_MAX);
        }
        return native_get_long(key, def);
    }
 
    /**
     * Get the value for the given key, returned as a boolean.
     * Values 'n', 'no', '0', 'false' or 'off' are considered false.
     * Values 'y', 'yes', '1', 'true' or 'on' are considered true.
     * (case insensitive).
     * If the key does not exist, or has any other value, then the default
     * result is returned.
     * @param key the key to lookup
     * @param def a default value to return
     * @return the key parsed as a boolean, or def if the key isn't found or is
     *         not able to be parsed as a boolean.
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static boolean getBoolean(String key, boolean def) {
        if (key.length() > PROP_NAME_MAX) {
            throw new IllegalArgumentException("key.length > " + PROP_NAME_MAX);
        }
        return native_get_boolean(key, def);
    }
 
    /**
     * Set the value for the given key.
     * @throws IllegalArgumentException if the key exceeds 32 characters
     * @throws IllegalArgumentException if the value exceeds 92 characters
     */
    public static void set(String key, String val) {
        if (key.length() > PROP_NAME_MAX) {
            throw new IllegalArgumentException("key.length > " + PROP_NAME_MAX);
        }
        if (val != null && val.length() > PROP_VALUE_MAX) {
            throw new IllegalArgumentException("val.length > " +
                PROP_VALUE_MAX);
        }
        native_set(key, val);
    }
}
使用"ReadDeviceID" activity 创建"com.deviceid"项目。将布局"main.xml"改写成下面的代码
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="fill_parent"
    android:layout_height="fill_parent"
    >
<TextView 
    android:layout_width="fill_parent"
    android:layout_height="wrap_content"
    android:text="@string/hello"
    />
<Button
    android:text="GetDeviceID"
    android:id="@+id/button1"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content">
</Button>
<TextView    
    android:id="@+id/textView1"   
    android:layout_width="fill_parent"
    android:layout_height="wrap_content">
</TextView>
</LinearLayout>
在"AndroidManifest.xml"文件中添加"READ_PHONE_STATE"许可，使应用程序可以登陆互联网。
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
      package="com.deviceid"
      android:versionCode="1"
      android:versionName="1.0">
    <uses-sdk android:minSdkVersion="7" />
 
    <application android:icon="@drawable/icon" android:label="@string/app_name">
        <activity android:name=".ReadDeviceID"
                  android:label="@string/app_name">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
 
    </application>
  
    <uses-permission
            android:name="android.permission.READ_PHONE_STATE" >
      </uses-permission>
   
</manifest>
 
输出结果
上方示例代码的输出结果如下图所示



Android设备获取唯一识别码-设备号-序号-UUID
统计安装数的时候，就要获取Android设备的唯一识别码。由于设备杂乱，为了保证设备号唯一性，可以采用获取UUID方式。
代码如下：
final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
 
    final String tmDevice, tmSerial, tmPhone, androidId;
    tmDevice = "" + tm.getDeviceId();
    tmSerial = "" + tm.getSimSerialNumber();
    androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
 
    UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
    String uniqueId = deviceUuid.toString();
原因在此：
所有的设备都可以返回一个 TelephonyManager.getDeviceId()
所有的GSM设备 (测试设备都装载有SIM卡) 可以返回一个TelephonyManager.getSimSerialNumber()
所有的CDMA 设备对于 getSimSerialNumber() 却返回一个空值！
所有添加有谷歌账户的设备可以返回一个 ANDROID_ID
所有的CDMA设备对于 ANDROID_ID 和 TelephonyManager.getDeviceId() 返回相同的值（只要在设置时添加了谷歌账户）
目前尚未测试的：没有SIM卡的GSM设备、没有添加谷歌账户的GSM设备、处于飞行模式的设备。


http://stackoverflow.com/questions/2785485/is-there-a-unique-android-device-id

http://android-developers.blogspot.com/2011/03/identifying-app-installations.html


