package com.example.cards.util;

import com.example.cards.model.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterParams {
    private String name;
    private String color;
    private String status;
    private String createdDate;
}
