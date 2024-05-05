package com.itheima.xiaotuxian.vo.goods;

import com.itheima.xiaotuxian.vo.goods.keyword.KeywordSaveVo;
import lombok.Data;

import java.util.List;

@Data
public class MultiKeywordVo {
    /**
     * 更新数据集合
     */
    private List<KeywordSaveVo> updates;
    /**
     * 删除数据集合
     */
    private List<String> removes;
}
