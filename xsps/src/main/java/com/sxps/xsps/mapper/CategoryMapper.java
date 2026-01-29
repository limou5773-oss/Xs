package com.sxps.xsps.mapper;

import com.sxps.xsps.pojo.Category;
import com.sxps.xsps.pojo.PageParam;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper {

    /** 查询全部分类 */
    @Select("SELECT id, name, create_time AS createTime, update_time AS updateTime " +
            "FROM category ORDER BY create_time DESC")
    List<Category> listAll();

    /** 新增分类 */
    @Insert("INSERT INTO category(name, create_time, update_time) " +
            "VALUES(#{name}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertCategory(Category category);

    /** 更新分类 */
    @Update("UPDATE category SET name=#{name}, update_time=NOW() WHERE id=#{id}")
    int updateCategory(Category category);

    /** 删除分类 */
    @Delete("DELETE FROM category WHERE id=#{id}")
    int deleteCategory(Long id);

    List<Category> page(@Param("p") PageParam param);
}
