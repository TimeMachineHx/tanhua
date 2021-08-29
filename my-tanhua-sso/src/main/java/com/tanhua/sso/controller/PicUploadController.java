package com.tanhua.sso.controller;

import com.tanhua.sso.service.PicUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * @program: my-tanhua
 * @description: 新用户上传信息、上传照片
 * @author: HongXin
 * @create: 2021-08-28 11:36
 */

@RestController
@RequestMapping("pic/upload")
public class PicUploadController {
    @Autowired
    private PicUploadService uploadService;

    @PostMapping
    public ResponseEntity<Object> upload(@RequestParam("file") MultipartFile multipartFile) throws IOException {
             return uploadService.upload(multipartFile);
    }
}
