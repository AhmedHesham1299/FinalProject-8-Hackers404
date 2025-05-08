package com.example.FinalProject.dtos;

import java.util.List;

public record PostUpdateRequestDto(
        String title,
        String content,
        List<String> tags) {
}