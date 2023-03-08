package pers.xxiangyeyu;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pers.xxiangyeyu.service.impl.ShopServiceImpl;
import pers.xxiangyeyu.utils.RedisIdWorker;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class HmDianPingApplicationTests {

    @Autowired
    private ShopServiceImpl shopService;

    @Autowired
    private RedisIdWorker redisIdWorker;

    @Test
    public void testSaveShop(){
        shopService.saveShop2Redis(1L,10L);
    }

    private ExecutorService es = Executors.newFixedThreadPool(500);

    @Test
    public void testWorker() throws InterruptedException {
        // 计数器数字统计完后才执行end，目的就是等循环跑完，计数器统计完，才到end
        CountDownLatch latch = new CountDownLatch(300);
        Runnable task = () -> {
            for (int i = 0; i < 100; i++) {
                long id = redisIdWorker.nextId("order");
                System.out.println("id = " + id);
            }
            latch.countDown();
        };
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 300; i++) {
            es.submit(task);
        }
        latch.await();
        long end = System.currentTimeMillis();
        System.out.println("time = " + (end - begin));
    }
}
