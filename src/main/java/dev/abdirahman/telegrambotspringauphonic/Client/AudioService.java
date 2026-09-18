package dev.abdirahman.telegrambotspringauphonic.Client;

import dev.abdirahman.telegrambotspringauphonic.Response.AuphonicResponse;
import dev.abdirahman.telegrambotspringauphonic.Response.OutputResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class AudioService {


    @Value("${auphonic.api.key}")
    private String apiKey;

    @Value("${auphonic.preset}")
    private String preset;

    @Value("${telegram.bot.token}")
    private String token;


    public URI sendToAuphonic(String title, byte[] audio, Long chatId) throws Exception {
        RestClient restClient = RestClient.create("https://auphonic.com/api");
        RestClient restTemplate = RestClient.create();
        ByteArrayResource resource = byteRessource(audio, title);

        MultiValueMap<String, Object> multipartData = createMultiform(resource.getFilename(), resource);

        //here i try to upload
        AuphonicResponse auphonicResponse = auphonicPostRequest(multipartData, restClient, "/simple/productions.json");
        System.out.println("Auphonic Response: " + auphonicResponse.data().uuid());
        System.out.println("hej");

        AuphonicResponse auphonicResponse2 = waitForProduction(auphonicResponse.data().uuid(), restClient);


        AuphonicResponse auphonicResponse3 = restClient.get()
                .uri("/production/jHnaabHp5sT2LMDGxS437R.json")
                .header("Authorization","bearer " + apiKey)
                .retrieve()
                .body(AuphonicResponse.class);

        String downloadUrl = auphonicResponse2.data().output_files().get(0).download_url() +"?bearer_token=" + apiKey;
        List<AuphonicResponse.OutputFile> output_files = auphonicResponse2.data().output_files();
        for(int i = 0; i < output_files.size(); i++) {
            System.out.println("Format: " + output_files.get(i).format());
        }


        System.out.println("Download URL: " + downloadUrl);
        restClient.get()
                .uri(downloadUrl)
                .exchange((request, response) -> {

                    System.out.println("STATUS: " + response.getStatusCode());
                    System.out.println("HEADERS: " + response.getHeaders());

                    return null;
                });
        OutputResponse file = restTemplate.get().uri(downloadUrl).exchange((request, response) ->{
            return new OutputResponse(response.getStatusCode().toString(), URI.create(response.getHeaders().getLocation().toString()), response.getBody().readAllBytes());
        });

        ResponseEntity respon = restTemplate.get().uri(downloadUrl).exchange((request, respond) -> {
            System.out.println("Status code: " + respond.getStatusCode());
            System.out.println("Header: " + respond.getHeaders());
            System.out.println("Body: " + respond.getBody().readAllBytes().length);

            return ResponseEntity.status(respond.getStatusCode()).headers(respond.getHeaders()).body(respond.getBody());
        });




        URI location = file.location();
        byte[] ad = restClient.get()
                .uri(location)
                .retrieve()
                .body(byte[].class);
        System.out.println("Ad: " + ad.length);
        System.out.println("Location: " + location);

        byte[] output = ad;
        ByteArrayResource byteRessource = byteRessource(output, title);
        MultiValueMap<String, Object> multipartData2 = new LinkedMultiValueMap<>();
        multipartData2.add("chat_id", chatId);
        multipartData2.add("title", title);
        multipartData2.add("audio", byteRessource);

        System.out.println(multipartData2.toString());


        auphonicPostRequest2(multipartData2,restTemplate, "https://api.telegram.org/bot" + token + "/sendAudio");

        return location;


        /*
        for (int i = 0; i<120; i++) {
            AuphonicResponse auphonicResponse2 = restClient.get()
                    .uri("/production/"+auphonicResponse.data().uuid()+".json")
                    .header("Authorization","bearer " + apiKey)
                    .retrieve()
                    .body(AuphonicResponse.class);



            int getStatus = Integer.parseInt(auphonicResponse.data().uuid());
            System.out.println(getStatus);
            String downloadUrl = auphonicResponse2.data().output_files().get(0).download_url();
            System.out.println("Download url (fake): "+downloadUrl);
            System.out.println(getStatus);
            if (getStatus == 3) {
                downloadUrl = auphonicResponse2.data().output_files().get(0).download_url();
                System.out.println("Download url (real): "+downloadUrl);
                Thread.sleep(5000);

            }
            throw new RuntimeException("Timed out waiting for Auphonic");

        }*/
    }

    public MultiValueMap<String, Object> createMultiform(String title, ByteArrayResource audio) {
        MultiValueMap<String, Object> multipartData =
                new LinkedMultiValueMap<>();
        multipartData.add("preset", preset);
        multipartData.add("title", title);
        multipartData.add("action", "start");
        multipartData.add("input_file", audio);

        return multipartData;
    }
    public AuphonicResponse auphonicPostRequest2(MultiValueMap<String, Object> multipartData, RestClient restClient, String url) {
        return restClient.post()
                .uri(url)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(multipartData)
                .retrieve()
                .body(AuphonicResponse.class);
    }

    public AuphonicResponse auphonicPostRequest(MultiValueMap<String, Object> multipartData, RestClient restClient, String url) {
        return restClient.post()
                .uri(url)
                .header("Authorization","bearer " + apiKey)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(multipartData)
                .retrieve()
                .body(AuphonicResponse.class);
    }

    public ByteArrayResource byteRessource(byte[] audio, String title) {
        return new ByteArrayResource(audio) {
            @Override
            public String getFilename() {
                return title;
            }
        };
    }

    private AuphonicResponse waitForProduction(
            String uuid,
            RestClient restClient) throws InterruptedException {

        for (int i = 0; i < 120; i++) {

            AuphonicResponse response = restClient.get()
                    .uri("/production/" + uuid + ".json")
                    .header("Authorization", "bearer " + apiKey)
                    .retrieve()
                    .body(AuphonicResponse.class);

            System.out.println(
                    "Auphonic status: " + response.data().status()
            );

            // Production finished
            if (response.data().status() == 3) {
                return response;
            }

            // Wait 5 seconds before checking again
            Thread.sleep(5000);
        }

        throw new RuntimeException(
                "Timed out waiting for Auphonic production: " + uuid
        );
    }

}
