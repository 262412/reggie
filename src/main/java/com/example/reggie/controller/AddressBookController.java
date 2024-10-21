package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.reggie.common.BaseContext;
import com.example.reggie.common.R;
import com.example.reggie.entity.AddressBook;
import com.example.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("addressBook")
public class AddressBookController {

    // 通过Spring的@Autowired注解自动注入AddressBookService实例
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增地址信息
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
        // 设置地址簿的用户ID为当前上下文的用户ID
        addressBook.setUserId(BaseContext.getCurrentId());
        // 记录日志，输出地址簿信息
        log.info("addressBook:{}", addressBook);
        // 调用服务层方法保存地址簿信息
        addressBookService.save(addressBook);
        // 返回成功响应，包含保存的地址簿信息
        return R.success(addressBook);
    }

    /**
     * 设置默认地址
     */
    @PutMapping("/default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        // 记录日志，输出地址簿信息
        log.info("addressBook:{}", addressBook);
        // 创建条件构造器，用于更新当前用户的地址默认状态
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        wrapper.set(AddressBook::getIsDefault, 0);
        // 执行更新操作，将当前用户的所有地址的默认状态设置为0（非默认）
        addressBookService.update(wrapper);

        // 将传入的地址簿对象设置为默认地址
        addressBook.setIsDefault(1);
        // 更新数据库中的该地址簿对象
        addressBookService.updateById(addressBook);
        // 返回成功响应，包含更新后的地址簿信息
        return R.success(addressBook);
    }

    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    public R get(@PathVariable Long id) {
        // 根据ID查询地址簿信息
        AddressBook addressBook = addressBookService.getById(id);
        // 判断查询结果是否为空，并返回相应的响应
        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            return R.error("没有找到该对象");
        }
    }

    /**
     * 查询默认地址
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault() {
        // 创建条件构造器，用于查询当前用户的默认地址
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        // 执行查询操作，获取默认地址
        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        // 判断查询结果是否为空，并返回相应的响应
        if (null == addressBook) {
            return R.error("没有找到该对象");
        } else {
            return R.success(addressBook);
        }
    }

    /**
     * 查询指定用户的全部地址
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook) {
        // 设置地址簿的用户ID为当前上下文的用户ID
        addressBook.setUserId(BaseContext.getCurrentId());
        // 记录日志，输出地址簿信息
        log.info("addressBook:{}", addressBook);

        // 创建条件构造器，用于查询当前用户的所有地址，并按更新时间降序排序
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        // 执行查询操作，获取地址列表
        return R.success(addressBookService.list(queryWrapper));
    }
}
