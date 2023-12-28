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