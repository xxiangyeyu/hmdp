package pers.xxiangyeyu.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.xxiangyeyu.dto.Result;
import pers.xxiangyeyu.entity.ShopType;
import pers.xxiangyeyu.service.IShopTypeService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xxiangyeyu
 * @since 2022-12-15
 */
@RestController
@RequestMapping("/shop-type")
public class ShopTypeController {
    @Resource
    private IShopTypeService typeService;

    @GetMapping("list")
    public Result queryTypeList() {
        List<ShopType> typeList = typeService
                .query().orderByAsc("sort").list();
        return Result.ok(typeList);
    }
}
