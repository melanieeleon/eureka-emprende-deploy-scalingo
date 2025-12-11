package com.example.eureka.shared.storage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class S3Helper {
	
	private final AmazonS3 s3Client;
	
	/**
	 * Generar URL Presigned
	 *
	 * @param bucketName
	 * @param keyObject
	 * @param contentDisposition
	 * @param contentType
	 * @param expiration
	 * @return
	 */
	public URL generatePresignedUrl(String bucketName, String keyObject, String contentDisposition, String contentType, Date expiration) {
		ResponseHeaderOverrides rho = new ResponseHeaderOverrides();
		rho.setContentDisposition(contentDisposition);
		rho.setContentType(contentType);
		
		GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, keyObject)
				.withResponseHeaders(rho)
				.withExpiration(expiration);
		
		return s3Client.generatePresignedUrl(request);
	}
	/**
	 * Genera URL Prefirmada para subir archivo al Bucket
	 * 
	 * @param bucketName
	 * @param keyObject
	 * @param expiration
	 * @param contentType
	 * @param contentLength
	 * @param metadata
	 * @return
	 */
	public URL generatePresignedPutUrl(String bucketName, String keyObject, Date expiration, String contentType, long contentLength, Map<String, String> metadata) {
		
     // Crear la solicitud para generar la URL prefirmada
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, keyObject);
        
        generatePresignedUrlRequest.setMethod(com.amazonaws.HttpMethod.PUT);
        generatePresignedUrlRequest.setExpiration(expiration);
        if (StringUtils.hasText(contentType))
        	generatePresignedUrlRequest.setContentType(contentType);
        
        generatePresignedUrlRequest.putCustomRequestHeader("Content-Length", String.valueOf(contentLength));
        
        if (metadata != null) {
        	for (Map.Entry<String, String> meta : metadata.entrySet()) {
        		generatePresignedUrlRequest.addRequestParameter("x-amz-meta-"+ meta.getKey() , meta.getValue());
			}
        }
        
        // Generar la URL prefirmada
        return s3Client.generatePresignedUrl(generatePresignedUrlRequest);
	}
	
	public void uploadFile(String bucketName, String keyObject, File file) {
		s3Client.putObject(bucketName, keyObject, file);
	}
	
	/**
	 * Descarga archivo de un Bucket
	 * @param bucketName
	 * @param fileNameInBucket
	 * @param downloadPath
	 * @return
	 * @throws IOException
	 */
	public File downloadFile(String bucketName, String fileNameInBucket, String downloadPath) throws IOException {
    	File fArchivo = null;
    	fArchivo = new File(downloadPath);
        try (S3Object o = s3Client.getObject(bucketName, fileNameInBucket);
             S3ObjectInputStream s3is = o.getObjectContent();
             FileOutputStream fos = new FileOutputStream(fArchivo);
        	) {
            byte[] read_buf = new byte[1024];
            int read_len = 0;
            while ((read_len = s3is.read(read_buf)) > 0) {
                fos.write(read_buf, 0, read_len);
            }
        }
        return fArchivo;
    }
	
	/**
	 * Elimina un archivo de un Bucket
	 * 
	 * @param bucketName
	 * @param keyObject
	 */
	public void deleteObjectFromBucket(String bucketName, String keyObject) {
		
		// Crear la solicitud de eliminación de objeto
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, keyObject);

        // Eliminar el objeto
        s3Client.deleteObject(deleteObjectRequest);
	}
	
	/**
	 * Obtiene content type en base al nombre del archivo
	 * @param filename
	 * @return
	 */
	public String getContentType(String filename) {
        try {
            Path path = Paths.get(filename);
            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                return "application/octet-stream";
            }
            return contentType;
        } catch (IOException e) {
            throw new RuntimeException("Error determining content type for file: " + filename, e);
        }
    }
	
}
