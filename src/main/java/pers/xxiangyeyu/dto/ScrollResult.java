package pers.xxiangyeyu.dto;

import lombok.Data;

import java.util.List;

/**
 * @author xxiangyeyu
 * @since 2022-12-15
 */
@Data
public class ScrollResult {
    private List<?> list;
    private Long minTime;
    private Integer offset;
}
