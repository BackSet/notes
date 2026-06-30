package com.notes.backend.bootstrap;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(InitialAdminProperties.class)
public class BootstrapConfig {
}
