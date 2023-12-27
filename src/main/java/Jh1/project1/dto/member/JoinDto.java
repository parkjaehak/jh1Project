package Jh1.project1.dto.member;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class JoinDto {


    // 숫자X
    @NotEmpty
    private String name;

    @NotEmpty
    private String loginId;

    @NotEmpty
    private String password;

}
