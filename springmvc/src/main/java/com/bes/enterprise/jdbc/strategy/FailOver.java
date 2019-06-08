package com.bes.enterprise.jdbc.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Created by JasonFitch on 6/5/2019.
 */
public class FailOver {

    public static String getTargetDataSource() {
        return "demo";
    }

    public static Object getTargetDataSource(Map<Object, Object> targetDataSources) {
        Set<Object> entries = targetDataSources.keySet();
        List<Object> lists = new ArrayList<>(entries);

        int size = lists.size();
        int selected = new Random(System.currentTimeMillis()).nextInt(size);
        return lists.get(selected);
    }

}
