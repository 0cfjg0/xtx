package com.itheima.xiaotuxian.controller.v8;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.itheima.xiaotuxian.controller.BaseController;
import com.itheima.xiaotuxian.exception.AuthException;
import com.itheima.xiaotuxian.service.business.BusinessAdService;
import com.itheima.xiaotuxian.service.classification.ClassificationFrontService;
import com.itheima.xiaotuxian.service.goods.GoodsBrandService;
import com.itheima.xiaotuxian.service.goods.GoodsSpuService;
import com.itheima.xiaotuxian.service.home.HomeHotRecommendService;
import com.itheima.xiaotuxian.service.marketing.MarketingOneStopService;
import com.itheima.xiaotuxian.service.marketing.MarketingTopicService;
import com.itheima.xiaotuxian.service.material.MaterialPictureService;
import com.itheima.xiaotuxian.service.search.SearchGoodsService;
import com.itheima.xiaotuxian.util.ConvertUtil;
import com.itheima.xiaotuxian.vo.Pager;
import com.itheima.xiaotuxian.vo.R;
import com.itheima.xiaotuxian.vo.classification.response.FrontResultMiniVo;
import com.itheima.xiaotuxian.vo.goods.goods.GoodsItemResultVo;
import com.itheima.xiaotuxian.vo.search.SearchQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * author:lvbc
 */
@Slf4j
@RestController
@RequestMapping("/")
public class HomV8Controller extends BaseController {
    @Autowired
    private ClassificationFrontService frontService;
    @Autowired
    private MaterialPictureService pictureService;
    @Value("${picture.head.resize:?quality=95&imageView}")
    private String headResize;
    @Autowired
    private SearchGoodsService searchGoodsService;
    @Autowired
    private GoodsSpuService goodsSpuService;
    /**
     * 首页-猜你喜欢（小程序调用）
     * 若用户有购买记录，则根据购买商品的前台分类id推荐
     * @param page 页码
     * @param pageSize 页尺寸
     * @return 最新专题
     */
    @GetMapping("/goods/guessLike")
    public R<Pager<GoodsItemResultVo>> getGuessLike(@RequestParam(name = "page", defaultValue = "1") Integer page
            , @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        SearchQueryVo queryVo = new SearchQueryVo();
        queryVo.setPage(page);
        queryVo.setPageSize(pageSize);
        try{
            //从token中获取memberId，根据用户获取其购买商品的前台类目id
            queryVo.setFrontIds(goodsSpuService.getFrontIdsByMemberId(getUserId()));
        }catch (AuthException e){
            // 没有token的时候才查询用户id
        }catch (Exception e){
            log.error("查询猜你喜欢报错",e);
        }
        var dataPage = searchGoodsService.searchByPage(queryVo);
        return R.ok(new Pager<>(dataPage.getTotal()
                , dataPage.getSize()
                , dataPage.getPages()
                , dataPage.getCurrent()
                , dataPage.getRecords().stream().map(esGoods -> ConvertUtil.convertHomeGoods(esGoods, getClient())).collect(Collectors.toList())));
//        return R.ok(searchGoodsService.findAllGoods(queryVo).stream()
//                .map(esGoods -> ConvertUtil.convertHomeGoods(esGoods, getClient()))
//                .collect(Collectors.toList()));

    }
    /**
     * 新版接口
     * 2022年11月2日17:53:09 用于发布小程序使用，过滤掉美食类别
     * @return
     */
    @GetMapping("/home/category/mutli")
    public R<List<FrontResultMiniVo>> getNewPmdCategoryHead() {
        // start 小程序要求接口能够返回慢一些，以方便前段测试下拉刷新 2023年1月10日10:19:32
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // end小程序要求接口能够返回慢一些，以方便前段测试下拉刷新 2023年1月10日10:19:32
        List<FrontResultMiniVo> result = frontService.findAllValidFront("0")
                .stream()
//                .filter(front->!front.getName().equals("美食")).collect(Collectors.toList())
//                .stream()
                .map(front -> {
                    var resultVo = BeanUtil.toBean(front, FrontResultMiniVo.class);
                    if(resultVo.getName().equals("美食")){
                        resultVo.setName("锦鲤");
                    }
                    //处理图片
                    Optional.ofNullable(front.getPictureId()).filter(StrUtil::isNotEmpty)
                            .ifPresent(pictureId -> resultVo.setIcon(pictureService.getPictureUrl(pictureId) + headResize));
                    return resultVo;
                }).collect(Collectors.toList());
        //模拟增加品牌的图标信息
        var brandHead = new FrontResultMiniVo();
        brandHead.setIcon(pictureService.getPictureUrl("1390110715762708482") + headResize);
        brandHead.setName("品牌");
        brandHead.setId("999999");
        result.add(brandHead);
//
//        var jinkou = new FrontResultMiniVo();
//        jinkou.setIcon("https://pcapi-xiaotuxian-front-devtest.itheima.net/miniapp/icons/home/jinkou.png");
//        jinkou.setName("进口");
//        jinkou.setId("999998");
//        result.add(jinkou);
//        var market = new FrontResultMiniVo();
//        market.setIcon("https://pcapi-xiaotuxian-front-devtest.itheima.net/miniapp/icons/home/market.png");
//        market.setName("超市");
//        market.setId("999997");
//        result.add(market);
//
//        var near = new FrontResultMiniVo();
//        near.setIcon("https://pcapi-xiaotuxian-front-devtest.itheima.net/miniapp/icons/home/fujin.png");
//        near.setName("附近");
//        near.setId("999996");
//        result.add(near);
//
//        var recharge = new FrontResultMiniVo();
//        recharge.setIcon("https://pcapi-xiaotuxian-front-devtest.itheima.net/miniapp/icons/home/chongzhi.png");
//        recharge.setName("充值");
//        recharge.setId("999995");
//        result.add(recharge);
//
//        var digital  = new FrontResultMiniVo();
//        digital .setIcon("https://pcapi-xiaotuxian-front-devtest.itheima.net/miniapp/icons/home/shuma3c.png");
//        digital .setName("数码3C");
//        digital .setId("999994");
//        result.add(digital );

        return R.ok(result);
    }
}
