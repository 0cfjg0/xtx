package com.itheima.xiaotuxian.service.basic.impl;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.entity.basic.BasicAreaInfo;
import com.itheima.xiaotuxian.mapper.basic.BasicAreaInfoMapper;
import com.itheima.xiaotuxian.service.basic.BasicAreaInfoService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: bc.lv
 * @Date: 2023年3月10日16:46:47
 * @Description:
 */
@Slf4j
@Service
public class BasicAreaInfoServiceImpl extends ServiceImpl<BasicAreaInfoMapper, BasicAreaInfo>
        implements BasicAreaInfoService {
    
    /**
     * @description: 根据pid查询下一级的数据
     * @param {String} pid
     * @return {*}
     * @author: lbc
     */
    @Override
    public List<BasicAreaInfo> getBasicAreaInfo(String pid) {
        List<BasicAreaInfo> results = this.getBaseMapper().selectList(Wrappers.<BasicAreaInfo>lambdaQuery()
                                                        .eq(StringUtils.isNotBlank(pid), BasicAreaInfo::getParentId, pid)
                                                        //不传pid，默认查询全部1级的数据，即查询所有的省份信息
                                                        .eq(StringUtils.isBlank(pid), BasicAreaInfo::getLevelType, 1)); 
 
        return results;
    }




    private BasicAreaInfo getObjectById(String id) {
        BasicAreaInfo basicAreaInfo = this.getById(id);
        return basicAreaInfo;
    }

    private BasicAreaInfo insertAreaInfo(JSONObject jsonObject1, BasicAreaInfo parent) {
        BasicAreaInfo entity = new BasicAreaInfo();
        String code = jsonObject1.getString("code");
        String level = jsonObject1.getString("level");
        String name = jsonObject1.getString("name");
        entity.setCityCode(code);
        entity.setName(name);
        log.info(name);
        entity.setId(code);

        entity.setShortName(name.length()>3 ? name.replace("高新技术产业开发区", "高新区").replace("满族自治县", "") : name.replace("省", "").replace("市", "").replace("区", "").replace("县", ""));
        if (null == parent) {
            entity.setParentId("0");
            entity.setMergerName("中国");
            entity.setMergerShortName("中国");
        } else {
            entity.setParentId(parent.getId());
            entity.setMergerName(parent.getMergerName() + "," + name);
            entity.setMergerShortName(parent.getMergerShortName() + "," + entity.getShortName());
            entity.setCityCode(parent.getCityCode());
            entity.setZipCode(parent.getZipCode());
            entity.setLng(parent.getLng());
            entity.setLat(parent.getLat());
        }
        entity.setPinyin(PinyinUtil.getPinyin(name).replace(" ", ""));
        entity.setFirstChar(PinyinUtil.getPinyin(name).substring(0, 1).toUpperCase());
        entity.setJianpin(PinyinUtil.getFirstLetter(name,"").toUpperCase());
        entity.setLevelType(String.valueOf(Integer.valueOf(level)+1));
        entity.setRemarks("2023-05-20新增");

        this.getBaseMapper().insert(entity);
        return entity;
    }

    private void dealLevelData(JSONArray array, BasicAreaInfo parent, List<JSONObject>  logConflicts,AtomicReference<String> result) {

        array.stream().forEach(jsonObject -> {
            // 1级的
            JSONObject jsonObject1 = (JSONObject) jsonObject;
            String code = jsonObject1.getString("code");
            String level = jsonObject1.getString("level");
            String name = jsonObject1.getString("name");
            System.out.println(code);
            System.out.println(level);
            System.out.println(name);

            // 检查数据库是否存在此code的数据，若不存在，则直接入库，若已经存在，直接返回
            var codeCity = getObjectById(code);
            BasicAreaInfo curBasicAreaInfo = null;
            if (null == codeCity) {
                // 为空，入库数据  不为空的情况，不更新数据库的数据
                var likeNameCity  = this.getBaseMapper().selectList(Wrappers.<BasicAreaInfo>lambdaQuery()
                                        .like(BasicAreaInfo::getName, name.substring(0,2))
                                        /* .eq(BasicAreaInfo::getLevelType, level) */);
                boolean confictFlag = false;
                boolean oneExistFlag = false;

                if(name.length() > 4){
                    var likeMoreNameCity  = this.getBaseMapper().selectList(Wrappers.<BasicAreaInfo>lambdaQuery()
                    .like(BasicAreaInfo::getName, name.substring(0,4)));
                    if(CollUtil.isNotEmpty(likeNameCity) && CollUtil.isNotEmpty(likeMoreNameCity) ){
                        logConflicts.add(jsonObject1);
                        confictFlag =true;
                        oneExistFlag = likeNameCity.size() == 1 && likeMoreNameCity.size() == 1 ? true:false;
                    }
                }else{
                    if(CollUtil.isNotEmpty(likeNameCity)){
                        logConflicts.add(jsonObject1);
                        confictFlag =true;
                        oneExistFlag = likeNameCity.size() == 1? true:false;
                    }
                }
                if(confictFlag){
                      //修复json文件 再写入文件中
                      if(oneExistFlag){
                        // result
                        result.set(result.get().replace(code, likeNameCity.get(0).getId()));
                        result.set(result.get().replace(name, likeNameCity.get(0).getName()));
                    }
                }else{
                    curBasicAreaInfo = insertAreaInfo(jsonObject1, parent);
                }
            }else{
                curBasicAreaInfo = codeCity;
            }
           
            JSONArray areaList = jsonObject1.getJSONArray("areaList");
            if (null == areaList || areaList.size() < 1) {
                log.info("最后一级了,name" + name);
            } else {
                if(null != curBasicAreaInfo ){
                    log.info("父级的name" + curBasicAreaInfo.getName());
                    dealLevelData(areaList, curBasicAreaInfo,logConflicts,result);
                } 
            }
        });

    }

   
}
