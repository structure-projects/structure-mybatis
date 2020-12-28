package cn.structure.example.mybatis.service;

import cn.structure.example.mybatis.entity.User;
import cn.structure.example.mybatis.mapper.IUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 用户service实现
 * </p>
 *
 * @author chuck
 * @version 1.0.1
 * @since 2020/12/27 16:53
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    @Resource
    private IUserMapper iUserMapper;

    @Override
    public User getUserById(Long id) {
        log.info("UserServiceImpl------> getUserById -------> id ={}",id);
        User user = iUserMapper.getUserById(id);
        log.info("UserServiceImpl------> getUserById ------->{}",((user==null) ? "this user is null" : user.getUsername()));
        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        log.info("UserServiceImpl------> getUserByUsername -------> username ={}",username);
        User user = iUserMapper.listUser(username);
        log.info("UserServiceImpl------> getUserById ------->{}",((user==null) ? "this user is null" : user.getUsername()));
        return user;
    }

    @Override
    public List<User> listUserPage(String username, int pageSize, int offset) {
        log.info("UserServiceImpl------> listUserPage -------> username ={},pageSize ={},offset = {}",username,pageSize,offset);
        List<User> userList = iUserMapper.listUserPage(new User(){{
            setUsername(username);
        }},pageSize,offset);
        log.info("UserServiceImpl------> listUserPage -------> userListSize = {}",userList.size());
        return userList;
    }

    @Override
    public int insertUser(User user) {
        log.info("UserServiceImpl----->insertUser-----> username ={}",user.getUsername());
        int row = iUserMapper.insertUser(user);
        log.info("UserServiceImpl----->insertUser-----> id ={}",user.getId());
        return row;
    }

    @Override
    public int updateUserById(User user) {
        log.info("UserServiceImpl----->updateUserById-----> id ={}",user.getId());
        int i = iUserMapper.updateUserById(user);
        log.info("UserServiceImpl----->updateUserById-----> rows = {}",i);
        return i;
    }

    @Override
    public int deleteById(Long id) {
        log.info("UserServiceImpl----->deleteById-----> id = {}",id);
        int delete = iUserMapper.delete(id);
        log.info("UserServiceImpl----->deleteById-----> rows = {}",delete);
        return delete;
    }
}
