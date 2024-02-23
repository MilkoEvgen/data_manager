package com.milko.user_provider.testcontainers;

import com.milko.user_provider.dto.input.AddressInputDto;
import com.milko.user_provider.dto.input.IndividualsInputDto;
import com.milko.user_provider.dto.input.UserInputDto;
import com.milko.user_provider.dto.output.AddressOutputDto;
import com.milko.user_provider.dto.output.IndividualsOutputDto;
import com.milko.user_provider.dto.output.UserOutputDto;
import com.milko.user_provider.model.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class IndividualsRestControllerV1Test {
    @Container
    static final PostgreSQLContainer<?> postgreSQLContainer;

    static {
        postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> String.format("r2dbc:postgresql://%s:%d/%s",
                postgreSQLContainer.getHost(),
                postgreSQLContainer.getFirstMappedPort(),
                postgreSQLContainer.getDatabaseName()));
        registry.add("spring.r2dbc.username", postgreSQLContainer::getUsername);
        registry.add("spring.r2dbc.password", postgreSQLContainer::getPassword);

        registry.add("spring.flyway.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.flyway.username", postgreSQLContainer::getUsername);
        registry.add("spring.flyway.password", postgreSQLContainer::getPassword);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String url;

    private AddressInputDto addressInputDto;
    private UserInputDto userInputDto;
    private IndividualsInputDto individualsInputDto;


    @BeforeEach
    public void init(){
        url = "http://localhost:" + port + "/api/v1/";
        addressInputDto = AddressInputDto.builder()
                .countryId(1)
                .address("address")
                .zipCode("zipCode")
                .city("city")
                .state("state")
                .build();
        userInputDto = UserInputDto.builder()
                .build();
        individualsInputDto = IndividualsInputDto.builder()
                .passportNumber("passportNumber")
                .phoneNumber("phoneNumber")
                .email("email")
                .build();
    }

    @AfterEach
    public void cleanDatabase(){
        jdbcTemplate.update("DELETE FROM person.individuals");
    }

    @Test
    void createIndividualsShouldReturnIndividualsOutputDto() {
        AddressOutputDto savedAddress = webTestClient.post().uri(url + "addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(addressInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AddressOutputDto.class)
                .returnResult()
                .getResponseBody();

        UUID savedAddressId = savedAddress.getId();
        userInputDto.setAddressId(savedAddressId);

        UserOutputDto savedUser = webTestClient.post().uri(url + "users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserOutputDto.class)
                .returnResult()
                .getResponseBody();
        UUID savedUserId = savedUser.getId();

        individualsInputDto.setUserId(savedUserId);

        webTestClient.post().uri(url + "individuals")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(individualsInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.user.id").isEqualTo(savedUserId.toString())
                .jsonPath("$.created").exists()
                .jsonPath("$.updated").exists()
                .jsonPath("$.passportNumber").isEqualTo("passportNumber")
                .jsonPath("$.phoneNumber").isEqualTo("phoneNumber")
                .jsonPath("$.email").isEqualTo("email")
                .jsonPath("$.verifiedAt").exists()
                .jsonPath("$.archivedAt").exists()
                .jsonPath("$.status").isEqualTo("ACTIVE");
    }

    @Test
    void createIndividualsShouldThrowExceptionIfUserNotExists() {
        individualsInputDto.setUserId(UUID.randomUUID());

        webTestClient.post().uri(url + "individuals")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(individualsInputDto)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateIndividualsShouldReturnIndividualsOutputDto() {
        AddressOutputDto savedAddress = webTestClient.post().uri(url + "addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(addressInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AddressOutputDto.class)
                .returnResult()
                .getResponseBody();

        UUID savedAddressId = savedAddress.getId();
        userInputDto.setAddressId(savedAddressId);

        UserOutputDto savedUser = webTestClient.post().uri(url + "users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserOutputDto.class)
                .returnResult()
                .getResponseBody();
        UUID savedUserId = savedUser.getId();
        individualsInputDto.setUserId(savedUserId);

        IndividualsOutputDto savedIndividual = webTestClient.post().uri(url + "individuals")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(individualsInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(IndividualsOutputDto.class)
                .returnResult()
                .getResponseBody();
        UUID savedIndividualId = savedIndividual.getId();

        IndividualsInputDto individualToUpdate = IndividualsInputDto.builder()
                .email("updated email")
                .build();

        webTestClient.patch().uri(url + "individuals" + "/" + savedIndividualId + "?reason=reason&comment=comment")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(individualToUpdate)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.user.id").isEqualTo(savedUserId.toString())
                .jsonPath("$.created").exists()
                .jsonPath("$.updated").exists()
                .jsonPath("$.passportNumber").isEqualTo("passportNumber")
                .jsonPath("$.phoneNumber").isEqualTo("phoneNumber")
                .jsonPath("$.email").isEqualTo("updated email")
                .jsonPath("$.verifiedAt").exists()
                .jsonPath("$.archivedAt").exists()
                .jsonPath("$.status").isEqualTo("ACTIVE");
    }

    @Test
    void updateIndividualsShouldThrowExceptionIfIndividualNotExists() {
        IndividualsInputDto individualToUpdate = IndividualsInputDto.builder()
                .email("updated email")
                .build();

        webTestClient.patch().uri(url + "individuals"+ "/" + UUID.randomUUID().toString() + "?reason=reason&comment=comment")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(individualToUpdate)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void findIndividualsByIdShouldReturnIndividualsOutputDto() {
        AddressOutputDto savedAddress = webTestClient.post().uri(url + "addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(addressInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AddressOutputDto.class)
                .returnResult()
                .getResponseBody();

        UUID savedAddressId = savedAddress.getId();
        userInputDto.setAddressId(savedAddressId);

        UserOutputDto savedUser = webTestClient.post().uri(url + "users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserOutputDto.class)
                .returnResult()
                .getResponseBody();
        UUID savedUserId = savedUser.getId();
        individualsInputDto.setUserId(savedUserId);

        IndividualsOutputDto savedIndividual = webTestClient.post().uri(url + "individuals")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(individualsInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(IndividualsOutputDto.class)
                .returnResult()
                .getResponseBody();
        UUID savedIndividualId = savedIndividual.getId();


        webTestClient.get().uri(url + "individuals" + "/" + savedIndividualId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.user.id").isEqualTo(savedUserId.toString())
                .jsonPath("$.created").exists()
                .jsonPath("$.updated").exists()
                .jsonPath("$.passportNumber").isEqualTo("passportNumber")
                .jsonPath("$.phoneNumber").isEqualTo("phoneNumber")
                .jsonPath("$.email").isEqualTo("email")
                .jsonPath("$.verifiedAt").exists()
                .jsonPath("$.archivedAt").exists()
                .jsonPath("$.status").isEqualTo("ACTIVE");
    }

    @Test
    void findIndividualsByIdShouldThrowExceptionIfIndividualNotExists() {
        webTestClient.get().uri(url + "individuals"+ "/" + UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound();
    }

//    @Test
//    void deleteIndividualsShouldReturnInteger() {
//        AddressOutputDto savedAddress = webTestClient.post().uri(url + "addresses")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(addressInputDto)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(AddressOutputDto.class)
//                .returnResult()
//                .getResponseBody();
//
//        UUID savedAddressId = savedAddress.getId();
//        userInputDto.setAddressId(savedAddressId);
//
//        UserOutputDto savedUser = webTestClient.post().uri(url + "users")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(userInputDto)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(UserOutputDto.class)
//                .returnResult()
//                .getResponseBody();
//        UUID savedUserId = savedUser.getId();
//        individualsInputDto.setUserId(savedUserId);
//
//        IndividualsOutputDto savedIndividual = webTestClient.post().uri(url + "individuals")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(individualsInputDto)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(IndividualsOutputDto.class)
//                .returnResult()
//                .getResponseBody();
//        UUID savedIndividualId = savedIndividual.getId();
//
//        webTestClient.delete().uri(url + "individuals" + "/" + savedIndividualId)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(Boolean.class)
//                .consumeWith(response -> {
//                    Assertions.assertTrue(response.getResponseBody());
//                });
//
//    }
}
