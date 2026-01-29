package com.sxps.xsps.mapper;

import com.sxps.xsps.pojo.Cart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartMapper {

    /** 查询某用户的购物车列表 */
    @Select("SELECT * FROM cart WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Cart> selectByUserId(Long userId);

    /** 根据ID查询单个 */
    @Select("SELECT * FROM cart WHERE id = #{id}")
    Cart selectById(Long id);

    /** 查询某用户购物车里是否已经有该商品（用于判断是新增还是数量+1） */
    @Select("SELECT * FROM cart WHERE user_id = #{userId} AND product_id = #{productId} LIMIT 1")
    Cart selectByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);

    /** 新增购物车 */
    @Insert("""
        INSERT INTO cart(user_id, product_id, product_name, price, quantity, cover, checked, create_time, update_time)
        VALUES(#{userId}, #{productId}, #{productName}, #{price}, #{quantity}, #{cover}, #{checked}, NOW(), NOW())
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Cart cart);

    /** 更新数量 */
    @Update("UPDATE cart SET quantity = #{quantity}, update_time = NOW() WHERE id = #{id}")
    int updateQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);

    /** 更新选中状态 */
    @Update("UPDATE cart SET checked = #{checked}, update_time = NOW() WHERE id = #{id}")
    int updateChecked(@Param("id") Long id, @Param("checked") Integer checked);

    /** 删除单个 */
    @Delete("DELETE FROM cart WHERE id = #{id}")
    int deleteById(Long id);

    /** 清空某用户的购物车 */
    @Delete("DELETE FROM cart WHERE user_id = #{userId}")
    int deleteByUserId(Long userId);

    /** 用于下单成功后，清理购物车中对应的商品 */
    @Delete("DELETE FROM cart WHERE user_id = #{userId} AND product_id = #{productId}")
    void deleteCartItem(@Param("userId") Long userId, @Param("productId") Long productId);
}
