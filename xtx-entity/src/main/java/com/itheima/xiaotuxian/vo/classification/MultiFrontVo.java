package com.itheima.xiaotuxian.vo.classification;

import lombok.Data;

import java.util.List;

@Data
public class MultiFrontVo {
    /**
     * 更新数据集合
     */
    private List<FrontSaveVo> updates;
    /**
     * 删除数据集合
     */
    private List<String> removes;
}
