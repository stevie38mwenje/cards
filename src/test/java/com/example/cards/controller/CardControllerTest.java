package com.example.cards.controller;

import com.example.cards.dto.CardRequest;
import com.example.cards.model.Card;
import com.example.cards.model.User;
import com.example.cards.response.GenericResponse;
import com.example.cards.service.CardService;
import com.example.cards.util.FilterParams;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

    @Mock
    private CardService cardService;
    @InjectMocks
    private CardController cardController;

    private MockMvc mockMvc;

    @BeforeEach()
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(cardController).build();
    }

    @Test
    public void GetMappingOfCardShouldReturnRespectiveCard() throws Exception {
        CardRequest cardRequest = new CardRequest();
        cardRequest.setName("Test Card");

        User mockUser = new User();

        Card mockCard = new Card();
        mockCard.setCardID(1L);
        mockCard.setName("Test Card");

        GenericResponse mockResponse = GenericResponse.builder()
                .data(mockCard)
                .message("card fetched successfully")
                .status(HttpStatus.OK.value())
                .success(true)
                .build();

        mockMvc.perform(get("/api/cards/1").
                        contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(cardRequest))
                .principal(new UsernamePasswordAuthenticationToken(mockUser, null))).
                andExpect(status().isOk()).
                andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testCreateCard() throws Exception {
        CardRequest cardRequest = new CardRequest();
        cardRequest.setName("Test Card");

        User mockUser = new User();

        Card mockCard = new Card();
        mockCard.setCardID(1L);
        mockCard.setName("Test Card");

        GenericResponse mockResponse = GenericResponse.builder()
                .data(mockCard)
                .message("saved card successfully")
                .status(HttpStatus.OK.value())
                .success(true)
                .build();

        when(cardService.createCard(eq(cardRequest), eq(mockUser))).thenReturn(ResponseEntity.ok(mockResponse));

        mockMvc.perform(post("/api/cards/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(cardRequest)) // Use writeValueAsString
                        .principal(new UsernamePasswordAuthenticationToken(mockUser, null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.cardID").value(mockCard.getCardID()))
                .andExpect(jsonPath("$.message").value("saved card successfully"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()));

        verify(cardService).createCard(eq(cardRequest), eq(mockUser));
    }



    @Test
    void testUpdateCard() {
        // Mocked objects
        CardRequest cardRequest = new CardRequest();
        User mockUser = new User();
        Card mockUpdatedCard = new Card();

        // Configure mock behavior
        when(cardService.updateCard(eq(cardRequest), eq(1L), eq(mockUser)))
                .thenReturn(ResponseEntity.ok(GenericResponse.builder()
                        .message("updated card successfully")
                        .status(HttpStatus.OK.value())
                        .success(true)
                        .data(mockUpdatedCard)
                        .build()));

        // Call the controller method
        ResponseEntity<GenericResponse> response = cardController.updateCard(cardRequest, 1L, mockUser);

        // Verify the response
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        Assertions.assertEquals("updated card successfully", response.getBody().getMessage());
        Assertions.assertEquals(mockUpdatedCard, response.getBody().getData());

        // Verify the service method was called
        verify(cardService).updateCard(eq(cardRequest), eq(1L), eq(mockUser));
    }

    @Test
    public void testDeactivateCard() {
        // Mocked objects
        User mockUser = new User();
        Card mockCard = new Card();
        Integer active = 0;

        // Configure mock behavior
        when(cardService.deactivate(eq(1L), eq(active), eq(mockUser)))
                .thenReturn(ResponseEntity.ok(GenericResponse.builder()
                        .message("Card deactivated successfully")
                        .status(HttpStatus.OK.value())
                        .success(true)
                        .data(mockCard)
                        .build()));

        // Call the controller method
        ResponseEntity<GenericResponse> response = cardController.deactivateCard(1L, active, mockUser);

        // Verify the response
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        Assertions.assertEquals("Card deactivated successfully", response.getBody().getMessage());
        Assertions.assertEquals(mockCard, response.getBody().getData());

        // Verify the service method was called
        verify(cardService).deactivate(eq(1L), eq(active), eq(mockUser));
    }

    @Test
    void testDeleteCard() {
        // Mocked objects
        User mockUser = new User();
        Card mockCard = new Card();

        // Configure mock behavior
        when(cardService.deleteCard(eq(1L), eq(mockUser)))
                .thenReturn(ResponseEntity.ok(GenericResponse.builder()
                        .message("Card deleted successfully")
                        .status(HttpStatus.OK.value())
                        .success(true)
                        .data(mockCard)
                        .build()));

        // Call the controller method
        ResponseEntity<GenericResponse> response = cardController.deleteCard(1L, mockUser);

        // Verify the response
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        Assertions.assertEquals("Card deleted successfully", response.getBody().getMessage());
        Assertions.assertEquals(mockCard, response.getBody().getData());

        // Verify the service method was called
        verify(cardService).deleteCard(eq(1L), eq(mockUser));
    }


    @Test
    void testGetCards() {
        User mockUser = new User();
        Page<Card> mockCardPage = new PageImpl<>(Collections.singletonList(new Card()));
        FilterParams mockFilterParams = new FilterParams();
        when(cardService.getCards(eq(mockFilterParams), eq(0), eq(10), eq("cardID"), eq(mockUser)))
                .thenReturn(ResponseEntity.ok(GenericResponse.builder()
                        .message("fetched cards successfully")
                        .status(HttpStatus.OK.value())
                        .success(true)
                        .data(mockCardPage)
                        .build()));

        // Call the controller method
        ResponseEntity<GenericResponse> response = cardController.getCards(mockUser, null, null, null, null, 0, 10, "cardID");

        // Verify the response
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        Assertions.assertEquals("fetched cards successfully", response.getBody().getMessage());
        Assertions.assertEquals(mockCardPage, response.getBody().getData());

        // Verify the service method was called
        verify(cardService).getCards(eq(mockFilterParams), eq(0), eq(10), eq("cardID"), eq(mockUser));
    }


}