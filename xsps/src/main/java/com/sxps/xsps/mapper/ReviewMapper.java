package com.sxps.xsps.mapper;

import com.sxps.xsps.pojo.Review;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ReviewMapper {

    // 1. 插入评价
    void insert(Review review);

    // 2. 根据订单ID查询是否已评价
    Review selectByOrderId(Long orderId);

    // 3. 查询所有评价（我们将在 XML 中给它写连表 SQL）
    List<Review> selectAll();

    // 4. 根据商品查询评价
    List<Review> selectByProductId(Long productId);
}