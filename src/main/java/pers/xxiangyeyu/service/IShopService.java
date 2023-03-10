package pers.xxiangyeyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.xxiangyeyu.dto.Result;
import pers.xxiangyeyu.entity.Shop;

/**
 * @author xxiangyeyu
 * @since 2022-12-15
 */
public interface IShopService extends IService<Shop> {

    Result queryById(Long id);

    Result update(Shop shop);
}
