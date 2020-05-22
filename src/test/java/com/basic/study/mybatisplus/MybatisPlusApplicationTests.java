package com.basic.study.mybatisplus;

import com.basic.study.mybatisplus.bean.User;
import com.basic.study.mybatisplus.mapper.UserMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
class MybatisPlusApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testGetList(){
        List<User> list = userMapper.selectList(null);
        Assert.assertEquals(2,list.size());
    }
    @Test
    public void testInsert(){
        User user = new User();
        user.setAge(18);
        user.setName("张三");
        user.setManagerId(1);
        user.setCreateTime(new Date());
        Assert.assertEquals(1,userMapper.insert(user));
    }
}
