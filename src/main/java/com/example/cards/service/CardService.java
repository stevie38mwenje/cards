package com.example.cards.service;

import com.example.cards.dto.CardRequest;
import com.example.cards.model.User;
import com.example.cards.response.GenericResponse;
import com.example.cards.util.FilterParams;
import org.springframework.http.ResponseEntity;


public interface CardService {
    ResponseEntity<GenericResponse> createCard(CardRequest cardRequest, User user);

    ResponseEntity<GenericResponse> getCards(FilterParams filterParams, Integer page,
                                               Integer size,
                                               String sortBy, User user);

    ResponseEntity<GenericResponse> updateCard(CardRequest cardRequest, Long cardID, User user);

    ResponseEntity<GenericResponse> getCard(Long cardID,User user);

    ResponseEntity<GenericResponse> deleteCard(Long cardID, User user);

    ResponseEntity<GenericResponse> deactivate(Long cardID, Integer active, User user);
}
