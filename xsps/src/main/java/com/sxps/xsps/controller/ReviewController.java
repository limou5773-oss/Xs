package com.sxps.xsps.controller;

import com.sxps.xsps.pojo.Result;
import com.sxps.xsps.pojo.Review;
import com.sxps.xsps.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // 顾客提交评价
    @PostMapping("/add")
    public Result<?> add(@RequestBody Review review) {
        try {
            reviewService.addReview(review);
            return Result.ok(); // 修改为你的 ok() 方法
        } catch (Exception e) {
            return Result.fail(e.getMessage()); // 修改为你的 fail() 方法
        }
    }

    // 商家查看评价列表
    @GetMapping("/list")
    public Result<?> list() {
        return Result.ok(reviewService.getAllReviews()); // 修改为你的 ok(data) 方法
    }

    // 获取某个商品的评价
    @GetMapping("/product/{productId}")
    public Result<?> getProductReviews(@PathVariable Long productId) {
        return Result.ok(reviewService.getReviewsByProduct(productId));
    }
}