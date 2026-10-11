package com.software.cloudtunnelgateway.config;

import com.software.cloudtunnelgateway.tunnel.StreamRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfiguration {

    @Bean
    public StreamRegistry streamRegistry() {
        return new StreamRegistry();
    }
}