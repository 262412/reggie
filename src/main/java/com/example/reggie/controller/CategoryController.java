package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.R;
import com.example.reggie.entity.Category;
import com.example.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
public class CategoryController {
    // 自动注入CategoryService接口的实例
    @Autowired
    private CategoryService categoryService;

    /**
     * 保存新增分类信息
     *
     * @param category 新增分类的实体对象，通过请求体传递
     * @return 返回表示操作结果的字符串信息
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 分页查询分类信息
     *
     * @param page 当前页码
     * @param pageSize 每页记录数
     * @return 返回包含分类信息的Page对象
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        // 创建Page对象，用于存储分页查询的结果
        Page pageInfo = new Page<>(page, pageSize);
        // 创建查询条件对象，并指定按照sort字段升序排序
        LambdaQueryWrapper <Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        // 执行分页查询
        categoryService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 删除分类信息
     *
     * @param ids 要删除的分类的ID
     * @return 返回表示操作结果的字符串信息
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        categoryService.remove(ids);
        return R.success("删除分类成功");
    }

    /**
     * 更新分类信息
     *
     * @param category 包含更新后信息的分类实体对象，通过请求体传递
     * @return 返回表示操作结果的字符串信息
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改分类成功");
    }
}
