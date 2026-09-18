package dev.abdirahman.telegrambotspringauphonic.Response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AuphonicResponse(Data data) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Data(
            String uuid,
            int status,
            String status_string,
            List<OutputFile> output_files
    ) {}
    public record OutputFile(
            String format,
            String download_url
    ) {}

}