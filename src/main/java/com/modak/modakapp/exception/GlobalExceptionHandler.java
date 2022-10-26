package com.modak.modakapp.exception;


import com.modak.modakapp.dto.response.CommonFailResponse;
import com.modak.modakapp.exception.anniversary.NoSuchAnniversaryException;
import com.modak.modakapp.exception.family.NoSuchFamilyException;
import com.modak.modakapp.exception.letter.NoSuchLetterException;
import com.modak.modakapp.exception.member.MemberAlreadyExistsException;
import com.modak.modakapp.exception.member.NoSuchMemberException;
import com.modak.modakapp.exception.todaytalk.AlreadyExistsTodayTalkException;
import com.modak.modakapp.exception.todo.NoSuchTodoException;
import com.modak.modakapp.exception.token.ExpiredAccessTokenException;
import com.modak.modakapp.exception.token.ExpiredRefreshTokenException;
import com.modak.modakapp.exception.token.NotMatchRefreshTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.sentry.Sentry;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<?> handleMalformedJwtException(MalformedJwtException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonFailResponse.response("JWT 포맷이 올바른지 확인하세요", "MalformedJwtException"));
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<?> handleSignatureException(SignatureException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonFailResponse.response("JWT 포맷이 올바른지 확인하세요", "SignatureException"));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> handleExpiredJwtException(ExpiredJwtException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonFailResponse.response("만료된 Token 입니다.", "ExpiredJwtException"));
    }

    @ExceptionHandler(ExpiredAccessTokenException.class)
    public ResponseEntity<?> handleExpiredAccessTokenException(ExpiredAccessTokenException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonFailResponse.response("만료된 Access Token 입니다.", "ExpiredAccessTokenException"));
    }

    @ExceptionHandler(ExpiredRefreshTokenException.class)
    public ResponseEntity<?> handleExpiredRefreshTokenException(ExpiredRefreshTokenException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonFailResponse.response("만료된 Refresh Token 입니다. 다시 로그인하세요", "ExpiredRefreshTokenException"));
    }

    @ExceptionHandler(NotMatchRefreshTokenException.class)
    public ResponseEntity<?> handleNotMatchRefreshTokenException(NotMatchRefreshTokenException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonFailResponse.response("회원이 가지고 있는 Refresh Token과 요청한 Refresh Token이 다릅니다.", "NotMatchRefreshTokenException"));
    }

    @ExceptionHandler(NoSuchMemberException.class)
    public ResponseEntity<?> handleNoSuchMemberException(NoSuchMemberException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CommonFailResponse.response("회원 정보가 없습니다. 회원가입 페이지로 이동하세요", "NoSuchMemberException"));
    }

    @ExceptionHandler(NoSuchFamilyException.class)
    public ResponseEntity<?> handleNoSuchFamilyException(NoSuchFamilyException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CommonFailResponse.response("가족 정보가 없습니다.", "NoSuchFamilyException"));
    }

    @ExceptionHandler(NoSuchLetterException.class)
    public ResponseEntity<?> handleNoSuchLetterException(NoSuchLetterException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CommonFailResponse.response("해당 편지 정보가 없습니다.", "NoSuchLetterException"));
    }

    @ExceptionHandler(NoSuchTodoException.class)
    public ResponseEntity<?> handleNoSuchTodoException(NoSuchTodoException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CommonFailResponse.response("할 일 정보가 없습니다.", "NoSuchTodoException"));
    }

    @ExceptionHandler(NoSuchAnniversaryException.class)
    public ResponseEntity<?> handleNoSuchAnniversaryException(NoSuchAnniversaryException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CommonFailResponse.response("기념일 정보가 없습니다.", "NoSuchAnniversaryException"));
    }

    @ExceptionHandler(MemberAlreadyExistsException.class)
    public ResponseEntity<?> handleMemberAlreadyExistsException(MemberAlreadyExistsException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonFailResponse.response("이미 가입된 회원입니다.", "MemberAlreadyExistsException"));
    }

    @ExceptionHandler(AlreadyExistsTodayTalkException.class)
    public ResponseEntity<?> handleAlreadyExistsTodayTalkException(AlreadyExistsTodayTalkException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonFailResponse.response("이미 오늘의 한 마디가 존재합니다.", "AlreadyExistsTodayTalkException"));
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<?> handleEmptyResultDataAccessException(EmptyResultDataAccessException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CommonFailResponse.response("요청한 리소스 정보가 없습니다.", "EmptyResultDataAccessException"));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<?> handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonFailResponse.response("헤더에 필수로 들어가야 하는 값이 없습니다.", "MissingRequestHeaderException"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonFailResponse.response(Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage(), "MethodArgumentNotValidException"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        e.printStackTrace();
        Sentry.captureException(e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonFailResponse.response(e.getMessage(), e.toString()));
    }
}
