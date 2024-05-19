package com.itheima.xiaotuxian.service.member;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.member.UserMember;
import com.itheima.xiaotuxian.vo.member.RegisterVo;
import com.itheima.xiaotuxian.vo.member.request.LoginVo;

import javax.servlet.http.HttpSession;

/**
 * 用户信息处理service
 * @author zsf
 */
public interface UserMemberService extends IService<UserMember> {
    /**
     * 发送注册验证码
     *
     * @param mobile 手机号
     * @return 操作结果
     */
    Boolean sendRegisterCode(String mobile);

    /**
     * 验证注册验证码
     *
     * @param mobile 手机号
     * @param code   验证码
     * @return 操作结果
     */
    Boolean checkRegisterCode(String mobile, String code);

    /**
     * 发送登录验证码
     *
     * @param mobile 手机号
     * @return 操作结果
     */
//    String code(String mobile, HttpSession session) ;


    /**
     * 三方登录_发送已有账号短信
     *
     * @param mobile 手机号码
     * @return 操作结果
     */
    Boolean sendSocialCode(String mobile);

    /**
     * 验证登录验证码
     *
     * @param mobile 手机号
     * @param code   验证码
     * @return 操作结果
     */
    Boolean checkLoginCode(String mobile, String code);

    /**
     * 发送修改密码验证码
     *
     * @param mobile 手机号
     * @return 操作结果
     */
    Boolean sendResetCode(String mobile);


    /**
     * 三方账号绑定
     *
     * @param mobile  手机号
     * @param unionId 三方标识
     * @param code    验证码
     * @return 用户信息
     */
    UserMember socialBind(String mobile, String unionId, String code);


    /**
     * 更新用户信息
     *
     * @param userMember 用户信息
     */
    void update(UserMember userMember);

    /**
     * 用户注册
     *
     * @param registerVo 注册信息
     * @return 用户信息
     */


    /**
     * 获取新的token值
     *
     * @param id       id
     * @param account  账户信息
     * @param nickname 昵称信息
     * @return string token信息
     */
    String getToken(String id, String account, String nickname);


    /**
     * 解除三方绑定方法
     *
     * @param userMember userMember
     * @return void
     * @description: 解除三方绑定方法
     */
    void unbind(UserMember userMember);

    /**
     * 根据openid查询用户信息
     *
     * @param openid
     * @return
     */
    UserMember findByOpenid(String openid);

    //登入
    LoginVo login(LoginVo vo);


    UserMember select(LoginVo vo);

}
