package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2018/4/30.
 */
public class TokenCache {

    private  static Logger logger = LoggerFactory.getLogger(TokenCache.class);

    public static  final  String TOKEN_PREFIX = "token_";

    /**
     * guava中的本地缓存,开始构建本地缓存，是一种调用链的形式(及.后再.),1000是缓存的初始化容量,当超过maxsize的值，guavadecache就会使用LRU()算法
     * LRU就是最小使用算法来移除缓存项,expireAfterAccess表示有效期，对应的单位是第二个参数,build选择CacheLoader，这是一个抽象类，在这里面写一个
     * 匿名的实现，或者单独写一个实现类，然后把类在这里实例化
     */
    private static LoadingCache<String,String> localCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS)
            .build(new CacheLoader<String, String>() {
                //默认的数据加载实现，当调用get取值的时候，如果key没有对应的值，就调用这个方法进行加载,
                @Override
                public String load(String s) throws Exception {
                    //由于token的key如果为null，然后null.equal()会报空指针异常,所以返回一个字符串的"null"
                    return "null";
                }
            });

    public static void setKey(String key,String value){
        localCache.put(key, value);
    }

    public static String getKey(String key){
        String value = null;
        try {
            value = localCache.get(key);
            if("null".equals(value)){
                return null;
            }
            return value;
        }catch (Exception e){
            logger.error("localCache get error",e);
        }
        return null;
    }
}
