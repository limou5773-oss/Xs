package com.sxps.xsps.service;

import com.sxps.xsps.pojo.PageParam;
import com.sxps.xsps.pojo.PageResult;
import com.sxps.xsps.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductService {

    PageResult<Product> page(PageParam param);

    Product get(Long id);

    boolean add(Product product);

    boolean update(Product product);

    boolean delete(Long id);

    List<Product> getRecommend(Integer count);
}
