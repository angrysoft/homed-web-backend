package ovh.angrysoft.homedbackend.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth")
public record UsersList(String users) {
}

