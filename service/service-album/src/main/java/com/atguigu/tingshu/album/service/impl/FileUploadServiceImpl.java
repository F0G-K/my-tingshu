package com.atguigu.tingshu.album.service.impl;

import com.atguigu.tingshu.album.config.MinioConstantProperties;
import com.atguigu.tingshu.album.service.FileUploadService;
import com.atguigu.tingshu.common.execption.GuiguException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service

public class FileUploadServiceImpl implements FileUploadService {

    /**
     * 图片文件大小上限：2MB
     */
    private static final long MAX_IMAGE_SIZE = 2 * 1024 * 1024;

    @Autowired
    private MinioClient minioClient;
    @Autowired
    private MinioConstantProperties minioConstantProperties;

    /**
     * 图片（封面、头像）文件上传
     * 前端提交文件参数名：file
     *
     * @param multipartFile
     * @return 文件在线访问地址
     */
    @Override
    public String fileUpload(MultipartFile multipartFile) {
        try {
            //1.校验文件
            //1.1 校验文件是否为空
            if (multipartFile == null || multipartFile.isEmpty()) {
                throw new GuiguException(201, "上传文件不能为空");
            }
            //1.2 校验文件大小：不能超过2MB
            if (multipartFile.getSize() > MAX_IMAGE_SIZE) {
                throw new GuiguException(201, "上传文件大小不能超过2MB");
            }
            //1.3 校验文件类型：MIME类型必须为图片
            String contentType = multipartFile.getContentType();
            if (!StringUtils.hasText(contentType) || !contentType.startsWith("image/")) {
                throw new GuiguException(201, "上传文件类型必须为图片");
            }
            //1.4 校验文件内容：尝试读取图片内容，防止伪造后缀/类型的非图片文件
            BufferedImage bufferedImage = ImageIO.read(multipartFile.getInputStream());
            if (bufferedImage == null) {
                throw new GuiguException(201, "上传文件内容不是有效图片");
            }

            //2.确保桶存在，不存在则创建
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(minioConstantProperties.getBucketName())
                            .build());
            if (!bucketExists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(minioConstantProperties.getBucketName())
                                .build());
            }

            //3.上传文件
            //3.1 构建文件对象名称 形式：/日期/随机文件名称.后缀
            String originalFilename = multipartFile.getOriginalFilename();
            String suffix = "";
            if (StringUtils.hasText(originalFilename) && originalFilename.contains(".")) {
                suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String folder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String fileName = UUID.randomUUID().toString().replaceAll("-", "");
            String objectName = "/" + folder + "/" + fileName + suffix;

            //3.2 将文件上传到minio
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConstantProperties.getBucketName())
                            .object(objectName)
                            .contentType(contentType)
                            .stream(multipartFile.getInputStream(), multipartFile.getSize(), -1)
                            .build());

            //3.3 拼接上传文件在线路径地址
            return minioConstantProperties.getEndpointUrl()
                    + "/" + minioConstantProperties.getBucketName()
                    + objectName;
        } catch (GuiguException e) {
            throw e;
        } catch (Exception e) {
            throw new GuiguException(201, "文件上传失败：" + e.getMessage());
        }
    }
}
