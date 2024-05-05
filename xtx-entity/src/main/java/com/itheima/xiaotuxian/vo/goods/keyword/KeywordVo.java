package com.itheima.xiaotuxian.vo.goods.keyword;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class KeywordVo {
    /**
     * id
     */
    private String id;
    /**
     * 关键词
     */
    private List<String> title;
    /**
     * 联想词
     */
    private List<String> associateWords;
    /**
     * 状态，0为开启，1为关闭
     */
    private Integer state;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /**
     * 关联信息
     */
    private String relationInfo;
    /**
     * 关联关系集合
     */
    private List<KeywordRelationVo> relations;
}
