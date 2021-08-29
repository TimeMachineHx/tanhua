package com.tanhua.sso.service;

import com.tanhua.sso.utils.OssUtils;
import com.tanhua.sso.vo.ErrorResult;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: my-tanhua
 * @description:
 * @author: HongXin
 * @create: 2021-08-28 11:49
 */

@Service
public class PicUploadService {
    //定义可上传的照片格式
    private static final String[] IMAGE_TYPE = new String[]{".bmp", ".jpg",
            ".jpeg", ".gif", ".png"};

    public ResponseEntity<Object> upload(MultipartFile multipartFile) throws IOException {
        //判断文件是否允许上传的格式
        boolean isLegal = false;

        for (String type : IMAGE_TYPE) {
            if (StringUtils.endsWithIgnoreCase(multipartFile.getOriginalFilename(), type)) {
                isLegal = true;
                break;
            }
        }
        if (!isLegal) {
            ErrorResult build = ErrorResult.builder().errCode("000002").errMessage("照片类型不符合！").build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(build);
        }

        //判断照片大小不超过5m
        long size = multipartFile.getSize();
        if (5 * 1024 * 1024 <= size) {
            ErrorResult build = ErrorResult.builder().errCode("000002").errMessage("照片大小不能超过5M！").build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(build);
        }

        /**
         * 文件上传
         */
        //获取文件名称
        String filename = multipartFile.getOriginalFilename();
        //新的文件名称
        String filePath = getFilePath(filename);
       //调用文件上传方法，返回文件访问路径
        String path = OssUtils.ossClient(filePath, multipartFile);
        if(StringUtils.isEmpty(path)){
            ErrorResult build = ErrorResult.builder().errCode("000002").errMessage("文件上传失败,请稍后重试").build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(build);
        }
        HashMap<String,String> map = new  HashMap<>();
        map.put("name",path);
        return  ResponseEntity.status(HttpStatus.OK).body(map);
    }

    /**
     * 文件存放路径与名称定义
     *
     * @param sourceFileName
     * @return
     */
    private String getFilePath(String sourceFileName) {
        // 获取当前时间
        DateTime dateTime = new DateTime();
        // lufei.jpg
        // images/yyyy/MM/dd/xxxxxxx.jpg
        // 是把文件名称和目录分离一起做了
        return "images/" + dateTime.toString("yyyy")
                + "/" + dateTime.toString("MM") + "/"
                + dateTime.toString("dd") + "/" + System.currentTimeMillis() +
                RandomUtils.nextInt(100, 9999) + "." +
                StringUtils.substringAfterLast(sourceFileName, ".");
    }
}
