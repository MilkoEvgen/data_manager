package com.milko.user_provider.testcontainers;

import com.milko.user_provider.dto.input.AddressInputDto;
import com.milko.user_provider.dto.input.RegisterIndividualInputDto;
import com.milko.user_provider.dto.input.UpdateUserInputDto;
import com.milko.user_provider.dto.output.AddressOutputDto;
import com.milko.user_provider.dto.output.IndividualOutputDto;
import com.milko.user_provider.dto.output.MerchantMemberOutputDto;
import com.milko.user_provider.dto.output.MerchantOutputDto;
import com.milko.user_provider.model.Status;
import com.milko.user_provider.model.User;
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
public class UserRestControllerV1Test {
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
    private UpdateUserInputDto updateUserInputDto;


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
                .secretKey("secret key")
                .firstName("first name")
                .lastName("last name")
                .passportNumber("passport number")
                .phoneNumber("phone number")
                .email("email@gmail.com")
                .build();
        updateUserInputDto = UpdateUserInputDto.builder()
                .user(User.builder().firstName("updated first name").build())
                .reason("reason")
                .comment("comment")
                .build();
    }

    @AfterEach
    public void cleanDatabase() {
        jdbcTemplate.update("DELETE FROM person.profile_history");
        jdbcTemplate.update("DELETE FROM person.individuals");
        jdbcTemplate.update("DELETE FROM person.users");
        jdbcTemplate.update("DELETE FROM person.addresses");
    }

    @Test
    void updateUserIdShouldReturnUserOutputDto() {
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
        updateUserInputDto.setUserId(savedUserId);

        webTestClient.patch().uri(url + "users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateUserInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(savedUserId.toString())
                .jsonPath("$.secretKey").isEqualTo("secret key")
                .jsonPath("$.created").exists()
                .jsonPath("$.updated").exists()
                .jsonPath("$.firstName").isEqualTo("updated first name")
                .jsonPath("$.lastName").isEqualTo("last name")
                .jsonPath("$.verifiedAt").exists()
                .jsonPath("$.archivedAt").exists()
                .jsonPath("$.status").isEqualTo(Status.ACTIVE.toString())
                .jsonPath("$.address.id").isEqualTo(savedAddressId.toString())
                .jsonPath("$.address.address").isEqualTo("address");
    }

    @Test
    void updateUserShouldThrowExceptionIfUserNotExist() {
        updateUserInputDto.setUserId(UUID.randomUUID());

        webTestClient.patch().uri(url + "users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateUserInputDto)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getUserByIdShouldReturnUserOutputDto() {
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

        webTestClient.get().uri(url + "users" + "/" + savedUserId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(savedUserId.toString())
                .jsonPath("$.secretKey").isEqualTo("secret key")
                .jsonPath("$.created").exists()
                .jsonPath("$.updated").exists()
                .jsonPath("$.firstName").isEqualTo("first name")
                .jsonPath("$.lastName").isEqualTo("last name")
                .jsonPath("$.verifiedAt").exists()
                .jsonPath("$.archivedAt").exists()
                .jsonPath("$.status").isEqualTo(Status.ACTIVE.toString())
                .jsonPath("$.address.id").isEqualTo(savedAddressId.toString())
                .jsonPath("$.address.address").isEqualTo("address");
    }

    @Test
    void getUserByIdShouldThrowExceptionIfUserNotExist() {

        webTestClient.get().uri(url + "users" + "/" + UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deleteShouldReturnUUID() {
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

        webTestClient.delete().uri(url + "users" + "/" + savedUserId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isEqualTo(savedUserId.toString());
    }

    @Test
    void deleteShouldThrowExceptionIfUserNotExist() {

        webTestClient.delete().uri(url + "users" + "/" + UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound();
    }
}
