package com.auth.dto;

import java.util.Map;

public record MeResponse(
        Long userId,
        String email,
        Map<String, String> workspaces
) {}
