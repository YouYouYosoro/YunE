package com.xxxx.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxxx.server.AdminUtils;
import com.xxxx.server.mapper.MenuMapper;
import com.xxxx.server.pojo.Menu;
import com.xxxx.server.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author czy
 * @since 2022-12-08
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据用户id查询菜单列表
     *
     * @return
     */
    @Override
    public List<Menu> getMenusByAdminId() {
        Integer adminId = AdminUtils.getCurrentAdmin().getId();
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        //从redis获取菜单数据
        List<Menu> menus = (List<Menu>) valueOperations.get("menu_" + adminId);
        //如果为空从数据库获取
        if (CollectionUtils.isEmpty(menus)) {
            menus = menuMapper.getMenusByAdminId(adminId);
            //将数据设置到redis中
            valueOperations.set("menu_" + adminId, menus);
        }
        return menus;
//        return menuMapper.getMenusByAdminId(((Admin) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId());
    }

    /**
     * 根据角色获取菜单列表
     *
     * @return
     */
    @Override
    public List<Menu> getMenusWithRole() {
        return menuMapper.getMenusWithRole();
    }

    /**
     * 查询所所有菜单
     *
     * @return
     */
    @Override
    public List<Menu> getAllMenus() {
        return menuMapper.getAllMenus();
    }
}
