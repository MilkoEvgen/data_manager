package com.milko.user_provider.testcontainers;

import com.milko.user_provider.dto.input.AddressInputDto;
import com.milko.user_provider.dto.output.AddressOutputDto;
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
public class AddressRestControllerV1Test {
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


    @BeforeEach
    public void init(){
        url = "http://localhost:" + port + "/api/v1/addresses";
        addressInputDto = AddressInputDto.builder()
                .countryId(1)
                .address("address")
                .zipCode("zipCode")
                .city("city")
                .state("state")
                .build();
    }

    @AfterEach
    public void cleanDatabase(){
        jdbcTemplate.update("DELETE FROM person.addresses");
    }

    @Test
    void createAddressShouldReturnAddressOutputDto() {

        webTestClient.post().uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(addressInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.created").exists()
                .jsonPath("$.updated").exists()
                .jsonPath("$.archived").exists()
                .jsonPath("$.address").isEqualTo("address")
                .jsonPath("$.zipCode").isEqualTo("zipCode")
                .jsonPath("$.city").isEqualTo("city")
                .jsonPath("$.state").isEqualTo("state")
                .jsonPath("$.country.id").isEqualTo(1);
    }

    @Test
    void createAddressShouldThrowExceptionIfCountryNotExists() {
        addressInputDto.setCountryId(999);

        webTestClient.post().uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(addressInputDto)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateAddressShouldReturnAddressOutputDto() {

        AddressOutputDto savedAddress = webTestClient.post().uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(addressInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AddressOutputDto.class)
                .returnResult()
                .getResponseBody();

        UUID savedAddressId = savedAddress.getId();

        AddressInputDto addressToUpdate = AddressInputDto.builder()
                .address("Updated address")
                .build();

        webTestClient.patch().uri(url + "/" + savedAddressId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(addressToUpdate)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.created").exists()
                .jsonPath("$.updated").exists()
                .jsonPath("$.archived").exists()
                .jsonPath("$.address").isEqualTo("Updated address")
                .jsonPath("$.zipCode").isEqualTo("zipCode")
                .jsonPath("$.city").isEqualTo("city")
                .jsonPath("$.state").isEqualTo("state")
                .jsonPath("$.country.id").isEqualTo(1);
    }

    @Test
    void updateAddressShouldThrowExceptionIfCountryNotExists() {
        webTestClient.patch().uri(url + "/" + UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(addressInputDto)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void findAddressByIdShouldReturnAddressOutputDto() {

        AddressOutputDto savedAddress = webTestClient.post().uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(addressInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AddressOutputDto.class)
                .returnResult()
                .getResponseBody();

        UUID savedAddressId = savedAddress.getId();

        webTestClient.get().uri(url + "/" + savedAddressId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.created").exists()
                .jsonPath("$.updated").exists()
                .jsonPath("$.archived").exists()
                .jsonPath("$.address").isEqualTo("address")
                .jsonPath("$.zipCode").isEqualTo("zipCode")
                .jsonPath("$.city").isEqualTo("city")
                .jsonPath("$.state").isEqualTo("state")
                .jsonPath("$.country.id").isEqualTo(1);
    }

    @Test
    void findAddressByIdShouldThrowExceptionIfCountryNotExists() {
        webTestClient.get().uri(url + "/" + UUID.randomUUID().toString())
                .exchange()
                .expectStatus().isNotFound();
    }

}
