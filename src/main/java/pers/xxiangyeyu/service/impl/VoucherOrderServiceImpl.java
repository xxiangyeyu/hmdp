package pers.xxiangyeyu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.xxiangyeyu.dto.Result;
import pers.xxiangyeyu.entity.SeckillVoucher;
import pers.xxiangyeyu.entity.VoucherOrder;
import pers.xxiangyeyu.mapper.VoucherOrderMapper;
import pers.xxiangyeyu.service.ISeckillVoucherService;
import pers.xxiangyeyu.service.IVoucherOrderService;
import pers.xxiangyeyu.utils.RedisIdWorker;
import pers.xxiangyeyu.utils.UserHolder;

import java.time.LocalDateTime;

/**
 * @author xxiangyeyu
 * @since 2022-12-15
 */
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

    @Autowired
    private ISeckillVoucherService iSeckillVoucherService;

    @Autowired
    private RedisIdWorker redisIdWorker;

    @Override
    public Result seckillVoucher(Long voucherId) {
        // 1、查询优惠券
        SeckillVoucher voucher = iSeckillVoucherService.getById(voucherId);
        // 2、判断秒杀是否开始
        if (voucher.getBeginTime().isAfter(LocalDateTime.now())) {
            // 尚未开始
            return Result.fail("秒杀尚未开始！");
        }
        // 3、判断秒杀是否已经结束
        if (voucher.getEndTime().isBefore(LocalDateTime.now())) {
            // 尚未开始
            return Result.fail("秒杀已经结束！");
        }
        // 4、判断库存是否充足
        if (voucher.getStock() < 1) {
            // 库存不足
            return Result.fail("库存不足");
        }

        Long userId = UserHolder.getUser().getId();
        synchronized (userId.toString().intern()) {
            // 获取代练对象（事务）
            IVoucherOrderService proxy = (IVoucherOrderService)AopContext.currentProxy();
            // 注：this.createVoucherOrder(voucherId)，非代理对象，本身是不具备事务属性，Spring事务失效可能性之一
            return proxy.createVoucherOrder(voucherId);
        }
    }

    @Transactional
    public Result createVoucherOrder(Long voucherId) {
        // 5、一人一单
        Long userId = UserHolder.getUser().getId();
        // 5.1、查询订单
        int count = query().eq("user_id", userId).eq("voucher_id", voucherId).count();
        // 5.2、判断是否存在
        if (count > 0) {
            // 用户已经购买过了
            return Result.fail("用户已经购买过一次！");
        }
        // 6、扣减库存
        boolean success = iSeckillVoucherService.update()
                .setSql("stock = stock - 1")
                .eq("voucher_id", voucherId).gt("stock", 0)
                .update();
        if (!success) {
            // 扣减失败
            return Result.fail("库存不足");
        }
        // 7、创建订单
        VoucherOrder voucherOrder = new VoucherOrder();
        // 7.1、订单id
        long orderId = redisIdWorker.nextId("order");
        voucherOrder.setId(orderId);
        // 7.2、用户id
        voucherOrder.setUserId(userId);
        // 7.3、代金券id
        voucherOrder.setVoucherId(voucherId);
        save(voucherOrder);
        // 8、返回订单id
        return Result.ok(orderId);
    }
}
