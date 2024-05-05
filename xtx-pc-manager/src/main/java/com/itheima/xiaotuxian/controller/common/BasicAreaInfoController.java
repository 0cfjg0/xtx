package com.itheima.xiaotuxian.controller.common;
/*
 * @author: lbc
 * @Date: 2023-05-20 16:50:18
 * @Descripttion:
 */

import java.util.List;

import com.itheima.xiaotuxian.entity.basic.BasicAreaInfo;
import com.itheima.xiaotuxian.service.basic.BasicAreaInfoService;
import com.itheima.xiaotuxian.vo.R;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/basicArea")
public class BasicAreaInfoController {

    @Autowired
    private BasicAreaInfoService basicAreaInfoService;


    /**
     * 根据传值，查询下一级的地址基础信息
     * 传pid，查询下一级。不传值，默认查询一级的（即所有省份的数据）
     * @description:
     * @param {*}
     * @return {*}
     * @author: lbc
     */
    @GetMapping()
    public R<List<BasicAreaInfo>> getBasicAreaInfo(@RequestParam(name = "pid", required = false) String pid) {
        var basicAreaInfos = basicAreaInfoService.getBasicAreaInfo(pid);
        return R.ok(basicAreaInfos);
    }
}
