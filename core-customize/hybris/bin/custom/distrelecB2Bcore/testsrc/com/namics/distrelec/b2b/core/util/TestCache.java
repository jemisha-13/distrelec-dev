package com.namics.distrelec.b2b.core.util;

import org.springframework.cache.Cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class TestCache implements Cache {

    public static class TestValueWrapper implements ValueWrapper{

        private final Object value;

        public TestValueWrapper(Object value){
            this.value = value;
        }

        @Override
        public Object get() {
            return value;
        }
    }

    private Map<Object, Object> inMemoryCacheMap = new HashMap<>();

    @Override
    public String getName() {
        return "TestCache";
    }

    @Override
    public Object getNativeCache() {
        return null;
    }

    @Override
    public ValueWrapper get(Object key) {
        return new TestValueWrapper(inMemoryCacheMap.get(key));
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return (T)inMemoryCacheMap.get(key);
    }

    @Override
    public <T> T get(Object o, Callable<T> callable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void put(Object key, Object value) {
        inMemoryCacheMap.put(key, value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        if(inMemoryCacheMap.containsKey(key)){
            return new TestValueWrapper(inMemoryCacheMap.get(key));
        }else{
            inMemoryCacheMap.put(key, value);
            return new TestValueWrapper(value);
        }
    }

    @Override
    public void evict(Object key) {
        inMemoryCacheMap.remove(key);
    }

    @Override
    public void clear() {
        inMemoryCacheMap.clear();
    }
}
