package ru.itmo.is.lab1.exception;

import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ExceptionEnum {
    BAD_REQUEST("Некорректный запрос", Response.Status.BAD_REQUEST),
    USER_NOT_FOUND("Пользователь не найден", Response.Status.NOT_FOUND),
    AUTHORIZATION_ERROR("Ошибка авторизации", Response.Status.UNAUTHORIZED),
    TOKEN_CHECKING_ERROR("Ошибка проверки jwt-токена", Response.Status.UNAUTHORIZED),
    USER_ALREADY_EXISTED("Пользователь с переданным логином уже существует", Response.Status.CONFLICT),
    VEHICLE_NOT_FOUND("Средство передвижения не найдено", Response.Status.NOT_FOUND),
    WRONG_CREDENTIALS("Неверные логин или пароль пользователя", Response.Status.UNAUTHORIZED),
    FORBIDDEN("Доступ запрещен", Response.Status.FORBIDDEN),
    ADMIN_REQUEST_NOT_FOUND("Заявка на администратора не найдена", Response.Status.NOT_FOUND),
    ACTIVE_ADMIN_REQUEST_ALREADY_EXIST("Активная заявка на администратора уже существует!", Response.Status.CONFLICT),
    ADMIN_REQUEST_IS_NOT_ACTIVE("Заявка на администратора уже обработана ранее", Response.Status.CONFLICT),
    BAD_FILE_FORMAT("Переданный формат файла не поддерживается", Response.Status.BAD_REQUEST),
    BAD_FILE_CONTENT("В файле содержатся некорректные сущности", Response.Status.BAD_REQUEST),
    SERVER_ERROR("Ошибка сервера", Response.Status.INTERNAL_SERVER_ERROR),
    VALIDATION_EXCEPTION("Валидация уникальных полей не пройдена", Response.Status.BAD_REQUEST),
    ;

    private final String error;
    private final String message;
    private final Response.Status status;

    ExceptionEnum(String message, Response.Status status) {
        this.error = name();
        this.message = message;
        this.status = status;
    }
}
