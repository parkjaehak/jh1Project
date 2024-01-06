package Jh1.project1.repository;

import Jh1.project1.domain.Member;

import java.util.List;
import java.util.Optional;

public abstract class AbstractMemberRepository implements MemberRepository {
    @Override
    public Member save(Member member) {
        return null;
    }

    @Override
    public Member findById(Long id) {
        return null;
    }

    @Override
    public Member findByLoginIdH2(String loginId) {
        return null;
    }
    @Override
    public Optional<Member> findByLoginIdMemory(String loginId) {
        return Optional.empty();
    }

    @Override
    public void update(String loginId, int money) {

    }

    @Override
    public void delete(String loginId) {

    }

    @Override
    public List<Member> findAll() {
        return null;
    }

    @Override
    public void clearStore() {

    }
}
