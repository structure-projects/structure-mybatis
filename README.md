# structure-mybatis
structure-mybatis 对mybatis进行部分功能的扩展

## structure-mybatis-starter ##
1. 提供自动注入实体默认值
- @Id package javax.persistence.Id
- @CreateTime 创建时间
- @UpdateTime 修改时间
2. pom 引用
```xml
    <dependency>
        <groupId>cn.structured</groupId>
        <artifactId>structure-mybatis-starter</artifactId>
        <version>${last.version}</version>
    </dependency>
```
### @Id自动生成ID的使用 ###
#### 配置 yml ####
```yaml
structure:
  mybatis:
    plugin:
      generate-id-type: snowflake # none  无则是数据库生成 ,uuid 生成UUID , snowflake 生成雪花耍法的ID 
      data-center: 0 #数据中心码 为雪花算法专有 0 - 31
      machine: 0 # 机器码 为雪花算法专有  0 - 31
```
#### 数据库实体的配置 ####
```java
    /**
     *  主键ID （会员信息表的ID相同）
     */
    @Id
    private Long id;
```
### @CreateTime 创建时间的注入
```java
    /**
     * 创建时间
     */
    @CreateTime
    @Column(name = "create_time")
    private Date createTime;
```
### @UpdateTime 修改时间的注入
```java
    @UpdateTime
    @Column(name = "update_time")
    private Date updateTime;
```
### 注意事项 ###
1. 禁止使用匿名内部类做插入和修改操作
2. 需要生成时属性必须为null
3. 注意生成的类型