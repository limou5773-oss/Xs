package com.sxps.xsps.controller;

import com.sxps.xsps.pojo.PageParam;
import com.sxps.xsps.pojo.Result;
import com.sxps.xsps.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping("/page")
    public Result<?> page(@RequestBody PageParam param) {
        return Result.ok(noticeService.page(param));
    }

    @PostMapping("/create")
    public Result<?> create(@RequestBody Map<String, String> param) {
        noticeService.create(param.get("title"), param.get("content"));
        return Result.ok("发布成功");
    }

    @PostMapping("/update")
    public Result<?> update(@RequestBody Map<String, Object> param) {
        Long id = Long.valueOf(param.get("id").toString());
        noticeService.update(id, param.get("title").toString(), param.get("content").toString());
        return Result.ok("修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return Result.ok("删除成功");
    }

    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Long id) {
        return Result.ok(noticeService.detail(id));
    }
}

