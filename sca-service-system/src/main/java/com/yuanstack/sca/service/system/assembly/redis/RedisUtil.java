package com.yuanstack.sca.service.system.assembly.redis;

import com.yuanstack.sca.service.system.common.constants.ModelEnum;
import com.yuanstack.sca.service.system.common.log.LogUtils;
import com.yuanstack.sca.service.system.config.EnvConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @description: RedisUtil
 * Redis工具类，基于spring和redis的redisTemplate工具类。针对所有的hash，都是以h开头的方法。
 * * 针对所有的Set，都是以s开头的方法。不含通用方法。针对所有的List，都是以l开头的方法,bitmap 是以b开头的下标
 * @author: hansiyuan
 * @date: 2022/6/29 3:25 PM
 */
@Component
@Slf4j
public class RedisUtil {

    @Resource
    private EnvConfig envConfig;

    @Resource
    private RedisTemplate<String, Serializable> redisTemplate;

    public RedisTemplate<String, Serializable> getRedisTemplate() {
        return redisTemplate;
    }

    private String prefix(String key) {
        return envConfig.getEnv() + ":" + key;
    }

    // ============================common=============================

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(this.prefix(key), time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(this.prefix(key), TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(this.prefix(key));
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(this.prefix(key[0]));
            } else {
                String[] newKeys = new String[key.length];
                for (int i = 0; i < key.length; i++) {
                    newKeys[i] = this.prefix(key[i]);
                }

                redisTemplate.delete(CollectionUtils.arrayToList(newKeys));
            }
        }
    }

    // ============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Serializable get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(this.prefix(key));
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Serializable value) {
        try {
            redisTemplate.opsForValue().set(this.prefix(key), value);
            return true;
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return false;
        }

    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Serializable value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(this.prefix(key), value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return false;
        }
    }

    /**
     * 缓存放入
     *
     * @param key
     * @param value
     * @param time  毫秒
     * @return
     */
    public boolean setIfAbsent(String key, Serializable value, long time) {
        try {
            return redisTemplate.opsForValue().setIfAbsent(this.prefix(key), value, time, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return false;
        }

    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(this.prefix(key), delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(this.prefix(key), -delta);
    }

    // ================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(this.prefix(key), item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(this.prefix(key));
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String, Serializable> map) {
        try {
            redisTemplate.opsForHash().putAll(this.prefix(key), map);
            return true;
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, Serializable> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(this.prefix(key), map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Serializable value) {
        try {
            redisTemplate.opsForHash().put(this.prefix(key), item, value);
            return true;
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Serializable value, long time) {
        try {
            redisTemplate.opsForHash().put(this.prefix(key), item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, String... item) {
        redisTemplate.opsForHash().delete(this.prefix(key), item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(this.prefix(key), item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    public double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(this.prefix(key), item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */
    public double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(this.prefix(key), item, -by);
    }

    // ============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    public Set<Serializable> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(this.prefix(key));
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return null;
        }
    }

    /**
     * 获取所有匹配的key
     *
     * @param key
     * @return
     */
    public Set<String> gKeys(String key) {
        try {
            return redisTemplate.keys(this.prefix(key));
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Serializable value) {
        try {
            return redisTemplate.opsForSet().isMember(this.prefix(key), value);
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Serializable... values) {
        try {
            return redisTemplate.opsForSet().add(this.prefix(key), values);
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long time, Serializable... values) {
        try {
            Long count = redisTemplate.opsForSet().add(this.prefix(key), values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(this.prefix(key));
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return 0;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Serializable... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(this.prefix(key), values);
            return count;
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return 0;
        }
    }

    // ============================zset=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    public Set<Serializable> zSGet(String key) {
        try {
            return redisTemplate.opsForSet().members(this.prefix(key));
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean zSHasKey(String key, Serializable value) {
        try {
            return redisTemplate.opsForSet().isMember(this.prefix(key), value);
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return false;
        }
    }

    public Boolean zSSet(String key, Serializable value, double score) {
        try {
            return redisTemplate.opsForZSet().add(this.prefix(key), value, 2);
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return false;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long zSSetAndTime(String key, long time, Serializable... values) {
        try {
            Long count = redisTemplate.opsForSet().add(this.prefix(key), values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public long zSGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(this.prefix(key));
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return 0;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long zSetRemove(String key, Serializable... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(this.prefix(key), values);
            return count;
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return 0;
        }
    }
    // ===============================list=================================

    /**
     * 获取list缓存的内容 取出来的元素 总数 end-start+1
     *
     * @param key   键
     * @param start 开始 0 是第一个元素
     * @param end   结束 -1代表所有值
     * @return
     */
    public List<Serializable> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(this.prefix(key), start, end);
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(this.prefix(key));
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Serializable lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(this.prefix(key), index);
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, Serializable value) {
        try {
            redisTemplate.opsForList().rightPush(this.prefix(key), value);
            return true;
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, Serializable value, long time) {
        try {
            redisTemplate.opsForList().rightPush(this.prefix(key), value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, List<Serializable> value) {
        try {
            redisTemplate.opsForList().rightPushAll(this.prefix(key), value);
            return true;
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, List<Serializable> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(this.prefix(key), value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lUpdateIndex(String key, long index, Serializable value) {
        try {
            redisTemplate.opsForList().set(this.prefix(key), index, value);
            return true;
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Serializable value) {
        try {
            Long remove = redisTemplate.opsForList().remove(this.prefix(key), count, value);
            return remove;
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return 0;
        }
    }


    // ===============================bitmap=================================

    /**
     * bitmap 创建值
     *
     * @param key    键
     * @param offset 下标
     * @param b      值
     * @return 移除的个数
     */
    public boolean bSet(String key, long offset, boolean b) {
        try {
            Boolean res = redisTemplate.opsForValue().setBit(this.prefix(key), offset, b);
            if (ObjectUtils.isEmpty(res)) {
                return false;
            }
            return res;
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return false;
        }
    }

    /**
     * bitmap 获取值
     *
     * @param key    键
     * @param offset 下标
     * @return 移除的个数
     */
    public boolean bGet(String key, long offset) {
        try {
            Boolean res = redisTemplate.opsForValue().getBit(this.prefix(key), offset);
            if (ObjectUtils.isEmpty(res)) {
                return false;
            }
            return res;
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, key, e);
            return false;
        }
    }


    /**
     * 执行脚本
     *
     * @param script
     * @param keys
     * @param args
     * @param <T>
     * @return
     */
    public <T> T execute(RedisScript<T> script, List<String> keys, Object... args) {
        try {
            List<String> wrapKeys = new ArrayList<>();
            for (String key : keys) {
                wrapKeys.add(this.prefix(key));
            }

            return redisTemplate.execute(script, wrapKeys, args);

        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, "RedisUtil.execute异常",
                    script, keys, args, e);
        }

        return null;
    }
}
