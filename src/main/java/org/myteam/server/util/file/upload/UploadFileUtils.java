package org.myteam.server.util.file.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UploadFileUtils {
    private static final Logger logger = LoggerFactory.getLogger(UploadFileUtils.class);
    public static final String UPLOAD_PATH = "/opt/movie"; // 업로드 경로
    //    public static final String UPLOAD_PATH = new File("src/main/resources/static").getAbsolutePath(); // 업로드 경로


    public static String uploadImage(String uploadPath, String detPath, String fileUrl) throws MalformedURLException {
        String imagesFolder = Paths.get(uploadPath, detPath).toString();

        makeDir(imagesFolder); // 폴더 생성

        logger.info("upload() called uploadPath: {}", uploadPath);
        logger.info("upload() called imagesFolder: {}", imagesFolder);

        // URL로부터 이미지 다운로드
        URL url = new URL(fileUrl);
        try (InputStream in = url.openStream()) {
            // 이미지 파일 이름 및 경로 설정
            String originalFilename = Paths.get(url.getPath()).getFileName().toString();
            String filePath = Paths.get(imagesFolder, originalFilename).toString();

            File file = new File(filePath);

            boolean exists = file.exists();
            if (exists) {
                file.delete();
            }

            Path path = Paths.get(filePath);

            // 파일 저장
            Files.copy(in, path);

            logger.info("업로드 및 저장 완료: {}", filePath);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("파일 업로드에 실패했습니다. : {}", e.getMessage());
            return "{ \"success\": false }";
        }
        return "{ \"success\": true }";
    }

    // 폴더 생성기
    private static void makeDir(String...paths) {
        // 폴더가 이미 존재한다면 함수 종료
        if (new File(paths[paths.length - 1]).exists()) {
            return;
        }
        for (String path : paths) {;
            File dirPath = new File(path);
            if (!dirPath.exists()) {
                dirPath.mkdir(); // 디렉토리 생성
            }
        }
    }
}
