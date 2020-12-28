package cn.structure.example.mybatisplus.service;

import cn.structure.example.mybatisplus.pojo.po.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author chuck
 * @since 2020-12-27
 */
public interface IUserService extends IService<User> {

    /**
     * 修改用户
     * @param user
     * @return
     */
    int update(User user);

    /**
     * 插入用户
     * @param user
     * @return
     */
    int insert(User user);

    /**
     * 通过用户名查找用户
     * @param username
     * @return
     */
    User getUserByUsername(String username);

    /**
     * 分页查找列表
     * @param username
     * @param pageSize
     * @param offset
     * @return
     */
    List<User> listUserPage(String username,int pageSize,int offset);

    /**
     * 分页查找列表
     * @param user
     * @param pageSize
     * @param offset
     * @return
     */
    List<User> listUserPage(User user,int pageSize,int offset);

}
