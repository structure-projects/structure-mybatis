package cn.structure.example.mybatis.mapper;

import cn.structure.example.mybatis.entity.User;
import cn.structure.starter.mybatis.annotation.SplitTable;
import cn.structure.starter.mybatis.enums.SplitTableEnum;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

/**
 * <p>
 * 用户表的mapper
 * </p>
 *
 * @author chuck
 * @version 1.0.1
 * @since 2020/12/27 16:52
 */
@Mapper
public interface IUserMapper {

    String sqlColumns = "id,username,password,is_unexpired,is_enabled,is_unlocked,is_deleted,create_time,update_time";

    /**
     * 通过ID查询用户信息
     * @param id
     * @return
     */
    @Results(id = "userBaseResult",value = {
            @Result(column = "is_unexpired",property = "unexpired",jdbcType = JdbcType.TINYINT),
            @Result(column = "is_enabled",property = "enabled",jdbcType = JdbcType.TINYINT),
            @Result(column = "is_unlocked",property = "unlocked",jdbcType = JdbcType.TINYINT),
            @Result(column = "is_deleted",property = "deleted",jdbcType = JdbcType.TINYINT),
            @Result(column = "create_time",property = "createTime",jdbcType = JdbcType.DATETIMEOFFSET),
            @Result(column = "update_time",property = "updateTime",jdbcType = JdbcType.DATETIMEOFFSET),
    })
    @Select("SELECT " + sqlColumns + " FROM user WHERE id = #{id}")
    User getUserById(@Param("id") Long id);
    /**
     * 通过用户名查找用户
     */
    @ResultMap("userBaseResult")
    @Select("SELECT " + sqlColumns + " FROM user WHERE username = #{username}")
    User listUser(@Param("username")String username);

    /**
     * 分页查询用户
     * @param user
     * @param pageSize
     * @param offset
     * @return
     */
    @ResultMap("userBaseResult")
    @Select("<script> SELECT " + sqlColumns + " FROM user " +
            "<where>" +
            "<if test ='user.username !=null'>username like CONCAT(CONCAT('%', #{user.username}), '%') </if>" +
            "</where>" +
            " LIMIT #{pageSize} offset #{offset} </script>")
    List<User> listUserPage(@Param("user") User user,@Param("pageSize") Integer pageSize,@Param("offset") Integer offset);

    /**
     * 插入用户
     * 添加语句如果使用数据库生成需要添加
     * @Options(useGeneratedKeys = true, keyProperty = "id")
     * @param user
     * @return
     */
    @Insert("INSERT INTO user (username,password,create_time,update_time) " +
            "VALUES(#{username},#{password},#{createTime},#{updateTime})")
    int insertUser(User user);

    /**
     * 按照创建时间分表插入 默认为： 年月分表
     * @param user
     * @return
     */
    @SplitTable(tableName = "user",splitType = SplitTableEnum.TIME,splitBy = "update_time")
    @Insert("INSERT INTO user (username,password,create_time,update_time) " +
            "VALUES(#{username},#{password},#{createTime},#{updateTime})")
    int insertUserSplitTable(User user);

    /**
     * 动态修改用户
     * @param user
     * @return
     */
    @Update("<script>" +
            "UPDATE user " +
            "<set>" +
            "<if test='password !=null'> password = #{password},</if>" +
            "<if test='unexpired !=null'> is_unexpired = #{unexpired},</if>" +
            "<if test='enabled !=null'> is_enabled = #{enabled},</if>" +
            "<if test='unlocked !=null'> is_unlocked = #{unlocked},</if>" +
            "<if test='deleted !=null'> is_deleted = #{deleted},</if>" +
            "<if test='updateTime !=null'> update_time = #{updateTime}</if>" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int updateUserById(User user);

    /**
     * 删除用户
     * @param id
     * @return
     */
    @Delete("DELETE FROM user WHERE id = #{id}")
    int delete(Long id);


}
