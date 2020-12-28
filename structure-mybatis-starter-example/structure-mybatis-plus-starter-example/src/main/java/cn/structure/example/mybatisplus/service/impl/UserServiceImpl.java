package cn.structure.example.mybatisplus.service.impl;

import cn.structure.example.mybatisplus.pojo.po.User;
import cn.structure.example.mybatisplus.mapper.UserMapper;
import cn.structure.example.mybatisplus.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author chuck
 * @since 2020-12-27
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public int update(User user) {
        return this.baseMapper.updateUser(user);
    }

    @Override
    public int insert(User user) {
        return this.baseMapper.insertUser(user);
    }

    @Override
    public User getUserByUsername(String username) {
        return this.baseMapper.getUserByUsername(username);
    }

    @Override
    public List<User> listUserPage(String username, int pageSize, int offset) {
        return this.baseMapper.listUserPage(new User(){{
            setUsername(username);
        }},pageSize,offset);
    }

    @Override
    public List<User> listUserPage(User user, int pageSize, int offset) {
        return this.baseMapper.listUserPage(user,pageSize,offset);
    }
}
