package com.milko.data_manager.testcontainers;

import com.milko.data_manager.dto.input.AddressInputDto;
import com.milko.data_manager.dto.input.MerchantInputDto;
import com.milko.data_manager.dto.input.MerchantMemberInvitationInputDto;
import com.milko.data_manager.dto.input.RegisterIndividualInputDto;
import com.milko.data_manager.dto.output.AddressOutputDto;
import com.milko.data_manager.dto.output.IndividualOutputDto;
import com.milko.data_manager.dto.output.MerchantMemberInvitationOutputDto;
import com.milko.data_manager.dto.output.MerchantOutputDto;
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
public class MerchantMemberInvitationRestControllerV1Test {
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
    private MerchantInputDto merchantInputDto;
    private MerchantMemberInvitationInputDto invitationInputDto;


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
        merchantInputDto = MerchantInputDto.builder()
                .companyName("company name")
                .companyId("company id")
                .email("email@gmail.com")
                .phoneNumber("phone number")
                .filled(true)
                .build();
        invitationInputDto = MerchantMemberInvitationInputDto.builder()
                .validForDays(1L)
                .firstName("first name")
                .lastName("last name")
                .email("email@gmail.com")
                .build();
    }

    @AfterEach
    public void cleanDatabase() {
        jdbcTemplate.update("DELETE FROM person.profile_history");
        jdbcTemplate.update("DELETE FROM person.merchant_members_invitations");
        jdbcTemplate.update("DELETE FROM person.individuals");
        jdbcTemplate.update("DELETE FROM person.merchants");
        jdbcTemplate.update("DELETE FROM person.users");
        jdbcTemplate.update("DELETE FROM person.addresses");
    }

    @Test
    void createInvitationShouldReturnInvitationOutputDto() {
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
        merchantInputDto.setCreatorId(savedUserId);

        MerchantOutputDto savedMerchant = webTestClient.post().uri(url + "merchants")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(merchantInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MerchantOutputDto.class)
                .returnResult()
                .getResponseBody();

        UUID savedMerchantId = savedMerchant.getId();
        invitationInputDto.setMerchantId(savedMerchantId);
        webTestClient.post().uri(url + "invite_member")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invitationInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.created").exists()
                .jsonPath("$.expires").exists()
                .jsonPath("$.merchant.id").isEqualTo(savedMerchantId.toString())
                .jsonPath("$.merchant.companyName").isEqualTo("company name")
                .jsonPath("$.merchant.companyId").isEqualTo("company id")
                .jsonPath("$.merchant.email").isEqualTo("email@gmail.com")
                .jsonPath("$.merchant.phoneNumber").isEqualTo("phone number")
                .jsonPath("$.firstName").isEqualTo("first name")
                .jsonPath("$.lastName").isEqualTo("last name")
                .jsonPath("$.email").isEqualTo("email@gmail.com")
                .jsonPath("$.status").isEqualTo("ACTIVE");
    }

    @Test
    void createInvitationShouldThrowExceptionIfMerchantNotExist() {
        invitationInputDto.setMerchantId(UUID.randomUUID());

        webTestClient.post().uri(url + "invite_member")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invitationInputDto)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void findAllByMerchantIdShouldReturnListOfInvitationOutputDto() {
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
        merchantInputDto.setCreatorId(savedUserId);

        MerchantOutputDto savedMerchant = webTestClient.post().uri(url + "merchants")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(merchantInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MerchantOutputDto.class)
                .returnResult()
                .getResponseBody();

        UUID savedMerchantId = savedMerchant.getId();
        invitationInputDto.setMerchantId(savedMerchantId);

        MerchantMemberInvitationOutputDto savedInvitation = webTestClient.post().uri(url + "invite_member")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invitationInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MerchantMemberInvitationOutputDto.class)
                .returnResult()
                .getResponseBody();

        webTestClient.get().uri(url + "invite_member" + "/" + savedMerchantId + "/find_all_by_merchant")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].id").exists()
                .jsonPath("$[0].created").exists()
                .jsonPath("$[0].expires").exists()
                .jsonPath("$[0].firstName").isEqualTo("first name")
                .jsonPath("$[0].lastName").isEqualTo("last name")
                .jsonPath("$[0].email").isEqualTo("email@gmail.com")
                .jsonPath("$[0].status").isEqualTo("ACTIVE");
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
        merchantInputDto.setCreatorId(savedUserId);

        MerchantOutputDto savedMerchant = webTestClient.post().uri(url + "merchants")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(merchantInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MerchantOutputDto.class)
                .returnResult()
                .getResponseBody();

        UUID savedMerchantId = savedMerchant.getId();
        invitationInputDto.setMerchantId(savedMerchantId);

        MerchantMemberInvitationOutputDto savedInvitation = webTestClient.post().uri(url + "invite_member")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invitationInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MerchantMemberInvitationOutputDto.class)
                .returnResult()
                .getResponseBody();

        UUID savedInvitationId = savedInvitation.getId();

        webTestClient.delete().uri(url + "invite_member" + "/" + savedInvitationId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isEqualTo(savedInvitationId.toString());
    }


}
