package pers.xxiangyeyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import pers.xxiangyeyu.entity.Voucher;

import java.util.List;

/**
 * @author xxiangyeyu
 * @since 2022-12-15
 */
public interface VoucherMapper extends BaseMapper<Voucher> {

    List<Voucher> queryVoucherOfShop(@Param("shopId") Long shopId);
}
