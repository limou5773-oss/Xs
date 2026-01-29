package com.sxps.xsps.mapper;

import com.sxps.xsps.pojo.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE openid = #{openid} LIMIT 1")
    User getByOpenId(@Param("openid") String openid);

    @Select("SELECT * FROM user WHERE mobile = #{mobile} LIMIT 1")
    User getByMobile(@Param("mobile") String mobile);

    @Select("SELECT * FROM user WHERE id = #{id} LIMIT 1")
    User findById(@Param("id") Long id);

    @Insert("""
        INSERT INTO user (openid, nickname, avatar, role, mobile, create_time, update_time)
        VALUES (#{openid}, #{nickname}, #{avatar}, #{role}, #{mobile}, NOW(), NOW())
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertUser(User user);

    @Update("""
        UPDATE user
        SET 
            nickname = #{nickname},
            avatar = #{avatar},
            role = #{role},
            mobile = #{mobile},
            update_time = #{updateTime}
        WHERE id = #{id}
    """)
    int updateUser(User user);

    @Update("""
        UPDATE user
        SET 
            nickname = #{nickname},
            avatar = #{avatar},
            role = #{role},
            mobile = #{mobile},
            update_time = #{updateTime}
        WHERE id = #{id}
    """)
    int updateById(User user);
}
