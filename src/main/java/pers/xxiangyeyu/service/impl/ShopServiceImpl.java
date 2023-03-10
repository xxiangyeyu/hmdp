package pers.xxiangyeyu.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.xxiangyeyu.dto.Result;
import pers.xxiangyeyu.entity.Shop;
import pers.xxiangyeyu.mapper.ShopMapper;
import pers.xxiangyeyu.service.IShopService;
import pers.xxiangyeyu.utils.RedisConstants;
import pers.xxiangyeyu.utils.RedisData;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author xxiangyeyu
 * @since 2022-12-15
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryById(Long id) {
        // 缓存击穿-互斥锁
        // Shop shop = queryWithPassThrough(id);

        // 缓存击穿-互斥锁
        // Shop shop = queryWithMutex(id);
        // 缓存击穿-逻辑过期
        Shop shop = queryWithLogicalExpire(id);
        if (shop == null) {
            return Result.fail("店铺不存在！");
        }
        // 返回
        return Result.ok(shop);
    }

    /**
     * 缓存穿透-缓存空对象
     * @param id
     * @return
     */
    public Shop queryWithPassThrough(Long id) {
        String key = RedisConstants.CACHE_SHOP_KEY + id;
        // 1、从redis查询商铺缓存
        String shopJson = stringRedisTemplate.opsForValue().get(key);
        // 2、判断是否存在
        if (StrUtil.isNotBlank(shopJson)) {
            // 3、存在，直接返回
            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
            return shop;
        }
        // 不为空说明shopJson是空字符串
        if (shopJson != null) {
            // 返回一个错误信息
            return null;
        }
        // 4、不存在，根据id查询数据库
        Shop shop = getById(id);
        // 5、不存在，返回错误
        if (shop == null) {
            // 将空值写入redis
            stringRedisTemplate.opsForValue().set(key, "", RedisConstants.CACHE_NULL_TTL, TimeUnit.MINUTES);
            // 返回错误信息
            return null;
        }
        // 6、存在，写入redis
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shop), RedisConstants.CACHE_SHOP_TTL, TimeUnit.MINUTES);
        // 7、返回
        return shop;
    }

    /**
     * 缓存击穿-互斥锁
     * @param id
     * @return
     */
    public Shop queryWithMutex(Long id) {
        String key = RedisConstants.CACHE_SHOP_KEY + id;
        // 1、从redis查询商铺缓存
        String shopJson = stringRedisTemplate.opsForValue().get(key);
        // 2、判断是否存在
        if (StrUtil.isNotBlank(shopJson)) {
            // 3、存在，直接返回
            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
            return shop;
        }
        // 4、实现缓存重建
        // 4.1 获取互斥锁
        String lockKey = RedisConstants.LOCK_SHOP_KEY + id;
        Shop shop;
        try {
            boolean isLock = tryLock(lockKey);
            // 4.2 判断是否获取成功
            if (!isLock) {
                // 4.3 失败，则休眠并重试
                Thread.sleep(50);
                queryWithMutex(id);
            }
            // 4.4 不存在，根据id查询数据库
            shop = getById(id);
            // 5、不存在，返回错误
            if (shop == null) {
                // 返回错误信息
                return null;
            }
            // 6、存在，写入redis
            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shop), RedisConstants.CACHE_SHOP_TTL, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException();
        } finally {
            // 7、释放互斥锁
            unLock(lockKey);
        }
        // 8、返回
        return shop;
    }


    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);
    /**
     * 缓存击穿-逻辑过期
     * @param id
     * @return
     */
    public Shop queryWithLogicalExpire(Long id) {
        String key = RedisConstants.CACHE_SHOP_KEY + id;
        // 1、从redis查询商铺缓存
        String shopJson = stringRedisTemplate.opsForValue().get(key);
        // 2、判断是否存在
        // 注:活动产品不一般提前进行缓存，不存在为空
        if (StrUtil.isBlank(shopJson)) {
            // 3、存在，直接返回
            return null;
        }

        // 4、命中，需要先把json反序列化为对象
        RedisData redisData = JSONUtil.toBean(shopJson, RedisData.class);
        Shop shop = JSONUtil.toBean((JSONObject) redisData.getData(), Shop.class);
        LocalDateTime expireTime = redisData.getExpireTime();
        // 5、判断是否过期
        if (expireTime.isAfter(LocalDateTime.now())) {
            // 5.1 未过期，直接返回店铺信息
            return shop;
        }
        // 5.2 已过期，需要缓存重建
        String lockKey = RedisConstants.LOCK_SHOP_KEY + id;
        boolean isLock = tryLock(lockKey);
        if (isLock) {
            // 6.3 成功，开启独立线程，实现缓存重建
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                try {
                    // 重建缓存
                    this.saveShop2Redis(id, 20L);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    // 释放锁
                    unLock(lockKey);
                }
            });
        }
        // 6.4 返回过期的商铺信息
        return shop;
    }

    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", RedisConstants.LOCK_SHOP_TTL, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }

    private void unLock(String key) {
        stringRedisTemplate.delete(key);
    }

    private void saveShop2Redis(Long id,Long expireSeconds) {
        // 1、查询店铺数据
        Shop shop = getById(id);
        // 2、封装逻辑过期时间
        RedisData redisData = new RedisData();
        redisData.setData(shop);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(expireSeconds));
        // 3、写入redis
        stringRedisTemplate.opsForValue().set(RedisConstants.CACHE_SHOP_KEY, JSONUtil.toJsonStr(redisData));
    }

    @Override
    @Transactional
    public Result update(Shop shop) {
        Long id = shop.getId();
        if (id == null) {
            return Result.fail("店铺id不能为空");
        }
        // 1、更新数据库
        updateById(shop);
        // 2、删除缓存
        stringRedisTemplate.delete(RedisConstants.CACHE_SHOP_KEY + id);
        return Result.ok();
    }
}
