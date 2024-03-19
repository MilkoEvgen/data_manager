package com.milko.user_provider.testcontainers;

import com.milko.user_provider.dto.input.AddressInputDto;
import com.milko.user_provider.dto.input.RegisterIndividualInputDto;
import com.milko.user_provider.dto.output.AddressOutputDto;
import com.milko.user_provider.dto.output.IndividualOutputDto;
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
public class ProfileHistoryRestControllerV1Test {
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
    }

    @AfterEach
    public void cleanDatabase() {
        jdbcTemplate.update("DELETE FROM person.profile_history");
        jdbcTemplate.update("DELETE FROM person.individuals");
        jdbcTemplate.update("DELETE FROM person.users");
        jdbcTemplate.update("DELETE FROM person.addresses");
    }

    @Test
    void getAllHistoryByUserIdShouldReturnHistoryOutputDto() {
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

        webTestClient.get().uri(url + "history" + "/" + savedUserId + "/" + "profile")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].id").exists()
                .jsonPath("$[0].created").exists()
                .jsonPath("$[0].user.id").isEqualTo(savedUserId.toString())
                .jsonPath("$[0].user.firstName").isEqualTo("first name")
                .jsonPath("$[0].user.lastName").isEqualTo("last name")
                .jsonPath("$[0].profileType").isEqualTo("INDIVIDUAL")
                .jsonPath("$[0].reason").isEqualTo("REGISTER")
                .jsonPath("$[0].comment").isEqualTo("User register like individual");
    }
}
