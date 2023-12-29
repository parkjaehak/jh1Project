package Jh1.project1.domain;

import lombok.Data;

/**
 * 멤버 객체를 달리 둔 이유
 *
 * 고객이 업로드한 파일명으로 서버 내부에 파일을 저장하면 안됨.
 * 서로 다른 고객이 같은 파일이름을 업로드 하는 경우 기존 파일 이름과 충돌이 날 수 있기 때문.
 * 서버에서는 저장할 파일명이 겹치지 않도록 내부에서 관리하는 별도의 파일명이 필요
 */
@Data
public class UploadFile {

    private String uploadFileName; //사용자가 업로드한 파일 이름(최초 이름)
    private String storeFileName;  //저장한 파일 이름(서버에서 사용하는 이름)

    public UploadFile(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }
}
