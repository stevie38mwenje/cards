package com.example.cards.service.impl;

import com.example.cards.dto.CardRequest;
import com.example.cards.exception.CustomException;
import com.example.cards.model.Card;
import com.example.cards.model.StatusEnum;
import com.example.cards.model.User;
import com.example.cards.repository.CardRepository;
import com.example.cards.response.GenericResponse;
import com.example.cards.service.CardService;
import com.example.cards.util.FilterParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private static final String ADMIN = "ADMIN";
    private static final String MEMBER = "MEMBER";


    @Override
    @Transactional
    public ResponseEntity<GenericResponse> createCard(CardRequest cardRequest, User user) {
        validateCardRequest(cardRequest);
        var uniqueCode = cardRequest.getName()+"_"+cardRequest.getColor();
        if(cardRepository.existsByCardCode(uniqueCode)){
            throw new CustomException("Given card already exists, please choose a unique name or color");
        }
        Card card = Card.builder()
                .name(cardRequest.getName())
                .color(cardRequest.getColor())
                .description(cardRequest.getDescription())
                .cardCode(uniqueCode)
                .active(1)
                .status(StatusEnum.TODO)
                .insertedBy(user)
                .createdDate(LocalDateTime.now())
                .build();
        log.info("Persisting card to cards table : {}",card);
        cardRepository.save(card);
        return ResponseEntity.ok(GenericResponse.builder().data(card)
                .message("saved card successfully").status(HttpStatus.OK.value())
                .success(true)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getCards(FilterParams filterParams, Integer page,
                                                    Integer size,
                                                    String sortBy, User user) {
        log.info("LOGGED IN USER : {}",user);

        StatusEnum status = null;
        if (filterParams.getStatus() != null) {
            status = StatusEnum.valueOf(filterParams.getStatus()); // Convert string to enum
        }
        String name = filterParams.getName();
        String color = filterParams.getColor();
        String createdDate = filterParams.getCreatedDate();

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        if (MEMBER.equals(user.getRole().name())) {
            if (name == null && color == null && status == null && createdDate == null) {
                Page<List<Card>> cards =  cardRepository.findByInsertedBy(user, pageable);
                return ResponseEntity.ok(GenericResponse.builder().data(Collections.singletonList(cards))
                        .message("fetched cards successfully").status(HttpStatus.OK.value())
                        .success(true)
                        .build());
            } else {
                Page<List<Card>> cards = cardRepository.findByInsertedByAndFilters(user, name, color, status, createdDate, pageable);
                return ResponseEntity.ok(GenericResponse.builder().data(Collections.singletonList(cards))
                        .message("fetched cards successfully").status(HttpStatus.OK.value())
                        .success(true)
                        .build());
            }

        } else if (ADMIN.equals(user.getRole().name())) {

            if (name == null && color == null && status == null && createdDate == null) {
                Page<Card> cards =  cardRepository.findAll(pageable);
                return ResponseEntity.ok(GenericResponse.builder().data(Collections.singletonList(cards))
                        .message("fetched cards successfully").status(HttpStatus.OK.value())
                        .success(true)
                        .build());

            }
            Page<List<Card>> cards =  cardRepository.findAllByNameAndColorAndStatusAndCreatedDate(name, String.valueOf(color), status,createdDate,pageable);
            return ResponseEntity.ok(GenericResponse.builder().data(Collections.singletonList(cards))
                    .message("fetched cards successfully").status(HttpStatus.OK.value())
                    .success(true)
                    .build());
        }
            GenericResponse response = GenericResponse.builder()
                .data(null)
                .message("You don't have permission to view cards")
                .status(HttpStatus.FORBIDDEN.value())
                .success(false)
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @Override
    @Transactional
    public ResponseEntity<GenericResponse> updateCard(CardRequest cardRequest, Long cardID, User user) {
        try{
            if (StringUtils.isBlank(cardRequest.getName())) {
                throw new CustomException("Name cannot be null, empty, or blank");
            }
            var card = cardRepository.findById(cardID).orElseThrow(()->
                    new CustomException("Card not found for the given id : %d".formatted(cardID)));

            var updatedCardPayload = formulateCardPayload(card,cardRequest,user);
            log.info("updated card payload : {}",updatedCardPayload);

            if(cardRepository.existsByCardCodeAndCardIDNot(updatedCardPayload.getCardCode(),cardID)){
                throw new CustomException("Given card already exists, please choose a unique name or color");
            }
            var updatedCard = cardRepository.save(updatedCardPayload);
            return ResponseEntity.ok(GenericResponse.builder().data(updatedCard)
                    .message("updated card successfully").status(HttpStatus.OK.value())
                    .success(true)
                    .build());
        }catch (CustomException ex){
            GenericResponse errorResponse = GenericResponse.builder()
                    .data(null)
                    .message(ex.getMessage())
                    .status(HttpStatus.NOT_FOUND.value())
                    .success(false)
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    private void validateCardRequest(CardRequest cardRequest) {
        if(!isValidColorFormat(cardRequest.getColor())){
            throw new CustomException("Color should be 6 alphanumeric characters prefixed with a #");
        }

        if (StringUtils.isBlank(cardRequest.getName())) {
            throw new CustomException("Name cannot be null, empty, or blank");
        }
    }

    @Override
    public ResponseEntity<GenericResponse> getCard(Long cardID, User user) {
        try {
            var card = cardRepository.findById(cardID).orElseThrow(() ->
                    new CustomException("Card not found for the given id : " + cardID));
            log.info("user id : {}",user.getUserID());

            if (card.getInsertedBy().getUserID().equals(user.getUserID())||ADMIN.equals(user.getRole().name())) {
                return ResponseEntity.ok(GenericResponse.builder().data(card)
                        .message("Fetched card successfully").status(HttpStatus.OK.value())
                        .success(true)
                        .build());
            } else {
                GenericResponse response = GenericResponse.builder()
                        .data(null)
                        .message("You don't have permission to view this card")
                        .status(HttpStatus.FORBIDDEN.value())
                        .success(false)
                        .build();
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        } catch (CustomException ex) {
            GenericResponse errorResponse = GenericResponse.builder()
                    .data(null)
                    .message(ex.getMessage())
                    .status(HttpStatus.NOT_FOUND.value())
                    .success(false)
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<GenericResponse> deleteCard(Long cardID, User user) {
        try{
            var card = cardRepository.findById(cardID).orElseThrow(()->
                    new CustomException("Card not found for the given id : "+ cardID));
            if (card.getInsertedBy().getUserID().equals(user.getUserID())||ADMIN.equals(user.getRole().name())) {
                cardRepository.deleteById(cardID);
                return ResponseEntity.ok(GenericResponse.builder().data(null)
                        .message("deleted card successfully").status(HttpStatus.OK.value())
                        .success(true)
                        .build());
            }
            GenericResponse response = GenericResponse.builder()
                    .data(null)
                    .message("You don't have permission to delete this card")
                    .status(HttpStatus.FORBIDDEN.value())
                    .success(false)
                    .build();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }catch (CustomException ex){
            GenericResponse errorResponse = GenericResponse.builder()
                    .data(null)
                    .message(ex.getMessage())
                    .status(HttpStatus.NOT_FOUND.value())
                    .success(false)
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        }

    }

    @Override
    public ResponseEntity<GenericResponse> deactivate(Long cardID, Integer active, User user) {
        var card = cardRepository.findById(cardID).orElseThrow(()->
                new CustomException("Card not found for the given id : "+ cardID));
        if (card.getInsertedBy().getUserID().equals(user.getUserID())||ADMIN.equals(user.getRole().name())) {
            card.setActive(active);
            cardRepository.save(card);
            return ResponseEntity.ok(GenericResponse.builder().data(card)
                    .message("deactivated card successfully").status(HttpStatus.OK.value())
                    .success(true)
                    .build());
        }
        GenericResponse errorResponse = GenericResponse.builder()
                .data(null)
                .message("You dont have permissions to deactivate/activate")
                .status(HttpStatus.NOT_FOUND.value())
                .success(false)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    private Card formulateCardPayload(Card oldCard, CardRequest newCardRequest, User user) {
        oldCard.setUpdatedDate(LocalDateTime.now());
        oldCard.setUpdatedBy(user.getUserID());
        oldCard.setName(
                newCardRequest.getName() != null ? newCardRequest.getName() : oldCard.getName());
        oldCard.setStatus(newCardRequest.getStatus()!=null? StatusEnum.valueOf(newCardRequest.getStatus().toUpperCase()) :oldCard.getStatus());
        oldCard.setColor(
                newCardRequest.getColor() != null ? newCardRequest.getColor() : oldCard.getColor());
        oldCard.setDescription(
                newCardRequest.getDescription() != null ? newCardRequest.getDescription() : oldCard.getDescription());
        return oldCard;
    }

    private boolean isValidColorFormat(String color) {
        return color != null && color.length() == 7 && color.startsWith("#")
                // Check if remaining characters are alphanumeric
                && color.substring(1).matches("^[a-fA-F0-9]*$");
    }
}
