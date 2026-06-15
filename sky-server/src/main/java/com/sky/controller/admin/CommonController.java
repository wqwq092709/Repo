package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin/common")
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        try {
            String originalFilename = file.getOriginalFilename();

            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectFile = UUID.randomUUID() + extension;
            String filePath = aliOssUtil.upload(file.getBytes(), objectFile);
            return Result.success(filePath);
        } catch (IOException ex) {
            return Result.error("文件上传失败");
        }
    }
}
