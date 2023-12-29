package Jh1.project1.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 상품 업로드만 하는 controller -> 상품 업로드 및 저장은 ItemController에서
 */
@Slf4j
@Controller
@RequestMapping("/upload")
public class UploadController {

    @Value("${file.dir}")
    private String fileDir;

    @GetMapping
    public String newFile() {
        return "item/uploadForm";
    }

    @PostMapping
    public String saveFile(@RequestParam String itemName,
                           @RequestParam MultipartFile file,HttpServletRequest request) throws IOException {

        log.info("request={}", request);
        log.info("itemName={}", itemName);
        log.info("multipartFile={}", file);

        if (!file.isEmpty()) {
            String fullPath = fileDir + file.getOriginalFilename();
            log.info("파일 저장 fullPath={}", fullPath);
            file.transferTo(new File(fullPath));
        }
        return "item/uploadForm";
    }
}