package com.sxps.xsps.mapper;

import com.sxps.xsps.pojo.Notice;
import com.sxps.xsps.pojo.PageParam;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NoticeMapper {

    List<Notice> page(PageParam param);

    @Insert("""
        INSERT INTO notice (title, content, is_published, create_time, update_time)
        VALUES (#{title}, #{content}, #{isPublished}, NOW(), NOW())
    """)
    int insert(Notice n);

    @Update("""
        UPDATE notice SET
            title = #{title},
            content = #{content},
            update_time = NOW()
        WHERE id = #{id}
    """)
    int update(Notice n);

    @Delete("DELETE FROM notice WHERE id = #{id}")
    int delete(Long id);

    @Select("SELECT * FROM notice WHERE id = #{id}")
    Notice find(Long id);

    //查询最新发布的公告内容 (给小程序用)
    @Select("SELECT content FROM notice WHERE is_published = 1 ORDER BY create_time DESC LIMIT 1")
    String selectLatest();
}
