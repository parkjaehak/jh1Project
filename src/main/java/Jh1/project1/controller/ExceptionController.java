package Jh1.project1.controller;

import Jh1.project1.exception.ViewException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Slf4j
@Controller
public class ExceptionController {

    //RequestDispatcher 상수로 정의되어 있음
    public static final String ERROR_EXCEPTION = "javax.servlet.error.exception";
    public static final String ERROR_EXCEPTION_TYPE = "javax.servlet.error.exception_type";
    public static final String ERROR_MESSAGE = "javax.servlet.error.message";
    public static final String ERROR_REQUEST_URI = "javax.servlet.error.request_uri";
    public static final String ERROR_SERVLET_NAME = "javax.servlet.error.servlet_name";
    public static final String ERROR_STATUS_CODE = "javax.servlet.error.status_code";



    /**---------------------------------------------------
     * 에러 발생시키는 코드
     * 예외 처리 방식
     *      1. Exception(예외)
     *      2. response.sendError(HTTP 상태 코드, 오류 메시지)
     */

    @GetMapping("/error-ex")
    public void errorEx() {
        throw new RuntimeException("예외 발생!");
    }

    @GetMapping("/error-404")
    public void error404(HttpServletResponse response) throws IOException {
        response.sendError(404, "404 오류!");
    }
    @GetMapping("/error-500")
    public void error500(HttpServletResponse response) throws IOException {
        response.sendError(500);
    }

    // RuntimeException은 unchecked 예외이기에 매소드에 throw가 안붙는다
    @GetMapping("/error-view")
    public void errorView() {
        throw new ViewException("view 사용한 오류화면 응답");
    }







    /**--------------------------------------------------------
     *에러 페이지 코드
     * (내가 직접 만든 ExceptionCustomizer(=BasicErrorController)를 사용할때만 사용
     */
    @RequestMapping("/error-page/404")
    public String errorPage404(HttpServletRequest request, HttpServletResponse response) {
        log.info("errorPage 404");
        return "error-page/404";
    }

    @RequestMapping("/error-page/500")
    public String errorPage500(HttpServletRequest request, HttpServletResponse response) {
        log.info("errorPage 500");
        return "error-page/500";
    }



    /**
     * --------------------------------------------------
     * 오류정보
     *  public enum DispatcherType {
     *     FORWARD, :  서블릿에서 다른 서블릿이나 JSP를 호출할 때
     *     INCLUDE, : 서블릿에서 다른 서블릿이나 JSP의 결과를 포함할 때
     *     REQUEST, : 클라이언트 요청
     *     ASYNC, :서블릿 비동기 호출
     *     ERROR :오류 요청
     *  }
     */
    private void printErrorInfo(HttpServletRequest request) {
        log.info("ERROR_EXCEPTION: ex=", request.getAttribute(ERROR_EXCEPTION));
        log.info("ERROR_EXCEPTION_TYPE: {}", request.getAttribute(ERROR_EXCEPTION_TYPE));
        log.info("ERROR_MESSAGE: {}", request.getAttribute(ERROR_MESSAGE)); //ex의 경우 NestedServletException 스프링이 한번 감싸서 반환
        log.info("ERROR_REQUEST_URI: {}", request.getAttribute(ERROR_REQUEST_URI));
        log.info("ERROR_SERVLET_NAME: {}", request.getAttribute(ERROR_SERVLET_NAME));
        log.info("ERROR_STATUS_CODE: {}", request.getAttribute(ERROR_STATUS_CODE));

        log.info("dispatchType={}", request.getDispatcherType());
    }




}
