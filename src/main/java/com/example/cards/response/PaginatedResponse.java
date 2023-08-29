package com.example.cards.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PaginatedResponse<T> {
    boolean success;
    Integer statusCode;
    String message;
    List<T> data;
    Long size;
    Long totalItems;
    Long totalPages;
    Integer currentPage;
}
