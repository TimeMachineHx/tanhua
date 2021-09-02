package com.tanhua.sso.utils;


import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @program: my-tanhua
 * @description: 图像上传包装类
 * @author: HongXin
 * @create: 2021-08-28 11:16
 */


public class OssUtils {

    public static String ossClient(String filename, MultipartFile multipartFile) throws IOException {
        Properties properties = new Properties();
        InputStream stream = OssUtils.class.getClassLoader().getResourceAsStream("aliyun.properties");
        properties.load(stream);
        String endpoint = properties.getProperty("aliyun.endpoint");
        String keyId = (String) properties.get("aliyun.accessKeyId");
        String keySecret = (String) properties.get("aliyun.accessKeySecret");
        String bucket = (String) properties.get("aliyun.bucketName");
        String url = (String) properties.get("aliyun.urlPrefix");
        OSS ossClient = new OSSClient(endpoint, keyId, keySecret);
        try {
            ossClient.putObject(bucket, filename, new ByteArrayInputStream(multipartFile.getBytes()));
            String path = url + filename;
            return path;
        } catch (OSSException e) {
            e.printStackTrace();
        }
        return null;
    }

}
