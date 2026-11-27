package com.slotera.shared.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
        "DATABASE_URL=jdbc:postgresql://localhost:55432/slotera",
        "DATABASE_USER=slotera",
        "DATABASE_PASSWORD=slotera_local"
})
@AutoConfigureMockMvc
@ActiveProfiles("prod")
class ProductionProfileTest {

    @Autowired
    MockMvc mvc;

    @Test
    void disablesOpenApiAndPreservesHealth() throws Exception {
        mvc.perform(get("/v3/api-docs")).andExpect(status().isNotFound());
        mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isNotFound());
        mvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }
}
