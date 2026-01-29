package com.sxps.xsps.mapper;

import com.sxps.xsps.pojo.Message;
import com.sxps.xsps.pojo.PageParam;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MerchantMessageMapper {

    /** 查询某个商家后台用户的某个订单消息（判断是新增还是更新） */
    @Select("""
        SELECT * FROM message
        WHERE receiver_id = #{receiverId}
          AND order_id = #{orderId}
        LIMIT 1
    """)
    Message findByOrder(@Param("receiverId") Long receiverId,
                        @Param("orderId") Long orderId);

    /** 插入一条订单消息 */
    @Insert("""
        INSERT INTO message (receiver_id, order_id, status, content, is_read, create_time)
        VALUES (#{receiverId}, #{orderId}, #{status}, #{content}, 0, NOW())
    """)
    int insertOrderMessage(@Param("receiverId") Long receiverId,
                           @Param("orderId") Long orderId,
                           @Param("status") Integer status,
                           @Param("content") String content);

    /** 更新同一条订单消息 */
    @Update("""
        UPDATE message
        SET status = #{status},
            content = #{content},
            create_time = NOW()
        WHERE id = #{id}
    """)
    int updateOrderMessage(@Param("id") Long id,
                           @Param("status") Integer status,
                           @Param("content") String content);

    /** 分页查询 */
    List<Message> page(@Param("receiverId") Long receiverId,
                       @Param("p") PageParam param);

    /** 未读数量 */
    @Select("SELECT COUNT(*) FROM message WHERE receiver_id = #{receiverId} AND is_read = 0")
    int countUnread(Long receiverId);

    /** 标记已读 */
    @Update("UPDATE message SET is_read = 1, read_time = NOW() WHERE id = #{id}")
    int markAsRead(Long id);
}
