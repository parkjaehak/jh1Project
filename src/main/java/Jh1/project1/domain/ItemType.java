package Jh1.project1.domain;

import lombok.Getter;

@Getter
public enum ItemType {

    BOOK("도서"), FOOD("식품"), ETC("기타");

    private final String description;

    ItemType(String description) {
        this.description = description;
    }
}
