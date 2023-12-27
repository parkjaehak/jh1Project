package Jh1.project1.domain;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class Member {

    private Long id;
    private String name;
    private String loginId;
    private String password;

}
