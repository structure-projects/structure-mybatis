package cn.structure.example.mybatis.controller;

import cn.structure.example.mybatis.entity.User;
import cn.structure.example.mybatis.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户模块控制器
 * </p>
 *
 * @author chuck
 * @version 1.0.1
 * @since 2020/12/27 16:52
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService iUserService;

    @GetMapping(value = "/getUserById")
    public User getUserById(@RequestParam("id")Long id) {
        return iUserService.getUserById(id);
    }

    @GetMapping(value = "/getUserByUsername")
    public User getUserByUsername(@RequestParam("username")String username) {
        return iUserService.getUserByUsername(username);
    }

    @GetMapping(value = "/list")
    public List<User> listUserPage(@RequestParam("username")String username,
                                   @RequestParam("pageSize")Integer pageSize,
                                   @RequestParam("offset")Integer offset) {
        return iUserService.listUserPage(username,pageSize,offset);
    }

    @PostMapping(value = "/add")
    public Map<String,Object> insertUser(@RequestBody User user) {
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        iUserService.insertUser(user);
        return new HashMap<String,Object>(){{
            put("code","SUCCESS");
            put("message","成功！");
            put("data",user.getId());
        }};
    }

    @PutMapping(value = "/update/{id}")
    public Map<String,Object> update(@PathVariable("id")Long id,@RequestBody User user) {
        user.setId(id);
        user.setUpdateTime(new Date());
        int i = iUserService.updateUserById(user);
        return new HashMap<String,Object>(){{
            put("code","SUCCESS");
            put("message","成功！");
            put("data",i);
        }};
    }

    @DeleteMapping(value = "/delete/{id}")
    public Map<String,Object> delete(@PathVariable("id")Long id) {
        int i = iUserService.deleteById(id);
        return new HashMap<String,Object>(){{
            put("code","SUCCESS");
            put("message","成功！");
            put("data",i);
        }};
    }
}
