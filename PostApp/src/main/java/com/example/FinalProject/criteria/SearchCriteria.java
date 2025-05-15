package com.example.FinalProject.criteria;

import java.time.LocalDate;
import java.util.List;

public record SearchCriteria(
        String keywords,
        List<String> tags,
        String authorId,
        LocalDate startDate,
        LocalDate endDate) {
}