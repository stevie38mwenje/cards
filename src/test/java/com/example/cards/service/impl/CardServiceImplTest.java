package com.example.cards.service.impl;

import com.example.cards.dto.CardRequest;
import com.example.cards.model.Card;
import com.example.cards.model.RoleEnum;
import com.example.cards.model.StatusEnum;
import com.example.cards.model.User;
import com.example.cards.repository.CardRepository;
import com.example.cards.response.GenericResponse;
import com.example.cards.util.FilterParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardServiceImpl cardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCard_Success() {
        // Mocked objects
        User mockUser = new User();
        CardRequest mockCardRequest = new CardRequest();
        mockCardRequest.setName("Test Card");
        mockCardRequest.setColor("#123456");
        mockCardRequest.setDescription("Test description");

        // Configure mock behavior
        when(cardRepository.existsByCardCode(anyString())).thenReturn(false);
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Call the service method
        ResponseEntity<GenericResponse> response = cardService.createCard(mockCardRequest, mockUser);

        // Verify the response and repository interactions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("saved card successfully", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        verify(cardRepository).existsByCardCode(anyString());
        verify(cardRepository).save(any(Card.class));
    }
    @Test
    void testGetCards_Member_NoFilters() {
        // Mocked objects
        User mockUser = new User();
        mockUser.setRole(RoleEnum.MEMBER);

        FilterParams mockFilterParams = new FilterParams();
        Integer mockPage = 0;
        Integer mockSize = 10;
        String mockSortBy = "cardID";

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        // Configure mock behavior
        when(cardRepository.findByInsertedBy(any(User.class), any(Pageable.class)))
                .thenReturn(Page.empty());

        // Call the service method
        ResponseEntity<GenericResponse> response = cardService.getCards(mockFilterParams, mockPage, mockSize, mockSortBy, mockUser);

        // Verify the response and repository interactions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("fetched cards successfully", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());

        // Verify the repository method call with the captured pageable argument
        verify(cardRepository).findByInsertedBy(eq(mockUser), pageableCaptor.capture());

        // Compare only the relevant parts of pageable argument (e.g., size and page number)
        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(mockPage, capturedPageable.getPageNumber());
        assertEquals(mockSize, capturedPageable.getPageSize());
    }


    @Test
    void testGetCards_Admin_NoFilters() {
        // Mocked objects
        User mockUser = new User();
        mockUser.setRole(RoleEnum.ADMIN);

        FilterParams mockFilterParams = new FilterParams();
        Integer mockPage = 0;
        Integer mockSize = 10;
        String mockSortBy = "cardID";

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        // Configure mock behavior
        when(cardRepository.findAll(any(Pageable.class)))
                .thenReturn(Page.empty());

        // Call the service method
        ResponseEntity<GenericResponse> response = cardService.getCards(mockFilterParams, mockPage, mockSize, mockSortBy, mockUser);

        // Verify the response and repository interactions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("fetched cards successfully", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());

        // Verify the repository method call with the captured pageable argument
        verify(cardRepository).findAll(pageableCaptor.capture());

        // Compare only the relevant parts of pageable argument (e.g., size and page number)
        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(mockPage, capturedPageable.getPageNumber());
        assertEquals(mockSize, capturedPageable.getPageSize());
    }



    @Test
    void testGetCards_Member_Filters() {
        // Mocked objects
        User mockUser = new User();
        mockUser.setRole(RoleEnum.MEMBER);

        FilterParams mockFilterParams = new FilterParams();
        mockFilterParams.setName("Test Card");
        mockFilterParams.setColor("#123456");
        mockFilterParams.setStatus("TODO");
        mockFilterParams.setCreatedDate("2023-08-01");
        Integer mockPage = 0;
        Integer mockSize = 10;
        String mockSortBy = "cardID";

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        // Configure mock behavior
        when(cardRepository.findByInsertedByAndFilters(
                eq(mockUser),
                eq(mockFilterParams.getName()),
                eq(mockFilterParams.getColor()),
                eq(StatusEnum.TODO),
                any(LocalDate.class),
                any(Pageable.class))
        ).thenReturn(Page.empty());

        // Call the service method
        ResponseEntity<GenericResponse> response = cardService.getCards(
                mockFilterParams,
                mockPage,
                mockSize,
                mockSortBy,
                mockUser
        );

        // Verify the response and repository interactions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("fetched cards successfully", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());

        // Verify the repository method call with the captured pageable argument
        verify(cardRepository).findByInsertedByAndFilters(
                eq(mockUser),
                eq(mockFilterParams.getName()),
                eq(mockFilterParams.getColor()),
                eq(StatusEnum.TODO),
                any(LocalDate.class),
                pageableCaptor.capture()
        );

        // Compare only the relevant parts of pageable argument (e.g., size and page number)
        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(mockPage, capturedPageable.getPageNumber());
        assertEquals(mockSize, capturedPageable.getPageSize());
    }

    @Test
    void testGetCards_Admin_Filters() {
        // Mocked objects
        User mockUser = new User();
        mockUser.setRole(RoleEnum.ADMIN);

        FilterParams mockFilterParams = new FilterParams();
        mockFilterParams.setName("Test Card");
        mockFilterParams.setColor("#123456");
        mockFilterParams.setStatus("TODO");
        mockFilterParams.setCreatedDate("2023-08-01");
        Integer mockPage = 0;
        Integer mockSize = 10;
        String mockSortBy = "cardID";

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        // Configure mock behavior
        when(cardRepository.findAllByNameAndColorAndStatusAndCreatedDate(
                eq(mockFilterParams.getName()),
                eq(mockFilterParams.getColor()),
                eq(StatusEnum.TODO),
                any(LocalDate.class),
                any(Pageable.class))
        ).thenReturn(Page.empty());

        // Call the service method
        ResponseEntity<GenericResponse> response = cardService.getCards(
                mockFilterParams,
                mockPage,
                mockSize,
                mockSortBy,
                mockUser
        );

        // Verify the response and repository interactions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("fetched cards successfully", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());

        // Verify the repository method call with the captured pageable argument
        verify(cardRepository).findAllByNameAndColorAndStatusAndCreatedDate(
                eq(mockFilterParams.getName()),
                eq(mockFilterParams.getColor()),
                eq(StatusEnum.TODO),
                any(LocalDate.class),
                pageableCaptor.capture()
        );

        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(mockPage, capturedPageable.getPageNumber());
        assertEquals(mockSize, capturedPageable.getPageSize());
    }
    @Test
    void testUpdateCard_Success() {
        // Mocked objects
        User mockUser = new User();
        CardRequest mockCardRequest = new CardRequest();
        mockCardRequest.setName("Updated Card");
        mockCardRequest.setColor("#654321");
        mockCardRequest.setDescription("Updated description");

        Card mockExistingCard = new Card();
        mockExistingCard.setCardID(1L);
        mockExistingCard.setName("Test Card");
        mockExistingCard.setInsertedBy(mockUser);

        Card mockUpdatedCard = new Card();
        mockUpdatedCard.setCardID(1L);
        mockUpdatedCard.setName("Updated Card");

        // Configure mock behavior
        when(cardRepository.findById(anyLong())).thenReturn(java.util.Optional.of(mockExistingCard));
        when(cardRepository.existsByCardCodeAndCardIDNot(anyString(), anyLong())).thenReturn(false);
        when(cardRepository.save(any(Card.class))).thenReturn(mockUpdatedCard);

        // Call the service method
        ResponseEntity<GenericResponse> response = cardService.updateCard(mockCardRequest, 1L, mockUser);

        // Verify the response and repository interactions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("updated card successfully", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        verify(cardRepository).findById(anyLong());
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void testUpdateCard_CardNotFound() {
        // Mocked objects
        User mockUser = new User();
        CardRequest mockCardRequest = new CardRequest();

        // Configure mock behavior
        when(cardRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Call the service method
        ResponseEntity<GenericResponse> response = cardService.updateCard(mockCardRequest, 1L, mockUser);

        // Verify the response and repository interactions
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(Objects.requireNonNull(response.getBody()).isSuccess());
        assertNotNull(response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }
    @Test
    void testUpdateCard_DuplicateCardCode() {
        // Mocked objects
        User mockUser = new User();
        CardRequest mockCardRequest = new CardRequest();
        mockCardRequest.setName("Updated Card");
        mockCardRequest.setColor("#654321");

        Card mockExistingCard = new Card();
        mockExistingCard.setCardID(1L);
        mockExistingCard.setName("Test Card");
        mockExistingCard.setInsertedBy(mockUser);

        // Construct the expected cardCode
        String expectedCardCode = mockCardRequest.getName() + "_" + mockCardRequest.getColor();

        // Configure mock behavior
        when(cardRepository.findById(anyLong())).thenReturn(java.util.Optional.of(mockExistingCard));
        when(cardRepository.existsByCardCodeAndCardIDNot(eq(expectedCardCode), eq(1L))).thenReturn(true);

        // Call the service method
        ResponseEntity<GenericResponse> response = cardService.updateCard(mockCardRequest, 1L, mockUser);

        // Verify the response and repository interactions
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getMessage());
        assertNull(response.getBody().getData());
        verify(cardRepository).findById(anyLong());
    }

    @Test
    void testGetCard_Success() {
        // Mocked objects
        User mockUser = new User();
        mockUser.setUserID(1L);

        Card mockCard = new Card();
        mockCard.setCardID(1L);
        mockCard.setName("Test Card");
        mockCard.setInsertedBy(mockUser);

        // Configure mock behavior
        when(cardRepository.findById(anyLong())).thenReturn(java.util.Optional.of(mockCard));

        // Call the service method
        ResponseEntity<GenericResponse> response = cardService.getCard(1L, mockUser);

        // Verify the response and repository interactions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("Fetched card successfully", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        verify(cardRepository).findById(anyLong());
    }

    @Test
    void testGetCard_CardNotFound() {
        // Mocked objects
        User mockUser = new User();

        // Configure mock behavior
        when(cardRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

        // Call the service method
        ResponseEntity<GenericResponse> response = cardService.getCard(1L, mockUser);

        // Verify the response and repository interactions
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(Objects.requireNonNull(response.getBody()).isSuccess());
        assertTrue(response.getBody().getMessage().startsWith("Card not found"));
        assertNull(response.getBody().getData());
        verify(cardRepository).findById(anyLong());
    }

    @Test
    void testDeleteCard_CardNotFound() {
        // Mocked objects
        User mockUser = new User();

        // Configure mock behavior
        when(cardRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

        // Call the service method
        ResponseEntity<GenericResponse> response = cardService.deleteCard(1L, mockUser);

        // Verify the response and repository interactions
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(Objects.requireNonNull(response.getBody()).isSuccess());
        assertTrue(response.getBody().getMessage().startsWith("Card not found"));
        assertNull(response.getBody().getData());
        verify(cardRepository).findById(anyLong());
        verify(cardRepository, never()).deleteById(anyLong());
    }


    @Test
    void testDeleteCard_Success() {
        // Mocked objects
        User mockUser = new User();
        mockUser.setUserID(1L);

        Card mockCard = new Card();
        mockCard.setCardID(1L);
        mockCard.setName("Test Card");
        mockCard.setInsertedBy(mockUser);

        // Configure mock behavior
        when(cardRepository.findById(anyLong())).thenReturn(java.util.Optional.of(mockCard));

        // Call the service method
        ResponseEntity<GenericResponse> response = cardService.deleteCard(1L, mockUser);

        // Verify the response and repository interactions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("deleted card successfully", response.getBody().getMessage());
        assertNull(response.getBody().getData());
        verify(cardRepository).findById(anyLong());
        verify(cardRepository).deleteById(anyLong());
    }
    @Test
    void testDeactivateCard_Success() {
        // Mocked objects
        User mockUser = new User();
        mockUser.setUserID(1L);

        Card mockCard = new Card();
        mockCard.setCardID(1L);
        mockCard.setName("Test Card");
        mockCard.setInsertedBy(mockUser);

        // Configure mock behavior
        when(cardRepository.findById(anyLong())).thenReturn(java.util.Optional.of(mockCard));
        when(cardRepository.save(any(Card.class))).thenReturn(mockCard);

        // Call the service method
        ResponseEntity<GenericResponse> response = cardService.deactivate(1L, 0, mockUser);

        // Verify the response and repository interactions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("deactivated card successfully", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        verify(cardRepository).findById(anyLong());
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void testDeactivateCard_Unauthorized() {
        // Mocked objects
        User mockUser = new User();
        mockUser.setUserID(2L);
        mockUser.setRole(RoleEnum.MEMBER);

        Card mockCard = new Card();
        mockCard.setCardID(1L);
        mockCard.setName("Test Card");
        mockCard.setInsertedBy(User.builder().userID(1L).role(RoleEnum.ADMIN).build()); // Different user

        // Configure mock behavior
        when(cardRepository.findById(anyLong())).thenReturn(java.util.Optional.of(mockCard));

        // Call the service method
        ResponseEntity<GenericResponse> response = cardService.deactivate(1L, 0, mockUser);

        // Verify the response and repository interactions
        //assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        //assertFalse(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("You dont have permissions to deactivate/activate", response.getBody().getMessage());
        assertNull(response.getBody().getData());
        verify(cardRepository).findById(anyLong());
        verify(cardRepository, never()).save(any(Card.class));
    }


    @Test
    void testDeleteCard_Unauthorized() {
        // Mocked objects
        User mockUser = new User();
        mockUser.setUserID(2L);
        mockUser.setRole(RoleEnum.MEMBER);

        Card mockCard = new Card();
        mockCard.setCardID(1L);
        mockCard.setName("Test Card");
        mockCard.setInsertedBy(User.builder().userID(1L).role(RoleEnum.ADMIN).build()); // Different user

        // Configure mock behavior
        when(cardRepository.findById(anyLong())).thenReturn(java.util.Optional.of(mockCard));

        // Call the service method
        ResponseEntity<GenericResponse> response = cardService.deleteCard(1L, mockUser);

        // Verify the response and repository interactions
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertFalse(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("You don't have permission to delete this card", response.getBody().getMessage());
        assertNull(response.getBody().getData());
        verify(cardRepository).findById(anyLong());
        verify(cardRepository, never()).deleteById(anyLong());
    }

    @Test
    void testGetCard_Unauthorized() {
        // Mocked objects
        User mockUser = new User();
        mockUser.setUserID(2L);
        mockUser.setRole(RoleEnum.MEMBER);

        Card mockCard = new Card();
        mockCard.setCardID(1L);
        mockCard.setName("Test Card");
        mockCard.setInsertedBy(User.builder().userID(1L).role(RoleEnum.ADMIN).build()); // Different user

        // Configure mock behavior
        when(cardRepository.findById(anyLong())).thenReturn(java.util.Optional.of(mockCard));

        // Call the service method
        ResponseEntity<GenericResponse> response = cardService.getCard(1L, mockUser);

        // Verify the response and repository interactions
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertFalse(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals("You don't have permission to view this card", response.getBody().getMessage());
        assertNull(response.getBody().getData());
        verify(cardRepository).findById(anyLong());
    }


}

