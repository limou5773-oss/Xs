package com.sxps.xsps.mapper;

import com.sxps.xsps.pojo.OrderItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MerchantOrderItemMapper {

    @Insert("""
        INSERT INTO order_item
        (order_id, product_id, product_name, price, quantity, cover)
        VALUES
        (#{orderId}, #{productId}, #{productName}, #{price}, #{quantity}, #{cover})
    """)
    int insert(OrderItem item);

    @Select("SELECT * FROM order_item WHERE order_id = #{orderId}")
    List<OrderItem> listByOrderId(Long orderId);

    @Update("""
    UPDATE orders
    SET status = #{status}, update_time = NOW()
    WHERE id = #{orderId}
""")
    int updateOrderStatus(@Param("orderId") Long orderId,
                          @Param("status") Integer status);

}
