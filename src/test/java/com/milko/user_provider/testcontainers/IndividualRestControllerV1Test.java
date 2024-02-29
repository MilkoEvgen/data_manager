package com.milko.user_provider.testcontainers;

import com.milko.user_provider.dto.input.AddressInputDto;
import com.milko.user_provider.dto.input.IndividualInputDto;
import com.milko.user_provider.dto.input.RegisterIndividualInputDto;
import com.milko.user_provider.dto.input.UpdateIndividualDto;
import com.milko.user_provider.dto.output.AddressOutputDto;
import com.milko.user_provider.dto.output.IndividualOutputDto;
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
public class IndividualRestControllerV1Test {
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
    private UpdateIndividualDto updateIndividualDto;


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
        individualInputDto = RegisterIndividualInputDto.builder()
                .secretKey("secret key")
                .firstName("first name")
                .lastName("last name")
                .passportNumber("passport number")
                .phoneNumber("phone number")
                .email("email@gmail.com")
                .build();
        updateIndividualDto = UpdateIndividualDto.builder()
                .individualInputDto(IndividualInputDto.builder().email("updated@gmail.com").build())
                .reason("reason")
                .comment("comment")
                .build();
    }

    @AfterEach
    public void cleanDatabase(){
        jdbcTemplate.update("DELETE FROM person.profile_history");
        jdbcTemplate.update("DELETE FROM person.individuals");
        jdbcTemplate.update("DELETE FROM person.users");
        jdbcTemplate.update("DELETE FROM person.addresses");
    }

    @Test
    void createIndividualShouldReturnIndividualOutputDto() {
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

        webTestClient.post().uri(url + "individuals")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(individualInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.user.id").exists()
                .jsonPath("$.user.secretKey").isEqualTo("secret key")
                .jsonPath("$.user.firstName").isEqualTo("first name")
                .jsonPath("$.user.lastName").isEqualTo("last name")
                .jsonPath("$.created").exists()
                .jsonPath("$.updated").exists()
                .jsonPath("$.passportNumber").isEqualTo("passport number")
                .jsonPath("$.phoneNumber").isEqualTo("phone number")
                .jsonPath("$.email").isEqualTo("email@gmail.com")
                .jsonPath("$.verifiedAt").exists()
                .jsonPath("$.archivedAt").exists()
                .jsonPath("$.status").isEqualTo("ACTIVE");
    }

    @Test
    void createIndividualShouldThrowExceptionIfNotAllFieldsFilled() {
        individualInputDto.setFirstName(null);

        webTestClient.post().uri(url + "individuals")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(individualInputDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void updateIndividualsShouldReturnIndividualOutputDto() {
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

        UUID savedIndividualId = savedIndividual.getId();
        updateIndividualDto.setIndividualId(savedIndividualId);

        webTestClient.patch().uri(url + "individuals")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateIndividualDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.user.id").exists()
                .jsonPath("$.user.secretKey").isEqualTo("secret key")
                .jsonPath("$.user.firstName").isEqualTo("first name")
                .jsonPath("$.user.lastName").isEqualTo("last name")
                .jsonPath("$.created").exists()
                .jsonPath("$.updated").exists()
                .jsonPath("$.passportNumber").isEqualTo("passport number")
                .jsonPath("$.phoneNumber").isEqualTo("phone number")
                .jsonPath("$.email").isEqualTo("updated@gmail.com")
                .jsonPath("$.verifiedAt").exists()
                .jsonPath("$.archivedAt").exists()
                .jsonPath("$.status").isEqualTo("ACTIVE");
    }

    @Test
    void updateIndividualShouldThrowExceptionIfIndividualNotExists() {
        updateIndividualDto.setIndividualId(UUID.randomUUID());
        webTestClient.patch().uri(url + "individuals")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateIndividualDto)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void findIndividualByIdShouldReturnIndividualOutputDto() {
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

        UUID savedIndividualId = savedIndividual.getId();


        webTestClient.get().uri(url + "individuals" + "/" + savedIndividualId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.user.id").exists()
                .jsonPath("$.user.secretKey").isEqualTo("secret key")
                .jsonPath("$.user.firstName").isEqualTo("first name")
                .jsonPath("$.user.lastName").isEqualTo("last name")
                .jsonPath("$.created").exists()
                .jsonPath("$.updated").exists()
                .jsonPath("$.passportNumber").isEqualTo("passport number")
                .jsonPath("$.phoneNumber").isEqualTo("phone number")
                .jsonPath("$.email").isEqualTo("email@gmail.com")
                .jsonPath("$.verifiedAt").exists()
                .jsonPath("$.archivedAt").exists()
                .jsonPath("$.status").isEqualTo("ACTIVE");
    }

    @Test
    void findIndividualByIdShouldThrowExceptionIfIndividualNotExists() {
        webTestClient.get().uri(url + "individuals"+ "/" + UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deleteIndividualShouldReturnUUID() {
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

        UUID savedIndividualId = savedIndividual.getId();

        webTestClient.delete().uri(url + "individuals" + "/" + savedIndividualId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UUID.class)
                .consumeWith(response -> {
                    Assertions.assertEquals(response.getResponseBody(), savedIndividualId);
                });

    }

    @Test
    void deleteIndividualByIdShouldThrowExceptionIfIndividualNotExists() {
        webTestClient.delete().uri(url + "individuals"+ "/" + UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound();
    }
}
