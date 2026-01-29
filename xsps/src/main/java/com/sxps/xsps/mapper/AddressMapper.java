package com.sxps.xsps.mapper;

import com.sxps.xsps.pojo.Address;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressMapper {

    @Select("SELECT * FROM address WHERE user_id = #{userId}")
    List<Address> findByUserId(Long userId);

    @Select("SELECT * FROM address WHERE id = #{id}")
    Address findById(Long id);

    @Insert("INSERT INTO address (user_id, name, phone, region, detail, is_default) " +
            "VALUES (#{userId}, #{name}, #{phone}, #{region}, #{detail}, #{isDefault})")
    int insert(Address address);

    @Update("UPDATE address SET name=#{name}, phone=#{phone}, region=#{region}, detail=#{detail}, is_default=#{isDefault} WHERE id=#{id}")
    int update(Address address);

    @Delete("DELETE FROM address WHERE id = #{id}")
    int delete(Long id);

    // ✅ 清除用户所有默认地址标记
    @Update("UPDATE address SET is_default = 0 WHERE user_id = #{userId}")
    void clearDefault(Long userId);

    // ✅ 获取用户默认地址
    @Select("SELECT * FROM address WHERE user_id = #{userId} AND is_default = 1 LIMIT 1")
    Address findDefaultByUserId(Long userId);
}
