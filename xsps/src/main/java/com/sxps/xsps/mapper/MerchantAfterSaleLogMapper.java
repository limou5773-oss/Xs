package com.sxps.xsps.mapper;

import com.sxps.xsps.pojo.AfterSaleLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MerchantAfterSaleLogMapper {

    @Insert("""
        INSERT INTO after_sale_log (after_sale_id, action, operator, create_time)
        VALUES(#{afterSaleId}, #{action}, #{operator}, #{createTime})
    """)
    int insert(AfterSaleLog log);

    @Select("""
        SELECT * FROM after_sale_log 
        WHERE after_sale_id = #{afterSaleId}
        ORDER BY create_time DESC
    """)
    List<AfterSaleLog> listByAfterSaleId(Long afterSaleId);
}
