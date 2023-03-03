package io.gamioo.redis;


import io.gamioo.common.exception.ServiceException;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.*;

import java.util.*;


/**
 * Redis操作类.
 *
 * @since 1.0
 */
public class Redis {
    private static final Logger logger = LogManager.getLogger(Redis.class);
    /**
     * 默认超时（毫秒）
     */
    public static final int DEFAULT_TIMEOUT = 5000;
    /**
     * 默认database
     */
    public static final int DEFAULT_DATABASE = 0;
    /**
     * jedis client 池
     */
    private JedisPool pool;

    public Redis(String host, int port) {
        this(host, port, DEFAULT_DATABASE);
    }

    public Redis(String host, int port, int database) {
        this(host, port, DEFAULT_TIMEOUT, null, database);
    }

    public Redis(String host, int port, String password) {
        this(host, port, DEFAULT_TIMEOUT, password, DEFAULT_DATABASE);
    }

    /**
     * 初始化Redis辅助类.
     *
     * @param host  IP
     * @param port  端口
     * @param index 库索引
     */
    public Redis(String host, int port, String password, int index) {
        // 通过config配置连接池参数
        this(host, port, DEFAULT_TIMEOUT, password, index);
    }

    /**
     * 初始化Redis辅助类.
     *
     * @param host  IP
     * @param port  端口
     * @param index 库Index
     */
    public Redis(String host, int port, int timeout, String password, int index) {
        // 通过config配置连接池参数
        this(GenericObjectPoolConfig.DEFAULT_MAX_TOTAL, host, port, timeout, password, index);
    }

    /**
     * @param connection 连接数
     */
    public Redis(int connection, String host, int port, int timeout, String password, int database) {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(connection);
        config.setMaxIdle(connection);
        pool = new JedisPool(config, host, port, timeout, password, database);
        logger.info("Redis info:host={},port={},database={},timeout={}ms,password={}", host, port, database, timeout,
                password);
        ping();
    }

    public void ping() {
        try (Jedis j = pool.getResource()) {
            String ret = j.ping();
            if ("PONG".equals(ret)) {//yes
                logger.info("Redis server connected ok");
            } else {
                logger.info("Redis server connected failed");
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    //
    // ------------------------------Key相关命令------------------------------
    //
    public String type(String key) {
        try (Jedis j = pool.getResource()) {
            String ret = j.type(key);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public Long del(String key) {
        try (Jedis j = pool.getResource()) {
            Long result = j.del(key);
            return result;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public String debug(String key) {
        try (Jedis j = pool.getResource()) {
            String result = j.debug(DebugParams.OBJECT(key));
            return result;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public void del(byte[] key) {
        try (Jedis j = pool.getResource()) {
            j.del(key);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 删除指定的Key.
     * <p>
     * 时间复杂度 o(N)，N为要移除Key的数量.<br>
     * 如果Key不存在，则直接忽略.
     *
     * @param keys Key列表
     * @return 被删除Keys的数量
     */
    public long del(String... keys) {
        if (keys == null || keys.length == 0) {
            return 0L;
        }
        long ret = 0L;
        try (Jedis j = pool.getResource()) {
            ret = j.del(keys);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return ret;
    }

    /**
     * 批量删除 由 (key + assistKey) 的组合 key
     *
     * @param key
     * @param datas
     */
    // public void batchDel(String key,String... assistKeys){
    // try (Jedis j = pool.getResource()){
    // Pipeline pipeline = j.pipelined();
    // for(String assistKey : assistKeys){
    // String delKey = key + assistKey;
    // pipeline.del(delKey);
    // }
    // pipeline.exec();
    //
    // } catch (Exception e) {
    //
    // throw new ServiceException(e);
    // }
    // }

    /**
     * 查找所有匹配给定的模式的Key.
     *
     * @param pattern 模式
     * @return Key集合
     * @warning KEYS 命令被用于处理一个大的数据库时，它们可能会阻塞服务器达数秒之久。
     */
    public Set<String> keys(String pattern) {
        try (Jedis j = pool.getResource()) {
            Set<String> ret = j.keys(pattern);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 查找所有匹配给定的模式的Key.
     *
     * @param pattern 模式
     * @return Key集合
     */
    public Set<byte[]> keys(byte[] pattern) {
        try (Jedis j = pool.getResource()) {
            Set<byte[]> ret = j.keys(pattern);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 设计Key的过期时间。
     * <p>
     * 如果Key已过期，将会被自动删除，设置了过期时间的Key被称之为volatile(不稳定) KEY.<br>
     *
     * @param key     KEY
     * @param seconds 过期时间（秒）
     * @return 如果设置了过期时间返回1，没有设置或不能设置过期时间返回0
     */
    public long expire(String key, int seconds) {
        long ret = 0L;
        try (Jedis j = pool.getResource()) {
            ret = j.expire(key, seconds);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return ret;
    }

    /**
     * 移除生存时间
     */
    public long persist(String key) {
        try (Jedis j = pool.getResource()) {
            long ret = j.persist(key);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 获取生存时间
     */
    public long ttl(String key) {
        try (Jedis j = pool.getResource()) {
            long ret = j.ttl(key);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    //
    // ------------------------------String相关命令------------------------------
    //

    /**
     * 设置key所对应的value值.
     * <p>
     * 时间复杂度为o(1)<br>
     * 如果Key已存在了，它会被覆盖，而不管它是什么类型。<br>
     * 这里干掉了返回值，因为SET不会失败，总返回OK
     *
     * @param key   KEY值
     * @param value KEY对应的Value
     */
    public void set(String key, String value) {
        try (Jedis j = pool.getResource()) {
            j.set(key, value);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 将key重命名为newKey.如果key与newkey相同，将返回错误，如果newkey已经存在，则值将被覆盖
     */
    public void rename(String key, String newKey) {
        try (Jedis j = pool.getResource()) {
            j.rename(key, newKey);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 获取key所对应的value值.
     * <p>
     * 时间复杂度为o(1)<br>
     * 如果key不存在，返回null.<br>
     * 如果key的value值不是String类型，就返回错误，因为Get只处理String类型的Values.
     *
     * @param key Key值
     * @return 如果Key存在，则返回对应的Value值.
     */
    public String get(String key) {
        try (Jedis j = pool.getResource()) {
            String ret = j.get(key);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 判断指定Key是否存在.
     * <p>
     * 时间复杂度o(1)<br>
     *
     * @param key Key值
     * @return 如果存在，则返回true.
     */
    public boolean exists(String key) {
        boolean ret = false;
        try (Jedis j = pool.getResource()) {
            ret = j.exists(key);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return ret;
    }

    /**
     * @param value the decrement value
     * @return the value of key after the decrement
     */
    public long decrBy(String key, long value) {
        Long ret = null;
        try (Jedis j = pool.getResource()) {
            ret = j.decrBy(key, value);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 将值 value 关联到 key ，并将 key 的过期时间设为 seconds (以秒为单位)
     */
    public String setex(String key, int seconds, String value) {
        try (Jedis j = pool.getResource()) {
            String ret = j.setex(key, seconds, value);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public String set(byte[] key, byte[] value) {
        try (Jedis j = pool.getResource()) {
            String ret = j.set(key, value);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public String setex(byte[] key, int seconds, byte[] value) {
        try (Jedis j = pool.getResource()) {
            String ret = j.setex(key, seconds, value);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public byte[] get(byte[] key) {
        try (Jedis j = pool.getResource()) {
            byte[] ret = j.get(key);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public List<String> mget(String... keys) {
        try (Jedis j = pool.getResource()) {
            List<String> ret = j.mget(keys);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public List<byte[]> mget(byte[]... keys) {
        try (Jedis j = pool.getResource()) {
            List<byte[]> ret = j.mget(keys);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public String mset(String... kvs) {
        try (Jedis j = pool.getResource()) {
            String ret = j.mset(kvs);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 时间复杂度： O(1) 返回值： 执行 INCR 命令之后 key 的值。
     */
    public long incr(String key) {
        try (Jedis j = pool.getResource()) {
            long ret = j.incr(key);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    //
    // ------------------------------Hash相关命令------------------------------
    //

    /**
     * 时间复杂度： O(1)。 返回 key 指定的哈希集包含的字段的数量。 返回值 整数：哈希集中字段的数量，当 key 指定的哈希集不存在时返回 0
     */
    public long hlen(String key) {
        long ret = 0;
        try (Jedis j = pool.getResource()) {
            ret = j.hlen(key);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return ret;
    }

    /**
     * 时间复杂度： O(1)。 返回字段是否是 key 指定的哈希集中存在的字段。
     */
    public boolean hexists(String key, String field) {
        boolean ret = false;
        try (Jedis j = pool.getResource()) {
            ret = j.hexists(key, field);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return ret;
    }

    /**
     * Hash操作，设置Key指定的Hash集中指定字段的值.
     * <p>
     * 时间复杂度为o(N)，其中N是被设置的字段数量.<br>
     * 该命令将重写所有在Hash集中存在字段，如果key指定的Hash集不存在，会创建一个新的Hash集并与key关联
     *
     * @param key   Key键
     * @param value Hash集
     * @return 状态码
     */
    public String hmset(String key, Map<String, String> value) {
        String ret = null;
        try (Jedis j = pool.getResource()) {
            ret = j.hmset(key, value);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return ret;
    }

    /**
     * Hash操作，设置Key指定的Hash集中指定字段的值.
     *
     * @param datas
     * @param key
     * @param field
     * @param value
     */
    public void hmset(Collection<Object> datas, String key, String field, String value) {
        try (Jedis j = pool.getResource()) {
            Pipeline pipeline = j.pipelined();
            for (Object element : datas) {
                String keyStr = key + element.toString();
                pipeline.hset(keyStr, field, value);
            }
            pipeline.exec();
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Hash操作，设置Key指定的Hash集中指定字段的值.
     * <p>
     * 时间复杂度为o(N)，其中N是被设置的字段数量.<br>
     * 该命令将重写所有在Hash集中存在字段，如果key指定的Hash集不存在，会创建一个新的Hash集并与key关联
     *
     * @param key   Key键
     * @param value Hash集
     * @return 状态码
     */
    public String hmset(byte[] key, Map<byte[], byte[]> value) {
        String ret = null;
        try (Jedis j = pool.getResource()) {
            ret = j.hmset(key, value);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return ret;
    }

    /**
     * 获取Key对应的Hash集中该字段所关联值的列表.
     * <p>
     * 时间复杂度o(N),其中N是被请求字段的数量<br>
     *
     * @param key    Key值
     * @param fields 指定字段
     * @return 如果存在此字段，则返回所关联的值列表，否则返回空列表.
     */
    public List<String> hmget(String key, String... fields) {
        List<String> ret = null;
        try (Jedis j = pool.getResource()) {
            ret = j.hmget(key, fields);
        } catch (Exception e) {
            ret = Collections.emptyList();
            throw new ServiceException(e);
        }
        return ret;
    }

    /**
     * 返回哈希表 key 中的所有域(所有的field集合).
     * <p>
     * <b>时间复杂度:</b> <br>
     * O(N)， N 为哈希表的大小
     *
     * @param key
     * @return 一个包含哈希表中所有域的表.<br>
     * 当 key 不存在时，返回一个空表
     */
    public Set<String> hkeys(String key) {
        Set<String> ret = null;
        try (Jedis j = pool.getResource()) {
            ret = j.hkeys(key);
        } catch (Exception e) {
            ret = Collections.emptySet();
            throw new ServiceException(e);
        }
        return ret;
    }

    /**
     * 返回哈希表 key 中所有域的值.
     * <p>
     * 时间复杂度o(N), N 为哈希表的大小<br>
     *
     * @param key Key值
     * @return 一个包含哈希表中所有值的表；当 key 不存在时，返回一个空表.
     */
    public List<String> hvals(String key) {
        List<String> ret = null;
        try (Jedis j = pool.getResource()) {
            ret = j.hvals(key);
        } catch (Exception e) {
            ret = Collections.emptyList();
            throw new ServiceException(e);
        }
        return ret;
    }

    /**
     * 获取Key对应的Hash集中该字段所关联的值.
     * <p>
     * 时间复杂度o(1)<br>
     *
     * @param key   Key值
     * @param field 指定字段
     * @return 如果存在此字段，则返回所关联的值，否则返回null
     */
    public String hget(String key, String field) {
        try (Jedis j = pool.getResource()) {
            String ret = j.hget(key, field);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 获取Key对应的Hash集中该字段所关联的值.
     * <p>
     * 时间复杂度o(1)<br>
     *
     * @param key   Key值
     * @param field 指定字段
     * @return 如果存在此字段，则返回所关联的值，否则返回null
     */
    public byte[] hget(byte[] key, byte[] field) {
        try (Jedis j = pool.getResource()) {
            byte[] ret = j.hget(key, field);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Hash操作，设置Key指定的Hash集中指定字段的值.
     */
    public Long hset(String key, String field, String value) {
        Long ret = null;
        try (Jedis j = pool.getResource()) {
            ret = j.hset(key, field, value);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return ret;
    }

    /**
     * Hash操作，设置Key指定的Hash集中指定字段的值(如果key不存在则设置成功).
     */
    public Long hsetnx(String key, String field, String value) {
        Long ret = null;
        try (Jedis j = pool.getResource()) {
            ret = j.hsetnx(key, field, value);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return ret;
    }

    /**
     * Hash操作，设置Key指定的Hash集中指定字段的值.
     */
    public Long hset(byte[] key, byte[] field, byte[] value) {
        Long ret = null;
        try (Jedis j = pool.getResource()) {
            ret = j.hset(key, field, value);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return ret;
    }

    /**
     * 获取key指定的Hash集中所有的字段和值。
     * <p>
     * 时间复杂度o(N),其中N是Hash集的大小。<br>
     * 使用命令行时请注意：<br>
     * 返回值中，每个字段名的下一个是它的值，所以返回值的长度是Hash集大小的两倍.
     *
     * @param key Key值
     * @return 如果key所对应的Hash集存在，则返回此集合，否则返回空列表.
     */
    public Map<String, String> hgetAll(String key) {
        Map<String, String> ret = null;
        try (Jedis j = pool.getResource()) {
            ret = j.hgetAll(key);
        } catch (Exception e) {
            ret = Collections.emptyMap();
            throw new ServiceException(e);
        }
        return ret;
    }

    /**
     * 获取key指定的Hash集中所有的字段和值。
     * <p>
     * 时间复杂度o(N),其中N是Hash集的大小。<br>
     * 使用命令行时请注意：<br>
     * 返回值中，每个字段名的下一个是它的值，所以返回值的长度是Hash集大小的两倍.
     *
     * @param key Key值
     * @return 如果key所对应的Hash集存在，则返回此集合，否则返回空列表.
     */
    public Map<byte[], byte[]> hgetAll(byte[] key) {
        Map<byte[], byte[]> ret = null;
        try (Jedis j = pool.getResource()) {
            ret = j.hgetAll(key);
        } catch (Exception e) {
            ret = Collections.emptyMap();
            throw new ServiceException(e);
        }
        return ret;
    }

    /**
     * 删除Key所对应的Hash集中指定field字段.
     * <p>
     * 时间复杂度 o(N)，其中N为要移除字段的数量.<br>
     * 如果指定字段不存在，则忽略处理，如果指定的Hash集不存在，应该指令返回0.<br>
     * 此指令可以返回被成功删除字段的数量，但目前没有需求，就不返回了.
     *
     * @param key   Key值
     * @param field 指定要删除的字段集合
     * @return If the field was present in the hash it is deleted and 1 is
     * returned, otherwise 0 is returned and no operation is performed.
     */
    public long hdel(String key, String... field) {
        try (Jedis j = pool.getResource()) {
            long ret = j.hdel(key, field);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 增加Key所对应的Hash集中指定字段的数值.
     * <p>
     * 时间复杂度为o(1)<br>
     * 如果Key不存在，会创建一个新的Hash集并与之关联。<br>
     * 如果指定字段不存在，则字段的值在该操作执行前被设计为0<br>
     * <b>注意：此命令支持的值范围限定在64位 有符号整数<b>
     *
     * @param key   Key值
     * @param field 指定字段
     * @param value 要加的值
     * @return 增值操作执行后该字段的值
     */
    public long hincrBy(String key, String field, long value) {
        long ret = 0L;
        try (Jedis j = pool.getResource()) {
            ret = j.hincrBy(key, field, value);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return ret;
    }

    //
    // ------------------------------List 有序的相关命令------------------------------
    //

    /**
     * 删除列表里最右边的元素。
     * <p>
     *
     * @param key KEY值
     * @return 最右边的元素
     */
    public String rpop(String key) {
        try (Jedis j = pool.getResource()) {
            String ret = j.rpop(key);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 获得列表的长度。
     * <p>
     *
     * @param key KEY值
     * @return 成员数量
     */
    public long llen(String key) {
        try (Jedis j = pool.getResource()) {
            long ret = j.llen(key);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 获得列表所有元素。
     * <p>
     *
     * @param key   KEY值
     * @param start 列表起始位置
     * @param end   列表结束位置
     * @return 列表所有元素
     */
    public List<String> lrange(String key, long start, long end) {
        List<String> ret = null;
        try (Jedis j = pool.getResource()) {
            ret = j.lrange(key, start, end);
        } catch (Exception e) {
            ret = Collections.emptyList();
            throw new ServiceException(e);
        }
        return ret;
    }

    public List<byte[]> lrange(byte[] key, int start, int end) {
        try (Jedis j = pool.getResource()) {
            List<byte[]> ret = j.lrange(key, start, end);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 返回列表key中，小标为index的元素
     * <p>
     *
     * @param key   KEY值
     * @param index VALUE值
     * @return 成员数量
     */
    public String lindex(String key, long index) {
        try (Jedis j = pool.getResource()) {
            String ret = j.lindex(key, index);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

//	/**
//	 * 返回列表长度
//	 * <p>
//	 *
//	 * @param key
//	 *            KEY值
//	 * @param value
//	 *            VALUE值
//	 * @param pivot
//	 *            位于这个值之前或者之后
//	 * @return 成员数量
//	 */
//	public long linsert(String key, BinaryClient.LIST_POSITION where, String pivot, String value) {
//		try (Jedis j = pool.getResource()) {
//			long ret = j.linsert(key, where, pivot, value);
//			return ret;
//		} catch (Exception e) {
//			throw new ServiceException(e);
//		}
//	}

    /**
     * 返回被移除的数量
     * <p>
     *
     * @param key   KEY值
     * @param value VALUE值
     * @return 被移除的数量
     */
    public long lrem(String key, String value) {
        try (Jedis j = pool.getResource()) {
            long ret = j.lrem(key, 1, value);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 从列表左边添加一个元素。
     * <p>
     *
     * @param key   KEY值
     * @param value VALUE值
     * @return 成员数量
     */
    public long lpush(String key, String... value) {
        try (Jedis j = pool.getResource()) {
            long ret = j.lpush(key, value);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public long lpush(byte[] key, byte[] value) {
        try (Jedis j = pool.getResource()) {
            long ret = j.lpush(key, value);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public long rpush(String key, String... value) {
        try (Jedis j = pool.getResource()) {
            long ret = j.rpush(key, value);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public List<String> blpop(int timeout, String... key) {
        try (Jedis j = pool.getResource()) {
            List<String> ret = j.blpop(timeout, key);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 修剪到指定范围的列表
     */
    public void ltrim(String key, int start, int end) {
        try (Jedis j = pool.getResource()) {
            j.ltrim(key, start, end);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    //
    // --------------------------------Set相关命令
    // 无序--------------------------------
    //

    /**
     * SADD操作，添加Set类型数据.
     * <p>
     * o(N) 时间复杂度中的N表示操作的成员数量.<br>
     * 如果在插入的过程中，参数中有的成员已存在，该成员将被忽略，其它成员正常插入。<br>
     * 如果执行命令之前，此KEY并不存在，将以此Key创建新的Set
     *
     * @param key   KEY值
     * @param value 成员列表
     * @return 本次操作实际插入的成员数量
     */
    public long sadd(String key, String value) {
        try (Jedis j = pool.getResource()) {
            long ret = j.sadd(key, value);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public long sadd(String key, String[] value) {
        try (Jedis j = pool.getResource()) {
            long ret = j.sadd(key, value);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public String save() {
        try (Jedis j = pool.getResource()) {
            String ret = j.save();
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 获取Key所对应的Set集合里面的所有值
     *
     * @param key KEY值
     * @return Set集中的所有元素
     * @warning SMEMBERS 命令被用于处理一个大的集合键时， 它们可能会阻塞服务器达数秒之久。
     */
    public Set<String> smembers(String key) {
        Set<String> ret = null;
        try (Jedis j = pool.getResource()) {
            ret = j.smembers(key);
        } catch (Exception e) {
            ret = Collections.emptySet();
            throw new ServiceException(e);
        }
        return ret;
    }

    public String spop(String key) {
        String ret = null;
        try (Jedis j = pool.getResource()) {
            ret = j.spop(key);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 从集合移除元素（如果不存在则忽略）
     *
     * @return 返回成功移除的元素个数
     */
    public long srem(String key, String value) {
        try (Jedis j = pool.getResource()) {
            long ret = j.srem(key, value);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 从集合移除元素（如果不存在则忽略）
     *
     * @return 返回成功移除的元素个数
     */
    public long srem(String key, String... value) {
        try (Jedis j = pool.getResource()) {
            long ret = j.srem(key, value);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * @return 返回集合的元素个数
     */
    public long scard(String key) {
        try (Jedis j = pool.getResource()) {
            long ret = j.scard(key);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * @return 如果成员元素是集合的成员，返回 1 。 如果成员元素不是集合的成员，或 key 不存在，返回 0 。
     */
    public boolean sismember(String key, String value) {
        try (Jedis j = pool.getResource()) {
            boolean ret = j.sismember(key, value);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 将元素从一个集合移动到另一个集合
     *
     * @return 成功移动，返回1 没有任何操作，返回0
     */
    public long smove(String srckey, String dstkey, String member) {
        try (Jedis j = pool.getResource()) {
            long ret = j.smove(srckey, dstkey, member);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    //
    // ------------------------------Sorted set相关命令
    // 有序---------------------------
    //

    /**
     * 添加指定成员到Key对应的Set集合中.
     * <p>
     * 时间复杂度o(log(N)) N为Set集合中的元素个数<br>
     * 每一个成员都有一个分数值，如果指定成员存在，那么其分数就会被更新成最新的。<br>
     * 如果不存在，则会创建一个新的。
     *
     * @param key    KEY值
     * @param score  分数值
     * @param member 指定成员
     * @return 返回添加到Set集合中的元素个数，不包括那种已存在只更新的分数的元素。
     */
    public long zadd(String key, double score, String member) {
        long ret = 0L;
        try (Jedis j = pool.getResource()) {
            ret = j.zadd(key, score, member);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return ret;
    }

    public long zadd(String key, Map<String, Double> map) {
        long ret = 0L;
        try (Jedis j = pool.getResource()) {
            ret = j.zadd(key, map);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return ret;
    }

    /**
     * 获取指定Key的Set集合中成员member的排名。
     * <p>
     * 其中Set集合成员按score值递增（由小到大）排列。<br>
     * <b>注意：排名以0为底，也就是说score值最小的成员排名为0.</b>
     *
     * @param key    KEY值
     * @param member 指定成员
     * @return 如果member是key对应Set集合中的成员，则返回member的排名值。
     */
    public Long zrank(String key, String member) {
        try (Jedis j = pool.getResource()) {
            Long ret = j.zrank(key, member);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 获取指定Key的Set集合中成员member的排名。
     * <p>
     * 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递减(从大到小)排序<br>
     * <b>注意：排名以0为底，也就是说score值最小的成员排名为0.</b>
     *
     * @param key    KEY值
     * @param member 指定成员
     * @return 如果member是key对应Set集合中的成员，则返回member的排名值。
     */
    public Long zrevrank(String key, String member) {
        try (Jedis j = pool.getResource()) {
            Long ret = j.zrevrank(key, member);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 获取指定Key对应的Set集合中指定区间内的成员。
     * <p>
     * 其中成员按Score值递增来排序，具有相同值的成员按字典序来排列.<br>
     *
     * @param key   KEY值
     * @param start 开始下标 rank
     * @param end   结束下标 rank
     * @return 如果指定区间有成员，则返回此区间的成员集合.
     */
    public Set<String> zrange(String key, long start, long end) {
        Set<String> ret = null;
        try (Jedis j = pool.getResource()) {
            ret = j.zrange(key, start, end);
        } catch (Exception e) {
            ret = Collections.emptySet();
            throw new ServiceException(e);
        }
        return ret;
    }

    /**
     * 获取指定Key对应的Set集合中指定区间内的成员。
     * <p>
     * 其中成员按Score值递减来排序，具有相同值的成员按字典序来排列.<br>
     *
     * @param key   KEY值
     * @param start 开始下标 rank
     * @param end   结束下标 rank
     * @return 如果指定区间有成员，则返回此区间的成员集合.
     */
    public Set<String> zrevrange(String key, long start, long end) {
        Set<String> ret = null;
        try (Jedis j = pool.getResource()) {
            ret = j.zrevrange(key, start, end);
        } catch (Exception e) {
            ret = Collections.emptySet();
            throw new ServiceException(e);
        }
        return ret;
    }

    /**
     * 获取指定Key对应的Set集合中指定分数的成员。
     *
     * @param key KEY值
     * @param min 最小值 score
     * @param max 最大值 score
     * @return 如果指定区间有成员，则返回此区间的成员集合.
     */
    public Set<String> zrangeByScore(String key, double min, double max) {
        Set<String> ret = null;
        try (Jedis j = pool.getResource()) {
            ret = j.zrangeByScore(key, min, max);
        } catch (Exception e) {
            ret = Collections.emptySet();
            throw new ServiceException(e);
        }
        return ret;
    }

    /**
     * 返回指定分数区间的元素集合（包括min和max） 按照分数从大到小排序
     *
     * @param key KEY值
     * @param min 最小值 score
     * @param max 最大值 score
     * @return 如果指定区间有成员，则返回此区间的成员集合.
     */
    public Set<String> zrevrangeByScore(String key, double max, double min) {
        try (Jedis j = pool.getResource()) {
            Set<String> ret = j.zrevrangeByScore(key, max, min);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 返回指定排名区间的元素集合（按照score从大到小排序）
     *
     * @param key
     * @param start 开始下标 rank
     * @param end   结束下标 rank
     * @return 指定区间内，带有 score 值(可选)的有序集成员的列表。
     */
    public Set<Tuple> zrevrangeWithScore(String key, int start, int end) {
        try (Jedis j = pool.getResource()) {
            Set<Tuple> ret = j.zrevrangeWithScores(key, start, end);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 返回指定排名区间的元素集合（按照score从小到大排序）
     *
     * @param key   主健值
     * @param start 开始下标 rank
     * @param end   结束下标 rank
     */
    public Set<Tuple> zrangeWithScore(String key, int start, int end) {
        try (Jedis j = pool.getResource()) {
            Set<Tuple> ret = j.zrangeWithScores(key, start, end);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 返回指定排名区间的元素集合（按照score从小到大排序）
     *
     * @param key 主健值
     * @param min 开始下标 rank
     * @param max 结束下标 rank
     */
    public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
        try (Jedis j = pool.getResource()) {
            Set<Tuple> ret = j.zrangeByScoreWithScores(key, min, max);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 返回指定排名区间的元素集合（按照score从大到小排序）
     *
     * @param min 开始下标 rank
     * @param max 结束下标 rank
     */
    public Set<Tuple> zrevrangeByScoreWithScores(String key, String min, String max) {
        try (Jedis j = pool.getResource()) {
            Set<Tuple> ret = j.zrevrangeByScoreWithScores(key, min, max);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 获取指定Key的Set集合中成员member的值。
     * <p>
     * <b>备注:</b><br>
     * 如果 member 元素不是有序集 key 的成员，或 key 不存在，返回 nil
     *
     * @param key    KEY值
     * @param member 指定成员
     * @return member 成员的 score 值，以字符串形式表示。
     */
    public long zscore(String key, String member) {
        try (Jedis j = pool.getResource()) {
            Double ret = j.zscore(key, member);
            if (ret == null) {
                return 0L;
            }
            return ret.longValue();
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 获取指定Key的Set集合中成员member的值。
     * <p>
     * <b>备注:</b><br>
     * 如果 member 元素不是有序集 key 的成员，或 key 不存在，返回 nil
     *
     * @param key    KEY值
     * @param member 指定成员
     * @return member 成员的 score 值，以字符串形式表示。
     */
    public Double zscorex(String key, String member) {
        try (Jedis j = pool.getResource()) {
            Double ret = j.zscore(key, member);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 返回key对应的Set集合中指定分数区间成员的个数。
     * <p>
     * 注意： min <= 此分数值 <= max
     *
     * @param key KEY值
     * @param min 分数值下限
     * @param max 分数值上限
     * @return 成员数量
     */
    public long zcount(String key, double min, double max) {
        long ret = 0L;
        try (Jedis j = pool.getResource()) {
            ret = j.zcount(key, min, max);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return ret;
    }

    /**
     * 返回key对应的Set集合中成员的个数。
     * <p>
     *
     * @param key KEY值
     * @return 成员数量
     */
    public long zcount(String key) {
        long ret = 0L;
        try (Jedis j = pool.getResource()) {
            ret = j.zcount(key, "-inf", "+inf");
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return ret;
    }

    /**
     * 删除指定Key对应的Set集合中指定的成员。
     * <p>
     *
     * @param key     KEY值
     * @param members 指定的成员
     * @return 返回被删除的元素的个数
     */
    public long zrem(String key, String... members) {
        try (Jedis j = pool.getResource()) {
            long ret = j.zrem(key, members);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 移除排名在start和stop（包含start，stop）在内的所有元素
     *
     * @return 返回移除元素的个数
     */
    public long zremrangeByRank(String key, int start, int stop) {
        try (Jedis j = pool.getResource()) {
            long ret = j.zremrangeByRank(key, start, stop);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 移除分数在min和max（包含min，max）在内的所有元素
     *
     * @return 返回移除元素的个数
     */
    public long zremrangeByScore(String key, double min, double max) {
        try (Jedis j = pool.getResource()) {
            long ret = j.zremrangeByScore(key, min, max);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 返回key对应的Set集合中成员的个数。
     * <p>
     *
     * @param key KEY值
     * @return 成员数量
     */
    public long zcard(String key) {
        try (Jedis j = pool.getResource()) {
            long ret = j.zcard(key);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 增加指定元素的分数
     *
     * @return 指定元素新的分数值
     */
    public double zincrby(String key, double increment, String member) {
        try (Jedis j = pool.getResource()) {
            double ret = j.zincrby(key, increment, member);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 执行脚本
     *
     * @param script
     * @return
     */
    public Object eval(String script) {
        try (Jedis j = pool.getResource()) {
            Object ret = j.eval(script);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    // ------------------------------------------订阅/发布------------------------------------

    /**
     * 将信息 message 发送到指定的频道 channel 。
     *
     * @return 接收到信息 message 的订阅者数量。
     * @version >= 2.0.0 时间复杂度： O(N+M)，其中 N 是频道channel 的订阅者数量，而 M
     * 则是使用模式订阅(subscribed patterns)的客户端的数量。
     */
    public long publish(String channel, String message) {
        try (Jedis j = pool.getResource()) {
            long ret = j.publish(channel, message);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 订阅给定的一个或多个频道的信息。
     *
     * @return 接收到的信息(请参见下面的代码说明)。
     * @version >= 2.0.0 时间复杂度： O(N)，其中 N 是订阅的频道的数量。
     */
    public void subscribe(JedisPubSub jedisPubSub, String... channel) {
        try (Jedis j = pool.getResource()) {
            j.subscribe(jedisPubSub, channel);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 订阅一个或多个符合给定模式的频道。
     * <p>
     * 每个模式以 * 作为匹配符，比如 it* 匹配所有以 it 开头的频道( it.news 、 it.blog 、 it.tweets 等等)，
     * news.* 匹配所有以 news. 开头的频道( news.it 、 news.global.today 等等)，诸如此类。
     *
     * @return 接收到的信息
     * @version >= 2.0.0 时间复杂度： O(N)， N 是订阅的模式的数量。
     */
    public void psubscribe(JedisPubSub jedisPubSub, String... channel) {
        try (Jedis j = pool.getResource()) {
            j.psubscribe(jedisPubSub, channel);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    //
    // --------------------------------Server相关命令-----------------------------
    //

    /**
     * 清空当前数据库里的所有数据，这个命令永远不会失败，使用时请注意加小心。
     */
    public void flushDB() {
        try (Jedis j = pool.getResource()) {
            j.flushDB();
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 获取当前数据库里的Keys的数量.
     *
     * @return Keys的数量
     */
    public long dbSize() {
        try (Jedis j = pool.getResource()) {
            long ret = j.dbSize();
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 退出客户端
     */
    public String quit() {
        try (Jedis j = pool.getResource()) {
            String ret = j.quit();
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public void destory() {
        pool.destroy();
    }

    /**
     * 可用版本： >= 2.6.0 时间复杂度： 这个命令在源实例上实际执行 DUMP 命令和 DEL 命令，在目标实例执行 RESTORE
     * 命令，查看以上命令的文档可以看到详细的复杂度说明。 key 数据在两个实例之间传输的复杂度为 O(N) 。
     */
    public String migrate(String host, int port, String key, int destinationDb, int timeout) {
        try (Jedis j = pool.getResource()) {
            String ret = j.migrate(host, port, key, destinationDb, timeout);
            return ret;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * @param fromHost  源redis ip
     * @param fromPort  源redis 端口
     * @param fromIndex 源DB
     * @param toHost    目标redis ip
     * @param toPort    目标 redis 端口
     * @param toIndex   目标DB
     * @param timeout   超时 毫秒
     * @param list      要转移的键列表
     */
    public static void migrate(String fromHost, int fromPort, int fromIndex, String toHost, int toPort, int toIndex,
                               int timeout, String... list) {
        long start = System.nanoTime();
        logger.info("数据迁移开始 fromHost={},fromPort={},fromIndex={},toHost={},toPort={},toIndex={},timeout={},list={}",
                fromHost, fromPort, fromIndex, toHost, toPort, toIndex, timeout, list);
        Redis redis = new Redis(fromHost, fromPort, fromIndex);
        List<Object> ret = redis.migrate(toHost, toPort, toIndex, timeout, list);
        redis.destory();
        logger.info("数据迁移完成 interval={},ret={}", (System.nanoTime() - start) / 1000000f, ret);
    }

    /**
     * 批量迁移键
     */
    public List<Object> migrate(String host, int port, int destinationDb, int timeout, String... list) {
        try (Jedis j = pool.getResource()) {
            Pipeline p = j.pipelined();
            for (String key : list) {
                p.migrate(host, port, key, destinationDb, timeout);
            }
            List<Object> ret = p.syncAndReturnAll();
            return ret;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 返回一个jedis实例，用于自己实现pipeline、multi、watch等
     */
    public Jedis getJedis() {
        return pool.getResource();
    }

    // public void returnJedis(Jedis j)
    // {
    //
    // }
    public JedisPool getPool() {
        return pool;
    }

    public void setPool(JedisPool pool) {
        this.pool = pool;
    }
}
