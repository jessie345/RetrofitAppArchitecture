package com.architecture.realarchitecture.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Copyright (c) 2014 Nono_Lilith All right reserved.
 */
public class Utils {

    /**
     * @param context c
     * @return true网路可用。false不可用
     * @since 1.0.0
     */
    public static boolean checkNet(Context context) {
        try {
            //获取连接管理对象
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                //获取活动的网络连接
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return false;
    }


    @NonNull
    public static Pair<String, String[]> buildSqlInSegment(Object[] ids) {
        StringBuilder sql = new StringBuilder();
        List<String> args = new ArrayList<>();

        String item = " in (?";
        int length = ids.length;
        for (int i = 0; i < length && ids[i] != null; i++) {
            Object id = ids[i];
            sql.append(item);
            args.add(id.toString());

            item = ", ?";
        }
        sql.append(")");

        return new Pair<>(sql.toString(), args.toArray(new String[args.size()]));
    }

    public static String map2JsonString(Map<String, Object> data) {
        ObjectMapper mapper = new ObjectMapper();
        // convert map to JSON string
        try {
            String json = mapper.writeValueAsString(data);
            return json;
        } catch (Exception e) {
            e.printStackTrace();

            return "";
        }
    }

    /**
     * transformForUiLayer json string 2 map
     *
     * @param jsonStr
     * @return
     */
    public static Map<String, Object> json2Map(String jsonStr) {
        ObjectMapper mapper = new ObjectMapper();

        // convert JSON string to Map
        Map<String, Object> map = null;
        try {
            map = mapper.readValue(jsonStr, new TypeReference<Map<String, String>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();

            map = new HashMap<>();
        }

        return map;
    }


    public static Map<String, Object> cursor2Map(Cursor cursor) {
        Map<String, Object> map = new HashMap<>();
        if (cursor != null && cursor.getColumnCount() > 0) {
            int count = cursor.getColumnCount();
            for (int i = 0; i < count; i++) {
                int type = cursor.getType(i);

                Object value = null;
                switch (type) {
                    case Cursor.FIELD_TYPE_STRING:
                        value = cursor.getString(i);
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        value = cursor.getInt(i);
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        value = cursor.getFloat(i);
                        break;
                    case Cursor.FIELD_TYPE_NULL:
                        LogUtils.d("未知的参数类型,type:" + type);
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        LogUtils.d("未知的参数类型,type:" + type);
                        break;
                }
                if (value != null) {
                    String name = cursor.getColumnName(i);
                    map.put(name, value);
                }
            }
        }
        return map;
    }

    public static ContentValues map2ContentValues(Map<String, Object> map) {
        ContentValues values = new ContentValues();
        if (map != null && map.size() > 0) {
            Set<Map.Entry<String, Object>> set = map.entrySet();
            Iterator<Map.Entry<String, Object>> itr = set.iterator();
            while (itr.hasNext()) {
                Map.Entry<String, Object> entry = itr.next();
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value != null) {
                    if (value instanceof String) {
                        values.put(key, (String) value);
                    } else if (value instanceof Integer) {
                        values.put(key, (Integer) value);
                    } else if (value instanceof Float) {
                        values.put(key, (Float) value);
                    } else {
                        LogUtils.d("未知的参数类型,key:" + key + ",value:" + value);

                    }
                } else {
                    LogUtils.d("未知的参数类型,key:" + key + ",value:" + value);
                }
            }
        }
        return values;
    }


    /**
     * 获取Json中的指定字符串，默认为空字符串
     *
     * @param key
     * @param obj
     * @return
     */
    public static String getStringFromJsonObj(String key, JSONObject obj) {
        String str = "";
        if (obj != null) {
            try {
                str = obj.getString(key);
            } catch (Exception e) {
                LogUtils.e(e);
            }
        }
        return str;
    }


    /**
     * 获取Map中的指定字符串，默认为空字符串
     *
     * @param key
     * @param map
     * @return
     */
    public static String getStringFromMap(String key, Map<String, Object> map) {
        String str = "";
        if (map != null && map.containsKey(key)) {
            try {
                str = (String) map.get(key);
            } catch (Exception e) {
                LogUtils.e(e);
            }
        }
        return str;
    }


}
