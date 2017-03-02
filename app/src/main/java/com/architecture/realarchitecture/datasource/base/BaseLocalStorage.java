package com.architecture.realarchitecture.datasource.base;

import java.util.List;
import java.util.Map;

/**
 * Created by liushuo on 16/3/19.
 * 基本数据存储服务访问接口，提供数据存储的基本能力
 */
public interface BaseLocalStorage {

    /**
     * 将数据保存到nosql 数据库
     *
     * @param dataType 数据类型
     * @param data
     * @return
     */
    long saveData(String dataType, Map<String, Object>... data);

    List<Map<String, Object>> queryItemsByIds(final String dataType, Object... ids);

    List<Map<String, Object>> queryItemsByTypes(final String dataType);

    void deleteDatasById(final String dataType, Object... ids);

    void clearDatasOfType(String dataType);

    void clearDataBase();


}
