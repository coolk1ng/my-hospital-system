package com.codesniper.controller;

import com.codesniper.common.result.Result;
import com.codesniper.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传Controller
 *
 * @author CodeSniper
 * @since 2022/7/7 12:58
 */
@RestController
@Api(tags = "文件操作")
@RequestMapping("/api/oss/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("fileUpload")
    @ApiOperation("文件上传")
    public Result<String> fileUpload(MultipartFile file) {
        return Result.ok(fileService.fileUpload(file));
    }
}
