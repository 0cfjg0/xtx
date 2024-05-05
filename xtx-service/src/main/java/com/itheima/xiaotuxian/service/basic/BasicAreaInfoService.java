package com.itheima.xiaotuxian.service.basic;
/*
 * @author: lbc
 * @Date: 2023-04-29 17:25:37
 * @Descripttion:
 */

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.basic.BasicAreaInfo;

public interface BasicAreaInfoService extends IService<BasicAreaInfo> {



    List<BasicAreaInfo> getBasicAreaInfo(String pid);
}
