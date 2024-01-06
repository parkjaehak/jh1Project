package Jh1.project1.dto.member;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class JoinDto {


    // 숫자X
    @NotEmpty
    private String name;

    @NotEmpty
    // 똑같은 Id일 경우 다시 Id만들도록
    private String loginId;

    @NotEmpty
    private String password;

}
