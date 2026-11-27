package com.slotera.shared.web;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.MultiValueMap;

public final class Pagination {

    private Pagination() {
    }

    public static PageRequest parse(MultiValueMap<String, String> parameters, Set<String> allowedSortFields) {
        int page = integer(parameters, "page", 0);
        int size = integer(parameters, "size", 20);
        if (page < 0 || size < 1 || size > 100) {
            throw new InvalidRequestException("Los parámetros de paginación están fuera de rango.");
        }

        String sortValue = single(parameters, "sort", "id,asc");
        String[] parts = sortValue.split(",", -1);
        if (parts.length != 2 || !allowedSortFields.contains(parts[0]) || !allowedSortFields.contains("id")) {
            throw new InvalidRequestException("El campo de orden no está permitido.");
        }
        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(parts[1]);
        } catch (IllegalArgumentException exception) {
            throw new InvalidRequestException("La dirección de orden no es válida.");
        }
        Sort sort = Sort.by(direction, parts[0]);
        if (!parts[0].equals("id")) {
            sort = sort.and(Sort.by(Sort.Direction.ASC, "id"));
        }
        return PageRequest.of(page, size, sort);
    }

    private static int integer(MultiValueMap<String, String> parameters, String name, int defaultValue) {
        String value = single(parameters, name, Integer.toString(defaultValue));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new InvalidRequestException("El parámetro " + name + " debe ser un entero.");
        }
    }

    private static String single(MultiValueMap<String, String> parameters, String name, String defaultValue) {
        List<String> values = parameters.get(name);
        if (values == null) {
            return defaultValue;
        }
        if (values.size() != 1 || values.get(0) == null) {
            throw new InvalidRequestException("El parámetro " + name + " debe aparecer una sola vez.");
        }
        return values.get(0);
    }
}
