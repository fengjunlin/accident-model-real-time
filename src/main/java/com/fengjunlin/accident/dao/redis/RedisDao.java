package com.fengjunlin.accident.dao.redis;

import com.fengjunlin.accident.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description redis注入给spring，只会初始化一个对象，由spring管理
 */
@Component("RedisDao")
public class RedisDao {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 删除hash数据
     *
     * @param key
     * @param hashField
     * @return
     * @throws Exception
     */
    public boolean hDel(String key, String... hashField) throws Exception {
        if (StringUtils.isEmpty(key)) {
            return false;
        }
        redisTemplate.opsForHash().delete(key, hashField);
        return true;
    }

    /**
     * 增加hash数据
     *
     * @param key
     * @param hashField
     * @param hashValue
     * @return
     * @throws Exception
     */
    public boolean hset(String key, String hashField, String hashValue) throws Exception {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(hashField) || StringUtils.isEmpty(hashValue)) {
            return false;
        }
        redisTemplate.opsForHash().put(key, hashField, hashValue);
        return true;
    }

    /**
     * 批量增加hash数据
     *
     * @param key
     * @param hash
     * @return
     * @throws Exception
     */
    public boolean hmSet(String key, Map<String, String> hash) throws Exception {
        if (StringUtils.isEmpty(key) || hash == null || hash.size() < 1) {
            return false;
        }
        redisTemplate.opsForHash().putAll(key, hash);
        return true;
    }

    /**
     * 获取hash数据
     *
     * @param key
     * @param hashfield
     * @return
     * @throws Exception
     */
    public String hGet(String key, String hashfield) throws Exception {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(hashfield)) {
            return null;
        }
        Object o = redisTemplate.opsForHash().get(key, hashfield);
        return null == o ? null : String.valueOf(o);
    }

    /**
     * 删除key-value数据
     *
     * @param key 有效时间 单位秒
     */
    public boolean del(String key) throws Exception {
        if (StringUtils.isEmpty(key)) {
            return true;
        }
        redisTemplate.delete(key);
        return true;
    }

    /**
     * 查询key-value数据
     *
     * @param key 有效时间 单位秒
     */
    public String get(String key) throws Exception {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        Object o = redisTemplate.opsForValue().get(key);
        return null == o ? null : String.valueOf(o);
    }


    /**
     * 批量获取一个key下的多个field的hash数据
     *
     * @param key
     * @param field
     * @return
     * @throws Exception
     */
    public List<String> hmGet(String key, String... field) throws Exception {
        if (StringUtils.isEmpty(key) || null == field || field.length <= 0) {
            return null;
        }
        return redisTemplate.opsForHash().multiGet(key, Arrays.asList(field));
    }

    /**
     * 获取redis中指定key对应的hash结构中所有的数据,map中的key是filed，map中的value是值
     *
     * @param
     * @return
     */
    public Map<String, String> hGetAll(String key) throws Exception {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 向集合添加一个或多个成员，返回添加成功的数量
     *
     * @param key
     * @param members
     * @return Long
     */
    public Long sAdd(String key, String... members) throws Exception {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        return redisTemplate.opsForSet().add(key, members);
    }

    /**
     * 获取某个hash中正则匹配field的值
     *
     * @param key
     * @param match
     * @return
     * @throws Exception
     */
    public List<Map.Entry<String, String>> hScan(String key, String match) throws Exception {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        if (StringUtils.isEmpty(match)) {
            throw new IllegalArgumentException("param error");
        }
        List<Map.Entry<String, String>> list = new ArrayList<>();
        while (true) {
            ScanOptions build = ScanOptions.scanOptions().match(match).count(5000).build();
            Cursor scan = redisTemplate.opsForHash().scan(key, build);
            long position = scan.getPosition();
            while (scan.hasNext()) {
                Object next = scan.next();
                list.add((Map.Entry<String, String>) next);
            }
            if (0 == position) {
                break;
            }
        }
        return list;
    }

    /**
     * 查询某个匹配内容的集合
     *
     * @param key
     * @param match
     * @return
     * @throws Exception
     */
    public List<String> sScan(String key, String match) throws Exception {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        if (StringUtils.isEmpty(match)) {
            throw new IllegalArgumentException("param error");
        }
        List<String> list = new ArrayList<>();
        while (true) {
            ScanOptions build = ScanOptions.scanOptions().match(match).count(5000).build();
            Cursor scan = redisTemplate.opsForSet().scan(key, build);
            long position = scan.getPosition();
            while (scan.hasNext()) {
                list.add(String.valueOf(scan.next()));
            }
            if (0 == position) {
                break;
            }
        }
        return list;
    }

    /**
     * 存储string
     *
     * @param key
     * @param value
     * @return
     * @throws Exception
     */
    public String set(String key, String value) throws Exception {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
            return null;
        }
        redisTemplate.opsForValue().set(key, value);
        return value;
    }

    /**
     * 存储zset元素
     *
     * @param key   zset的key
     * @param score 分数，排序一句
     * @param value zset的value
     * @return
     * @throws Exception
     */
    public void zAdd(String key, double score, String value) throws Exception {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
            return;
        }
        redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 删除开始到结束位置的数据，包前也包后
     *
     * @param key   zset的key
     * @param start 删除的开始的位置
     * @param end   删除的结束的位置
     * @return
     * @throws Exception
     */
    public void zRemRangeByRank(String key, int start, int end) throws Exception {
        if (StringUtils.isEmpty(key)) {
            return;
        }
        redisTemplate.opsForZSet().removeRange(key, start, end);
    }

    /**
     * 修改key名称
     *
     * @param oldKey 旧key
     * @param newKey 新key
     * @return
     * @throws Exception
     */
    public void rename(String oldKey, String newKey) throws Exception {
        if (StringUtils.isEmpty(oldKey) || StringUtils.isEmpty(newKey)) {
            return;
        }
        redisTemplate.rename(oldKey, newKey);
    }

    /**
     * 获取两个索引之间的数据
     *
     * @param key   zset的key
     * @param start 开始位置
     * @param end   结束位置
     * @return
     * @throws Exception
     */
    public Set<String> zRange(String key, long start, long end) throws Exception {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * key 的value + 1
     *
     * @param key key
     * @return
     * @throws Exception
     */
    public Long incr(String key) throws Exception {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        return redisTemplate.opsForValue().increment(key, 1);
    }

    /**
     * key 的value + 1
     *
     * @param key key
     * @return
     * @throws Exception
     */
    public Long incrExpire(String key, int seconds) throws Exception {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        Long result = redisTemplate.opsForValue().increment(key, 1);
        if (0 < seconds) {
            redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
        }
        return result;
    }

    /**
     * mget多个key的数据
     *
     * @param key key
     * @return
     * @throws Exception
     */
    public List<String> mGet(String... key) throws Exception {
        if (null == key || key.length == 0) {
            return null;
        }
        List<String> list = redisTemplate.opsForValue().multiGet(Arrays.asList(key));
        if (list.size() == 0) {
            for (String k : key) {
                list.add("0");
            }
        }
        return list;
    }

    /**
     * smembers一个key的集合的所有数据
     *
     * @param key key
     * @return
     * @throws Exception
     */
    public Set<String> sMembers(String key) throws Exception {
        Set<String> set = null;
        if (StringUtils.isEmpty(key)) {
            return set;
        }
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 判断key释放存在
     *
     * @param key key
     * @return
     * @throws Exception
     */
    public boolean exists(String key) throws Exception {
        Set<String> set = null;
        if (StringUtils.isEmpty(key)) {
            return false;
        }
        return redisTemplate.hasKey(key);
    }

    /**
     * 获取key的集合
     *
     * @param key
     * @return
     * @throws Exception
     */
    public Set<String> getKeys(String key) throws Exception {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        return redisTemplate.keys(key);
    }

    /**
     * 获取key的集合大小
     *
     * @param key
     * @return
     * @throws Exception
     */
    public long sCard(String key) throws Exception {
        if (StringUtils.isEmpty(key)) {
            return 0;
        }
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 查询某个匹配内容的string的数量
     *
     * @param match
     * @return
     * @throws Exception
     */
    public List<String> scan(String match) throws Exception {
        if (StringUtils.isEmpty(match) || "*".equals(match)) {
            throw new IllegalArgumentException("param error");
        }
        Set keys = redisTemplate.keys(match);
        if (null == keys || keys.size() == 0) {
            return null;
        }
        return new ArrayList<>(keys);
    }


    /**
     * 获取set数据
     *
     * @param key
     * @return
     * @throws Exception
     */
    public Set<String> members(String key) throws Exception {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 删除set数据
     *
     * @param key
     * @param member
     * @return
     * @throws Exception
     */
    public void sRem(String key, String member) throws Exception {
        if (StringUtils.isEmpty(key)) {
            return;
        }
        redisTemplate.opsForSet().remove(key, member);
    }


}
