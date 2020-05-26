/*******************************************************************************
 * @(#)MybatisPlusPageTest.java 2020/5/26
 *
 * Copyright 2020 emrubik Group Ltd. All rights reserved.
 * EMRubik PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *******************************************************************************/
package com.basic.study.mybatisplus;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.basic.study.mybatisplus.bean.User;
import com.basic.study.mybatisplus.mapper.UserMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * mybatis-plus 分页测试
 *
 * @author <a href="mailto:mazh@emrubik.com">Ma Zhihao</a>
 * @version $$Revision 1.5 $$ 2020/5/26 8:41
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MybatisPlusPageTest {

    @Autowired
    private UserMapper userMapper;

    /**
     * mybatis-plus 3.2.0 分页返回total为0 解决方案
     */


    @Test
    /**
     *简单的分页
     */
    public void selectPage(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.lt("age",30);
        Page<User> page = new Page<>(1, 6);
        IPage<User> IPage = userMapper.selectPage(page, queryWrapper);
        System.out.println("总页数" + IPage.getPages());
        System.out.println("总记录数" + IPage.getTotal());
        List<User> userList = IPage.getRecords();
        userList.forEach(System.out::println);
        Assert.assertEquals(4,IPage.getTotal());
    }
}
