package Jh1.project1.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

/**
 * Exception`을 Resolve(해결)하는 것이 목적 --------- ex) Exception을 sendError로 변환 후
 *
 * 1. 수동 등록 : ExceptionCustomizer에서 등록
 * 2. 자동 등록 : spring boot가 ErrorPage를 등록
 */
@Slf4j
public class IllegalArgumentHandlerExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        try {
            if (ex instanceof IllegalArgumentException) {
                log.info("IllegalArgumentException resolver to 400");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
                return new ModelAndView();
            }
        }
        catch (IOException e) {
            log.error("resolver ex", e);
        }
        return null;
    }
}