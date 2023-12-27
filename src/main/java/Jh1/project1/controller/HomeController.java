package Jh1.project1.controller;

import Jh1.project1.SessionConst;
import Jh1.project1.argumentResolver.Login;
import Jh1.project1.domain.Member;
import Jh1.project1.repository.MemberRepository;
import Jh1.project1.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final SessionUtil sessionManager;

    /**
     * @Login 애노테이션 있으면 직접만든 ArgumentResolver가 동작
     * 자동으로 세션에 있는 로그인 member객체 찾고 새션이 없으면 null 반환
     */

    @GetMapping("/")
    public String homeLogin(@Login Member loginMember, Model model) {

        //세션에 회원 데이터가 없으면 home
        if (loginMember == null) {
            return "home";
        }
        //세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);

        return "loginHome";
    }
}
