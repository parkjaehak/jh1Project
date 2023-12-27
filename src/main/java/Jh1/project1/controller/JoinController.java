package Jh1.project1.controller;

import Jh1.project1.domain.Member;
import Jh1.project1.dto.member.JoinDto;
import Jh1.project1.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class JoinController {

    private final MemberRepository memberRepository;

    @GetMapping("/join")
    public String joinForm(@ModelAttribute("member") Member member) {
        log.info("member={}", member);
        return "member/joinForm";
    }

    @PostMapping("/join")
    public String join(@Validated @ModelAttribute("member") JoinDto joinDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "member/joinForm";
        }

        Member member = new Member();

        member.setName(joinDto.getName());
        member.setLoginId(joinDto.getLoginId());
        member.setPassword(joinDto.getPassword());

        Member savedMember = memberRepository.save(member);


        return "redirect:/";
    }
}
