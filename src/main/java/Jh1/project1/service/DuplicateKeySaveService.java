package Jh1.project1.service;

import Jh1.project1.domain.Member;
import Jh1.project1.exception.MyDbException;
import Jh1.project1.exception.MyDuplicateKeyException;
import Jh1.project1.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class DuplicateKeySaveService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member createMemberId(Member member) {

        String name = member.getName();
        String memberId = member.getLoginId();
        String password = member.getPassword();
        int money = member.getMoney();

        try {
            memberRepository.save(member);
            log.info("saveId={}", memberId);
            return member;
        }
        catch (DuplicateKeyException e) { // 어떤 스프링 예외가 잡히는지는 직접 알아야 한다.

            // 예외 확인
            log.info("resultEx", e);
            log.info("e.getClass()={}", e.getClass());

            log.info("키 중복, 복구 시도");
            String retryId = generateNewId(memberId);
            log.info("retryId={}", retryId);

            Member retryMember = new Member(retryId, money);
            retryMember.setName(name);
            retryMember.setPassword(password);

            memberRepository.save(retryMember);
            return retryMember;
        }
        catch (MyDbException e) {
            log.info("데이터 접근 계층 예외", e);
            throw e;
        }

    }
    private String generateNewId(String memberId) {
        return memberId + new Random().nextInt(10000);
    }
}
