package com.sxps.xsps.mapper;

import com.sxps.xsps.pojo.Order;
import com.sxps.xsps.pojo.PageParam;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MerchantOrderMapper {

    /**
     * 插入订单
     */
    @Insert("""
        INSERT INTO orders
        (order_no, user_id, merchant_id,
         total_price, pay_price,
         status, address_snapshot, remark,
         create_time, update_time)
        VALUES
        (#{orderNo}, #{userId}, #{merchantId},
         #{totalPrice}, #{payPrice},
         #{status}, #{addressSnapshot}, #{remark},
         #{createTime}, #{updateTime})
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);

    /**
     * 根据 ID 查询（不含 items）
     */
    @Select("SELECT * FROM orders WHERE id = #{id}")
    Order selectById(@Param("id") Long id);

    /**
     * 按用户查询订单列表（不含 items）
     */
    @Select("""
        SELECT *
        FROM orders
        WHERE user_id = #{userId}
        ORDER BY create_time DESC
    """)
    List<Order> listByUserId(@Param("userId") Long userId);

    /**
     * 商家端订单列表（不含 items）
     * （给以前的接口用，如果不用也可以保留）
     */
    @Select("""
        SELECT *
        FROM orders
        WHERE merchant_id = #{merchantId}
          AND (#{status} IS NULL OR status = #{status})
        ORDER BY create_time DESC
    """)
    List<Order> listByMerchant(@Param("merchantId") Long merchantId,
                               @Param("status") Integer status);

    /**
     * 按状态列出订单（自动任务用）
     */
    @Select("SELECT * FROM orders WHERE status = #{status}")
    List<Order> listByStatus(@Param("status") Integer status);

    /**
     * 更新订单
     */
    @Update("""
        UPDATE orders
        SET 
            pay_price = #{payPrice},
            status = #{status},
            remark = #{remark},
            update_time = #{updateTime}
        WHERE id = #{id}
    """)
    int updateById(Order order);

    /**
     * 售后同意退款 → 修改订单状态
     */
    @Update("""
        UPDATE orders
        SET status = #{status}, update_time = NOW()
        WHERE id = #{orderId}
    """)
    int updateOrderStatus(@Param("orderId") Long orderId,
                          @Param("status") Integer status);

    /**
     * 分页查询（配合 XML + PageHelper）
     * 参数统一用 PageParam
     */
    List<Order> pageList(PageParam param);
}
