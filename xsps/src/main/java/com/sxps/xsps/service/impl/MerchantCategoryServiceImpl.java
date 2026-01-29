package com.sxps.xsps.service.impl;

import com.github.pagehelper.PageHelper;
import com.sxps.xsps.mapper.CategoryMapper;
import com.sxps.xsps.pojo.Category;
import com.sxps.xsps.pojo.PageParam;
import com.sxps.xsps.pojo.PageResult;
import com.sxps.xsps.service.MerchantCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantCategoryServiceImpl implements MerchantCategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public List<Category> list() {
        return categoryMapper.listAll();
    }

    @Override
    public boolean save(Category category) {
        return categoryMapper.insertCategory(category) > 0;
    }

    @Override
    public boolean update(Category category) {
        return categoryMapper.updateCategory(category) > 0;
    }

    @Override
    public boolean removeById(Long id) {
        return categoryMapper.deleteCategory(id) > 0;
    }

    @Override
    public PageResult<Category> page(PageParam param) {
        PageHelper.startPage(param.getPage(), param.getSize());
        return new PageResult<>(categoryMapper.page(param));
    }

}
