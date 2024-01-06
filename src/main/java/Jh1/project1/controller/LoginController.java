package Jh1.project1.controller;

import Jh1.project1.SessionConst;
import Jh1.project1.domain.Member;
import Jh1.project1.dto.member.LoginDto;
import Jh1.project1.service.LoginService;
import Jh1.project1.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final SessionUtil sessionManager; // 직접 만든 Session manager

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("member") Member member) {
        return "member/loginForm";
    }

    @PostMapping("/login")
    public String login(@Validated @ModelAttribute("member") LoginDto loginDto, BindingResult bindingResult,
                        @RequestParam(defaultValue = "/") String redirectURL, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "member/loginForm";
        }
        Member loginMember = loginService.login(loginDto.getLoginId(), loginDto.getPassword());

        // memory 에서 null 올라올 경우
        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            log.info("errors={}", bindingResult);
            return "member/loginForm";
        }
        // h2 db에서 exception올라올 경우
     /*   try {

        }catch ()*/

        //세션이 있으면 있는 세션 반환, 없으면 세션 생성
        HttpSession session = request.getSession(true);
        //세션에 로그인 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        return "redirect:" + redirectURL;
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        //세션을 삭제
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return "redirect:/";
    }

}
