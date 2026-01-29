package com.sxps.xsps.service;

import com.sxps.xsps.pojo.Category;
import com.sxps.xsps.pojo.PageParam;
import com.sxps.xsps.pojo.PageResult;

import java.util.List;

public interface MerchantCategoryService {
    List<Category> list();
    boolean save(Category category);
    boolean update(Category category);
    boolean removeById(Long id);
    PageResult<Category> page(PageParam param);
}
