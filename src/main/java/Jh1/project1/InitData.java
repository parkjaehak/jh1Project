package Jh1.project1;

import Jh1.project1.domain.Item;
import Jh1.project1.domain.Member;
import Jh1.project1.repository.ItemRepository;
import Jh1.project1.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitData {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;

    @PostConstruct
    public void init() {

        itemRepository.save(new Item("Batman", 10000, 10));
        itemRepository.save(new Item("Joker", 20000, 20));

        Member member = new Member();
        member.setLoginId("atom8426");
        member.setPassword("0000");
        member.setName("jaehak");
        memberRepository.save(member);
    }
}
