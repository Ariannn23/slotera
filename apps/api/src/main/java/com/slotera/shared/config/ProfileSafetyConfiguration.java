package com.slotera.shared.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration(proxyBeanMethods = false)
@Profile("prod & (dev | test)")
class ProfileSafetyConfiguration {

    ProfileSafetyConfiguration() {
        throw new IllegalStateException("El perfil prod no se puede combinar con dev ni test.");
    }
}
