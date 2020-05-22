/*******************************************************************************
 * @(#)UserMapper.java 2020/5/22
 *
 * Copyright 2020 emrubik Group Ltd. All rights reserved.
 * EMRubik PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *******************************************************************************/
package com.basic.study.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basic.study.mybatisplus.bean.User;
import org.springframework.stereotype.Component;

/**
 * 用户mapper
 *
 * @author <a href="mailto:mazh@emrubik.com">Ma Zhihao</a>
 * @version $$Revision 1.5 $$ 2020/5/22 12:32
 */
/**
 * @Component 是为了解决IDEA Could not autowire. No beans of 'UserMapper' type found. 报错问题，实
 * 际不加不影响操作,修改idea File ——> Settings ——> Editor ——> Inspections 为warn  级别也可以
 */
@Component
public interface UserMapper extends BaseMapper<User>{
}
