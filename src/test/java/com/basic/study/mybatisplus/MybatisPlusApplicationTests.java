package com.basic.study.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlLike;
import com.basic.study.mybatisplus.bean.User;
import com.basic.study.mybatisplus.mapper.UserMapper;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
}
