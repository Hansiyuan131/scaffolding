package com.yuanstack.sca.service.system.assembly.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.CompleteMultipartUploadResult;
import com.aliyun.oss.model.UploadFileRequest;
import com.aliyun.oss.model.UploadFileResult;
import com.yuanstack.sca.service.system.common.constants.ModelEnum;
import com.yuanstack.sca.service.system.common.log.LogUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Date;

/**
 * @description: OSS工具类
 * @author: hansiyuan
 * @date: 2022/6/29 11:40 AM
 */
@Slf4j
public class OSSUtils {

    /**
     * 文件上传
     */
    public static boolean uploadFile(String endpoint, String accessKeyId, String accessKeySecret,
                                     String bucketName, String localFilePath, String fileName) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, fileName);

            // The local file to upload---it must exist.
            uploadFileRequest.setUploadFile(localFilePath);
            // Sets the concurrent upload task number to 5.
            uploadFileRequest.setTaskNum(5);
            // Sets the part size to 1MB.
            uploadFileRequest.setPartSize(1024 * 1024 * 1);
            // Enables the checkpoint file. By default it's off.
            uploadFileRequest.setEnableCheckpoint(true);

            UploadFileResult uploadResult = ossClient.uploadFile(uploadFileRequest);

            CompleteMultipartUploadResult multipartUploadResult =
                    uploadResult.getMultipartUploadResult();

            return true;
        } catch (OSSException oe) {
            LogUtils.error(log, ModelEnum.COMMON_OSS, "OSSUtils.uploadImage异常", localFilePath, oe);
        } catch (ClientException ce) {
            LogUtils.error(log, ModelEnum.COMMON_OSS, "OSSUtils.uploadImage异常", localFilePath, ce);
        } catch (Throwable e) {
            LogUtils.error(log, ModelEnum.COMMON_OSS, "OSSUtils.uploadImage异常", localFilePath, e);
        } finally {
            ossClient.shutdown();
        }

        return false;
    }

    /***
     *
     */
    public static String getOssUrl(String endpoint, String accessKeyId, String accessKeySecret,
                                   String bucketName, String fileName) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            // 设置URL过期时间为1小时。
            Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
            // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
            URL url = ossClient.generatePresignedUrl(bucketName, fileName, expiration);
            return url.toString();
        } catch (Exception exception) {
            LogUtils.error(log, ModelEnum.COMMON_OSS, "OSSUtils.getOssUrl异常", exception);
        } finally {
            // 关闭OSSClient。
            ossClient.shutdown();
        }
        return null;
    }
}


