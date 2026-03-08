package com.sxps.xsps.service.impl;

import com.sxps.xsps.mapper.ReviewMapper;
import com.sxps.xsps.mapper.MerchantOrderMapper; // 引入订单Mapper
import com.sxps.xsps.pojo.Review;
import com.sxps.xsps.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 引入事务

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private MerchantOrderMapper orderMapper; // 注入订单Mapper

    @Override
    @Transactional // 加上事务注解，保证评价和更新订单状态同时成功或失败
    public void addReview(Review review) {
        // 1. 检查是否重复评价
        if (reviewMapper.selectByOrderId(review.getOrderId()) != null) {
            throw new RuntimeException("该订单已评价，请勿重复操作");
        }

        // 2. 插入评价数据
        reviewMapper.insert(review);

        // 3. 关键一步：将该订单的状态更新为 6（已评价）
        orderMapper.updateOrderStatus(review.getOrderId(), 6);
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewMapper.selectAll();
    }

    @Override
    public List<Review> getReviewsByProduct(Long productId) {
        return reviewMapper.selectByProductId(productId);
    }
}