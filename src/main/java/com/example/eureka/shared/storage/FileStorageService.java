package com.example.eureka.shared.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final S3Helper s3Helper;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.region:us-east-2}")
    private String region;

    // Límites configurables
    @Value("${upload.max-file-size-mb:500}") // 500MB por defecto
    private long maxFileSizeMB;

    /**
     * Subir archivo y obtener URL pública permanente
     */
    public String uploadFile(MultipartFile file) throws IOException {
        // Validar tamaño
        long maxSizeBytes = maxFileSizeMB * 1024 * 1024;
        if (file.getSize() > maxSizeBytes) {
            throw new IllegalArgumentException(
                    "Archivo muy grande. Máximo permitido: " + maxFileSizeMB + "MB. " +
                            "Tamaño del archivo: " + (file.getSize() / 1024 / 1024) + "MB"
            );
        }

        // Validar tipo de archivo
        String contentType = file.getContentType();
        if (!isValidFileType(contentType)) {
            throw new IllegalArgumentException(
                    "Tipo de archivo no permitido. Solo se aceptan imágenes, videos y PDFs. " +
                            "Tipo recibido: " + contentType
            );
        }

        // Validar tamaño específico según tipo
        validateFileSizeByType(file);

        // Generar nombre único para el archivo
        String fileName = generateFileName(file.getOriginalFilename());

        // Convertir MultipartFile a File
        File tempFile = convertMultipartToFile(file);

        try {
            // Subir archivo a S3
            s3Helper.uploadFile(bucketName, fileName, tempFile);

            // Retornar URL pública permanente
            return getPermanentUrl(fileName);
        } finally {
            // Eliminar archivo temporal (siempre, incluso si hay error)
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    /**
     * Obtener URL pública permanente (sin expiración)
     * Requiere que el bucket tenga acceso público configurado
     */
    public String getPermanentUrl(String fileName) {
        // URL pública estándar de S3 (permanente)
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName, region, fileName);
    }

    /**
     * Obtener URL prefirmada (para archivos privados)
     */
    public String getPresignedUrl(String fileName, int expirationDays) {
        Date expiration = new Date(System.currentTimeMillis() + expirationDays * 24 * 60 * 60 * 1000L);
        URL url = s3Helper.generatePresignedUrl(
                bucketName,
                fileName,
                "inline",
                s3Helper.getContentType(fileName),
                expiration
        );
        return url.toString();
    }

    /**
     * Eliminar archivo
     */
    public void deleteFile(String fileName) {
        s3Helper.deleteObjectFromBucket(bucketName, fileName);
    }

    /**
     * Validar tipo de archivo
     */
    private boolean isValidFileType(String contentType) {
        if (contentType == null) return false;

        return contentType.startsWith("image/") ||
                contentType.startsWith("video/") ||
                contentType.equals("application/pdf");
    }

    /**
     * Validar tamaño según tipo de archivo
     */
    private void validateFileSizeByType(MultipartFile file) {
        String contentType = file.getContentType();
        long fileSizeMB = file.getSize() / 1024 / 1024;

        if (contentType != null && contentType.startsWith("image/")) {
            // Imágenes: máximo 10MB
            if (file.getSize() > 10 * 1024 * 1024) {
                throw new IllegalArgumentException(
                        "Imagen muy grande. Máximo 10MB. Tamaño actual: " + fileSizeMB + "MB"
                );
            }
        } else if (contentType != null && contentType.startsWith("video/")) {
            // Videos: máximo definido en configuración
            if (file.getSize() > maxFileSizeMB * 1024 * 1024) {
                throw new IllegalArgumentException(
                        "Video muy grande. Máximo " + maxFileSizeMB + "MB. Tamaño actual: " + fileSizeMB + "MB"
                );
            }
        }
    }

    /**
     * Generar nombre único para el archivo
     */
    private String generateFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        // Sanitizar el nombre del archivo
        extension = extension.toLowerCase().replaceAll("[^a-z0-9.]", "");
        return UUID.randomUUID().toString() + extension;
    }

    /**
     * Convertir MultipartFile a File
     */
    private File convertMultipartToFile(MultipartFile multipartFile) throws IOException {
        // Usar nombre único para evitar conflictos
        String uniqueFilename = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
        File file = new File(System.getProperty("java.io.tmpdir") + "/" + uniqueFilename);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());
        }

        return file;
    }

    /**
     * Obtener información del archivo
     */
    public FileInfo getFileInfo(MultipartFile file) {
        return FileInfo.builder()
                .originalName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .size(file.getSize())
                .sizeMB(file.getSize() / 1024.0 / 1024.0)
                .build();
    }

    /**
     * Clase para información del archivo
     */
    @lombok.Builder
    @lombok.Data
    public static class FileInfo {
        private String originalName;
        private String contentType;
        private long size;
        private double sizeMB;
    }
}