package Jh1.project1.handler;

import Jh1.project1.dto.error.ErrorDto;
import Jh1.project1.exception.UserException;
import Jh1.project1.exception.ViewException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @ExceptionHandler 예외 처리 방법
     *
     * 애노테이션을 선언, 해당 컨트롤러에서 처리하고 싶은 예외를 지정 -> @ControllerAdvice를 사용하면 정상,예외 코드를 분리 가능
     *
     * 1.  @ExceptionHandler에 지정한 부모 클래스는 자식 클래스까지 처리할 수 있다.
     * 2. `@ExceptionHandler에 예외를 생략할 수 있다. 생략하면 메서드 파라미터의 예외가 지정된다.
     *
     */
    // HTTP 컨버터가 사용되고, 응답이 JSON으로 반환
    // @ResponseStatus는 애노테이션이므로 HTTP 응답 코드를 동적으로 변경불가
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorDto illegalExHandle(IllegalArgumentException e) {
        log.error("[exceptionHandle] ex", e);
        return new ErrorDto("BAD", e.getMessage());
    }

    // HTTP 컨버터가 사용되고, HTTP 메시지 바디에 직접 응답
    // ResponseEntity는 HTTP 응답 코드를 프로그래밍해서 동적으로 변경가능
    @ExceptionHandler
    public ResponseEntity<ErrorDto> userExHandle(UserException e) {
        log.error("[exceptionHandle] ex", e);
        ErrorDto ErrorDto = new ErrorDto("USER-EX", e.getMessage());
        return new ResponseEntity<>(ErrorDto, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorDto exHandle(Exception e) {
        log.error("[exceptionHandle] ex", e);
        return new ErrorDto("EX", "내부 오류");
    }

    // HTML 오류 화면 응답
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ViewException.class)
    public ModelAndView ex(ViewException e) {
        log.info("view exception e", e);
        log.info("e.getMessage={}", e.getMessage());
        return new ModelAndView("error-page/viewError");
    }

}
