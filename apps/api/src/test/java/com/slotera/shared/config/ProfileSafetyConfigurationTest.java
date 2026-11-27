package com.slotera.shared.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class ProfileSafetyConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(ProfileSafetyConfiguration.class);

    @Test
    void rejectsProductionCombinedWithDevelopmentOrTestRegardlessOfOrder() {
        for (String profiles : List.of("prod,dev", "dev,prod", "prod,test", "test,prod")) {
            runner.withPropertyValues("spring.profiles.active=" + profiles)
                    .run(context -> assertThat(context).hasFailed());
        }
    }

    @Test
    void permitsIndividualProfiles() {
        for (String profile : List.of("prod", "dev", "test")) {
            runner.withPropertyValues("spring.profiles.active=" + profile)
                    .run(context -> assertThat(context).hasNotFailed());
        }
    }
}
