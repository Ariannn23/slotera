package com.slotera.shared.web;

import jakarta.validation.constraints.NotBlank;

public record SolicitudPrueba(
        @NotBlank(message = "El campo 'nombre' es obligatorio y no puede estar vacío")
        String nombre
) { }
