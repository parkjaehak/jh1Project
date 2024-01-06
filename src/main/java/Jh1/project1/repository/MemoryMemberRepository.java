package Jh1.project1.repository;

import Jh1.project1.domain.Member;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//@Repository
public class MemoryMemberRepository extends AbstractMemberRepository {

    private static Map<Long, Member> memory = new ConcurrentHashMap<>();
    private static long sequence = 0L;

    @Override
    public Member save(Member member) {
        member.setId(++sequence);
        memory.put(member.getId(), member);
        return member;
    }

    @Override
    public Member findById(Long id) {
        Member findMember = memory.get(id);
        return findMember;
    }

    @Override
    public Optional<Member> findByLoginIdMemory(String loginId) {
        return findAll().stream()
                .filter(m -> m.getLoginId().equals(loginId))
                .findFirst();
    }

/*
    @Override
    public Optional<Member> findByLoginId(String loginId) {
        // 모든 Member 객체를 가져온다.
        List<Member> allMembers = findAll();

        // 루프를 통해 loginId와 일치하는 Member를 찾는다.
        for (Member member : allMembers) {
            if (member.getLoginId().equals(loginId)) {
                // 일치하는 Member를 찾으면 Optional로 감싸서 반환한다.
                return Optional.of(member);
            }
        }

        // 루프를 모두 돌았는데도 일치하는 Member를 찾지 못했을 경우 빈 Optional을 반환한다.
        return Optional.empty();
    }
*/


    @Override
    public List<Member> findAll() {
        ArrayList<Member> members = new ArrayList<>(memory.values());
        return members;
    }

    @Override
    public void clearStore() {
        memory.clear();
    }
}
