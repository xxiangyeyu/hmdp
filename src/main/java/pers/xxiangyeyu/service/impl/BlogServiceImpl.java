package pers.xxiangyeyu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.xxiangyeyu.entity.Blog;
import pers.xxiangyeyu.mapper.BlogMapper;
import pers.xxiangyeyu.service.IBlogService;

/**
 * @author xxiangyeyu
 * @since 2022-12-15
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {

}
