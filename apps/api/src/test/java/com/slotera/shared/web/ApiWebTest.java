package com.slotera.shared.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(ApiWebTest.FixtureController.class)
@Import({ApiExceptionHandler.class, ApiWebTest.FixtureController.class})
@ExtendWith(OutputCaptureExtension.class)
class ApiWebTest {

    @Autowired
    MockMvc mvc;

    @Test
    void validatesRequestWithoutReturningSubmittedValue() throws Exception {
        mvc.perform(post("/fixture").contentType(MediaType.APPLICATION_JSON).content("{\"quantity\":3}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.quantity").value(3));
        mvc.perform(post("/fixture").contentType(MediaType.APPLICATION_JSON).content("{\"quantity\":-123}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.type").value("/problems/validation-error"))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.instance").value("/fixture"))
                .andExpect(jsonPath("$.fields[0].field").value("quantity"))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("-123"))));
        mvc.perform(post("/fixture").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void validatesDeveloperExercise(CapturedOutput output) throws Exception {
        mvc.perform(post("/fixture/nombre").contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer SLOT_FAKE_TOKEN")
                        .content("{\"nombre\":\"Carlos Mendoza\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Carlos Mendoza"));
        mvc.perform(post("/fixture/nombre").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"   \"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fields[0].field").value("nombre"));
        assertThat(output.getAll()).doesNotContain("SLOT_FAKE_TOKEN", "Carlos Mendoza");
    }

    @Test
    void returnsSafeErrorsAndPreservesHttpStatuses(CapturedOutput output) throws Exception {
        mvc.perform(post("/fixture").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
        mvc.perform(get("/missing"))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("NOT_FOUND"));
        mvc.perform(get("/fixture"))
                .andExpect(status().isMethodNotAllowed()).andExpect(header().string("Allow", "POST"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value("METHOD_NOT_ALLOWED"));
        mvc.perform(get("/fixture/fail"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("secret-fixture"))));
        mvc.perform(post("/fixture").contentType(MediaType.TEXT_PLAIN).content("x"))
                .andExpect(status().isUnsupportedMediaType());
        assertThat(output.getAll()).doesNotContain("secret-fixture");
    }

    @Test
    void usesStablePaginationAndRejectsInvalidParameters() throws Exception {
        mvc.perform(get("/fixture/items").param("size", "2").param("sort", "name,asc"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
        mvc.perform(get("/fixture/items").param("size", "2").param("page", "1").param("sort", "name,asc"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(3));
        for (String query : List.of("page=-1", "size=0", "size=101", "page=abc", "sort=secret,asc", "sort=name,sideways", "page=0&page=1")) {
            mvc.perform(get("/fixture/items?" + query)).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
        }
    }

    record FixtureRequest(@NotNull @Min(1) Integer quantity) { }
    record FixtureItem(int id, String name) { }

    @RestController
    static class FixtureController {
        @PostMapping("/fixture")
        FixtureRequest create(@Valid @RequestBody FixtureRequest request) {
            return request;
        }

        @PostMapping("/fixture/nombre")
        SolicitudPrueba validateName(@Valid @RequestBody SolicitudPrueba request) {
            return request;
        }

        @GetMapping("/fixture/fail")
        void fail() {
            throw new IllegalStateException("secret-fixture");
        }

        @GetMapping("/fixture/items")
        List<FixtureItem> list(@RequestParam MultiValueMap<String, String> params) {
            PageRequest request = Pagination.parse(params, Set.of("id", "name"));
            Comparator<FixtureItem> comparator = (left, right) -> {
                for (Sort.Order order : request.getSort()) {
                    int result = order.getProperty().equals("name")
                            ? left.name().compareTo(right.name()) : Integer.compare(left.id(), right.id());
                    if (result != 0) {
                        return order.isAscending() ? result : -result;
                    }
                }
                return 0;
            };
            List<FixtureItem> items = List.of(new FixtureItem(2, "A"), new FixtureItem(3, "B"), new FixtureItem(1, "A"));
            return items.stream().sorted(comparator).skip(request.getOffset()).limit(request.getPageSize()).toList();
        }
    }
}
