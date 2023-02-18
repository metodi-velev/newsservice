package com.example.newsservice.validators;

import com.example.newsservice.dto.NewsDetailsDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class NewsDetailsDtoValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return NewsDetailsDto.class == clazz;
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "title", "title.empty", "News title must not be empty or null.");
        ValidationUtils.rejectIfEmpty(errors, "text", "text.empty", "News text must not be empty or null");
        NewsDetailsDto news = (NewsDetailsDto) target;

        if (news.getTitle() != null &&
                news.getTitle().length() < 6) {
            errors.rejectValue("title", "title.too.short", "News title must be at least 6 characters long.");
        }

        if (news.getTitle() != null &&
                news.getTitle().length() > 50) {
            errors.rejectValue("title", "title.too.long", "News title must be at most 50 characters long.");
        }

        if (news.getText() != null &&
                news.getText().length() < 20) {
            errors.rejectValue("text", "title.too.short", "News text must be at least 20 characters long.");
        }

        if (news.getText() != null &&
                news.getText().length() > 200) {
            errors.rejectValue("text", "text.too.long", "News text must be at most 200 characters long.");
        }
    }
}
