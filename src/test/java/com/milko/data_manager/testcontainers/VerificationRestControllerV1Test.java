package com.milko.data_manager.testcontainers;

import com.milko.data_manager.dto.input.AddressInputDto;
import com.milko.data_manager.dto.input.RegisterIndividualInputDto;
import com.milko.data_manager.dto.input.VerificationStatusInputDto;
import com.milko.data_manager.dto.output.AddressOutputDto;
import com.milko.data_manager.dto.output.IndividualOutputDto;
import com.milko.data_manager.dto.output.VerificationStatusOutputDto;
import com.milko.data_manager.model.StatusOfVerification;
import org.junit.jupiter.api.AfterEach;
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
public class VerificationRestControllerV1Test {
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
    private RegisterIndividualInputDto individualInputDto;
    private VerificationStatusInputDto verificationStatusInputDto;


    @BeforeEach
    public void init() {
        url = "http://localhost:" + port + "/api/v1/";
        addressInputDto = AddressInputDto.builder()
                .countryId(1)
                .address("address")
                .zipCode("zipCode")
                .city("city")
                .state("state")
                .build();
        individualInputDto = RegisterIndividualInputDto.builder()
                .authServiceId(UUID.fromString("7866b462-a8b9-4b64-8af6-eb9a8e474d09"))
                .firstName("first name")
                .lastName("last name")
                .passportNumber("passport number")
                .phoneNumber("phone number")
                .email("email@gmail.com")
                .build();
        verificationStatusInputDto = VerificationStatusInputDto.builder()
                .profileType("USER")
                .details("details")
                .build();
    }

    @AfterEach
    public void cleanDatabase() {
        jdbcTemplate.update("DELETE FROM person.profile_history");
        jdbcTemplate.update("DELETE FROM person.verification_statuses");
        jdbcTemplate.update("DELETE FROM person.individuals");
        jdbcTemplate.update("DELETE FROM person.users");
        jdbcTemplate.update("DELETE FROM person.addresses");
    }

    @Test
    void createVerificationStatusShouldReturnVerificationStatus() {
        AddressOutputDto savedAddress = webTestClient.post().uri(url + "addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(addressInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AddressOutputDto.class)
                .returnResult()
                .getResponseBody();

        UUID savedAddressId = savedAddress.getId();
        individualInputDto.setAddressId(savedAddressId);

        IndividualOutputDto savedIndividual = webTestClient.post().uri(url + "individuals")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(individualInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(IndividualOutputDto.class)
                .returnResult()
                .getResponseBody();

        UUID savedUserId = savedIndividual.getUser().getId();
        verificationStatusInputDto.setProfileId(savedUserId);

        webTestClient.post().uri(url + "verification_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(verificationStatusInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.created").exists()
                .jsonPath("$.updated").exists()
                .jsonPath("$.profile.id").isEqualTo(savedUserId.toString())
                .jsonPath("$.profile.firstName").isEqualTo("first name")
                .jsonPath("$.profile.lastName").isEqualTo("last name")
                .jsonPath("$.profileType").isEqualTo("USER")
                .jsonPath("$.details").isEqualTo("details")
                .jsonPath("$.verificationStatus").isEqualTo(StatusOfVerification.NOT_VERIFIED.toString());
    }

    @Test
    void requestVerificationShouldReturnVerificationStatus() {
        AddressOutputDto savedAddress = webTestClient.post().uri(url + "addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(addressInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AddressOutputDto.class)
                .returnResult()
                .getResponseBody();

        UUID savedAddressId = savedAddress.getId();
        individualInputDto.setAddressId(savedAddressId);

        IndividualOutputDto savedIndividual = webTestClient.post().uri(url + "individuals")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(individualInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(IndividualOutputDto.class)
                .returnResult()
                .getResponseBody();

        UUID savedUserId = savedIndividual.getUser().getId();
        verificationStatusInputDto.setProfileId(savedUserId);

        VerificationStatusOutputDto savedVerification = webTestClient.post().uri(url + "verification_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(verificationStatusInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(VerificationStatusOutputDto.class)
                .returnResult()
                .getResponseBody();

        UUID savedStatusId = savedVerification.getId();

        webTestClient.post().uri(url + "verification_statuses" + "/" + savedStatusId + "/" + "request")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.created").exists()
                .jsonPath("$.updated").exists()
                .jsonPath("$.profile.id").isEqualTo(savedUserId.toString())
                .jsonPath("$.profile.firstName").isEqualTo("first name")
                .jsonPath("$.profile.lastName").isEqualTo("last name")
                .jsonPath("$.profileType").isEqualTo("USER")
                .jsonPath("$.details").isEqualTo("details")
                .jsonPath("$.verificationStatus").isEqualTo(StatusOfVerification.VERIFICATION_REQUESTED.toString());
    }

    @Test
    void requestVerificationShouldThrowExceptionIfVerificationStatusNotExists() {
        webTestClient.post().uri(url + "verification_statuses" + "/" + UUID.randomUUID() + "/" + "request")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void verifyShouldReturnVerificationStatus() {
        AddressOutputDto savedAddress = webTestClient.post().uri(url + "addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(addressInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AddressOutputDto.class)
                .returnResult()
                .getResponseBody();

        UUID savedAddressId = savedAddress.getId();
        individualInputDto.setAddressId(savedAddressId);

        IndividualOutputDto savedIndividual = webTestClient.post().uri(url + "individuals")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(individualInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(IndividualOutputDto.class)
                .returnResult()
                .getResponseBody();

        UUID savedUserId = savedIndividual.getUser().getId();
        verificationStatusInputDto.setProfileId(savedUserId);

        VerificationStatusOutputDto savedVerification = webTestClient.post().uri(url + "verification_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(verificationStatusInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(VerificationStatusOutputDto.class)
                .returnResult()
                .getResponseBody();

        UUID savedStatusId = savedVerification.getId();

        webTestClient.post().uri(url + "verification_statuses" + "/" + savedStatusId + "/" + "verify")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.created").exists()
                .jsonPath("$.updated").exists()
                .jsonPath("$.profile.id").isEqualTo(savedUserId.toString())
                .jsonPath("$.profile.firstName").isEqualTo("first name")
                .jsonPath("$.profile.lastName").isEqualTo("last name")
                .jsonPath("$.profileType").isEqualTo("USER")
                .jsonPath("$.details").isEqualTo("details")
                .jsonPath("$.verificationStatus").isEqualTo(StatusOfVerification.VERIFIED.toString());
    }

    @Test
    void verifyShouldThrowExceptionIfVerificationStatusNotExists() {
        webTestClient.post().uri(url + "verification_statuses" + "/" + UUID.randomUUID() + "/" + "verify")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void findVerificationStatusByIdShouldReturnVerificationStatus() {
        AddressOutputDto savedAddress = webTestClient.post().uri(url + "addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(addressInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AddressOutputDto.class)
                .returnResult()
                .getResponseBody();

        UUID savedAddressId = savedAddress.getId();
        individualInputDto.setAddressId(savedAddressId);

        IndividualOutputDto savedIndividual = webTestClient.post().uri(url + "individuals")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(individualInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(IndividualOutputDto.class)
                .returnResult()
                .getResponseBody();

        UUID savedUserId = savedIndividual.getUser().getId();
        verificationStatusInputDto.setProfileId(savedUserId);

        VerificationStatusOutputDto savedVerification = webTestClient.post().uri(url + "verification_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(verificationStatusInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(VerificationStatusOutputDto.class)
                .returnResult()
                .getResponseBody();

        UUID savedStatusId = savedVerification.getId();

        webTestClient.get().uri(url + "verification_statuses" + "/" + savedStatusId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.created").exists()
                .jsonPath("$.updated").exists()
                .jsonPath("$.profile.id").isEqualTo(savedUserId.toString())
                .jsonPath("$.profile.firstName").isEqualTo("first name")
                .jsonPath("$.profile.lastName").isEqualTo("last name")
                .jsonPath("$.profileType").isEqualTo("USER")
                .jsonPath("$.details").isEqualTo("details")
                .jsonPath("$.verificationStatus").isEqualTo(StatusOfVerification.NOT_VERIFIED.toString());
    }

    @Test
    void findVerificationStatusByIdShouldThrowExceptionIfVerificationStatusNotExists() {
        webTestClient.get().uri(url + "verification_statuses" + "/" + UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound();
    }
}
