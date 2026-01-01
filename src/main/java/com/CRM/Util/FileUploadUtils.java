package com.CRM.Util;

import org.springframework.web.multipart.MultipartFile;

import com.CRM.Exception.FileUploadException;

import lombok.experimental.UtilityClass;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class FileUploadUtils {
    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    public static final String MEDIA_PATTERN = "^.*\\.(?i)(jpg|jpeg|png|gif|bmp|mp4|avi)$";
    public static final String DATE_FORMAT = "yyyyMMddHHmmss";
    public static final String FILE_NAME_FORMAT = "%s_%s";

    public static boolean isAllowedExtension(final String fileName, final String pattern) {
        final Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(fileName);
        return matcher.matches();
    }

    public static void assertAllowed(MultipartFile file, String pattern) throws FileUploadException {
        final long size = file.getSize();
        if (size > MAX_FILE_SIZE) {
            throw new FileUploadException("File size is too large");
        }

        final String fileName = file.getOriginalFilename();
        if (!isAllowedExtension(fileName, pattern)) {
            throw new FileUploadException("Only jpg, png, gif, bmp, mp4, avi files are allowed");
        }
    }

    public static String generateFileName(String name) {
        // Làm sạch tên file trước khi ghép với ngày tháng để tránh lỗi hệ thống sau này
        String cleanName = name.replaceAll("\\s+", "_").replaceAll("[^a-zA-Z0-9._-]", "");
        final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        final String date = dateFormat.format(new Date());
        return String.format(FILE_NAME_FORMAT, date, cleanName);
    }

    public static String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    public static String getResourceTypeFromExtension(String extension) {
        String ext = extension.toLowerCase();
        List<String> videoExtensions = Arrays.asList("mp4", "avi");

        if (videoExtensions.contains(ext)) {
            return "video";
        }
        return "image";
    }
}