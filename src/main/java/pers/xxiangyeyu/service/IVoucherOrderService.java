package pers.xxiangyeyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.xxiangyeyu.dto.Result;
import pers.xxiangyeyu.entity.VoucherOrder;

/**
 * @author xxiangyeyu
 * @since 2022-12-15
 */
public interface IVoucherOrderService extends IService<VoucherOrder> {

    Result seckillVoucher(Long voucherId);

    Result createVoucherOrder(Long voucherId);
}
