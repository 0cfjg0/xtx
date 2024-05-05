package com.itheima.xiaotuxian.controller.goods;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.statics.UserMemberStatic;
import com.itheima.xiaotuxian.controller.BaseController;
import com.itheima.xiaotuxian.entity.goods.GoodsBrand;
import com.itheima.xiaotuxian.entity.member.UserMemberCollect;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.service.goods.GoodsBrandService;
import com.itheima.xiaotuxian.service.material.MaterialPictureService;
import com.itheima.xiaotuxian.service.member.UserMemberCollectService;
import com.itheima.xiaotuxian.vo.R;
import com.itheima.xiaotuxian.vo.classification.FrontSimpleVo;
import com.itheima.xiaotuxian.vo.goods.BrandConditionVo;
import com.itheima.xiaotuxian.vo.goods.BrandListResultVo;
import com.itheima.xiaotuxian.vo.goods.BrandVo;
import com.itheima.xiaotuxian.vo.goods.brand.BrandQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping("/brand")
public class BrandController extends BaseController {
    @Autowired
    private GoodsBrandService brandService;
    @Autowired
    private MaterialPictureService pictureService;
    @Autowired
    private UserMemberCollectService collectService;

    /**
     * 获取品牌检索条件
     *
     * @return 品牌检索条件
     */
    @GetMapping("/conditions")
    public R<BrandConditionVo> getConditions() {
        var vo = new BrandConditionVo();
        vo.setFirstWords(brandService.findAllFirstWord());
        vo.setProductionPlaces(brandService.findAllProductionPlace());
        vo.setCategories(brandService.findAllFront().stream().map(front -> BeanUtil.toBean(front, FrontSimpleVo.class)).collect(Collectors.toList()));
        return R.ok(vo);
    }

    /**
     * 获取品牌列表
     *
     * @param frontId         前台分类Id
     * @param productionPlace 产地
     * @return 品牌列表
     */
    @GetMapping
    public R<List<BrandListResultVo>> findAllMulti(@RequestParam(name = "categoryId", required = false) String frontId
            , @RequestParam(name = "productionPlace", required = false) String productionPlace) {
        var resultList = new ArrayList<BrandListResultVo>();
        var queryVo = new BrandQueryVo();
        queryVo.setProductionPlace(productionPlace);
        queryVo.setFrontId(frontId);
        brandService.findAllEntities(queryVo)
                .stream()
                .collect(Collectors.groupingBy(GoodsBrand::getFirstWord, Collectors.toList()))
                .forEach((key, value) -> {
                    var resultVo = new BrandListResultVo();
                    resultVo.setFirstWord(key);
                    resultVo.setBrands(value.stream().map(this::convertBrand).collect(Collectors.toList()));
                    resultList.add(resultVo);
                });
        CollUtil.sortByProperty(resultList, "firstWord");
        return R.ok(resultList);
    }

    /**
     * 获取品牌列表-APP
     *
     * @param frontId         前台分类Id
     * @param productionPlace 产地
     * @param firstWord       首字母
     * @return 品牌列表
     */
    @GetMapping("/simple")
    public R<List<BrandVo>> findAllList(@RequestParam(name = "categoryId", required = false) String frontId
            , @RequestParam(name = "productionPlace", required = false) String productionPlace
            , @RequestParam(name = "firstWord", required = false) String firstWord) {
        var queryVo = new BrandQueryVo();
        queryVo.setProductionPlace(productionPlace);
        queryVo.setFrontId(frontId);
        queryVo.setFirstWord(firstWord);
        return R.ok(brandService.findAllEntities(queryVo).stream().map(this::convertBrand).collect(Collectors.toList()));
    }

    /**
     * 获取品牌详情
     *
     * @param id 品牌id
     * @return 品牌详情
     */
    @GetMapping("/{id}")
    public R<BrandVo> findById(@PathVariable(name = "id") String id) {
        var brand = brandService.getById(id);
        if (brand == null) {
            throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
        }
        var result = convertBrand(brand);
        //是否已关注
        var ab = new AtomicBoolean(false);
        Stream.of(hasToken()).filter(Boolean.TRUE::equals).forEach(hasToken -> ab.set(collectService.countCollect(getUserId(), id, UserMemberStatic.COLLECT_TYPE_BRAND) > 0));
        result.setIsCollect(ab.get());
        return R.ok(result);
    }

    /**
     * 转换对象
     *
     * @param brand 品牌信息
     * @return 转换后品牌信息
     */
    private BrandVo convertBrand(GoodsBrand brand) {
        var brandVo = BeanUtil.toBean(brand, BrandVo.class);
        brandVo.setBrandImage(getPictureUrl(brand.getBrandImageId()));
        brandVo.setLogo(getPictureUrl(brand.getLogoId()));
        brandVo.setProductionPlace(brand.getProductionPlaceCountry());
        brandVo.setCollectNum(collectService.count(Wrappers
                .<UserMemberCollect>lambdaQuery()
                .eq(UserMemberCollect::getCollectType, UserMemberStatic.COLLECT_TYPE_BRAND)
                .eq(UserMemberCollect::getCollectObjectId, brand.getId())));
        return brandVo;
    }

    /**
     * 获取图片链接
     *
     * @param id 图片id
     * @return 图片链接
     */
    private String getPictureUrl(String id) {
        String url = null;
        if (StrUtil.isNotEmpty(id)) {
            var picture = pictureService.getById(id);
            if (picture != null) {
                url = picture.getUrl();
            }
        }
        return url;
    }
}
