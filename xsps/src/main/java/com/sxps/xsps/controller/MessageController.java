package com.sxps.xsps.controller;

import com.sxps.xsps.pojo.PageParam;
import com.sxps.xsps.pojo.PageResult;
import com.sxps.xsps.pojo.Result;
import com.sxps.xsps.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /** 分页查询消息 */
    @PostMapping("/page/{receiverId}")
    public Result<PageResult<?>> page(@PathVariable Long receiverId,
                                      @RequestBody PageParam param) {
        return Result.ok(messageService.page(receiverId, param));
    }

    /** 未读数量 */
    @GetMapping("/unread/{receiverId}")
    public Result<?> unread(@PathVariable Long receiverId) {
        return Result.ok(messageService.countUnread(receiverId));
    }

    /** 标记已读 */
    @PostMapping("/read/{id}")
    public Result<?> read(@PathVariable Long id) {
        return messageService.markAsRead(id)
                ? Result.ok("已读")
                : Result.fail("消息不存在");
    }
}
