package Jh1.project1.controller;

import Jh1.project1.domain.Member;
import Jh1.project1.dto.member.JoinDto;
import Jh1.project1.repository.MemberRepository;
import Jh1.project1.service.DuplicateKeySaveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    private final DuplicateKeySaveService duplicateKeySaveService;

    @GetMapping("/join")
    public String joinForm(@ModelAttribute("member") Member member) {
        log.info("member={}", member);
        return "member/joinForm";
    }

    @PostMapping("/join")
    public String join(@Validated @ModelAttribute("member") JoinDto joinDto, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "member/joinForm";
        }

        Member member = new Member();

        member.setName(joinDto.getName());
        member.setLoginId(joinDto.getLoginId());
        member.setPassword(joinDto.getPassword());
        member.setMoney(10000); //default 값

        Member createMember = duplicateKeySaveService.createMemberId(member);// save는 서비스 계층에서 실행

        // 키 중복 복구한 아이디인지 체크
        if (!(member.getLoginId().equals(createMember.getLoginId()))) {
            String retryLoginId = createMember.getLoginId(); //아이디가 중복일 경우 임의로 생성된 아이디를 고객이 알고 있어야함
            log.info("retryLoginId={}",retryLoginId);
            model.addAttribute("retryMember", createMember);
            return "member/retryView"; // 고객에게 retryLoginId를 보여주는 뷰
        }

        //memberRepository.save(member);
        return "redirect:/";
    }
}
