package jdk.util.map;

import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by user on 2018/4/19.
 */
public class ConCurrentHashMapStudy {
    @Test
    public void testConcurrentHashMap() {
        ConcurrentHashMap map = new ConcurrentHashMap();
        map.put("","");
        map.get("");
        HashMap hashMap = new HashMap();
    }
}
