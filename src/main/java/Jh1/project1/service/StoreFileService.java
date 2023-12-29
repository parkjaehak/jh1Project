package Jh1.project1.service;

import Jh1.project1.domain.UploadFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class StoreFileService {

    @Value("${file.dir}")
    private String fileDir;

    public String getFullPath(String filename) {
        return fileDir + filename;
    }

    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {

        List<UploadFile> storeFileResult = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                storeFileResult.add(storeFile(multipartFile));
            }
        }
        return storeFileResult;
    }

    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {

        if (multipartFile.isEmpty()) {
            return null;
        }

        // a.png -> 51041c62-86e4-4274-801d-614a7d994edb.png
        String originalFilename = multipartFile.getOriginalFilename(); //a.png
        String storeFileName = createStoreFileName(originalFilename); //51041c62-86e4-4274-801d-614a7d994edb.png
        multipartFile.transferTo(new File(getFullPath(storeFileName)));

        return new UploadFile(originalFilename, storeFileName);
    }

    private static String createStoreFileName(String originalFilename) {
        String extExt = extractExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();
        String storeFileName = uuid + "." + extExt;
        return storeFileName;
    }

    private static String extractExtension(String originalFilename) {
        int position = originalFilename.lastIndexOf(".");
        // extExt(extractExtension) : png
        String extExt = originalFilename.substring(position + 1);
        return extExt;
    }
}

