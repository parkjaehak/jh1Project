package Jh1.project1.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberDto {

    private String memberId; // loginId
    private String intro;
}
