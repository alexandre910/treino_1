
package com.app.treino.dto;

import java.util.List;

public record WorkoutXExercicio(
        String id,
        String name,
        String target,
        String description,
        List<String> instructions,
        String gifUrl
) {}
