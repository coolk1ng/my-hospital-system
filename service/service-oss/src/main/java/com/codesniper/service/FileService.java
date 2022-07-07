package com.codesniper.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件操作Service
 *
 * @author CodeSniper
 * @since 2022/7/7 13:02
 */
public interface FileService {
    /** 
     * 文件上传
     * @param file 
     * @return String
     */
    String fileUpload(MultipartFile file);
}
