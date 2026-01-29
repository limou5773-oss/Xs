package com.sxps.xsps.mapper;

import com.sxps.xsps.pojo.AfterSale;
import com.sxps.xsps.pojo.OrderItem;
import com.sxps.xsps.pojo.PageParam;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MerchantAfterSaleMapper {

    @Insert("""
        INSERT INTO after_sale(
            order_id,
            user_id,
            merchant_id,
            type,
            reason,
            status,
            create_time
        ) VALUES(
            #{orderId},
            #{userId},
            #{merchantId},
            #{type},
            #{reason},
            #{status},
            #{createTime}
        )
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AfterSale afterSale);

    @Select("""
        SELECT a.*, o.order_no AS orderNo
        FROM after_sale a
        LEFT JOIN orders o ON a.order_id = o.id
        WHERE a.merchant_id = #{merchantId}
          AND (#{status} IS NULL OR a.status = #{status})
        ORDER BY a.create_time DESC
    """)
    List<AfterSale> listByMerchant(@Param("merchantId") Long merchantId,
                                   @Param("status") Integer status);

    @Select("""
        SELECT a.*, o.order_no AS orderNo
        FROM after_sale a
        LEFT JOIN orders o ON a.order_id = o.id
        WHERE a.id = #{id}
    """)
    AfterSale getById(Long id);

    /** 用户端详情：根据订单 ID 拿最近一条售后记录 */
    @Select("""
        SELECT *
        FROM after_sale
        WHERE order_id = #{orderId}
        ORDER BY create_time DESC
        LIMIT 1
    """)
    AfterSale getLatestByOrderId(@Param("orderId") Long orderId);

    /** 取消售后：根据 订单ID + 用户ID 拿最近一条售后记录 */
    @Select("""
        SELECT *
        FROM after_sale
        WHERE order_id = #{orderId}
          AND user_id = #{userId}
        ORDER BY create_time DESC
        LIMIT 1
    """)
    AfterSale getByOrderAndUser(@Param("orderId") Long orderId,
                                @Param("userId") Long userId);

    @Update("""
        UPDATE after_sale
        SET status = #{status}, handle_time = NOW()
        WHERE id = #{id}
    """)
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Select("SELECT * FROM order_item WHERE order_id = #{orderId}")
    List<OrderItem> listItems(Long orderId);

    List<AfterSale> pageList(PageParam param);
}
