package Jh1.project1.controller;

import Jh1.project1.dto.error.ErrorDto;
import Jh1.project1.dto.member.MemberDto;

import Jh1.project1.exception.BadRequestException;
import Jh1.project1.exception.UserException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

/**
 * Exception Resolver를 사용해 WAS까지 전달할 필요없이 예외를 dispatcher servlet에 위임하는 이유
 *
 * """
 * 예외가 발생해서 서블릿을 넘어 WAS까지 예외가 전달되면 HTTP 상태코드가 500으로 처리된다.
 * 발생하는 예외에 따라서 400, 404 등등 다른 상태코드로 처리하고 싶다.
 * 오류 메시지, 형식등을 API마다 다르게 처리하고 싶다.
 * """
 */
@Slf4j
@RestController
public class ApiExceptionController {


    /**---------------------------------------------------
     * 에러 발생시키는 코드
     * 예외 처리 방식
     *      1. Exception(예외)
     *      2. response.sendError(HTTP 상태 코드, 오류 메시지)
     */
    @GetMapping("/api/members/{id}")
    public MemberDto getMember(@PathVariable("id") String id) {

        if (id.equals("ex")) {
            throw new RuntimeException("잘못된 사용자");
        }
        if (id.equals("bad")) {
            throw new IllegalArgumentException("잘못된 입력 값");
            //throw new BadRequestException();
            //throw new ResponseStatusException(HttpStatus.NOT_FOUND, "error.bad", new IllegalArgumentException());
        }
        if (id.equals("user-ex")) {
            throw new UserException("사용자 오류");
        }
        return new MemberDto(id, "hello " + id);
    }





    /**--------------------------------------------------------
     *에러 페이지 코드 : application/json
     * (내가 직접 만든 ExceptionCustomizer(=BasicErrorController)를 사용할때만 사용
     * 스프링이 BasicErrorController를 통해 알아서 실행시켜줌
     */
    @RequestMapping(value = "/error-page/500", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> errorPage500Api(HttpServletRequest request) {

        log.info("API errorPage 500");
        Map<String, Object> result = new HashMap<>();

        Exception ex = (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object sc = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        result.put("status", sc);
        result.put("message", ex.getMessage());

        Integer statusCodeInteger = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        ResponseEntity responseEntity = new ResponseEntity(result, HttpStatus.valueOf(statusCodeInteger));

        return responseEntity;
    }

}
