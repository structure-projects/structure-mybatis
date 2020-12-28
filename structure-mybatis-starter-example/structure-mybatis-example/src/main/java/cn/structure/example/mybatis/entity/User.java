package cn.structure.example.mybatis.entity;

import cn.structure.starter.mybatis.annotation.CreateTime;
import cn.structure.starter.mybatis.annotation.UpdateTime;
import lombok.Data;

import javax.persistence.Id;
import java.util.Date;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author chuck
 * @version 1.0.1
 * @since 2020/12/27 16:47
 */
@Data
public class User {

    /**
     * ID
     */
    @Id
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 是否过期 0：过期 1：未过期
     */
    private Boolean unexpired;

    /**
     * 是否启用 1:  启用 0:未启用
     */
    private Boolean enabled;

    /**
     *是否锁定 0:  锁定 1:未锁定
     */
    private Boolean unlocked;

    /**
     * 是否删除 0：未删除 1：删除
     */
    private Boolean deleted;

    /**
     * 创建时间
     */
    @CreateTime
    private Date createTime;

    /**
     * 修改时间
     */
    @UpdateTime()
    private Date updateTime;
}
