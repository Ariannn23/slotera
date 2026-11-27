package com.slotera;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.core.Violations;

class ModularityTest {

    @Test
    void verifiesApplicationModules() {
        ApplicationModules modules = ApplicationModules.of(SloteraApplication.class);
        assertThat(modules.stream()).isNotEmpty();
        modules.verify();
    }

    @Test
    void detectsCyclesInRepresentativeFixtures() {
        ApplicationModules valid = ApplicationModules.of("fixtures.valid", location -> true);
        ApplicationModules invalid = ApplicationModules.of("fixtures.invalid", location -> true);
        assertThat(valid.stream()).hasSize(2);
        assertThat(invalid.stream()).hasSize(2);
        valid.verify();
        assertThatThrownBy(invalid::verify).isInstanceOf(Violations.class);
    }
}
