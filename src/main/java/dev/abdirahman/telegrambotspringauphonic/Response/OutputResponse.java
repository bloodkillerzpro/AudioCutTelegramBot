package dev.abdirahman.telegrambotspringauphonic.Response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.net.URI;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OutputResponse(String status, URI location, byte[] audio) {
}
