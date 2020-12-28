package cn.structure.example.mybatisplus.controller;

import cn.structure.example.mybatisplus.pojo.po.User;
import cn.structure.example.mybatisplus.service.IUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author chuck
 * @since 2020-12-27
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService iUserService;

    @GetMapping(value = "/getUserById")
    public User getUserById(@RequestParam("id")Long id) {
        return iUserService.getById(id);
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
        iUserService.save(user);
        return new HashMap<String,Object>(){{
            put("code","SUCCESS");
            put("message","成功！");
            put("data",user.getId());
        }};
    }

    @PutMapping(value = "/update/{id}")
    public Map<String,Object> update(@PathVariable("id")Long id,@RequestBody User user) {
        user.setId(id);
        boolean b = iUserService.updateById(user);
        return new HashMap<String,Object>(){{
            put("code","SUCCESS");
            put("message","成功！");
            put("data",b);
        }};
    }

    @DeleteMapping(value = "/delete/{id}")
    public Map<String,Object> delete(@PathVariable("id")Long id) {
        boolean i = iUserService.removeById(id);
        return new HashMap<String,Object>(){{
            put("code","SUCCESS");
            put("message","成功！");
            put("data",i);
        }};
    }
}

