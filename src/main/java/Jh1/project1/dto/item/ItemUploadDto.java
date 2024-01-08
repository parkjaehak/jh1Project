package Jh1.project1.dto.item;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ItemUploadDto {

    private Long id;
    private String itemBrand;
    private MultipartFile attachFile; // 첨부파일 하나
    private List<MultipartFile> imageFiles; //이미지파일 여러 개
}