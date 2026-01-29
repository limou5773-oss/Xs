package com.sxps.xsps.service.impl;

import com.github.pagehelper.PageHelper;
import com.sxps.xsps.mapper.ProductMapper;
import com.sxps.xsps.pojo.PageParam;
import com.sxps.xsps.pojo.PageResult;
import com.sxps.xsps.pojo.Product;
import com.sxps.xsps.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper mapper;

    /**
     * 分页查询
     */
    @Override
    public PageResult<Product> page(PageParam param) {
        // 1. 设置分页参数 (当前页, 每页数量)
        PageHelper.startPage(param.getPage(), param.getSize());

        // 2. 执行查询 (MyBatis 会自动拦截并拼接 LIMIT)
        List<Product> list = mapper.page(param);

        // 3. 封装结果返回
        return new PageResult<>(list);
    }

    /**
     * 根据ID查询详情
     */
    @Override
    public Product get(Long id) {
        return mapper.getById(id);
    }

    /**
     * 添加商品
     */
    @Override
    public boolean add(Product product) {
        // 返回受影响行数 > 0 表示成功
        return mapper.add(product) > 0;
    }

    /**
     * 更新商品
     */
    @Override
    public boolean update(Product product) {
        return mapper.update(product) > 0;
    }

    /**
     * 删除商品
     */
    @Override
    public boolean delete(Long id) {
        return mapper.delete(id) > 0;
    }

    /**
     * ⭐⭐⭐ 新增：获取随机推荐商品 ⭐⭐⭐
     * 对应 Controller 里的 productService.getRecommend(6)
     */
    @Override
    public List<Product> getRecommend(Integer count) {
        // 调用 Mapper 中定义的 selectRandom 方法
        return mapper.selectRandom(count);
    }
}