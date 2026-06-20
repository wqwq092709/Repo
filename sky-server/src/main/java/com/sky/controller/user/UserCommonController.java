package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Api(tags = "C端公共接口")
@RestController
@RequestMapping("/user/common")
public class UserCommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    @ApiOperation("上传用户头像")
    public Result<String> upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Result.error("请选择头像文件");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.lastIndexOf(".") >= 0) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String objectName = "avatar/" + UUID.randomUUID() + extension;
            return Result.success(aliOssUtil.upload(file.getBytes(), objectName));
        } catch (IOException ex) {
            return Result.error("头像上传失败");
        }
    }
}
