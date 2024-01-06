package Jh1.project1.service;

import Jh1.project1.domain.Member;
import Jh1.project1.exception.MyDbException;
import Jh1.project1.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountTransferService {

    private final MemberRepository memberRepository;

    @Transactional
    public void accountTransfer(String fromId, String toId, int money) {
        bizLogic(fromId, toId, money);
    }

    private void bizLogic(String fromId, String toId, int money) {
        Member fromMember = memberRepository.findByLoginIdH2(fromId);
        Member toMember = memberRepository.findByLoginIdH2(toId);

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

