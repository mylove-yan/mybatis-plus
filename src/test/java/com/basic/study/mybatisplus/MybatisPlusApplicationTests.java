package com.basic.study.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlLike;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.basic.study.mybatisplus.bean.User;
import com.basic.study.mybatisplus.mapper.UserMapper;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class MybatisPlusApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    /**
     * 查询一个例子
     */
    public void testGetList(){
        List<User> list = userMapper.selectList(null);
        Assert.assertEquals(2,list.size());
    }
    @Test
    /**
     * 插入一个例子
     */
    public void testInsert(){
        User user = new User();
        user.setAge(36);
        user.setName("马五");
        user.setManagerId(1);
        user.setCreateTime(new Date());
        Assert.assertEquals(1,userMapper.insert(user));
    }
    @Test
    /**
     * 查询单个例子
     */
    public void queryById(){
        User user = userMapper.selectById(1);
        Assert.assertNotNull(user);
    }

    @Test
    /**
     * 根据id集合进行查询
     */
    public void queryByIds(){
        List<Integer> list = Arrays.asList(1,2,3);
        List<User> userList = userMapper.selectBatchIds(list);
        Assert.assertEquals(3,userList.size());
    }

    @Test
    /**
     * 根据Map<String,Object> 进行查询
     */
    public void queryByMap(){
        Map<String,Object> map = Maps.newHashMap();
        map.put("name","马誌浩");
        map.put("age",29);
        List<User> userList = userMapper.selectByMap(map);
        Assert.assertEquals(1,userList.size());
    }

    @Test
    /**
     * 根据Wrapper进行查询
     * 名字中包含雨并且年龄小于40
     * name like '%誌%' and age<40
     */
    public void queryByWrapper(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        //QueryWrapper<User> query= Wrappers.query();
        queryWrapper.like("name","誌").lt("age",40);
        List<User> userList = userMapper.selectList(queryWrapper);
        Assert.assertEquals(1,userList.size());
    }

    public void likeWrapper(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        boolean condition = false;
        queryWrapper.like(condition,"name","誌");//condition  符合条件才加上
        queryWrapper.likeLeft("name","誌");//%誌
        queryWrapper.likeRight("name","誌");//誌%
    }

    @Test
    /**
     * 根据Wrapper构造器条件进行查询
     * name like ‘%誌%’ and age between 20 and 40 and email is not null
     */
    public void queryByWrapper2(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.like("name","誌").between("age",16,30).isNotNull("email");
        List<User> userList = userMapper.selectList(queryWrapper);
        Assert.assertEquals(1,userList.size());
    }

    @Test
    /**
     * 根据Wrapper构造器条件进行查询
     * name like ‘马%’ or age >= 25 order by age desc,id asc
     */
    public void queryByWrapper3(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.likeLeft("name","马").or().ge("age",25).orderByDesc("age").orderByAsc("id");
        List<User> userList = userMapper.selectList(queryWrapper);
        Assert.assertEquals(3,userList.size());
    }

    @Test
    /**
     * 创建日期为2020年5月22日并且直属上级为名字为马姓
     * to_char(create_time,'yyyy-MM-dd')='2020-5-22' and manager_id in (select id from user where name like '马%')
     */
    public void queryByWrapper4(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper
                .apply("to_char(create_time,'yyyy-MM-dd') ={0}", "2020-05-22")  //这种方式不会sql注入
                //.apply("date_format(create_time,'%Y-%m-%d') ='2019-02-14'") //这样写有可能会引起sql注入 比如 '2019-02-14' or true or true,会查出全部记录
                .inSql(" manager_id", "select id from user_t where name like '马%'");
        List<User> userList = userMapper.selectList(queryWrapper);
        userList.forEach(System.out::println);
    }

    @Test
    /**
     * 根据Wrapper构造器条件进行查询
     * 名字为马姓并且（年龄小于40或邮箱不为空）
     *  name like '马%' and (age<40 or email is not null)
     */
    public void queryByWrapper5(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.likeRight("name", "马").and(wq -> wq.lt("age", 30).or().isNotNull("email"));
        List<User> userList = userMapper.selectList(queryWrapper);
        Assert.assertEquals(2,userList.size());
    }

    @Test
    /**
     * 根据Wrapper构造器条件进行查询
     * 名字为马姓或者（年龄小于40并且年龄大于20并且邮箱不为空）
     * name like '马%' or (age<40 and age>20 and email is not null)
     */
    public void queryByWrapper6(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.likeRight("name", "马").or(wq->wq.between("age",28,30).isNotNull("email"));
        List<User> userList = userMapper.selectList(queryWrapper);
        Assert.assertEquals(1,userList.size());
    }

    @Test
    /**
     *（年龄小于40或邮箱不为空）并且名字为马姓
     * (age<40 or email is not null) and name like '马%'
     */
    public void queryByWrapper7(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        //key为数据库的列名，不是实体中的属性名
        queryWrapper.nested(wq -> wq.lt("age", 40).or().isNotNull("email"))
                .likeRight("name", "马");

        List<User> userList = userMapper.selectList(queryWrapper);
        userList.forEach(System.out::println);
    }

    @Test
    /**
     * 年龄为30、31、34、35
     *     age in (30、31、34、35)
     */
    public void queryByWrapper8(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.in("age",Arrays.asList(30 ,31,34,40));
        List<User> userList = userMapper.selectList(queryWrapper);
        Assert.assertEquals(0,userList.size());
    }

    @Test
    /**
     *只返回满足条件的其中一条语句即可
     */
    public void queryByWrapper9(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.likeRight("name", "马").and(wq -> wq.lt("age", 30).or().isNotNull("email")).last("limit 1");;
        List<User> userList = userMapper.selectList(queryWrapper);
        Assert.assertEquals(1,userList.size());
    }

    @Test
    /**
     *条件构造器查询 select不列出全部字段
     *name like ‘%雨%’ and age<40
     */
    public void selectByWrapperSelect() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        //QueryWrapper<User> query= Wrappers.query();
        //select的列名如果和属性名不一致 可以这么写,但是没有查询的列映射到属性会变成null
        queryWrapper.select("id userId","name realName","email").like("name","马").lt("age",40);
        List<User> userList = userMapper.selectList(queryWrapper);
        Assert.assertEquals(3,userList.size());
    }
    @Test
    /**
     *   条件构造器查询 select不列出全部字段
     */
    public void selectByWrapperSelect2() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.select(User.class,tableFieldInfo -> !tableFieldInfo.getColumn().equals("create_time")&&
                !tableFieldInfo.getColumn().equals("manager_id"));//查的时候排除create_time和manager_id
        List<User> userList = userMapper.selectList(queryWrapper);
        userList.forEach(System.out::println);
    }
    @Test
    /**
     * 条件构造器查询 select不列出全部字段 Condition
     */
    public void selectByWrapperCondition() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        String name="马";
        String email="";
        queryWrapper.like(StringUtils.isNotEmpty(name),"name",name)
                .like(StringUtils.isNotEmpty(email),"email",email);
        List<User> userList = userMapper.selectList(queryWrapper);
        userList.forEach(System.out::println);
    }

    @Test
    /**
     * 条件构造器查询
     * name like ‘%雨%’ and age<40
     */
    public void selectByWrapperEntity() {
        User whereUser =new User();
        whereUser.setName("马三"); //但默认这样写是按照=来查询，如果想要like，可以在实体类属性的@TableField 中设置condition = SqlCondition.LIKE
        whereUser.setAge(18);
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>(whereUser);
        //queryWrapper.like("name", "刘").lt("age", 40); //可以继续条件查询 name=? AND age=? AND name LIKE ? AND age < ?
        List<User> userList = userMapper.selectList(queryWrapper);
        userList.forEach(System.out::println);
    }

    @Test
    /**
     * allEq的作用可以把参数map中 key对应的value为null进行处理  true  xx is null  false  去除 xx
     */
    public void selectByWrapperAllEq() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        Map<String,Object> parms= new HashMap<String,Object>();
        parms.put("name","马誌浩");
        //parms.put("age",25);
        parms.put("age",null);//sql里就是age IS NULL
        queryWrapper.allEq(parms,false);
        //queryWrapper.allEq(parms);
        List<User> userList = userMapper.selectList(queryWrapper);
        userList.forEach(System.out::println);
    }


    @Test
    public void selectByWrapperAllEq3() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        Map<String,Object> parms= new HashMap<String,Object>();
        parms.put("name","马誌浩");
        //parms.put("age",25);
        parms.put("age",null);
        queryWrapper.allEq((k,v)->!k.equals("name"),parms);// 满足条件的（true）才会添加到条件中
        List<User> userList = userMapper.selectList(queryWrapper);
        userList.forEach(System.out::println);
    }


}
