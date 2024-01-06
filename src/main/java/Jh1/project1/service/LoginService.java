package Jh1.project1.service;

import Jh1.project1.domain.Member;
import Jh1.project1.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;

    // 로그인 로직
    public Member login(String loginId, String password) {

        // 1. MemoryMemberRepository로 넘겨 줄때 사용
     /*   Member member = memberRepository.findByLoginIdMemory(loginId)
                .filter(m -> m.getPassword().equals(password))
                .orElse(null);
*/
        // 2. H2MemberRepository로 넘겨 줄때 사용
        Member member = memberRepository.findByLoginIdH2(loginId);
        if (member != null && member.getPassword().equals(password)) {
            // 로그인 성공
        } else {
            member = null; // 로그인 실패 시 member를 null로 설정
        }
        return member;
    }
}
