package com.sxps.xsps.controller;

import com.sxps.xsps.pojo.PageParam;
import com.sxps.xsps.pojo.PageResult;
import com.sxps.xsps.pojo.Result;
import com.sxps.xsps.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/message")//消息页面
@RequiredArgsConstructor
public class AdminMessageController {

    private final MessageService messageService;

    /** 后台商家分页消息 */
    @PostMapping("/page/{merchantUserId}")
    public Result<PageResult<?>> page(@PathVariable Long merchantUserId,
                                      @RequestBody PageParam param) {
        return Result.ok(messageService.page(merchantUserId, param));
    }

    /** 未读数量 */
    @GetMapping("/unread/{merchantUserId}")
    public Result<?> unread(@PathVariable Long merchantUserId) {
        return Result.ok(messageService.countUnread(merchantUserId));
    }

    /** 标记为已读 */
    @PostMapping("/read/{id}")
    public Result<?> read(@PathVariable Long id) {
        return messageService.markAsRead(id)
                ? Result.ok("已读")
                : Result.fail("消息不存在");
    }
}
