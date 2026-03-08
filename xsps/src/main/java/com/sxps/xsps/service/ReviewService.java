package com.sxps.xsps.service;

import com.sxps.xsps.pojo.Review;
import java.util.List;

public interface ReviewService {
    void addReview(Review review);
    List<Review> getAllReviews();
    List<Review> getReviewsByProduct(Long productId);
}