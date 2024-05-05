package com.itheima.xiaotuxian.service.goods.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.statics.CommonStatic;
import com.itheima.xiaotuxian.constant.statics.FrontStatic;
import com.itheima.xiaotuxian.constant.statics.GoodsStatic;
import com.itheima.xiaotuxian.constant.statics.UserMemberStatic;
import com.itheima.xiaotuxian.entity.goods.*;
import com.itheima.xiaotuxian.entity.member.UserMemberCollect;
import com.itheima.xiaotuxian.entity.property.PropertyValue;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.mapper.goods.*;
import com.itheima.xiaotuxian.service.classification.ClassificationBackendService;
import com.itheima.xiaotuxian.service.classification.ClassificationFrontService;
import com.itheima.xiaotuxian.service.goods.*;
import com.itheima.xiaotuxian.service.manager.ManagerAdminService;
import com.itheima.xiaotuxian.service.material.MaterialPictureService;
import com.itheima.xiaotuxian.service.material.MaterialVideoService;
import com.itheima.xiaotuxian.service.member.UserMemberAddressService;
import com.itheima.xiaotuxian.service.member.UserMemberCollectService;
import com.itheima.xiaotuxian.service.mq.producer.GoodsProducer;
import com.itheima.xiaotuxian.service.order.OrderSkuEvaluateService;
import com.itheima.xiaotuxian.service.property.PropertyGroupService;
import com.itheima.xiaotuxian.service.property.PropertyMainService;
import com.itheima.xiaotuxian.service.property.PropertyValueService;
import com.itheima.xiaotuxian.vo.classification.BackendSimpleVo;
import com.itheima.xiaotuxian.vo.classification.FrontSimpleVo;
import com.itheima.xiaotuxian.vo.goods.brand.BrandSimpleVo;
import com.itheima.xiaotuxian.vo.goods.goods.*;
import com.itheima.xiaotuxian.vo.goods.goods.goodsNew.GoodsDetailNewVo;
import com.itheima.xiaotuxian.vo.goods.goods.goodsNew.SkuSaleConfigNewVo;
import com.itheima.xiaotuxian.vo.material.PictureSimpleVo;
import com.itheima.xiaotuxian.vo.property.PropertySimpleVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: itheima
 * @Date: 2023/7/11 3:28 下午
 * @Description:
 */
@Service
@Slf4j
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private GoodsSpuService goodsSpuService;
    @Autowired
    private GoodsSkuService skuService;
    @Autowired
    private GoodsSkuPropertyValueService skuPropertyValueService;
    @Autowired
    private GoodsSpuMainPictureMapper spuMainPictureMapper;
    @Autowired
    private GoodsSpuMainVideoMapper spuMainVideoMapper;
    @Autowired
    private GoodsSpuPictureMapper spuPictureMapper;
    @Autowired
    private GoodsSpuPropertyMapper spuPropertyMapper;
    @Autowired
    private GoodsSkuPropertyValueMapper skuPropertyValueMapper;
    @Autowired
    private GoodsAuditLogMapper auditLogMapper;
    @Autowired
    private MaterialPictureService pictureService;
    @Autowired
    private MaterialVideoService videoService;
    @Autowired
    private ClassificationBackendService backendService;
    @Autowired
    private GoodsBrandService brandService;
    @Autowired
    private PropertyGroupService propertyGroupService;
    @Autowired
    private PropertyMainService propertyMainService;
    @Autowired
    private PropertyValueService propertyValueService;
    @Autowired
    private GoodsProducer goodsProducer;
    @Autowired
    private ManagerAdminService adminService;
    @Autowired
    private OrderSkuEvaluateService skuEvaluateService;
    @Autowired
    private UserMemberCollectService collectService;
    @Autowired
    private UserMemberAddressService addressService;
    @Autowired
    private ClassificationFrontService frontService;

    /**
     * 后台管理保存商品信息
     * @param saveVo 商品信息
     * @param opUser
     * @return
     */
    @Transactional
    @Override
    public Boolean saveGoods(GoodsSaveVo saveVo, String opUser) {
        var spu = BeanUtil.toBean(saveVo, GoodsSpu.class);
        //检查并设置spu编码
        Optional.ofNullable(spu.getId()).ifPresentOrElse(id ->
                Optional.ofNullable(goodsSpuService.getById(id))
                        .ifPresentOrElse(source -> spuCodeDuplicate(spu.getSpuCode(), id),
                                () -> {
                                    throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
                                })
                , () -> {
                    spuCodeDuplicate(spu.getSpuCode(), null);
                    spu.setCreator(opUser);
                }
        );
        // 处理后台类目信息
        Optional.ofNullable(saveVo.getBackend()).map(BackendSimpleVo::getId)
                .filter(StrUtil::isNotEmpty)
                .ifPresent(id ->
                        Optional.ofNullable(backendService.getById(id))
                                .ifPresentOrElse(backend -> spu.setClassificationBackendId(backend.getId())
                                        , () -> {
                                            throw new BusinessException(ErrorMessageEnum.GOODS_INVALID_BACKEND);
                                        })
                );
        // 处理品牌信息
        Optional.ofNullable(saveVo.getBrand()).map(BrandSimpleVo::getId)
                .filter(StrUtil::isNotEmpty)
                .ifPresent(id ->
                        Optional.ofNullable(brandService.getById(id))
                                .ifPresentOrElse(brand -> spu.setBrandId(brand.getId())
                                        , () -> {
                                            throw new BusinessException(ErrorMessageEnum.GOODS_INVALID_BRAND);
                                        })
                );
        // 处理上架时间
        Optional.ofNullable(saveVo.getShelfTime()).filter(StrUtil::isNotEmpty).ifPresent(shelfTime -> spu.setShelfTime(LocalDateTime.parse(shelfTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        // 将商品重置回待审核状态
        spu.setAuditState(GoodsStatic.AUDIT_STATE_WAIT);
        //保存主体信息
        Optional.ofNullable(spu.getId()).ifPresentOrElse(id -> goodsSpuService.updateById(spu), () -> goodsSpuService.save(spu));
        // 处理商品主图信息
        Optional.ofNullable(saveVo.getMainPictures()).ifPresent(pictureSaveVo -> {
                    spuMainPictureMapper.delete(Wrappers.<GoodsSpuMainPicture>lambdaQuery().eq(GoodsSpuMainPicture::getSpuId, spu.getId()));
                    Optional.ofNullable(pictureSaveVo.getPc()).filter(CollUtil::isNotEmpty).ifPresent(pictureIds ->
                            pictureIds.forEach(pictureId -> {
                                var gsmp = new GoodsSpuMainPicture();
                                gsmp.setPictureId(pictureId);
                                gsmp.setCreateTime(LocalDateTime.now());
                                gsmp.setSpuId(spu.getId());
                                gsmp.setType(CommonStatic.MATERIAL_SHOW_PC);
                                spuMainPictureMapper.insert(gsmp);
                            }));
                    Optional.ofNullable(pictureSaveVo.getApp()).filter(CollUtil::isNotEmpty).ifPresent(pictureIds ->
                            pictureIds.forEach(pictureId -> {
                                var gsmp = new GoodsSpuMainPicture();
                                gsmp.setPictureId(pictureId);
                                gsmp.setCreateTime(LocalDateTime.now());
                                gsmp.setSpuId(spu.getId());
                                gsmp.setType(CommonStatic.MATERIAL_SHOW_APP);
                                spuMainPictureMapper.insert(gsmp);
                            }));
                }
        );
        // 处理主视频信息
        Optional.ofNullable(saveVo.getMainVideos()).ifPresent(videoSaveVo -> {
                    spuMainVideoMapper.delete(Wrappers.<GoodsSpuMainVideo>lambdaQuery().eq(GoodsSpuMainVideo::getSpuId, spu.getId()));
                    Optional.ofNullable(videoSaveVo.getPc()).ifPresent(videoId -> {
                        var gsmv = new GoodsSpuMainVideo();
                        gsmv.setVideoId(videoId);
                        gsmv.setCreateTime(LocalDateTime.now());
                        gsmv.setSpuId(spu.getId());
                        gsmv.setType(CommonStatic.MATERIAL_SHOW_PC);
                        spuMainVideoMapper.insert(gsmv);
                    });
                    Optional.ofNullable(videoSaveVo.getApp()).ifPresent(videoId -> {
                        var gsmv = new GoodsSpuMainVideo();
                        gsmv.setVideoId(videoId);
                        gsmv.setCreateTime(LocalDateTime.now());
                        gsmv.setSpuId(spu.getId());
                        gsmv.setType(CommonStatic.MATERIAL_SHOW_APP);
                        spuMainVideoMapper.insert(gsmv);
                    });
                }
        );
        // 处理商品图片信息
        Optional.ofNullable(saveVo.getPictures()).ifPresent(pictureSaveVo -> {
                    spuPictureMapper.delete(Wrappers.<GoodsSpuPicture>lambdaQuery().eq(GoodsSpuPicture::getSpuId, spu.getId()));
                    Optional.ofNullable(pictureSaveVo.getPc()).filter(CollUtil::isNotEmpty).ifPresent(pictureIds ->
                            pictureIds.forEach(pictureId -> {
                                var gsp = new GoodsSpuPicture();
                                gsp.setPictureId(pictureId);
                                gsp.setCreateTime(LocalDateTime.now());
                                gsp.setSpuId(spu.getId());
                                gsp.setType(CommonStatic.MATERIAL_SHOW_PC);
                                spuPictureMapper.insert(gsp);
                            }));
                    Optional.ofNullable(pictureSaveVo.getApp()).filter(CollUtil::isNotEmpty).ifPresent(pictureIds ->
                            pictureIds.forEach(pictureId -> {
                                var gsp = new GoodsSpuPicture();
                                gsp.setPictureId(pictureId);
                                gsp.setCreateTime(LocalDateTime.now());
                                gsp.setSpuId(spu.getId());
                                gsp.setType(CommonStatic.MATERIAL_SHOW_APP);
                                spuPictureMapper.insert(gsp);
                            }));
                }
        );
        // 处理商品非销售属性
        Optional.ofNullable(saveVo.getSpuProperties())
                .filter(CollUtil::isNotEmpty)
                .ifPresent(propertyValueVos -> {

                            spuPropertyMapper.delete(Wrappers.<GoodsSpuProperty>lambdaQuery().eq(GoodsSpuProperty::getSpuId, spu.getId()));
                            propertyValueVos.forEach(propertyValueVo ->
                                    Optional.ofNullable(propertyValueVo.getPropertyValueName()).filter(CollUtil::isNotEmpty).ifPresent(valueNames ->
                                            valueNames.forEach(valueName -> {
                                                var gsp = new GoodsSpuProperty();
                                                gsp.setCreateTime(LocalDateTime.now());
                                                gsp.setPropertyGroupName(propertyValueVo.getPropertyGroupName());
                                                gsp.setPropertyMainName(propertyValueVo.getPropertyMainName());
                                                gsp.setPropertyValueName(valueName);
                                                gsp.setSpuId(spu.getId());
                                                spuPropertyMapper.insert(gsp);
                                            })));
                        }
                );
        // 处理商品销售属性
        Optional.ofNullable(saveVo.getSaleProperties())
                .filter(CollUtil::isNotEmpty)
                .ifPresent(saleProperties -> {
                    //获取当前已存在skuId集合
                    var sourceIds = skuService.list(Wrappers.<GoodsSku>lambdaQuery().eq(GoodsSku::getSpuId, spu.getId())).stream().map(GoodsSku::getId).collect(Collectors.toList());
                    var skuIds = new ArrayList<String>();
                    saleProperties.forEach(saleProperty -> {
                        var sku = BeanUtil.toBean(saleProperty, GoodsSku.class);
                        sku.setSpuId(spu.getId());
                        Optional.ofNullable(sku.getId()).ifPresentOrElse(id ->
                                        Optional.ofNullable(skuService.getById(id))
                                                .ifPresentOrElse(source -> {
                                                            skuCodeDuplicate(sku.getSkuCode(), spu.getId(), source.getId());
                                                            skuService.updateById(sku);
                                                        },
                                                        () -> {
                                                            throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
                                                        })
                                , () -> {
                                    skuCodeDuplicate(sku.getSkuCode(), spu.getId(), null);
                                    sku.setCreator(opUser);
                                    skuService.save(sku);
                                }
                        );
                        skuIds.add(sku.getId());
                        // 处理sku属性值
                        Optional.ofNullable(saleProperty.getPropertyValues())
                                .filter(CollUtil::isNotEmpty)
                                .ifPresent(propertyValueVos -> {
                                    skuPropertyValueMapper.delete(Wrappers.<GoodsSkuPropertyValue>lambdaQuery().eq(GoodsSkuPropertyValue::getSkuId, sku.getId()));
                                    propertyValueVos.forEach(propertyValueVo -> {
                                        var skuPropertyValue = BeanUtil.toBean(propertyValueVo, GoodsSkuPropertyValue.class);
                                        skuPropertyValue.setSkuId(sku.getId());
                                        skuPropertyValue.setCreateTime(LocalDateTime.now());
                                        // 处理属性组
                                        fillSkuPropertyValue(skuPropertyValue,opUser);
                                        // 处理值图片
                                        Optional.ofNullable(propertyValueVo.getPropertyValuePicture())
                                                .map(PictureSimpleVo::getId).filter(StrUtil::isNotEmpty)
                                                .ifPresent(id ->
                                                        Optional.ofNullable(pictureService.findById(id)).ifPresent(picture -> skuPropertyValue.setPropertyValuePictureId(id))
                                                );
                                        skuPropertyValueMapper.insert(skuPropertyValue);
                                    });
                                });
                    });

                    // 移除无效sku
                    Optional.ofNullable(CollUtil.disjunction(CollUtil.intersection(sourceIds, skuIds), sourceIds))
                            .filter(CollUtil::isNotEmpty)
                            .ifPresent(ids -> {
                                skuPropertyValueMapper.delete(Wrappers.<GoodsSkuPropertyValue>lambdaQuery().in(GoodsSkuPropertyValue::getSkuId, ids));
                                skuService.remove(Wrappers.<GoodsSku>lambdaQuery().eq(GoodsSku::getSpuId, spu.getId()).in(GoodsSku::getId, ids));
                            });
                });
        // 发送商品操作消息
        goodsProducer.sendOperator(spu.getId(), GoodsStatic.OP_TYPE_SAVE);
        return true;
    }

    @Transactional
    @Override
    public Boolean deleteById(String id) {
        Optional.ofNullable(goodsSpuService.getById(id)).ifPresent(spu -> {
            spu.setState(GoodsStatic.STATE_RECYCLE);
            spu.setDeleteTime(LocalDateTime.now());
            goodsSpuService.updateById(spu);
            // 发送商品操作消息
            goodsProducer.sendOperator(spu.getId(), GoodsStatic.OP_TYPE_DELETE);
        });
        return true;
    }

    @Transactional
    @Override
    public Boolean auditGoods(GoodsAuditSaveVo auditSaveVo) {
        if (StrUtil.isEmpty(auditSaveVo.getId()) && CollUtil.isEmpty(auditSaveVo.getIds())) {
            throw new BusinessException(ErrorMessageEnum.OBJECT_EMPTY);
        }
        if (StrUtil.isNotEmpty(auditSaveVo.getId())) {
            auditGoods(auditSaveVo.getId(), auditSaveVo.getAuditState(), auditSaveVo.getRejectDecription());
            goodsProducer.sendOperator(auditSaveVo.getId(), GoodsStatic.OP_TYPE_AUDIT);
        }
        if (CollUtil.isNotEmpty(auditSaveVo.getIds())) {
            auditSaveVo.getIds().forEach(id -> auditGoods(id, auditSaveVo.getAuditState(), auditSaveVo.getRejectDecription()));
            auditSaveVo.getIds().forEach(id -> goodsProducer.sendOperator(id, GoodsStatic.OP_TYPE_AUDIT));
        }
        return true;
    }

    @Override
    public Boolean updateSaleableInventory(GoodsSaleableInventoryVo inventoryVo) {
        Optional.ofNullable(skuService.getById(inventoryVo.getId())).ifPresentOrElse(sku -> {
            sku.setSaleableInventory(inventoryVo.getSaleableInventory());
            skuService.updateById(sku);
            goodsProducer.sendOperator(sku.getSpuId(), GoodsStatic.OP_TYPE_FLUSH);
        }, () -> {
            throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
        });
        return true;
    }

    @Override
    public Boolean shelfGoods(String id, Integer state) {
        GoodsSpu spu = goodsSpuService.getById(id);
        if (spu == null) {
            throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
        }
        Stream.of(state).filter(s -> s == GoodsStatic.STATE_SELLING).forEach(s -> {
            goodsProducer.sendOperator(id, GoodsStatic.OP_TYPE_FLUSH);
            spu.setState(s);
            goodsSpuService.updateById(spu);
        });
        Stream.of(state).filter(s -> s == GoodsStatic.STATE_STORING).forEach(s -> {
            goodsProducer.sendOperator(id, GoodsStatic.OP_TYPE_SAVE);
            spu.setState(s);
            goodsSpuService.updateById(spu);
        });
        return true;
    }

    /**
     * 通过查询条件查询关联的商品spu信息
     *
     * @param queryVo 查询条件
     * @return
     */
    @Override
    public Page<GoodsVo> findByPage(GoodsQueryPageVo queryVo) {
        // 处理前台分类参数
        var frontBackEndIds = new HashSet<String>();
        Optional.ofNullable(queryVo.getFrontId()).filter(StrUtil::isNotEmpty).ifPresent(frontId -> {
            frontBackEndIds.addAll(backendService.findBackendIdsByFrontIdDown(frontId));
            Stream.of(frontBackEndIds).filter(CollUtil::isEmpty).forEach(ids -> queryVo.setBan("-1000"));
        });
        //构建查询
        var pageResult = new Page<GoodsSpu>(queryVo.getPage() == null ? 1 : queryVo.getPage(), queryVo.getPageSize() == null ? 10 : queryVo.getPageSize());
       var pageData = goodsSpuService.getPageData(queryVo,pageResult,frontBackEndIds);
        var result = new Page<GoodsVo>(pageData.getCurrent(), pageData.getSize(), pageData.getTotal());
        result.setRecords(pageData.getRecords().stream()
                .map(spu -> {
                    var vo = BeanUtil.toBean(spu, GoodsVo.class);
                    //处理图片
                    Optional.ofNullable(spuMainPictureMapper.selectOne(Wrappers.<GoodsSpuMainPicture>lambdaQuery()
                            .eq(GoodsSpuMainPicture::getSpuId, spu.getId())
                            .last("LIMIT 1")
                    )).ifPresent(goodsSpuMainPicture -> Optional.ofNullable(pictureService.findById(goodsSpuMainPicture.getPictureId()))
                            .ifPresent(picture -> vo.setPicture(picture.getUrl()))
                    );
                    //处理提交人信息
                    Optional.ofNullable(adminService.getById(spu.getCreator()))
                            .ifPresent(admin -> {
                                admin.setPassword(null);
                                admin.setUsername(null);
                                vo.setCreator(admin);
                            });
                    //处理审核信息
                    Optional.ofNullable(auditLogMapper.selectList(Wrappers.<GoodsAuditLog>lambdaQuery().eq(GoodsAuditLog::getSpuId, spu.getId()).orderByDesc(GoodsAuditLog::getCreateTime)))
                            .filter(CollUtil::isNotEmpty)
                            .ifPresent(logs -> {
                                var log = logs.get(0);
                                vo.setAuditDesc(log.getRejectDecription());
                                vo.setAuditTime(log.getCreateTime());
                            });
                    return vo;
                })
                .collect(Collectors.toList()));
        return result;
    }

    @Override
    public Page<GoodsSkuVo> findSkuByPage(SkuQueryVo queryVo) {
        var pageResult = new Page<GoodsSku>(queryVo.getPage() == null ? 1 : queryVo.getPage(), queryVo.getPageSize() == null ? 10 : queryVo.getPageSize());
        var pageData = skuService.findByPage(pageResult, queryVo);
        var result = new Page<GoodsSkuVo>(pageData.getCurrent(), pageData.getSize(), pageData.getTotal());
        result.setRecords(pageData.getRecords().stream()
                .map(sku -> {
                    var vo = BeanUtil.toBean(sku, GoodsSkuVo.class);
                    //处理价格信息
                    vo.setPrice(sku.getSellingPrice());
                    //处理spu信息
                    Optional.ofNullable(goodsSpuService.getById(sku.getSpuId()))
                            .ifPresent(spu -> {
                                vo.setName(spu.getName());
                                vo.setSpuCode(spu.getSpuCode());
                                vo.setState(spu.getState());
                                vo.setPublishTime(spu.getPublishTime());
                                //处理图片信息
                                Optional.ofNullable(spuMainPictureMapper.selectOne(Wrappers.<GoodsSpuMainPicture>lambdaQuery()
                                        .eq(GoodsSpuMainPicture::getSpuId, spu.getId())
                                        .orderByAsc(GoodsSpuMainPicture::getCreateTime)
                                        .last("LIMIT 1")
                                )).ifPresent(goodsSpuMainPicture -> Optional.ofNullable(pictureService.findById(goodsSpuMainPicture.getPictureId()))
                                        .ifPresent(picture -> vo.setPicture(picture.getUrl()))
                                );
                            });
                    //处理规格信息
                    Optional.ofNullable(skuPropertyValueMapper.selectList(Wrappers.<GoodsSkuPropertyValue>lambdaQuery().eq(GoodsSkuPropertyValue::getSkuId, sku.getId())))
                            .filter(CollUtil::isNotEmpty)
                            .ifPresent(skuPropertyValues -> vo.setSpecification(
                                    String.join("-", skuPropertyValues.stream().map(GoodsSkuPropertyValue::getPropertyValueName).collect(Collectors.toList())))
                            );
                    return vo;
                })
                .collect(Collectors.toList()));
        return result;
    }

    @Override
    public GoodsDetailVo findGoodsById(String id) {
        var spu = goodsSpuService.getById(id);
        if (spu == null) {
            throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
        }
        var detailVo = BeanUtil.toBean(spu, GoodsDetailVo.class);
        // 处理后台分类信息
        Optional.ofNullable(spu.getClassificationBackendId())
                .filter(StrUtil::isNotEmpty)
                .ifPresent(backendId -> detailVo.setBackend(backendService.findBackendLink(backendId)));
        // 处理品牌信息
        Optional.ofNullable(spu.getBrandId())
                .filter(StrUtil::isNotEmpty)
                .ifPresent(brandId -> Optional.ofNullable(brandService.getById(brandId))
                        .ifPresent(brand -> detailVo.setBrand(BeanUtil.toBean(brand, BrandSimpleVo.class)))
                );
        // 处理主图片集合
        Optional.ofNullable(spuMainPictureMapper.selectList(Wrappers.<GoodsSpuMainPicture>lambdaQuery().eq(GoodsSpuMainPicture::getSpuId, spu.getId())))
                .filter(CollUtil::isNotEmpty)
                .ifPresent(goodsSpuMainPictures -> {
                    var mainPictures = goodsSpuMainPictures.stream().collect(Collectors.groupingBy(GoodsSpuMainPicture::getType));
                    var mainPictureVos = new GoodsPictureVo();
                    Optional.ofNullable(mainPictures.get(CommonStatic.MATERIAL_SHOW_PC)).filter(CollUtil::isNotEmpty).ifPresent(pictures ->
                            mainPictureVos.setPc(pictures.stream().map(goodsSpuMainPicture -> {
                                var ar = new AtomicReference<GoodsMaterialVo>();
                                Optional.ofNullable(pictureService.findById(goodsSpuMainPicture.getPictureId()))
                                        .ifPresent(picture -> {
                                            var vo = new GoodsMaterialVo();
                                            vo.setId(picture.getId());
                                            vo.setName(picture.getName());
                                            vo.setUrl(picture.getUrl());
                                            ar.set(vo);
                                        });
                                return ar.get();
                            }).filter(Objects::nonNull).collect(Collectors.toList())));
                    Optional.ofNullable(mainPictures.get(CommonStatic.MATERIAL_SHOW_APP)).filter(CollUtil::isNotEmpty).ifPresent(pictures ->
                            mainPictureVos.setApp(pictures.stream().map(goodsSpuMainPicture -> {
                                var ar = new AtomicReference<GoodsMaterialVo>();
                                Optional.ofNullable(pictureService.findById(goodsSpuMainPicture.getPictureId()))
                                        .ifPresent(picture -> {
                                            var vo = new GoodsMaterialVo();
                                            vo.setId(picture.getId());
                                            vo.setName(picture.getName());
                                            vo.setUrl(picture.getUrl());
                                            ar.set(vo);
                                        });
                                return ar.get();
                            }).filter(Objects::nonNull).collect(Collectors.toList())));
                    detailVo.setMainPictures(mainPictureVos);
                });
        // 处理主视频集合
        Optional.ofNullable(spuMainVideoMapper.selectList(Wrappers.<GoodsSpuMainVideo>lambdaQuery().eq(GoodsSpuMainVideo::getSpuId, spu.getId())))
                .filter(CollUtil::isNotEmpty)
                .ifPresent(goodsSpuMainVideos -> {
                            var mainVideos = goodsSpuMainVideos.stream().collect(Collectors.groupingBy(GoodsSpuMainVideo::getType));
                            var videoVos = new GoodsVideoVo();
                            Optional.ofNullable(mainVideos.get(CommonStatic.MATERIAL_SHOW_PC)).filter(CollUtil::isNotEmpty).ifPresent(videos -> {
                                var list = videos.stream().map(goodsSpuMainVideo -> {
                                    var ar = new AtomicReference<GoodsVideoDetailVo>();
                                    Optional.ofNullable(videoService.findById(goodsSpuMainVideo.getVideoId()))
                                            .ifPresent(video -> {
                                                var vo = new GoodsVideoDetailVo();
                                                vo.setId(video.getId());
                                                vo.setName(video.getName());
                                                vo.setUrl(video.getUrl());
                                                vo.setCover(video.getCoverMark().equals(1) ? video.getCoverImg() : video.getSquareCoverImg());
                                                ar.set(vo);
                                            });
                                    return ar.get();
                                }).filter(Objects::nonNull).collect(Collectors.toList());
                                if (CollUtil.isNotEmpty(list)) {
                                    videoVos.setPc(list.get(0));
                                }
                            });
                            Optional.ofNullable(mainVideos.get(CommonStatic.MATERIAL_SHOW_APP)).filter(CollUtil::isNotEmpty).ifPresent(videos -> {
                                var list = videos.stream().map(goodsSpuMainVideo -> {
                                    var ar = new AtomicReference<GoodsVideoDetailVo>();
                                    Optional.ofNullable(videoService.findById(goodsSpuMainVideo.getVideoId()))
                                            .ifPresent(video -> {
                                                var vo = new GoodsVideoDetailVo();
                                                vo.setId(video.getId());
                                                vo.setName(video.getName());
                                                vo.setUrl(video.getUrl());
                                                vo.setCover(video.getCoverMark().equals(1) ? video.getCoverImg() : video.getSquareCoverImg());
                                                ar.set(vo);
                                            });
                                    return ar.get();
                                }).filter(Objects::nonNull).collect(Collectors.toList());
                                Stream.of(CollUtil.isNotEmpty(list)).filter(Boolean.TRUE::equals).forEach(match -> videoVos.setApp(list.get(0)));
                            });
                            detailVo.setMainVideos(videoVos);
                        }
                );
        // 处理商品图片集合
        Optional.ofNullable(spuPictureMapper.selectList(Wrappers.<GoodsSpuPicture>lambdaQuery().eq(GoodsSpuPicture::getSpuId, spu.getId())))
                .filter(CollUtil::isNotEmpty)
                .ifPresent(goodsSpuPictures ->
                        {
                            var mainPictures = goodsSpuPictures.stream().collect(Collectors.groupingBy(GoodsSpuPicture::getType));
                            var pictureVos = new GoodsPictureVo();
                            Optional.ofNullable(mainPictures.get(CommonStatic.MATERIAL_SHOW_PC)).filter(CollUtil::isNotEmpty).ifPresent(pictures ->
                                    pictureVos.setPc(pictures.stream().map(goodsSpuMainPicture -> {
                                        var ar = new AtomicReference<GoodsMaterialVo>();
                                        Optional.ofNullable(pictureService.findById(goodsSpuMainPicture.getPictureId()))
                                                .ifPresent(picture -> {
                                                    var vo = new GoodsMaterialVo();
                                                    vo.setId(picture.getId());
                                                    vo.setName(picture.getName());
                                                    vo.setUrl(picture.getUrl());
                                                    ar.set(vo);
                                                });
                                        return ar.get();
                                    }).filter(Objects::nonNull).collect(Collectors.toList())));
                            Optional.ofNullable(mainPictures.get(CommonStatic.MATERIAL_SHOW_APP)).filter(CollUtil::isNotEmpty).ifPresent(pictures ->
                                    pictureVos.setApp(pictures.stream().map(goodsSpuMainPicture -> {
                                        var ar = new AtomicReference<GoodsMaterialVo>();
                                        Optional.ofNullable(pictureService.findById(goodsSpuMainPicture.getPictureId()))
                                                .ifPresent(picture -> {
                                                    var vo = new GoodsMaterialVo();
                                                    vo.setId(picture.getId());
                                                    vo.setName(picture.getName());
                                                    vo.setUrl(picture.getUrl());
                                                    ar.set(vo);
                                                });
                                        return ar.get();
                                    }).filter(Objects::nonNull).collect(Collectors.toList())));

                            detailVo.setPictures(pictureVos);
                        }
                );
        // 处理审核日志
        Stream.of(spu.getAuditState()).filter(auditStatus -> GoodsStatic.AUDIT_STATE_WAIT != auditStatus).forEach(auditStatus ->
                Optional.ofNullable(auditLogMapper.selectList(Wrappers.<GoodsAuditLog>lambdaQuery().eq(GoodsAuditLog::getSpuId, spu.getId()).orderByDesc(GoodsAuditLog::getCreateTime)))
                        .filter(CollUtil::isNotEmpty)
                        .ifPresent(goodsAuditLogs ->
                                detailVo.setAuditLogs(goodsAuditLogs.stream().map(goodsAuditLog -> {
                                    var vo = BeanUtil.toBean(goodsAuditLog, AuditLogVo.class);
                                    vo.setCommiter("管理员");
                                    return vo;
                                }).collect(Collectors.toList()))));
        // 处理非销售属性集合
        Optional.ofNullable(spuPropertyMapper.selectList(Wrappers.<GoodsSpuProperty>lambdaQuery().eq(GoodsSpuProperty::getSpuId, spu.getId())))
                .filter(CollUtil::isNotEmpty)
                .ifPresent(goodsSpuProperties -> {
                    var spuProperties = new ArrayList<GoodsPropertyVo>();
                    var groupMap = goodsSpuProperties.stream().collect(Collectors.groupingBy(GoodsSpuProperty::getPropertyGroupName, Collectors.toList()));
                    groupMap.forEach((key, value) -> {
                        var propertyGroupVo = new GoodsPropertyVo();
                        propertyGroupVo.setName(key);
                        Optional.ofNullable(value)
                                .filter(CollUtil::isNotEmpty)
                                .ifPresent(properties -> {
                                    var propertyVos = new ArrayList<GoodsPropertyVo>();
                                    var propertyMap = properties.stream().collect(Collectors.groupingBy(GoodsSpuProperty::getPropertyMainName, Collectors.toList()));
                                    propertyMap.forEach((pkey, pvalue) -> {
                                        var propertyVo = new GoodsPropertyVo();
                                        propertyVo.setName(pkey);
                                        Optional.ofNullable(pvalue)
                                                .filter(CollUtil::isNotEmpty)
                                                .ifPresent(propertyValues -> propertyVo.setPropertyValues(CollUtil.getFieldValues(propertyValues, "propertyValueName", String.class)));
                                        propertyVos.add(propertyVo);
                                    });
                                    propertyGroupVo.setProperties(propertyVos);
                                });
                        spuProperties.add(propertyGroupVo);
                    });
                    detailVo.setSpuProperties(spuProperties);
                });
        // 处理商品sku信息
        var skuIndexMap = new HashMap<String, List<String>>();
        var skuInfoIndexMap = new HashMap<String, SkuIndexVo>();
        var tableMap = new HashMap<String, HashSet<String>>();
        var saleConfigMap = new HashMap<String, List<SkuSaleConfigVo>>();
        Optional.ofNullable(skuService.list(Wrappers.<GoodsSku>lambdaQuery().eq(GoodsSku::getSpuId, spu.getId()).orderByDesc(GoodsSku::getCreateTime)))
                .filter(CollUtil::isNotEmpty).ifPresent(goodsSkus ->
                goodsSkus.forEach(sku -> {
                    skuInfoIndexMap.put(sku.getId(), new SkuIndexVo(sku.getId(), sku.getSaleableInventory(), sku.getMarketPrice(), sku.getSellingPrice(), sku.getSkuCode()));
                    Optional.ofNullable(skuPropertyValueMapper.selectList(Wrappers.<GoodsSkuPropertyValue>lambdaQuery()
                            .eq(GoodsSkuPropertyValue::getSkuId, sku.getId())
                            .orderByDesc(GoodsSkuPropertyValue::getCreateTime))).filter(CollUtil::isNotEmpty).ifPresent(values ->
                            values.forEach(value -> {
                                // 处理table
                                Optional.ofNullable(tableMap.get(value.getPropertyMainName())).ifPresentOrElse(tableValues -> {
                                    tableValues.add(value.getPropertyValueName());
                                    tableMap.put(value.getPropertyMainName(), tableValues);
                                }, () -> {
                                    var tableValues = new HashSet<String>();
                                    tableValues.add(value.getPropertyValueName());
                                    tableMap.put(value.getPropertyMainName(), tableValues);
                                });
                                // 处理Index
                                Optional.ofNullable(skuIndexMap.get(sku.getId())).ifPresentOrElse(valueNames -> {
                                    valueNames.add(value.getPropertyValueName());
                                    skuIndexMap.put(sku.getId(), valueNames);
                                }, () -> {
                                    var valueNames = new ArrayList<String>();
                                    valueNames.add(value.getPropertyValueName());
                                    skuIndexMap.put(sku.getId(), valueNames);
                                });
                                // 处理已配置销售属性
                                String saleConfigKey = value.getPropertyGroupName() + "___" + value.getPropertyMainName();
                                Optional.ofNullable(saleConfigMap.get(saleConfigKey)).ifPresentOrElse(saleConfigs -> {
                                    var saleConfig = getSaleConfig(value);
                                    var appendFlags = new ArrayList<Boolean>();
                                    saleConfigs.forEach(sc -> {
                                        if ("中国码".equals(value.getPropertyParentValueName())) {
                                            //TODO ???
                                            System.out.println("aa");
                                        }
                                        var strValues = String.join("@|,@", sc.getValues());
                                        var strCurValues = String.join("@|,@", saleConfig.getValues());
                                        appendFlags.add(StrUtil.equals(strValues, strCurValues));
                                        log.info("strValues:{}  strCurValues:{}   saleConfigKey:{} ab:{}", strValues, strCurValues, saleConfigKey);
                                    });
                                    Stream.of(appendFlags).filter(flags -> flags.stream().allMatch(Boolean.FALSE::equals)).forEach(match -> {
//                                        Optional.ofNullable(value.getPropertyParentValueName()).filter(StrUtil::isNotEmpty).ifPresent(parentValueName ->
//                                                Optional.ofNullable(saleConfig.getValues()).filter(CollUtil::isNotEmpty).ifPresent(configValues -> configValues.add(0, parentValueName)));
                                        saleConfigs.add(saleConfig);
                                        saleConfigMap.put(saleConfigKey, saleConfigs);
                                    });
                                }, () -> {
                                    var saleConfigs = new ArrayList<SkuSaleConfigVo>();
                                    var saleConfig = getSaleConfig(value);
//                                    Optional.ofNullable(value.getPropertyParentValueName()).filter(StrUtil::isNotEmpty).ifPresent(parentValueName ->
//                                            Optional.ofNullable(saleConfig.getValues()).filter(CollUtil::isNotEmpty).ifPresent(configValues -> configValues.add(0, parentValueName)));
                                    saleConfigs.add(saleConfig);
                                    saleConfigMap.put(saleConfigKey, saleConfigs);
                                });
                            }));
                }));
        var skuInfo = new SkuInfoVo();
        skuInfo.setSkuIndex(skuIndexMap.entrySet().stream().collect(Collectors.toMap(e -> String.join("___", e.getValue()), e -> skuInfoIndexMap.get(e.getKey()))));
        skuInfo.setTables(tableMap.entrySet().stream().map(e -> new SkuTableVo(e.getKey(), new ArrayList<>(e.getValue()))).collect(Collectors.toList()));
        skuInfo.setSaleConfigs(saleConfigMap);
        detailVo.setSkuInfo(skuInfo);
        return detailVo;
    }


    /**
     * 根据id查询商品详情-新版
     * @param id
     * @return
     */
    @Override
    public GoodsDetailNewVo findGoodsByIdNew(String id) {
        var spu = goodsSpuService.getById(id);
        if (spu == null) {
            throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
        }
        var detailVo = BeanUtil.toBean(spu, GoodsDetailNewVo.class);

        // 处理后台分类信息
        Optional.ofNullable(spu.getClassificationBackendId())
                .filter(StrUtil::isNotEmpty)
                .ifPresent(backendId -> detailVo.setBackend(backendService.getBackendListByLayer(backendId)));
        // 处理品牌信息
        Optional.ofNullable(spu.getBrandId())
                .filter(StrUtil::isNotEmpty)
                .ifPresent(brandId -> Optional.ofNullable(brandService.getById(brandId))
                        .ifPresent(brand -> detailVo.setBrand(BeanUtil.toBean(brand, BrandSimpleVo.class)))
                );
        // 处理主图片集合
        Optional.ofNullable(spuMainPictureMapper.selectList(Wrappers.<GoodsSpuMainPicture>lambdaQuery().eq(GoodsSpuMainPicture::getSpuId, spu.getId())))
                .filter(CollUtil::isNotEmpty)
                .ifPresent(goodsSpuMainPictures -> {
                    var mainPictures = goodsSpuMainPictures.stream().collect(Collectors.groupingBy(GoodsSpuMainPicture::getType));
                    var mainPictureVos = new GoodsPictureVo();
                    Optional.ofNullable(mainPictures.get(CommonStatic.MATERIAL_SHOW_PC)).filter(CollUtil::isNotEmpty).ifPresent(pictures ->
                            mainPictureVos.setPc(pictures.stream().map(goodsSpuMainPicture -> {
                                var ar = new AtomicReference<GoodsMaterialVo>();
                                Optional.ofNullable(pictureService.findById(goodsSpuMainPicture.getPictureId()))
                                        .ifPresent(picture -> {
                                            var vo = new GoodsMaterialVo();
                                            vo.setId(picture.getId());
                                            vo.setName(picture.getName());
                                            vo.setUrl(picture.getUrl());
                                            ar.set(vo);
                                        });
                                return ar.get();
                            }).filter(Objects::nonNull).collect(Collectors.toList())));
                    Optional.ofNullable(mainPictures.get(CommonStatic.MATERIAL_SHOW_APP)).filter(CollUtil::isNotEmpty).ifPresent(pictures ->
                            mainPictureVos.setApp(pictures.stream().map(goodsSpuMainPicture -> {
                                var ar = new AtomicReference<GoodsMaterialVo>();
                                Optional.ofNullable(pictureService.findById(goodsSpuMainPicture.getPictureId()))
                                        .ifPresent(picture -> {
                                            var vo = new GoodsMaterialVo();
                                            vo.setId(picture.getId());
                                            vo.setName(picture.getName());
                                            vo.setUrl(picture.getUrl());
                                            ar.set(vo);
                                        });
                                return ar.get();
                            }).filter(Objects::nonNull).collect(Collectors.toList())));
                    detailVo.setMainPictures(mainPictureVos);
                });
        // 处理主视频集合
        Optional.ofNullable(spuMainVideoMapper.selectList(Wrappers.<GoodsSpuMainVideo>lambdaQuery().eq(GoodsSpuMainVideo::getSpuId, spu.getId())))
                .filter(CollUtil::isNotEmpty)
                .ifPresent(goodsSpuMainVideos -> {
                            var mainVideos = goodsSpuMainVideos.stream().collect(Collectors.groupingBy(GoodsSpuMainVideo::getType));
                            var videoVos = new GoodsVideoVo();
                            Optional.ofNullable(mainVideos.get(CommonStatic.MATERIAL_SHOW_PC)).filter(CollUtil::isNotEmpty).ifPresent(videos -> {
                                var list = videos.stream().map(goodsSpuMainVideo -> {
                                    var ar = new AtomicReference<GoodsVideoDetailVo>();
                                    Optional.ofNullable(videoService.findById(goodsSpuMainVideo.getVideoId()))
                                            .ifPresent(video -> {
                                                var vo = new GoodsVideoDetailVo();
                                                vo.setId(video.getId());
                                                vo.setName(video.getName());
                                                vo.setUrl(video.getUrl());
                                                vo.setCover(video.getCoverMark().equals(1) ? video.getCoverImg() : video.getSquareCoverImg());
                                                ar.set(vo);
                                            });
                                    return ar.get();
                                }).filter(Objects::nonNull).collect(Collectors.toList());
                                if (CollUtil.isNotEmpty(list)) {
                                    videoVos.setPc(list.get(0));
                                }
                            });
                            Optional.ofNullable(mainVideos.get(CommonStatic.MATERIAL_SHOW_APP)).filter(CollUtil::isNotEmpty).ifPresent(videos -> {
                                var list = videos.stream().map(goodsSpuMainVideo -> {
                                    var ar = new AtomicReference<GoodsVideoDetailVo>();
                                    Optional.ofNullable(videoService.findById(goodsSpuMainVideo.getVideoId()))
                                            .ifPresent(video -> {
                                                var vo = new GoodsVideoDetailVo();
                                                vo.setId(video.getId());
                                                vo.setName(video.getName());
                                                vo.setUrl(video.getUrl());
                                                vo.setCover(video.getCoverMark().equals(1) ? video.getCoverImg() : video.getSquareCoverImg());
                                                ar.set(vo);
                                            });
                                    return ar.get();
                                }).filter(Objects::nonNull).collect(Collectors.toList());
                                Stream.of(CollUtil.isNotEmpty(list)).filter(Boolean.TRUE::equals).forEach(match -> videoVos.setApp(list.get(0)));
                            });
                            detailVo.setMainVideos(videoVos);
                        }
                );
        // 处理商品图片集合
        Optional.ofNullable(spuPictureMapper.selectList(Wrappers.<GoodsSpuPicture>lambdaQuery().eq(GoodsSpuPicture::getSpuId, spu.getId())))
                .filter(CollUtil::isNotEmpty)
                .ifPresent(goodsSpuPictures ->
                        {
                            var mainPictures = goodsSpuPictures.stream().collect(Collectors.groupingBy(GoodsSpuPicture::getType));
                            var pictureVos = new GoodsPictureVo();
                            Optional.ofNullable(mainPictures.get(CommonStatic.MATERIAL_SHOW_PC)).filter(CollUtil::isNotEmpty).ifPresent(pictures ->
                                    pictureVos.setPc(pictures.stream().map(goodsSpuMainPicture -> {
                                        var ar = new AtomicReference<GoodsMaterialVo>();
                                        Optional.ofNullable(pictureService.findById(goodsSpuMainPicture.getPictureId()))
                                                .ifPresent(picture -> {
                                                    var vo = new GoodsMaterialVo();
                                                    vo.setId(picture.getId());
                                                    vo.setName(picture.getName());
                                                    vo.setUrl(picture.getUrl());
                                                    ar.set(vo);
                                                });
                                        return ar.get();
                                    }).filter(Objects::nonNull).collect(Collectors.toList())));
                            Optional.ofNullable(mainPictures.get(CommonStatic.MATERIAL_SHOW_APP)).filter(CollUtil::isNotEmpty).ifPresent(pictures ->
                                    pictureVos.setApp(pictures.stream().map(goodsSpuMainPicture -> {
                                        var ar = new AtomicReference<GoodsMaterialVo>();
                                        Optional.ofNullable(pictureService.findById(goodsSpuMainPicture.getPictureId()))
                                                .ifPresent(picture -> {
                                                    var vo = new GoodsMaterialVo();
                                                    vo.setId(picture.getId());
                                                    vo.setName(picture.getName());
                                                    vo.setUrl(picture.getUrl());
                                                    ar.set(vo);
                                                });
                                        return ar.get();
                                    }).filter(Objects::nonNull).collect(Collectors.toList())));

                            detailVo.setPictures(pictureVos);
                        }
                );
        // 处理审核日志
        Stream.of(spu.getAuditState()).filter(auditStatus -> GoodsStatic.AUDIT_STATE_WAIT != auditStatus).forEach(auditStatus ->
                Optional.ofNullable(auditLogMapper.selectList(Wrappers.<GoodsAuditLog>lambdaQuery().eq(GoodsAuditLog::getSpuId, spu.getId()).orderByDesc(GoodsAuditLog::getCreateTime)))
                        .filter(CollUtil::isNotEmpty)
                        .ifPresentOrElse(goodsAuditLogs ->{
                                detailVo.setAuditLogs(goodsAuditLogs.stream().map(goodsAuditLog -> {
                                    var vo = BeanUtil.toBean(goodsAuditLog, AuditLogVo.class);
                                    vo.setCommiter("管理员");
                                    return vo;
                                }).collect(Collectors.toList()));
                        },()->{
                            detailVo.setAuditLogs(new ArrayList<>());
                        }));
        // 处理非销售属性集合
        Optional.ofNullable(spuPropertyMapper.selectList(Wrappers.<GoodsSpuProperty>lambdaQuery().eq(GoodsSpuProperty::getSpuId, spu.getId())))
                .filter(CollUtil::isNotEmpty)
                .ifPresentOrElse(goodsSpuProperties -> {
                    var spuProperties = new ArrayList<GoodsPropertyVo>();
                    var groupMap = goodsSpuProperties.stream().collect(Collectors.groupingBy(GoodsSpuProperty::getPropertyGroupName, Collectors.toList()));
                    groupMap.forEach((key, value) -> {
                        var propertyGroupVo = new GoodsPropertyVo();
                        propertyGroupVo.setName(key);
                        Optional.ofNullable(value)
                                .filter(CollUtil::isNotEmpty)
                                .ifPresent(properties -> {
                                    var propertyVos = new ArrayList<GoodsPropertyVo>();
                                    var propertyMap = properties.stream().collect(Collectors.groupingBy(GoodsSpuProperty::getPropertyMainName, Collectors.toList()));
                                    propertyMap.forEach((pkey, pvalue) -> {
                                        var propertyVo = new GoodsPropertyVo();
                                        propertyVo.setName(pkey);
                                        Optional.ofNullable(pvalue)
                                                .filter(CollUtil::isNotEmpty)
                                                .ifPresent(propertyValues -> propertyVo.setPropertyValues(CollUtil.getFieldValues(propertyValues, "propertyValueName", String.class)));
                                        propertyVos.add(propertyVo);
                                    });
                                    propertyGroupVo.setProperties(propertyVos);
                                });
                        spuProperties.add(propertyGroupVo);
                    });
                    detailVo.setSpuProperties(spuProperties);
                },()->{
                    detailVo.setSpuProperties(new ArrayList<>());
                });
        // 处理商品sku信息
//        var skuIndexMap = new HashMap<String, List<String>>();
//        var skuInfoIndexMap = new HashMap<String, SkuIndexVo>();
        List<GoodsSkuSaveVo> saleProperties = new ArrayList<>();

//        var tableMap = new HashMap<String, HashSet<String>>();
//        var saleConfigMap = new HashMap<String, List<SkuSaleConfigNewVo>>();
        Optional.ofNullable(skuService.list(Wrappers.<GoodsSku>lambdaQuery()
                                        .eq(GoodsSku::getSpuId, spu.getId())
                                        .orderByAsc(GoodsSku::getId)
                                        .orderByAsc(GoodsSku::getCreateTime)))
                .filter(CollUtil::isNotEmpty)
                .ifPresent(goodsSkus ->
                    goodsSkus.forEach(sku -> {
                        GoodsSkuSaveVo skuVo = new GoodsSkuSaveVo();
                        BeanUtil.copyProperties(sku,skuVo);
//                        skuInfoIndexMap.put(sku.getId(), new SkuIndexVo(sku.getId(), sku.getSaleableInventory(), sku.getMarketPrice(), sku.getSellingPrice(), sku.getSkuCode()));
                        List<GoodsPropertyValueVo> pvos =new ArrayList<>();
                        Optional.ofNullable(skuPropertyValueMapper.selectList(Wrappers.<GoodsSkuPropertyValue>lambdaQuery()
                                                                .eq(GoodsSkuPropertyValue::getSkuId, sku.getId())
                                                                //增加排序字段
                                                                .orderByAsc(GoodsSkuPropertyValue::getId)
                                                                .orderByAsc(GoodsSkuPropertyValue::getCreateTime)))
                                .filter(CollUtil::isNotEmpty)
                                .ifPresent(values ->
                                        values.forEach(value -> {
                                            GoodsPropertyValueVo skuPropertyValueVo = new GoodsPropertyValueVo();
                                            BeanUtil.copyProperties(value,skuPropertyValueVo);
                                            Optional.ofNullable(value.getPropertyValuePictureId())
                                                    .filter(StrUtil::isNotEmpty).flatMap(pictureId ->
                                                    Optional.ofNullable(pictureService.findById(pictureId))).ifPresent(picture ->
                                                    skuPropertyValueVo.setPropertyValuePicture(BeanUtil.toBean(picture, PictureSimpleVo.class)));

                                            pvos.add(skuPropertyValueVo);
                                // 处理table
//                                Optional.ofNullable(tableMap.get(value.getPropertyMainName())).ifPresentOrElse(tableValues -> {
//                                    tableValues.add(value.getPropertyValueName());
//                                    tableMap.put(value.getPropertyMainName(), tableValues);
//                                }, () -> {
//                                    var tableValues = new HashSet<String>();
//                                    tableValues.add(value.getPropertyValueName());
//                                    tableMap.put(value.getPropertyMainName(), tableValues);
//                                });
                                // 处理Index
//                                Optional.ofNullable(skuIndexMap.get(sku.getId())).ifPresentOrElse(valueNames -> {
//                                    valueNames.add(value.getPropertyValueName());
//                                    skuIndexMap.put(sku.getId(), valueNames);
//                                }, () -> {
//                                    var valueNames = new ArrayList<String>();
//                                    valueNames.add(value.getPropertyValueName());
//                                    skuIndexMap.put(sku.getId(), valueNames);
//                                });
                                // 处理已配置销售属性
//                                String saleConfigKey = value.getPropertyGroupName() + "___" + value.getPropertyMainName();
//                                Optional.ofNullable(saleConfigMap.get(saleConfigKey))
//                                        .ifPresentOrElse(saleConfigs -> {
//                                            var saleConfig = getSaleConfigNew(value);
//                                            var appendFlags = new ArrayList<Boolean>();
//                                            saleConfigs.forEach(sc -> {
//                                                if ("中国码".equals(value.getPropertyParentValueName())) {
//                                                    //TODO ???
//                                                    System.out.println("aa");
//                                                }
//                                                var strValues = String.join("@|,@", sc.getValueName());
//                                                var strCurValues = String.join("@|,@", saleConfig.getValueName());
//                                                appendFlags.add(StrUtil.equals(strValues, strCurValues));
//                                                log.info("strValues:{}  strCurValues:{}   saleConfigKey:{} ab:{}", strValues, strCurValues, saleConfigKey);
//                                            });
//                                            Stream.of(appendFlags).filter(flags -> flags.stream().allMatch(Boolean.FALSE::equals)).forEach(match -> {
//        //                                        Optional.ofNullable(value.getPropertyParentValueName()).filter(StrUtil::isNotEmpty).ifPresent(parentValueName ->
//        //                                                Optional.ofNullable(saleConfig.getValues()).filter(CollUtil::isNotEmpty).ifPresent(configValues -> configValues.add(0, parentValueName)));
//                                                saleConfigs.add(saleConfig);
//                                                saleConfigMap.put(saleConfigKey, saleConfigs);
//                                            });
//                                        }, () -> {
//                                            var saleConfigs = new ArrayList<SkuSaleConfigNewVo>();
//                                            var saleConfig = getSaleConfigNew(value);
//        //                                    Optional.ofNullable(value.getPropertyParentValueName()).filter(StrUtil::isNotEmpty).ifPresent(parentValueName ->
//        //                                            Optional.ofNullable(saleConfig.getValues()).filter(CollUtil::isNotEmpty).ifPresent(configValues -> configValues.add(0, parentValueName)));
//                                            saleConfigs.add(saleConfig);
//                                            saleConfigMap.put(saleConfigKey, saleConfigs);
//                                        });
                            }));
                        skuVo.setPropertyValues(pvos);
                        saleProperties.add(skuVo);
                }/*,()->{
                        saleProperties
                    }*/));

//        var skuInfo = new SkuInfoNewVo();
//        skuInfo.setSkuIndex(skuIndexMap.entrySet().stream().collect(Collectors.toMap(e -> String.join("___", e.getValue()), e -> skuInfoIndexMap.get(e.getKey()))));
//        skuInfo.setSaleConfigs(saleConfigMap);
//        skuInfo.setSaleProperties(saleProperties);
//        detailVo.setSkuInfo(skuInfo);
        detailVo.setSaleProperties(saleProperties);
        return detailVo;
    }
    /**
     * 处理商品已配置信息
     *
     * @param skuPropertyValue 原始信息
     * @return 处理后信息
     */
    private SkuSaleConfigVo getSaleConfig(GoodsSkuPropertyValue skuPropertyValue) {
        var saleConfig = new SkuSaleConfigVo();
        var values = new ArrayList<String>();
        // 处理父级属性值名称
        Optional.ofNullable(propertyValueService.findPropertyValueById(skuPropertyValue.getPropertyValueId())).ifPresentOrElse(propertyValue ->
                Optional.ofNullable(propertyValue.getPid()).filter(StrUtil::isNotEmpty).ifPresent(pid ->
                        Optional.ofNullable(propertyValueService.findPropertyValueById(pid)).ifPresent(parent ->
                                values.add(parent.getValueName()))), () -> {
            // 当propertyValueId为null时，即为自定义值，需额外处理其可能包含的父属性信息
            Optional.ofNullable(skuPropertyValue.getPropertyParentValueName()).filter(StrUtil::isNotEmpty).ifPresent(parentValueName ->
                    Stream.of(parentValueName).filter(pvn -> !pvn.equals(skuPropertyValue.getPropertyValueName())).forEach(values::add));
        });
        values.add(skuPropertyValue.getPropertyValueName());
        saleConfig.setValues(values);
        // 处理备注
        saleConfig.setRemark(skuPropertyValue.getPropertyValueDescription());
        // 处理图片信息
        Optional.ofNullable(skuPropertyValue.getPropertyValuePictureId())
                .filter(StrUtil::isNotEmpty).flatMap(pictureId ->
                Optional.ofNullable(pictureService.findById(pictureId))).ifPresent(picture ->
                saleConfig.setImg(BeanUtil.toBean(picture, PictureSimpleVo.class)));
        return saleConfig;
    }

    private SkuSaleConfigNewVo getSaleConfigNew(GoodsSkuPropertyValue skuPropertyValue) {
        var saleConfig = new SkuSaleConfigNewVo();
//        var values = new ArrayList<String>();
//        // 处理父级属性值名称
//        Optional.ofNullable(propertyValueService.findPropertyValueById(skuPropertyValue.getPropertyValueId())).ifPresentOrElse(propertyValue ->
//                Optional.ofNullable(propertyValue.getPid()).filter(StrUtil::isNotEmpty).ifPresent(pid ->
//                        Optional.ofNullable(propertyValueService.findPropertyValueById(pid)).ifPresent(parent ->
//                                values.add(parent.getValueName()))), () -> {
//            // 当propertyValueId为null时，即为自定义值，需额外处理其可能包含的父属性信息
//            Optional.ofNullable(skuPropertyValue.getPropertyParentValueName()).filter(StrUtil::isNotEmpty).ifPresent(parentValueName ->
//                    Stream.of(parentValueName).filter(pvn -> !pvn.equals(skuPropertyValue.getPropertyValueName())).forEach(values::add));
//        });
//        values.add(skuPropertyValue.getPropertyValueName());
//        saleConfig.setValues(values);
        saleConfig.setValueName(skuPropertyValue.getPropertyValueName());
        // 处理备注
        saleConfig.setRemark(skuPropertyValue.getPropertyValueDescription());
        // 处理图片信息
        Optional.ofNullable(skuPropertyValue.getPropertyValuePictureId())
                .filter(StrUtil::isNotEmpty).flatMap(pictureId ->
                Optional.ofNullable(pictureService.findById(pictureId))).ifPresent(picture ->
                saleConfig.setValuePicture(BeanUtil.toBean(picture, PictureSimpleVo.class)));
        return saleConfig;
    }

    @Override
    public Boolean undoBackend(String backendId) {
        Optional.ofNullable(backendId).filter(StrUtil::isNotEmpty)
                .ifPresent(bid ->
                        Optional.ofNullable(goodsSpuService.list(Wrappers.<GoodsSpu>lambdaQuery().eq(GoodsSpu::getClassificationBackendId, bid)))
                                .filter(CollUtil::isNotEmpty)
                                .ifPresent(spus ->
                                        spus.forEach(spu -> {
                                                    goodsSpuService.update(spu, Wrappers.<GoodsSpu>lambdaUpdate()
                                                            .eq(GoodsSpu::getId, spu.getId())
                                                            .set(GoodsSpu::getState, GoodsStatic.STATE_STORING)
                                                            .set(GoodsSpu::getAuditState, GoodsStatic.AUDIT_STATE_WAIT)
                                                            .set(GoodsSpu::getClassificationBackendId, "0")
                                                            .set(GoodsSpu::getBrandId, "0"));
                                                    goodsProducer.sendOperator(spu.getId(), GoodsStatic.OP_TYPE_SAVE);
                                                }
                                        )
                                ));
        return true;
    }



    @Override
    public List<GoodsSku> findAllSku(String spuId) {
        return skuService.list(Wrappers.<GoodsSku>lambdaQuery().eq(GoodsSku::getSpuId, spuId));
    }

    @Override
    public List<GoodsSpuMainPicture> findAllSpuMainPicture(String spuId, Integer type) {
        return spuMainPictureMapper.selectList(Wrappers.<GoodsSpuMainPicture>lambdaQuery()
                .eq(StrUtil.isNotEmpty(spuId), GoodsSpuMainPicture::getSpuId, spuId)
                .eq(type != null, GoodsSpuMainPicture::getType, type)
        );
    }

    @Override
    public String getSpuPicture(String spuId, Integer type) {
        String result = null;
        var pictures = findAllSpuMainPicture(spuId, type);
        if (CollUtil.isNotEmpty(pictures)) {
            var picture = pictureService.getById(pictures.get(0).getPictureId());
            if (picture != null) {
                result = picture.getUrl();
            }
        }
        return result;
    }

    @Override
    public BigDecimal getSpuPrice(String spuId) {
        BigDecimal price = null;
        var skus = skuService.list(Wrappers
                .<GoodsSku>lambdaQuery()
                .eq(GoodsSku::getSpuId, spuId)
        );
        if (null != skus && CollUtil.isNotEmpty(skus)) {
            log.info(JSON.toJSONString(skus));
            price = skus.stream().map(GoodsSku::getSellingPrice)
                    .filter(Objects::nonNull)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.valueOf(0.00d));
        }
        return price;
    }

    @Override
    public Integer countByTrash() {
        return goodsSpuService.count(Wrappers.<GoodsSpu>lambdaQuery().eq(GoodsSpu::getState, GoodsStatic.STATE_RECYCLE));
    }

    @Override
    public Integer countByEdit() {
        return goodsSpuService.count(Wrappers.<GoodsSpu>lambdaQuery().eq(GoodsSpu::getEditState, GoodsStatic.EDIT_STATE_DRAFT));
    }

    @Override
    public GoodsSku findSkuById(String id) {
        return skuService.getById(id);
    }

    @Override
    public SaleGoodsDetailVo findSaleGoodsDetailsById(String spuId, String client, String userId) {
        var spu = goodsSpuService.getById(spuId);
        if (spu == null) {
            throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
        }
        var detailsVo = BeanUtil.toBean(spu, SaleGoodsDetailVo.class);
        //处理备注
        detailsVo.setDesc(CommonStatic.REQUEST_CLIENT_PC.equals(client) ? spu.getPcDecription() : spu.getAppDecription());
        /**
         * 处理折扣信息 目前的逻辑中没有recommend信息，
         * 所以此处的逻辑暂时修改成sku中最低价格和其对应的售卖价格，并计算折扣（前台目前不需要折扣这个字段）
         */
//        Optional.ofNullable(recommendService.findOne(id, 1)).ifPresent(recommend -> {
//            detailsVo.setDiscount(recommend.getDiscount());
//            //计算折扣价格
//            detailsVo.setOldPrice(detailsVo.getPrice());
//            detailsVo.setPrice(detailsVo.getPrice().multiply(recommend.getDiscount()));
//            detailsVo.setPrice(detailsVo.getPrice().divide(new BigDecimal(10), 2, RoundingMode.HALF_UP));
//        });
        List<GoodsSku> goodsSkus = skuService.selectMinPice(spuId);
        Optional.ofNullable(goodsSkus).filter(CollUtil::isNotEmpty).ifPresent(skus->{
            detailsVo.setOldPrice(skus.get(0).getMarketPrice());
            detailsVo.setPrice(skus.get(0).getSellingPrice());
            detailsVo.setDiscount(detailsVo.getPrice().divide(detailsVo.getOldPrice(),2,RoundingMode.HALF_UP));
        });
        var frontSimpleVos = new ArrayList<FrontSimpleVo>();
        //处理前台类目信息--处理品牌信息
        Optional.ofNullable(spu.getBrandId()).filter(StrUtil::isNotEmpty).ifPresent(brandId ->
                Optional.ofNullable(brandService.getById(brandId)).ifPresent(brand -> {
                    var brandVo = BeanUtil.toBean(brand, BrandSimpleVo.class);
                    Optional.ofNullable(brand.getLogoId()).filter(StrUtil::isNotEmpty).ifPresent(logoId ->
                            Optional.ofNullable(pictureService.findById(logoId)).ifPresent(picture -> {
                                brandVo.setPicture(picture.getUrl());
                                brandVo.setLogo(picture.getUrl());
                            }));
                    //处理前台类目信息--根据品牌来获取 2023-04-26 注释掉下面的代码，不再根据品牌查找前台类目，直接根据后台类目id查找前台类目信息
//                    frontSimpleVos.addAll(frontService.findFrontSimplesByRelationId(brandId,FrontStatic.RELATION_TYPE_BRAND));
                    detailsVo.setBrand(brandVo);
                }));
        //处理前台类目信息--处理销售属性信息
//        Optional.ofNullable(skuPropertyValueService.findAllSkuPropertyValueBySpu(spuId))
//                .filter(CollUtil::isNotEmpty)
//                .ifPresent(skuPropertyValues -> {
//                    skuPropertyValues.stream().forEach(skuPropertyValue->{
//                                var groupIdd = skuPropertyValue.getPropertyGroupId();
//                                //注释掉，不再根据属性信息查找前台类目信息
////                                frontSimpleVos.addAll(frontService.findFrontSimplesByRelationId(groupIdd,FrontStatic.RELATION_TYPE_BRAND));
//                            });
//                });
        //处理前台类目信息--处理关联类目信息
        Optional.ofNullable(spu.getClassificationBackendId()).filter(StrUtil::isNotEmpty)
                .ifPresent(classificationBackendId ->{
                        /* 需要查询当前后台类目及父级类目关联的前台类目信息 */
                        var frontList = frontService.findFrontSimplesByRelationId(classificationBackendId,FrontStatic.RELATION_TYPE_BACKEND);
                        frontSimpleVos.addAll(frontList);
                        //没有关联前台类目时，需要查看父级或者父级的父级是否有关联
                    if(CollUtil.isEmpty(frontList)){
                            var parentBackends = backendService.getBackendLinkList(classificationBackendId);
                            parentBackends.stream().anyMatch(parentBackend->{
                                var parentFrontList = frontService.findFrontSimplesByRelationId(parentBackend.getId(),FrontStatic.RELATION_TYPE_BACKEND);
                                if(CollUtil.isNotEmpty(parentFrontList)){
                                    frontSimpleVos.addAll(parentFrontList);
                                }
                                return CollUtil.isNotEmpty(parentFrontList);
                            });
                        }
                }
        );
        if(CollUtil.isEmpty(frontSimpleVos)){
            throw new BusinessException(ErrorMessageEnum.GOODS_INVALID_BACKEND);
        }
        /** 
         * 前台类目信息
         * 最简单的是把前台类目id传过来，这个地方带修复 这个地方应该返回后台类目信息
         * 后台类目信息时唯一可以查询的，前台可能有多个 */
        detailsVo.setCategories(frontSimpleVos);
        //处理评价数量
        detailsVo.setCommentCount(skuEvaluateService.countEvaluate(spu.getId(), new BigDecimal(0), false));
        //处理收藏数量
        detailsVo.setCollectCount(collectService.count(Wrappers.<UserMemberCollect>lambdaQuery()
                .eq(UserMemberCollect::getCollectObjectId, spu.getId())
                .eq(UserMemberCollect::getCollectType, UserMemberStatic.COLLECT_TYPE_GOODS)));
        //处理主图视频
        var mainVideos = new ArrayList<String>();
        Optional.ofNullable(spuMainVideoMapper.selectList(Wrappers.<GoodsSpuMainVideo>lambdaQuery()
                        .eq(GoodsSpuMainVideo::getSpuId, spu.getId()).eq(GoodsSpuMainVideo::getType,CommonStatic.REQUEST_CLIENT_PC.equals(client) ? CommonStatic.MATERIAL_SHOW_PC:CommonStatic.MATERIAL_SHOW_APP)))
                .filter(CollUtil::isNotEmpty).ifPresent(goodsSpuMainVideos ->
                goodsSpuMainVideos.forEach(goodsSpuMainVideo ->
                        Optional.ofNullable(videoService.findById(goodsSpuMainVideo.getVideoId())).ifPresent(video ->
                                mainVideos.add(video.getUrl()))));
        detailsVo.setMainVideos(mainVideos);
        //处理主图视频比例
        detailsVo.setVideoScale(CommonStatic.REQUEST_CLIENT_PC.equals(client) ? spu.getPcVideoScale() : spu.getAppVideoScale());
        //处理主图
        var mainPictures = new ArrayList<String>();
        Optional.ofNullable(
                spuMainPictureMapper.selectList(Wrappers.<GoodsSpuMainPicture>lambdaQuery()
                        .eq(GoodsSpuMainPicture::getSpuId, spu.getId()).eq(GoodsSpuMainPicture::getType,CommonStatic.REQUEST_CLIENT_PC.equals(client) ? CommonStatic.MATERIAL_SHOW_PC:CommonStatic.MATERIAL_SHOW_APP)))
                .filter(CollUtil::isNotEmpty).ifPresent(goodsSpuMainPictures ->
                goodsSpuMainPictures.forEach(goodsSpuMainPicture ->
                        Optional.ofNullable(pictureService.findById(goodsSpuMainPicture.getPictureId())).ifPresent(picture ->
                                mainPictures.add(picture.getUrl()))));
        detailsVo.setMainPictures(mainPictures);
        //处理可选规格和sku
        var goodsSpec = this.findGoodsSpecById(spuId,client);
        //规格
        detailsVo.setSpecs(goodsSpec.getSpecs());
        //sku
        detailsVo.setSkus(goodsSpec.getSkus());
        //处理商品分类
        //处理商品属性及商品详情图片
        var details = new SpuDetailsVo();
        //商品属性
        var properties = new ArrayList<PropertySimpleVo>();
        Optional.ofNullable(spuPropertyMapper.selectList(Wrappers.<GoodsSpuProperty>lambdaQuery()
                                                                .eq(GoodsSpuProperty::getSpuId, spu.getId())))
                .filter(CollUtil::isNotEmpty).ifPresent(goodsSpuProperties ->
                goodsSpuProperties.forEach(goodsSpuProperty -> properties.add(new PropertySimpleVo(null, goodsSpuProperty.getPropertyMainName(), goodsSpuProperty.getPropertyValueName()))));
        details.setProperties(properties);
        //详情图片
        var pictures = new ArrayList<String>();
        Optional.ofNullable(spuPictureMapper.selectList(Wrappers.<GoodsSpuPicture>lambdaQuery()
                                                                .eq(GoodsSpuPicture::getSpuId, spu.getId())
                                                                .eq(GoodsSpuPicture::getType, CommonStatic.REQUEST_CLIENT_PC.equals(client) ? CommonStatic.MATERIAL_SHOW_PC:CommonStatic.MATERIAL_SHOW_APP)))
                .filter(CollUtil::isNotEmpty).ifPresent(goodsSpuMainPictures ->
                goodsSpuMainPictures.forEach(goodsSpuMainPicture ->
                        Optional.ofNullable(pictureService.findById(goodsSpuMainPicture.getPictureId())).ifPresent(picture ->
                                pictures.add(picture.getUrl()))));
        details.setPictures(pictures);
        detailsVo.setDetails(details);
        //处理是否已收藏和默认地址
        Optional.ofNullable(userId).filter(StrUtil::isNotEmpty).ifPresent(uid -> {
            //是否已收藏
            detailsVo.setIsCollect(collectService.countCollect(uid, spu.getId(), UserMemberStatic.COLLECT_TYPE_GOODS) > 0);
            // 用户地址列表
            Optional.ofNullable(addressService.getAddressByUid(uid)).ifPresent(userAddresses->{
                detailsVo.setUserAddresses(userAddresses);
            });
        });
        //处理是否为预售
        detailsVo.setIsPreSale(false);
        Optional.ofNullable(spu.getPublishTime()).ifPresent(publishTime -> detailsVo.setIsPreSale(publishTime.isAfter(LocalDateTime.now())));
        return detailsVo;
    }

    /**
     *
     * 根据spuId获取当前商品的spec
     * 2023-04-27 梳理此处排序的规则（sku中的spec顺序需要和spec集合中的顺序一致）
     * 首先可以查询出所有的规格信息（外部spec） （可以根据spu关联）
     * 然后根据这个外部spec集合的顺序，操作sku下的spec
     * @param spuId
     * @return
     */
    @Override
    public GoodsSpecVo findGoodsSpecById(String spuId,String client) {
        var result = new GoodsSpecVo();
        var spuSpecs = skuPropertyValueService.findDistinctSpecsValueBySpu(spuId,client);

        result.setSpecs(spuSpecs);
        //sku数据
        var skuProperties = skuPropertyValueService.findAllSkuPropertyValueBySpu(spuId);
        var skuPropertiesMap = new HashMap<String,GoodsSkuPropertyValue>();
        var skuPropertyBySkuIdMap = new HashMap<String,List<GoodsSkuPropertyValue>>();
        Optional.ofNullable(skuProperties).filter(CollUtil::isNotEmpty).ifPresent(skuPropertieList->{
            skuPropertieList.forEach(skuPropertie ->{
                skuPropertiesMap.put(skuPropertie.getPropertyMainId()+":"+skuPropertie.getPropertyValueId(),skuPropertie);
            });
            //jdk8神奇 list实现分组功能 可以替代部分sql分组实现 
            skuPropertyBySkuIdMap.putAll(skuPropertieList.stream().collect(Collectors.groupingBy(GoodsSkuPropertyValue::getSkuId)));
        });
        List<GoodsSku> skuList = skuService.list(Wrappers.<GoodsSku>lambdaQuery().eq(GoodsSku::getSpuId, spuId));
        result.setSkus(skuList.stream().map(sku->{
            var skuVo = new SkuVo();
            skuVo.setOldPrice(sku.getMarketPrice());
            skuVo.setPrice(sku.getSellingPrice());
            skuVo.setId(sku.getId());
            skuVo.setSkuCode(sku.getSkuCode());
            skuVo.setInventory(sku.getSaleableInventory());
            AtomicReference<String> skuPic = new AtomicReference<>("");

            var skuSpecs = new ArrayList<SkuSpecVo>();
            Optional.ofNullable(skuPropertyBySkuIdMap.get(sku.getId())).filter(CollUtil::isNotEmpty).ifPresent(
                    skuPropertys->{
                        skuSpecs.addAll(skuPropertys.stream().map(skuProperty->{
                            var skuSpecVo = new SkuSpecVo();
                            skuSpecVo.setName(skuProperty.getPropertyMainName());
                            skuSpecVo.setValueName(skuProperty.getPropertyValueName());
                            // 判断是否存图片信息
                             Optional.ofNullable(pictureService.getById(skuProperty.getPropertyValuePictureId()))
                                    .ifPresent(picture->{
                                        if(CommonStatic.REQUEST_CLIENT_PC.equals(client)){
                                            skuPic.set(picture.getUrl()+"");
                                        }else{
                                            skuPic.set(picture.getUrl());
                                        }
                                    });
                            return skuSpecVo;
                        }).collect(Collectors.toList()));
                    }
            );
            if(StringUtils.isNoneBlank()){
                skuVo.setPicture(skuPic.get());
            }
            skuVo.setSpecs(skuSpecs);
            return skuVo;
        }).collect(Collectors.toList()));

        return result;
    }

    @Override
    public List<GoodsSkuPropertyValue> findSkuPropertyValueBySkuId(String skuId) {
        return skuPropertyValueMapper.selectList(Wrappers.<GoodsSkuPropertyValue>lambdaQuery().eq(GoodsSkuPropertyValue::getSkuId, skuId));
    }


    /**
     * 减少库存的方法
     *
     * @param skuId skuId
     * @param spuId spuId
     */
    @Override
    public void decreaseStock(String skuId, String spuId) {
        // FIXME 2022年10月14日00:24:57 需要调整下
        Optional.ofNullable(skuId).filter(StrUtil::isNotEmpty).ifPresent(id ->
                Optional.ofNullable(this.findSkuById(id)).ifPresent(sku -> {
                    var stock = sku.getSaleableInventory() - 1;
                    Stream.of(stock).filter(num -> num > 0).forEach(sku::setSaleableInventory);
                    Stream.of(stock).filter(num -> num <= 0).forEach(num -> sku.setSaleableInventory(0));
                    skuService.updateById(sku);
                }));
        Optional.ofNullable(spuId).filter(StrUtil::isNotEmpty).ifPresent(id ->
                Optional.ofNullable(goodsSpuService.getById(id)).ifPresent(spu -> {
                    /**
                     * 此处的Inventory一直为0，所以此处的逻辑不正确，
                     * 先调整轮询sku的库存，若是所有的sku的库存为0，改成售罄的状态，
                     * 若是sku中存在库存，累加所有的库存值，更新到spu中
                     */
                    Optional.ofNullable(skuService.list(Wrappers.<GoodsSku>lambdaQuery().eq(GoodsSku::getSpuId, spu.getId()).orderByDesc(GoodsSku::getCreateTime)))
                            .filter(CollUtil::isNotEmpty)
                            .ifPresent(goodsSkus -> {
                                goodsSkus.forEach(sku -> {
                                   spu.setInventory(spu.getInventory() +sku.getSaleableInventory());
                                 });
                            });
                    var stock = spu.getInventory() - 1;
                    Stream.of(stock).filter(num -> num > 0).forEach(spu::setInventory);
                    Stream.of(stock).filter(num -> num <= 0).forEach(num -> {
                        spu.setInventory(0);
                        spu.setState(GoodsStatic.STATE_SOLD_OUT);

                        log.info("当前spu售罄,先下架商品：%s，在修改商品:%s",spuId,spu);
                        goodsProducer.sendOperator(spuId, GoodsStatic.OP_TYPE_DELETE);
                    });
                    goodsSpuService.updateById(spu);

                })
        );
    }
    /**
     * 将规格的处理封装到方法中
     * @param skuId
     * @param client
     * @param specs
     * @return
     */
    @Override
    public String getGoodsAttrsText(String skuId, String client, ArrayList<SkuSpecVo> specs) {
        var sb = new StringBuilder();
        Optional.ofNullable(this.findSkuPropertyValueBySkuId(skuId))
            .filter(CollUtil::isNotEmpty).ifPresent(goodsSkuPropertyValues->{
                goodsSkuPropertyValues.forEach(goodsSkuPropertyValue ->{
                    if(CommonStatic.REQUEST_CLIENT_PC.equals(client)){
                        sb.append(String.format("%s:%s ", goodsSkuPropertyValue.getPropertyMainName(), goodsSkuPropertyValue.getPropertyValueName()));
                    }else{
                        Optional.ofNullable(specs).ifPresent(specList->{
                            var spec = new SkuSpecVo();
                            spec.setName(goodsSkuPropertyValue.getPropertyMainName());
                            spec.setValueName(goodsSkuPropertyValue.getPropertyValueName());
                            specs.add(spec);
                        });
                        sb.append(String.format("%s， ", goodsSkuPropertyValue.getPropertyValueName()));
                    }
                });
                if(sb.lastIndexOf("， ") >0){
                    sb.delete(sb.lastIndexOf("， "),sb.length());
                }
        });
        return sb.toString();
    }

    /**
     * 检查商品spu编码是否重复
     *
     * @param spuCode spu编码
     * @param curId   当前商品spu Id
     */
    private void spuCodeDuplicate(String spuCode, String curId) {
        Stream.of(
                goodsSpuService.count(Wrappers
                        .<GoodsSpu>lambdaQuery()
                        .ne(StrUtil.isNotEmpty(curId), GoodsSpu::getId, curId)
                        .eq(GoodsSpu::getSpuCode, spuCode)
                )
        ).filter(count -> count > 0).forEach(count -> {
            throw new BusinessException(ErrorMessageEnum.GOODS_DUPLICATE_SPU_CODE);
        });
    }

    /**
     * 检查商品sku编码是否重复
     *
     * @param skuCode sku编码
     * @param curId   当前商品sku Id
     */
    private void skuCodeDuplicate(String skuCode, String spuId, String curId) {
        Stream.of(skuService.selectCount(Wrappers
                        .<GoodsSku>lambdaQuery()
                        .ne(StrUtil.isNotEmpty(curId), GoodsSku::getId, curId)
                        .eq(GoodsSku::getSkuCode, skuCode)
                        .eq(StrUtil.isNotEmpty(spuId), GoodsSku::getSpuId, spuId)
                )
        ).filter(count -> count > 0).forEach(count -> {
            throw new BusinessException(ErrorMessageEnum.GOODS_DUPLICATE_SKU_CODE);
        });
    }

    /**
     * 填充sku属性值信息
     *
     * @param skuPropertyValue sku属性值信息
     * @param opUser
     */
    private void fillSkuPropertyValue(GoodsSkuPropertyValue skuPropertyValue, String opUser) {
        Optional.ofNullable(skuPropertyValue.getPropertyGroupName()).filter(StrUtil::isNotEmpty).ifPresent(groupName ->
                Optional.ofNullable(propertyGroupService.findGroupByName(groupName)).ifPresent(propertyGroup -> {
                    skuPropertyValue.setPropertyGroupId(propertyGroup.getId());
                    Optional.ofNullable(skuPropertyValue.getPropertyMainName()).filter(StrUtil::isNotEmpty).ifPresent(mainName ->
                            Optional.ofNullable(propertyMainService.findPropertyByMainName(propertyGroup.getId(), mainName)).ifPresent(propertyMain -> {
                                skuPropertyValue.setPropertyMainId(propertyMain.getId());
                                Optional.ofNullable(skuPropertyValue.getPropertyValueName()).filter(StrUtil::isNotEmpty).ifPresent(valueName ->
                                        Optional.ofNullable(propertyValueService.findPropertyValueByName(propertyMain.getId(), valueName)).ifPresentOrElse(propertyValue -> {
                                            skuPropertyValue.setPropertyValueId(propertyValue.getId());
                                            Optional.ofNullable(propertyValue.getValuePicture()).filter(StrUtil::isNotEmpty).ifPresent(skuPropertyValue::setPropertyValuePictureId);
                                        },()->{
                                            // 2022年3月29日12:24:55 对于不存在的数据，保存到属性
                                            PropertyValue vo = new PropertyValue() ;
                                            vo.setMainId(propertyMain.getId());
                                            vo.setRemark(skuPropertyValue.getPropertyValueDescription());
                                            vo.setValueName(skuPropertyValue.getPropertyValueName());
                                            vo.setCreator(opUser);
                                            vo.setCreateTime(LocalDateTime.now());
                                            propertyValueService.save(vo);
                                            skuPropertyValue.setPropertyValueId(vo.getId());
//                                            Optional.ofNullable(propertyValue.getValuePicture()).filter(StrUtil::isNotEmpty).ifPresent(skuPropertyValue::setPropertyValuePictureId);
                                        })
                                );
                            })
                    );
                })
        );
    }

    /**
     * 审核商品
     *
     * @param id               spuId
     * @param auditState       审核状态
     * @param rejectDecription 驳回说明
     */
    public void auditGoods(String id, Integer auditState, String rejectDecription) {
        if (auditState == null || !CollUtil.contains(Arrays.asList(GoodsStatic.AUDIT_STATE_PASS, GoodsStatic.AUDIT_STATE_REJECT), auditState)) {
            throw new BusinessException(ErrorMessageEnum.GOODS_AUDIT_UN_SUPPORT);
        }
        if (GoodsStatic.AUDIT_STATE_REJECT == auditState && StrUtil.isEmpty(rejectDecription)) {
            throw new BusinessException(ErrorMessageEnum.GOODS_REJECT_DESCRIPTION_NOT_NULL);
        }

        Optional.ofNullable(goodsSpuService.getById(id))
                .ifPresentOrElse(spu -> {
                            // 仅当spu 审核状态为 待审核、驳回 和 商品状态为 出售中、仓库中、已售罄 且 编辑状态为 提交 时，支持审核
                            boolean isProcess = spu.getAuditState() != GoodsStatic.AUDIT_STATE_PASS
                                    && (spu.getState() != GoodsStatic.STATE_HISTORY || spu.getState() != GoodsStatic.STATE_RECYCLE)
                                    && spu.getEditState() == GoodsStatic.EDIT_STATE_SUBMIT;
                            Stream.of(isProcess).filter(Boolean.TRUE::equals).forEach(process -> {
                                // 符合条件，进行处理
                                var auditLog = new GoodsAuditLog();
                                auditLog.setAuditType(auditState);
                                auditLog.setCommiter("管理员");
                                auditLog.setRejectDecription(rejectDecription);
                                auditLog.setSpuId(spu.getId());
                                auditLog.setSpuName(spu.getName());
                                auditLog.setCreator("1");
                                auditLogMapper.insert(auditLog);
                                //根据审核状态更新商品状态
                                Stream.of(auditState).filter(state -> state == GoodsStatic.AUDIT_STATE_PASS)
                                        .forEach(state -> {
                                            //审核-通过
                                            spu.setAuditState(GoodsStatic.AUDIT_STATE_PASS);
                                            spu.setState(GoodsStatic.STATE_SELLING);
                                            spu.setPublishTime(LocalDateTime.now());
                                        });
                                Stream.of(auditState).filter(state -> state == GoodsStatic.AUDIT_STATE_REJECT)
                                        .forEach(state ->
                                                //审核-驳回
                                                spu.setAuditState(GoodsStatic.AUDIT_STATE_REJECT)
                                        );
                                goodsSpuService.updateById(spu);
                            });
                            Stream.of(isProcess).filter(Boolean.FALSE::equals).forEach(process -> {
                                throw new BusinessException(ErrorMessageEnum.GOODS_INVALID_STATE);
                            });
                        }
                        , () -> {
                            throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
                        });
    }

}
