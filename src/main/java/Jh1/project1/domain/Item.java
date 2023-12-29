package Jh1.project1.domain;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Data
public class Item {

    private Long id;
    private String itemName;
    private Integer price;
    private Integer quantity;

    private Boolean open; //판매여부
    private List<String> regions; //등록지역
    private ItemType itemType; //종류
    private String deliveryCode; //배송방식

    private UploadFile attachFile; // 첨부파일 하나
    private List<UploadFile> imageFiles; // 이미지파일 여러개
    private String itemBrand;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
