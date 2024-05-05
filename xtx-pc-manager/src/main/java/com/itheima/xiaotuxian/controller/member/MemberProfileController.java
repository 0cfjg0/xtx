package com.itheima.xiaotuxian.controller.member;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.statics.CommonStatic;
import com.itheima.xiaotuxian.constant.statics.UserMemberStatic;
import com.itheima.xiaotuxian.controller.BaseController;
import com.itheima.xiaotuxian.entity.member.UserMember;
import com.itheima.xiaotuxian.entity.member.UserMemberFavorite;
import com.itheima.xiaotuxian.entity.member.UserMemberOpenInfo;
import com.itheima.xiaotuxian.entity.member.UserMemberProperty;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.service.classification.ClassificationFrontService;
import com.itheima.xiaotuxian.service.material.MaterialPictureService;
import com.itheima.xiaotuxian.service.member.UserMemberAddressService;
import com.itheima.xiaotuxian.service.member.UserMemberFavoriteService;
import com.itheima.xiaotuxian.service.member.UserMemberOpenInfoService;
import com.itheima.xiaotuxian.service.member.UserMemberPropertyService;
import com.itheima.xiaotuxian.service.member.UserMemberService;
import com.itheima.xiaotuxian.util.MaterialUtil;
import com.itheima.xiaotuxian.vo.R;
import com.itheima.xiaotuxian.vo.member.MemberResultVo;
import com.itheima.xiaotuxian.vo.member.UserMemberPropertyVo;
import com.itheima.xiaotuxian.vo.member.request.MemberRequestVo;
import com.itheima.xiaotuxian.vo.member.response.InterestResultVo;
import com.itheima.xiaotuxian.vo.member.response.MemberCheckResultVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/member/profile")
public class MemberProfileController extends BaseController {
    @Autowired
    private UserMemberService userMemberService;
    @Autowired
    private UserMemberOpenInfoService userMemberOpenInfoService;
    @Value("${tmp.file-directory}")
    private String fileDirectory;
    @Autowired
    private MaterialPictureService pictureService;
    @Autowired
    private ClassificationFrontService classificationFrontService;
    @Autowired
    private UserMemberFavoriteService userMemberFavoriteService;
    @Autowired
    private UserMemberPropertyService userMemberPropertyService;

    @Autowired
    private UserMemberAddressService userMemberAddressService;
    @Value("${account.avatar}")
    private String avatar;
    /**
     * 个人信息-获取
     *
     * @return 个人信息
     */
    @GetMapping
    public R<MemberResultVo> profile() {
        var userMember = userMemberService.getById(getUserId());
        if (userMember == null) {
            throw new BusinessException(ErrorMessageEnum.TOKEN_ERROR);
        }
        var userMemberVo = BeanUtil.toBean(userMember, MemberResultVo.class);
        userMemberVo.setFullLocation(userMemberAddressService.getOtherFullLocationAddress(userMember.getProvinceCode(),userMember.getCityCode(),userMember.getCountyCode()));
        if(null == userMemberVo.getAvatar()){
            userMemberVo.setAvatar(avatar);
        }
        return R.ok(userMemberVo);
    }

    @GetMapping("/check")
    public R<MemberResultVo> checkProfile() {
        try {
            var userMember = userMemberService.getById(getUserId());
            if (userMember == null) {
                throw new BusinessException(ErrorMessageEnum.TOKEN_ERROR);
            }
            var userMemberVo = BeanUtil.toBean(userMember, MemberCheckResultVo.class);
            userMemberVo.setFullLocation(userMemberAddressService.getFullLocationAddress(userMember.getProvinceCode(),
                    userMember.getCityCode(), userMember.getCountyCode()));
            if (null == userMemberVo.getAvatar()) {
                userMemberVo.setAvatar(avatar);
            }
            var token = request.getHeader("Authorization").replace("Bearer ", "");
            userMemberVo.setToken(token);
            return R.ok(userMemberVo);
        } catch (Exception e) {
            return R.ok();
        }
    }

    /**
     * 更新个人信息
     *
     * @param vo 个人信息
     * @return 操作结果
     */
    @PutMapping
    public R<MemberCheckResultVo> updateProfile(@RequestBody MemberRequestVo vo) {
        var updateUser = BeanUtil.toBean(vo, UserMember.class);
        updateUser.setId(getUserId());
        userMemberService.update(updateUser);

        if(CommonStatic.REQUEST_CLIENT_MINIAPP.equals(getClient())){
            //修改第三方信息表数据
            var userOpenInfo = BeanUtil.toBean(updateUser, UserMemberOpenInfo.class);
            userOpenInfo.setId(null);
            userOpenInfo.setMobile(updateUser.getMobile());
            userOpenInfo.setSource(CommonStatic.MATERIAL_SHOW_MINIAPP);
            userMemberOpenInfoService.update(userOpenInfo, Wrappers.<UserMemberOpenInfo>lambdaUpdate()
                    .eq(UserMemberOpenInfo::getUserId,updateUser.getId())
                    .eq(UserMemberOpenInfo::getIsDelete,0) );
        }
        var userMember = userMemberService.getById(updateUser.getId());
        var userMemberVo = BeanUtil.toBean(userMember, MemberCheckResultVo.class);
        userMemberVo.setFullLocation(userMemberAddressService.getFullLocationAddress(userMember.getProvinceCode(),userMember.getCityCode(),userMember.getCountyCode()));
        if(null == userMemberVo.getAvatar()){
            userMemberVo.setAvatar(avatar);
        }
        // 如果是手机端增加token值返回
         if(!CommonStatic.REQUEST_CLIENT_MINIAPP.equals(getClient())){
             var token = request.getHeader("Authorization").replace("Bearer ", "");
             userMemberVo.setToken(token);
         }
        return R.ok(userMemberVo);
    }

    /**
     * 修改头像
     *
     * @param attachment 头像文件
     * @return 操作结果
     * @throws Exception
     */
    @PostMapping("/avatar")
    public R<Map<String,String>> updateAvatar(@RequestParam(name = "file") MultipartFile attachment) {
        if (MaterialUtil.checkFileSize(attachment.getSize(), 5120, MaterialUtil.FILE_SIZE_UNIT_K)) {
            throw new BusinessException(ErrorMessageEnum.FILE_LARGER);
        }
        var userMember = userMemberService.getById(getUserId());
        if (userMember == null) {
            throw new BusinessException(ErrorMessageEnum.MEMBER_DOES_NOT_EXIST);
        }
        var localMaterial = MaterialUtil.saveToLocal(fileDirectory, MaterialUtil.FILE_AVATAR, attachment);
        userMember.setAvatar(pictureService.saveAvatar(localMaterial.getFilePath()));
        userMemberService.updateById(userMember);
        // 增加小程序第三方登录的情况，保持和登录的头像一致
        if(CommonStatic.REQUEST_CLIENT_MINIAPP.equals(getClient())){
            //修改第三方信息表数据
            UserMemberOpenInfo entity = new UserMemberOpenInfo();
            entity.setAvatar(userMember.getAvatar());
            userMemberOpenInfoService.update(entity, Wrappers.<UserMemberOpenInfo>lambdaUpdate()
                    .eq(UserMemberOpenInfo::getUserId,userMember.getId())
                    .eq(UserMemberOpenInfo::getIsDelete,0) );
        }

        Map<String,String> map = new HashMap<>();
        map.put("avatar", userMember.getAvatar());
        return R.ok(map);
    }

    /**
     * 兴趣分类-获取
     *
     * @return 兴趣分类
     */
    @GetMapping("/interest")
    public R<InterestResultVo> getInterest() {
        //后期可以改成关联sql的查询
        var frontList = classificationFrontService.findAllValidFront("0");
        var favoriteMap = userMemberFavoriteService.list(
                    Wrappers.<UserMemberFavorite>lambdaQuery().eq(UserMemberFavorite::getMemberId, getUserId())
                ).stream()
                .collect(Collectors.toMap(UserMemberFavorite::getFavoriteObjectId, favorite -> favorite));
        return R.ok(frontList.stream().map(classificationFront -> {
            var vo = new InterestResultVo();
            vo.setId(classificationFront.getId());
            vo.setName(classificationFront.getName());
            vo.setCheck(favoriteMap.containsKey(classificationFront.getId()));
            if (StrUtil.isNotEmpty(classificationFront.getPictureId())) {
                var picture = pictureService.getById(classificationFront.getPictureId());
                if (picture != null) {
                    vo.setUrl(picture.getUrl());
                }
            }
            return vo;
        }).collect(Collectors.toList()));
    }

    /**
     * 兴趣分类-修改
     *
     * @param interestResultVos 兴趣分类
     * @return 操作结果
     */
    @PutMapping("/interest")
    public R<String> updateInterest(@RequestBody List<String> interestResultVos) {
        var memberId = getUserId();
        userMemberFavoriteService.remove(Wrappers.<UserMemberFavorite>lambdaQuery().eq(UserMemberFavorite::getMemberId, memberId));
        var saveDate = interestResultVos.stream().map(str -> {
            var favorite = new UserMemberFavorite();
//            favorite.setFavoriteObjectId(vo.getId());
            favorite.setFavoriteObjectId(str);
            favorite.setMemberId(memberId);
            favorite.setCreateTime(LocalDateTime.now());
            favorite.setFavoriteType(UserMemberStatic.FAVORITE_TYPE_CLASSIFICATION_FRONT);
            return favorite;
        }).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(saveDate)) {
            userMemberFavoriteService.saveBatch(saveDate);
        }
        return R.ok(saveDate);
    }

    /**
     * 我的尺码-新增
     *
     * @param vo 尺寸信息
     * @return 操作结果
     */
    @PostMapping("/property")
    public R<String> saveProperty(@RequestBody UserMemberPropertyVo vo) {
        var memberId = getUserId();
        if (userMemberPropertyService.count(Wrappers.<UserMemberProperty>lambdaQuery().eq(UserMemberProperty::getMemberId, memberId)) >= 10) {
            throw new BusinessException(ErrorMessageEnum.MEMBER_MAXIMUM_ITEM_ERROR);
        }
        var userMemberProperty = BeanUtil.toBean(vo, UserMemberProperty.class);
        userMemberProperty.setMemberId(memberId);
        userMemberProperty.setCreator(memberId);
        userMemberPropertyService.save(userMemberProperty);
        return R.ok(userMemberProperty);
    }

    /**
     * 我的尺码-修改
     *
     * @param id 尺寸Id
     * @param vo 尺寸信息
     * @return 操作结果
     */
    @PutMapping("/property/{id}")
    public R<String> updateProperty(@PathVariable(name = "id") String id, @RequestBody UserMemberPropertyVo vo) {
        var property = userMemberPropertyService.getById(id);
        if (property == null) {
            throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
        }
        if (!property.getMemberId().equals(getUserId())) {
            throw new BusinessException(ErrorMessageEnum.ORDER_NO_PRIVILEGE);
        }
        var userMemberProperty = BeanUtil.toBean(vo, UserMemberProperty.class);
        userMemberProperty.setId(id);
        userMemberPropertyService.updateById(userMemberProperty);
        return R.ok();
    }

    /**
     * 我的尺码-删除
     *
     * @param id 尺寸id
     * @return 操作结果
     */
    @DeleteMapping("/property/{id}")
    public R<String> deleteProperty(@PathVariable(name = "id") String id) {
        var property = userMemberPropertyService.getById(id);
        if (property == null) {
            throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
        }
        if (!property.getMemberId().equals(getUserId())) {
            throw new BusinessException(ErrorMessageEnum.ORDER_NO_PRIVILEGE);
        }
        userMemberPropertyService.removeById(id);
        return R.ok();
    }

    /**
     * 我的尺码-获取
     *
     * @return 我的尺寸列表
     */
    @GetMapping("/property")
    public R<List<UserMemberPropertyVo>> findAllProperty() {
        var properties = userMemberPropertyService.list(Wrappers.<UserMemberProperty>lambdaQuery().eq(UserMemberProperty::getMemberId, getUserId()));
        return R.ok(properties.stream().map(property -> BeanUtil.toBean(property, UserMemberPropertyVo.class)).collect(Collectors.toList()));
    }
}
