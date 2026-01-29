package com.sxps.xsps.mapper;

import com.sxps.xsps.pojo.PageParam;
import com.sxps.xsps.pojo.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {

    // 查询在 XML (ProductMapper.xml)
    List<Product> page(PageParam param);

    @Select("""
        SELECT p.*, c.name AS categoryName
        FROM product p
        LEFT JOIN category c ON p.category_id = c.id
        WHERE p.id = #{id}
    """)
    Product getById(Long id);

    @Insert("""
        INSERT INTO product(
            merchant_id, category_id, name, price, stock,
            unit, cover, description, status, create_time, update_time
        ) VALUES (
            #{merchantId}, #{categoryId}, #{name}, #{price}, #{stock},
            #{unit}, #{cover}, #{description}, #{status}, NOW(), NOW()
        )
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int add(Product product);

    @Update("""
        UPDATE product SET
            category_id = #{categoryId},
            name = #{name},
            price = #{price},
            stock = #{stock},
            unit = #{unit},
            cover = #{cover},
            description = #{description},
            status = #{status},
            update_time = NOW()
        WHERE id = #{id}
    """)
    int update(Product product);

    @Delete("DELETE FROM product WHERE id=#{id}")
    int delete(Long id);
    List<Product> selectRandom(@Param("limit") Integer limit);

    // 减库存
    @Update("UPDATE product SET stock = stock - #{num}, update_time = NOW() WHERE id = #{id} AND stock >= #{num}")
    int deductStock(@Param("id") Long id, @Param("num") Integer num);

    //新增：回滚库存 (取消订单时调用)
    @Update("UPDATE product SET stock = stock + #{num}, update_time = NOW() WHERE id = #{id}")
    int incrementStock(@Param("id") Long id, @Param("num") Integer num);
}