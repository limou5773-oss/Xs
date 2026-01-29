package com.sxps.xsps.controller;

import com.sxps.xsps.pojo.Result;
import com.sxps.xsps.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notice") //小程序公告
@RequiredArgsConstructor
public class ClientNoticeController {

    private final NoticeService noticeService;

    /**
     * 获取最新公告 (给小程序首页用)
     */
    @GetMapping("/latest")
    public Result<String> getLatest() {
        String content = noticeService.getLatest();
        if (content == null || content.isEmpty()) {
            return Result.ok("欢迎光临社区生鲜店，新鲜每一天！");
        }
        return Result.ok(content);
    }
}