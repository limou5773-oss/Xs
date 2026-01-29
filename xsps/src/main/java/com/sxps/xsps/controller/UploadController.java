package com.sxps.xsps.controller;

import com.sxps.xsps.pojo.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @PostMapping
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            // 上传到项目根路径 /imgs/
            String uploadDir = System.getProperty("user.dir") + File.separator + "imgs" + File.separator;
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成唯一文件名
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File dest = new File(uploadDir + fileName);
            file.transferTo(dest);

            // 访问路径
            String fileUrl = "http://localhost:8080/imgs/" + fileName;

            return Result.ok(fileUrl);
        } catch (Exception e) {
            return Result.fail("上传失败：" + e.getMessage());
        }
    }
}