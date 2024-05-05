package com.itheima.xiaotuxian.controller.marketing;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.statics.UserMemberStatic;
import com.itheima.xiaotuxian.controller.BaseController;
import com.itheima.xiaotuxian.entity.marketing.MarketingTopic;
import com.itheima.xiaotuxian.entity.marketing.MarketingTopicClassification;
import com.itheima.xiaotuxian.entity.member.UserMemberCollect;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.service.marketing.MarketingTopicClassificationService;
import com.itheima.xiaotuxian.service.marketing.MarketingTopicService;
import com.itheima.xiaotuxian.service.member.UserMemberCollectService;
import com.itheima.xiaotuxian.vo.Pager;
import com.itheima.xiaotuxian.vo.R;
import com.itheima.xiaotuxian.vo.marketing.TopicClassificationVo;
import com.itheima.xiaotuxian.vo.marketing.TopicVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping("/topic")
public class TopicController extends BaseController {
    @Autowired
    private MarketingTopicService topicService;
    @Autowired
    private MarketingTopicClassificationService topicClassificationService;
    @Autowired
    private UserMemberCollectService collectService;

    /**
     * 获取专题分类
     *
     * @param limit 最大条目数
     * @return 专题分类
     */
    @GetMapping("/classification")
    public R<List<TopicClassificationVo>> findAllClassification(@RequestParam(name = "limit", defaultValue = "7") Integer limit) {
        return R.ok(topicClassificationService.list(Wrappers
                        .<MarketingTopicClassification>lambdaQuery()
                        .last(String.format("limit %d", limit))
                        .orderByDesc(MarketingTopicClassification::getUpdateTime)
                ).stream()
                        .map(topicClassification -> BeanUtil.toBean(topicClassification, TopicClassificationVo.class))
                        .collect(Collectors.toList())
        );
    }

    /**
     * 获取专题列表
     *
     * @param page             页码
     * @param pageSize         页尺寸
     * @param classificationId 专题分类id
     * @param sortField        排序字段，默认以updateTime字段倒序
     * @param sortRule         排序规则，asc为正序、desc为倒序，默认为倒序
     * @return 专题列表
     */
    @GetMapping
    public R<Pager<TopicVo>> findAll(@RequestParam(name = "page", defaultValue = "1") Integer page
            , @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize
            , @RequestParam(name = "classificationId", required = false) String classificationId
            , @RequestParam(name = "sortField", defaultValue = "updateTime") String sortField
            , @RequestParam(name = "sortRule", defaultValue = "desc") String sortRule) {
        var topicPage = topicService.findByPage(page, pageSize, classificationId, sortField, sortRule);
        return R.ok(new Pager<TopicVo>(topicPage.getTotal()
                , topicPage.getSize()
                , topicPage.getPages()
                , topicPage.getCurrent()
                , topicPage.getRecords().stream().map(topic -> {
            var vo = BeanUtil.toBean(topic, TopicVo.class);
            if (StrUtil.isNotEmpty(topic.getClassificationId())) {
                var classification = topicClassificationService.getById(topic.getClassificationId());
                if (classification != null) {
                    vo.setClassification(BeanUtil.toBean(classification, TopicClassificationVo.class));
                }
            }
            return vo;
        }).filter(ObjectUtil::isNotNull).collect(Collectors.toList())));
    }

    /**
     * 获取热门专题
     * 热门计算方式为：按收藏数 -> 浏览数 -> 更新时间 倒叙，取最顶部的7条
     * 该实现可进阶 使用 Elasticsearch 来实现按时间衰减计算热度方式
     *
     * @return 热门专题
     */
    @GetMapping("/hot")
    public R<List<TopicVo>> findHot() {
        return R.ok(topicService.list(Wrappers
                        .<MarketingTopic>lambdaQuery()
                        .orderByDesc(MarketingTopic::getCollectNum, MarketingTopic::getViewNum, MarketingTopic::getUpdateTime)
                        .last("limit 7")
                ).stream()
                        .map(topic -> BeanUtil.toBean(topic, TopicVo.class))
                        .collect(Collectors.toList())
        );
    }

    /**
     * 获取专题详情
     *
     * @param id 专题id
     * @return 专题详情
     */
    @GetMapping("/{id}")
    public R<TopicVo> findById(@PathVariable(name = "id") String id) {
        var topic = topicService.getById(id);
        if (topic == null) {
            throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
        }
        var result = BeanUtil.toBean(topic, TopicVo.class);
        if (StrUtil.isNotEmpty(topic.getClassificationId())) {
            var classification = topicClassificationService.getById(topic.getClassificationId());
            if (classification != null) {
                result.setClassification(BeanUtil.toBean(classification, TopicClassificationVo.class));
            }
        }
        var collecctFlag = new AtomicBoolean(false);
        Stream.of(hasToken()).filter(Boolean.TRUE::equals).forEach(hasToken -> collecctFlag.set(collectService.countCollect(getUserId(), id, UserMemberStatic.COLLECT_TYPE_TOPIC) > 0));
        result.setIsCollect(collecctFlag.get());
        return R.ok(result);
    }

    /**
     * 专题收藏/取消收藏
     *
     * @param id 专题id
     * @return 操作结果
     */
    @PostMapping("/{id}/collect")
    public R<String> collect(@PathVariable(name = "id") String id) {
        String userId = getUserId();
        var topic = topicService.getById(id);
        if (topic == null) {
            throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
        }
        var collect = collectService.getOne(Wrappers
                .<UserMemberCollect>lambdaQuery()
                .eq(UserMemberCollect::getCollectType, UserMemberStatic.COLLECT_TYPE_TOPIC)
                .eq(UserMemberCollect::getMemberId, getUserId())
                .eq(UserMemberCollect::getCollectObjectId, id));
        if (collect != null) {
            // 取消收藏
            collectService.removeById(collect.getId());
            topic.setCollectNum(topic.getCollectNum() > 0 ? topic.getCollectNum() - 1 : 0);
            topicService.updateById(topic);
        } else {
            // 收藏
            collectService.save(new UserMemberCollect(null, userId, UserMemberStatic.COLLECT_TYPE_TOPIC, id, LocalDateTime.now()));
            topic.setCollectNum(topic.getCollectNum() + 1);
            topicService.updateById(topic);
        }
        return R.ok();
    }
}
