package com.example.flywalkswim.configuration;

import com.example.flywalkswim.model.Organisms;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({Organisms.class})
public class Configurator {

}
