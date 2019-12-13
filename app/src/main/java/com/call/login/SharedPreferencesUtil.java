package com.call.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/6/13.
 */
public class SharedPreferencesUtil {
    private static SharedPreferences sp;

    public static void saveBoolean(Context context, String fileName, String key, boolean value) {
        if (sp == null) {
            sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(Context context, String fileName, String key, boolean defValue) {
        if (sp == null) {
            sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key, defValue);
    }

    public static boolean getBoolean(Context context, String fileName, String key) {
        if (sp == null) {
            sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key, false);
    }

    public static void saveString(Context context, String fileName, String key, String value) {
        if (sp == null) {
            sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        }
        sp.edit().putString(key, value).commit();
    }

    public static String getString(Context context, String fileName, String key, String defValue) {
        if (sp == null) {
            sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        }
        return sp.getString(key, defValue);
    }

    public static String getString(Context context, String fileName, String key) {
        if (sp == null) {
            sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        }
        return sp.getString(key, "");
    }

    /**
     * 保存对象
     *
     * @param context 上下文
     * @param key     键
     * @param obj     要保存的对象（Serializable的子类）
     * @param <T>     泛型定义
     */
    public static <T extends Serializable> void putObject(Context context, String fileName, String key, T obj) {
        try {
            put(context,fileName, key, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取对象
     *
     * @param context 上下文
     * @param key     键
     * @param <T>     指定泛型
     * @return 泛型对象
     */
    public static <T extends Serializable> T getObject(Context context, String fileName, String key) {
        try {
            return (T) get(context,fileName, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setFloat(Context context, final String key,
                                final float value) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        settings.edit().putFloat(key, value).apply();
    }

    public static float getFloat(Context context, final String key,
                                 final float defaultValue) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        return settings.getFloat(key, defaultValue);
    }

    public static void setLong(Context context, final String key,
                               final long value) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        settings.edit().putLong(key, value).apply();
    }

    public static long getLong(Context context, final String key,
                               final long defaultValue) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        return settings.getLong(key, defaultValue);
    }

    public static void putStringList(Context context, String fileName, String key, List<String> list) {
        try {
            put(context,fileName, key, list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getStringList(Context context, String fileName, String key) {
        try {
            return (List<String>) get(context ,fileName, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 存储List集合
     *
     * @param context 上下文
     * @param key     存储的键
     * @param list    存储的集合
     */
    public static void putSerializableList(Context context, String fileName, String key, List<? extends Serializable> list) {
        try {
            put(context,fileName, key, list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取List集合
     *
     * @param context 上下文
     * @param key     键
     * @param <E>     指定泛型
     * @return List集合
     */
    public static <E extends Serializable> List<E> getSerializableList(Context context, String fileName, String key) {
        try {
            return (List<E>) get(context,fileName, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void clear(Context context, String fileName){
        sp = context.getSharedPreferences(fileName,Context.MODE_PRIVATE);
        sp.edit().clear().commit();
    }


    /**
     * 存储Map集合
     *
     * @param context 上下文
     * @param key     键
     */
    public static <K,T> Map<K,T> loadMap(Context context, String fileName, String key){
        if (sp == null){
            sp = context.getSharedPreferences(fileName,Context.MODE_PRIVATE);
        }
        String jsonStr = getString(context,fileName,key);
        return new Gson().fromJson(jsonStr,new TypeToken<Map<K,T>>(){}.getType());
    }

    public static <K,T> void saveMap(Context context, String fileName, String key, Map<K,T> map) {
        Log.e("test","save a map");
        if (map == null || map.isEmpty() || map.size() < 1){
            return;
        }
        if (sp == null) {
            sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        }
        Gson gson = new Gson();
        String json = gson.toJson(map);
        Log.e("test","json:" + json);
        saveString(context,fileName,key,json);
    }



    /**
     * 存储对象
     */
    private static void put(Context context, String fileName, String key, Object obj)
            throws IOException {
        if (obj == null) {//判断对象是否为空
            return;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        // 将对象放到OutputStream中
        // 将对象转换成byte数组，并将其进行base64编码
        String objectStr = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
        baos.close();
        oos.close();

        saveString(context,fileName, key, objectStr);
    }

    /**
     * 获取对象
     */
    private static Object get(Context context, String fileName, String key)
            throws IOException, ClassNotFoundException {
        String wordBase64 = getString(context,fileName, key);
        // 将base64格式字符串还原成byte数组
        if (TextUtils.isEmpty(wordBase64)) { //不可少，否则在下面会报java.io.StreamCorruptedException
            return null;
        }
        byte[] objBytes = Base64.decode(wordBase64.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(objBytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        // 将byte数组转换成product对象
        Object obj = ois.readObject();
        bais.close();
        ois.close();
        return obj;
    }



}
