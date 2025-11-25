package com.errand.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Component
public class FileUploadUtil {
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/jpg",
            "image/png"
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * 验证文件是否符合要求
     */
    public static String validateFile(MultipartFile file, String fileType) {
        if (file == null || file.isEmpty()) {
            return fileType + "文件不能为空";
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            return fileType + "大小不能超过5MB";
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            return fileType + "格式不支持，仅支持JPG、JPEG、PNG格式";
        }

        // 检查文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            if (!Arrays.asList("jpg", "jpeg", "png").contains(extension)) {
                return fileType + "扩展名不支持，仅支持jpg、jpeg、png格式";
            }
        }

        return null; // 验证通过
    }

    /**
     * 获取文件扩展名
     */
    public static String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 检查是否是图片文件
     */
    public static boolean isImageFile(MultipartFile file) {
        if (file == null) return false;

        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
}
