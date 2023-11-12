package tech.konso.toolsmanagement.system.commons.configs.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "integration.file-storage-api")
public class FileStorageProperties {
    private String url;
    private Integer webClientBufferMegabytes;
}
