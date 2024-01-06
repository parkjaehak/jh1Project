package Jh1.project1.service;

import Jh1.project1.domain.Member;
import Jh1.project1.repository.H2MemberRepository;
import Jh1.project1.repository.MemberRepository;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;


import javax.sql.DataSource;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
public class MemberServiceTest {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    AccountTransferService accountTransferService;
    @Autowired
    DuplicateKeySaveService duplicateKeySaveService;

    @AfterEach
    void after(){
        log.info("끝");
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }


    @TestConfiguration
    static class TestConfig {
        // DataSourceTransactionManager : 트랜잭션 매니저를 스프링 빈으로 등록한다.
        // 스프링이 제공하는 트랜잭션 AOP는 스프링 빈에 등록된 트랜잭션 매니저를 찾아서 사용하기 때문에 트랜잭션 매니저를 스프링 빈으로 등록해두어야 한다.
        private final DataSource dataSource;
        public TestConfig(DataSource dataSource) {
            this.dataSource = dataSource;
        }
        @Bean
        MemberRepository memberRepository() {
            MemberRepository memberRepository = new H2MemberRepository(dataSource);
            return memberRepository;
        }
        @Bean
        AccountTransferService accountTransferService() {
            AccountTransferService accountTransferService = new AccountTransferService(memberRepository());
            return accountTransferService;
        }
        @Bean
        DuplicateKeySaveService duplicateKeySaveService() {
            DuplicateKeySaveService duplicateKeySaveService = new DuplicateKeySaveService(memberRepository());
            return duplicateKeySaveService;
        }
    }

    @Test
    void AopCheck() {
        log.info("memberService class={}", accountTransferService.getClass());
        log.info("memberRepository class={}", memberRepository.getClass());
        assertThat(AopUtils.isAopProxy(accountTransferService)).isTrue();
        assertThat(AopUtils.isAopProxy(memberRepository)).isFalse();
    }

    @Test
    @DisplayName("중복 키 예외 해결")
    void DuplicateKeySave() {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberDuplicateA = new Member(MEMBER_A, 10000);

        //when
        Member initMember = duplicateKeySaveService.createMemberId(memberA);
        Member retryMember = duplicateKeySaveService.createMemberId(memberDuplicateA);

        //then
        log.info("retryMember.getLoginId()={}", retryMember.getLoginId());
        assertThat(memberDuplicateA.getLoginId()).isNotEqualTo(retryMember.getLoginId());
    }
    //@Test
    @DisplayName("정상 이체")
    void accountTransfer() {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);
        //when
        accountTransferService.accountTransfer(memberA.getLoginId(), memberB.getLoginId(), 2000);
        //then
        Member findMemberA = memberRepository.findByLoginIdH2(memberA.getLoginId());
        Member findMemberB= memberRepository.findByLoginIdH2(memberB.getLoginId());

        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }
    //@Test
    @DisplayName("이체중 예외 발생")
    void accountTransferEx(){
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberEx = new Member(MEMBER_EX, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberEx);
        //when
        assertThatThrownBy(() -> accountTransferService.accountTransfer(memberA.getLoginId(), memberEx.getLoginId(), 2000))
                .isInstanceOf(IllegalStateException.class);
        //then
        Member findMemberA = memberRepository.findByLoginIdH2(memberA.getLoginId());
        Member findMemberEx = memberRepository.findByLoginIdH2(memberEx.getLoginId());

        //memberA의 돈이 롤백 되어야함
        assertThat(findMemberA.getMoney()).isEqualTo(10000);
        assertThat(findMemberEx.getMoney()).isEqualTo(10000);
    }
}
