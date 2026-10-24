
package com.soulmatch.backend.questionnaires;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class QuestionnaireNotFoundException extends RuntimeException {

    public QuestionnaireNotFoundException(String message) {
        super(message);
    }
}
