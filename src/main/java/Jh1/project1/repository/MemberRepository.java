package Jh1.project1.repository;

import Jh1.project1.domain.Item;
import Jh1.project1.domain.Member;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    Member save(Member member);

    Member findById(Long id);

    //로그인 ID로 찾기
    Optional<Member> findByLoginId(String loginId);

    void update(String loginId, int money);

    void delete(String loginId);

    List<Member> findAll();

    void clearStore();
}
