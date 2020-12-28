package cn.structure.example.mybatis.service;

import cn.structure.example.mybatis.entity.User;

import java.util.List;

/**
 * <p>
 * 用户service接口
 * </p>
 *
 * @author chuck
 * @version 1.0.1
 * @since 2020/12/27 16:52
 */
public interface IUserService {

    /**
     * 通过ID获取用户
     * @param id
     * @return
     */
    User getUserById(Long id);

    /**
     * 通过用户名获取用户
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
     * 插入用户信息
     * @param user
     * @return
     */
    int insertUser(User user);

    /**
     * 修改用户信息
     * @param user
     * @return
     */
    int updateUserById(User user);

    /**
     * 删除用户信息
     * @param id
     * @return
     */
    int deleteById(Long id);
}

