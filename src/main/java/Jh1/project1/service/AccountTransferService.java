package Jh1.project1.service;

import Jh1.project1.domain.Member;
import Jh1.project1.exception.MyDbException;
import Jh1.project1.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@Slf4j
@Service
public class AccountTransferService {

    private final TransactionTemplate txTemplate;
    private final MemberRepository memberRepository;

    public AccountTransferService(PlatformTransactionManager transactionManager, MemberRepository memberRepository) {
        this.txTemplate = new TransactionTemplate(transactionManager);
        this.memberRepository = memberRepository;
    }

    public void accountTransfer(String fromId, String toId, int money)  {

        // 트랜잭션 템플릿 덕분에 트랜잭션을 시작하고, 커밋하거나 롤백하는 코드가 모두 제거되었다
        txTemplate.executeWithoutResult((status) -> {
            try {
                bizLogic(fromId, toId, money);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private void bizLogic(String fromId, String toId, int money) {
        Optional<Member> memberFrom = memberRepository.findByLoginId(fromId);
        Optional<Member> memberTo = memberRepository.findByLoginId(toId);
        // Optional 값 추출
        Member fromMember = memberFrom.orElse(null);
        Member toMember = memberTo.orElse(null);
        // defensive coding 필요
        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember); // 예외발생로직
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    private void validation(Member toMember) {
        if (toMember.getLoginId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}

