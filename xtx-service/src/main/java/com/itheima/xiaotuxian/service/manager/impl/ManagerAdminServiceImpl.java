package com.itheima.xiaotuxian.service.manager.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.entity.manager.ManagerAdmin;
import com.itheima.xiaotuxian.entity.manager.ManagerRole;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.mapper.manager.ManagerAdminMapper;
import com.itheima.xiaotuxian.service.manager.ManagerAdminService;
import com.itheima.xiaotuxian.service.manager.ManagerRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @author: itheima
 * @Date: 2023/7/11 3:31 下午
 * @Description:
 */
@Service
public class ManagerAdminServiceImpl extends ServiceImpl<ManagerAdminMapper, ManagerAdmin> implements ManagerAdminService {

    @Autowired
    private ManagerRoleService managerRoleService;

    @Override
    public ManagerAdmin login(ManagerAdmin managerAdmin) {
        var lambdaQueryChainWrapper = this.lambdaQuery();
        lambdaQueryChainWrapper.eq(ManagerAdmin::getUsername, managerAdmin.getUsername());
        var sourceAdmin = lambdaQueryChainWrapper.one();
        if (sourceAdmin == null) {
            throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
        }
        var checkPassword = SecureUtil.md5(managerAdmin.getPassword());
        if (sourceAdmin.getPassword().equals(checkPassword)) {
            return sourceAdmin;
        }
        throw new BusinessException(ErrorMessageEnum.MEMBER_PASSWORD_INVALID);
    }

    @Override
    public Boolean saveAndUpdate(ManagerAdmin managerAdmin) {
        if (StringUtils.isEmpty(managerAdmin.getUsername())) {
            managerAdmin.setUsername(null);
        }
        if (StringUtils.isEmpty(managerAdmin.getPassword())) {
            managerAdmin.setPassword(null);
        }
        if (!StringUtils.isEmpty(managerAdmin.getPassword())) {
            managerAdmin.setPassword(SecureUtil.md5(managerAdmin.getPassword()));
        }
        if (managerAdmin.getId() != null) {
            this.updateById(managerAdmin);
        } else {
            this.save(managerAdmin);
        }
        return true;
    }

    @Override
    public boolean hasRole(String role, ManagerAdmin user)
    {
        if(StringUtils.isEmpty(role)){
            return true;
        }
        var roles = managerRoleService.findAll(user.getId());
        boolean flag =false;
        if (CollectionUtils.isEmpty(roles)){
            return false;
        }
        for (ManagerRole managerRole: roles) {
            if("admin".equals(managerRole.getRoleKey())||managerRole.getRoleKey().equals(role)){
                flag =  true;
                break;
            }
        }
        return flag;
    }


}
