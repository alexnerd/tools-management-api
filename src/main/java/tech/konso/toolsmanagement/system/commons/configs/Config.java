package tech.konso.toolsmanagement.system.commons.configs;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import tech.konso.toolsmanagement.system.commons.configs.properties.FileStorageProperties;

@Configuration
@EnableConfigurationProperties({FileStorageProperties.class})
public class Config {
    @Bean
    @Qualifier("integration-file-storage-api")
    public WebClient getFileStorageWebClient(WebClient.Builder webclientBuilder, FileStorageProperties properties) {
        return webclientBuilder
                .baseUrl(properties.getUrl())
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(properties.getWebClientBufferMegabytes()
                                        * 1024 * 1024))
                        .build())
                .build();
    }
}
