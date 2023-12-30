package Jh1.project1.service;

import Jh1.project1.domain.Member;
import Jh1.project1.repository.H2MemberRepository;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.SQLException;
import java.util.Optional;

import static Jh1.project1.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MemberServiceTest {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    private H2MemberRepository memberRepository;
    private AccountTransferService accountTransferService;

    @BeforeEach
    void before() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        PlatformTransactionManager transactionManager = new JdbcTransactionManager(dataSource);

        memberRepository = new H2MemberRepository(dataSource);
        accountTransferService = new AccountTransferService(transactionManager,memberRepository);
    }

    @AfterEach
    void after() throws SQLException {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);
        //when
        accountTransferService.accountTransfer(memberA.getLoginId(), memberB.getLoginId(), 2000);
        //then
        Optional<Member> findOptionalA = memberRepository.findByLoginId(memberA.getLoginId());
        Optional<Member> findOptionalB = memberRepository.findByLoginId(memberB.getLoginId());
        Member findMemberA = findOptionalA.orElse(null);
        Member findMemberB = findOptionalB.orElse(null);

        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }
    @Test
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
        Optional<Member> findOptionalA = memberRepository.findByLoginId(memberA.getLoginId());
        Optional<Member> findOptionalEx = memberRepository.findByLoginId(memberEx.getLoginId());
        Member findMemberA = findOptionalA.orElse(null);
        Member findMemberEx = findOptionalEx.orElse(null);

        //memberA의 돈만 2000원 줄었고, ex의 돈은 10000원 그대로이다.
        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberEx.getMoney()).isEqualTo(10000);
    }
}
