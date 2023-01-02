package pers.xxiangyeyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.xxiangyeyu.dto.Result;
import pers.xxiangyeyu.entity.Voucher;

/**
 * @author xxiangyeyu
 * @since 2022-12-15
 */
public interface IVoucherService extends IService<Voucher> {

    Result queryVoucherOfShop(Long shopId);

    void addSeckillVoucher(Voucher voucher);
}
