package Jh1.project1.domain;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class Member {

    private Long id;
    private String name;

    private String loginId;
    private String password;

    private int money; // 해당 회원이 소지한 금액

    public Member() {
    }

    public Member(String loginId, int money) {
        this.loginId = loginId;
        this.money = money;
    }
}
