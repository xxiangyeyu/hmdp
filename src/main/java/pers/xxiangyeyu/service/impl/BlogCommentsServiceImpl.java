package pers.xxiangyeyu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.xxiangyeyu.entity.BlogComments;
import pers.xxiangyeyu.mapper.BlogCommentsMapper;
import pers.xxiangyeyu.service.IBlogCommentsService;

/**
 * @author xxiangyeyu
 * @since 2022-12-15
 */
@Service
public class BlogCommentsServiceImpl extends ServiceImpl<BlogCommentsMapper, BlogComments> implements IBlogCommentsService {

}
