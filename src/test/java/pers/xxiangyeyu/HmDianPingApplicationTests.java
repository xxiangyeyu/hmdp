package pers.xxiangyeyu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pers.xxiangyeyu.service.impl.ShopServiceImpl;

@SpringBootTest
public class HmDianPingApplicationTests {

    @Autowired
    private ShopServiceImpl shopService;

    public void testSaveShop(){
        shopService.saveShop2Redis(1L,10L);
    }
}
