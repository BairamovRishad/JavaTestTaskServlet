package com.barnyard.staff.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.Locale;

@ControllerAdvice
public class AppControllerAdvice {
    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNotFoundException(Locale locale, NotFoundException ex) {
        String notFoundMessage = messageSource.getMessage(ex.getMessage(), null, ex.getMessage(), locale);
        return new ModelAndView("error/error_modal", "errorMessage", notFoundMessage);
    }
}
