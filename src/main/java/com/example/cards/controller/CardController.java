package com.example.cards.controller;

import com.example.cards.dto.CardRequest;
import com.example.cards.model.User;
import com.example.cards.response.GenericResponse;
import com.example.cards.service.CardService;
import com.example.cards.util.FilterParams;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/cards")
@AllArgsConstructor
@Slf4j
public class CardController {
    private final CardService cardService;

    @PostMapping("add")
    public ResponseEntity<GenericResponse> createCard(@Valid @RequestBody CardRequest cardRequest, @AuthenticationPrincipal User user) {
        return cardService.createCard(cardRequest,user);
    }
    @GetMapping("/fetch")
    public ResponseEntity<GenericResponse> getCards( @AuthenticationPrincipal User user,
                                                     @RequestParam(required = false) String name,
                                                     @RequestParam(required = false) String color,
                                                     @RequestParam(required = false) String status,
                                                     @RequestParam(required = false) String creationDate,
                                                     @RequestParam(defaultValue = "0") Integer page,
                                                     @RequestParam(defaultValue = "10") Integer size,
                                                     @RequestParam(defaultValue = "cardID") String sortBy
                                                     ) {
        FilterParams filterParams = FilterParams.builder().name(name).color(color).status(status).createdDate(creationDate).build();
        return cardService.getCards(filterParams,page,size,sortBy,user);
    }

    @GetMapping("/{cardID}")
    public ResponseEntity<GenericResponse> getCard(@PathVariable Long cardID, @AuthenticationPrincipal User user) {
        return cardService.getCard(cardID,user);
    }



    @PutMapping("/{cardID}")
    public ResponseEntity<GenericResponse> updateCard(@RequestBody CardRequest cardRequest,
                                                      @PathVariable Long cardID, @AuthenticationPrincipal User user) {
        return cardService.updateCard(cardRequest,cardID,user);
    }

    @PutMapping("/active/{cardID}")
    public ResponseEntity<GenericResponse> deactivateCard(@PathVariable Long cardID,@RequestParam Integer active, @AuthenticationPrincipal User user) {
        return cardService.deactivate(cardID,active,user);
    }
    @DeleteMapping("/{cardID}")
    public ResponseEntity<GenericResponse> deleteCard(@PathVariable Long cardID, @AuthenticationPrincipal User user) {
        return cardService.deleteCard(cardID,user);
    }

}
