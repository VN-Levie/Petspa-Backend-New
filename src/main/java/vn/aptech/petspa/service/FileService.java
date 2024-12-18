package vn.aptech.petspa.service;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.UUID;

@Service
public class FileService {

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
    private final String defaultUploadDir = "src/main/resources/static/uploads/";

    // Kiểm tra kích thước file
    public boolean isImageSize(long size) {
        return size > 0 && size <= MAX_IMAGE_SIZE;
    }

    // Kiểm tra định dạng ảnh bằng extension và MIME type
    public boolean isImage(InputStream fileInputStream, String fileName) {
        try {
            if (!fileName.contains(".")) {
                return false;
            }
            // Kiểm tra extension trước
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            if (!extension.matches("jpg|jpeg|png|gif")) {
                return false;
            }

            // Kiểm tra MIME type (sử dụng Apache Tika)
            Tika tika = new Tika();
            String mimeType = tika.detect(fileInputStream);
            return mimeType.matches("image/jpeg|image/png|image/gif");
        } catch (IOException e) {
            return false; // Nếu không thể kiểm tra, không chấp nhận file
        }
    }

    public String uploadFile(MultipartFile file, String directory) {
        try {
            // Kiểm tra file hợp lệ
            validateFile(file);

            // Làm sạch tên file
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null || originalFileName.isBlank()) {
                throw new IllegalArgumentException("File name is invalid.");
            }
            String cleanFileName = convertFileName(originalFileName);

            // Xử lý đường dẫn lưu trữ
            String safeDirectory = sanitizeDirectory(directory);
            String uploadDir = defaultUploadDir + (safeDirectory != null ? safeDirectory + "/" : "");
            Path uploadPath = Paths.get(uploadDir);

            // Tạo thư mục nếu chưa tồn tại
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Tạo tên file an toàn và kiểm tra trùng lặp
            String fileName = System.currentTimeMillis() + "-" + cleanFileName;
            Path filePath = uploadPath.resolve(fileName);

            // Nếu file đã tồn tại, thêm hậu tố ngẫu nhiên
            while (Files.exists(filePath)) {
                fileName = UUID.randomUUID().toString() + "-" + cleanFileName;
                filePath = uploadPath.resolve(fileName);
            }

            // Lưu file
            Files.copy(file.getInputStream(), filePath);

            // Tạo URL
            return "/uploads/" + (safeDirectory != null ? safeDirectory + "/" : "") + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Error saving file", e);
        }
    }

    public String uploadFile(MultipartFile file) {
        return uploadFile(file, null);
    }

    public String convertFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return fileName;
        }

        String name = fileName.substring(0, fileName.lastIndexOf("."));
        String extension = fileName.substring(fileName.lastIndexOf("."));
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);
        String withoutAccent = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        String replaced = withoutAccent.replaceAll("[^a-zA-Z0-9]+", "-");
        String singleDash = replaced.replaceAll("-{2,}", "-");
        return singleDash.replaceAll("^-|-$", "").toLowerCase() + extension;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null.");
        }
        if (!isImageSize(file.getSize())) {
            throw new IllegalArgumentException("File size exceeds the maximum limit.");
        }
        try (InputStream inputStream = file.getInputStream()) {
            if (!isImage(inputStream, file.getOriginalFilename())) {
                throw new IllegalArgumentException("File is not a valid image.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading file for validation.", e);
        }
    }

    private String sanitizeDirectory(String directory) {
        if (directory == null || directory.isBlank()) {
            return null;
        }
        // Loại bỏ các ký tự không hợp lệ trong đường dẫn
        return directory.replaceAll("[^a-zA-Z0-9/_-]", "").replaceAll("/{2,}", "/");
    }
}
