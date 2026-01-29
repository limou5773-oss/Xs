package com.sxps.xsps.mapper;

import com.sxps.xsps.pojo.MerchantUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MerchantUserMapper {

    @Select("SELECT id, merchant_id AS merchantId, username, password, " +
            "created_at AS createdAt, updated_at AS updatedAt " +
            "FROM merchant_user WHERE username = #{username}")
    MerchantUser findByUsername(String username);

    @Update("UPDATE merchant_user SET password = #{password} WHERE id = #{id}")
    void updatePassword(Long id, String password);
}
