package com.milko.user_provider.testcontainers;

import com.milko.user_provider.dto.input.*;
import com.milko.user_provider.dto.output.AddressOutputDto;
import com.milko.user_provider.dto.output.IndividualOutputDto;
import com.milko.user_provider.dto.output.MerchantMemberOutputDto;
import com.milko.user_provider.dto.output.MerchantOutputDto;
import com.milko.user_provider.model.MerchantMember;
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
public class MerchantMemberRestControllerV1Test {
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
    private RegisterMerchantMemberInputDto memberInputDto;
    private UpdateMerchantMemberDto updateMerchantMemberDto;


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
        memberInputDto = RegisterMerchantMemberInputDto.builder()
                .authServiceId(UUID.fromString("7866b462-a8b9-4b64-8af6-eb9a8e474d09"))
                .firstName("first name")
                .lastName("last name")
                .memberRole("member role")
                .build();
        updateMerchantMemberDto = UpdateMerchantMemberDto.builder()
                .merchantMember(MerchantMember.builder().memberRole("updated role").build())
                .reason("reason")
                .comment("comment")
                .build();
    }

    @AfterEach
    public void cleanDatabase() {
        jdbcTemplate.update("DELETE FROM person.profile_history");
        jdbcTemplate.update("DELETE FROM person.merchant_members");
        jdbcTemplate.update("DELETE FROM person.individuals");
        jdbcTemplate.update("DELETE FROM person.merchants");
        jdbcTemplate.update("DELETE FROM person.users");
        jdbcTemplate.update("DELETE FROM person.addresses");
    }

    @Test
    void createMerchantMemberShouldReturnMerchantMemberOutputDto() {
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
        memberInputDto.setAddressId(savedAddressId);
        memberInputDto.setMerchantId(savedMerchantId);
        webTestClient.post().uri(url + "merchant_members")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(memberInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.user.id").exists()
                .jsonPath("$.user.firstName").isEqualTo("first name")
                .jsonPath("$.user.lastName").isEqualTo("last name")
                .jsonPath("$.created").exists()
                .jsonPath("$.updated").exists()
                .jsonPath("$.merchant.id").isEqualTo(savedMerchantId.toString())
                .jsonPath("$.merchant.companyName").isEqualTo("company name")
                .jsonPath("$.merchant.companyId").isEqualTo("company id")
                .jsonPath("$.memberRole").isEqualTo("member role")
                .jsonPath("$.status").isEqualTo("ACTIVE");
    }

    @Test
    void createMerchantMemberShouldThrowExceptionIfMerchantNotExist() {
        AddressOutputDto savedAddress = webTestClient.post().uri(url + "addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(addressInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AddressOutputDto.class)
                .returnResult()
                .getResponseBody();

        UUID savedAddressId = savedAddress.getId();
        memberInputDto.setAddressId(savedAddressId);
        memberInputDto.setMerchantId(UUID.randomUUID());

        webTestClient.post().uri(url + "merchant_members")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(memberInputDto)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void updateMerchantMemberShouldReturnUpdatedMerchantMemberOutputDto() {
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
        memberInputDto.setAddressId(savedAddressId);
        memberInputDto.setMerchantId(savedMerchantId);
        MerchantMemberOutputDto savedMerchantMember = webTestClient.post().uri(url + "merchant_members")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(memberInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MerchantMemberOutputDto.class)
                .returnResult()
                .getResponseBody();

        updateMerchantMemberDto.setMerchantMemberId(savedMerchantMember.getId());

        webTestClient.patch().uri(url + "merchant_members")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateMerchantMemberDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.user.id").exists()
                .jsonPath("$.user.firstName").isEqualTo("first name")
                .jsonPath("$.user.lastName").isEqualTo("last name")
                .jsonPath("$.created").exists()
                .jsonPath("$.updated").exists()
                .jsonPath("$.merchant.id").isEqualTo(savedMerchantId.toString())
                .jsonPath("$.merchant.companyName").isEqualTo("company name")
                .jsonPath("$.merchant.companyId").isEqualTo("company id")
                .jsonPath("$.memberRole").isEqualTo("updated role")
                .jsonPath("$.status").isEqualTo("ACTIVE");
    }

    @Test
    void updateMerchantMemberShouldThrowExceptionIfMerchantMemberNotExist() {
        updateMerchantMemberDto.setMerchantMemberId(UUID.randomUUID());

        webTestClient.patch().uri(url + "merchant_members")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateMerchantMemberDto)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getByIdShouldReturnMerchantMemberOutputDto() {
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
        memberInputDto.setAddressId(savedAddressId);
        memberInputDto.setMerchantId(savedMerchantId);
        MerchantMemberOutputDto savedMerchantMember = webTestClient.post().uri(url + "merchant_members")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(memberInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MerchantMemberOutputDto.class)
                .returnResult()
                .getResponseBody();

        UUID savedMerchantMemberId = savedMerchantMember.getId();

        webTestClient.get().uri(url + "merchant_members" + "/" + savedMerchantMemberId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.user.id").exists()
                .jsonPath("$.user.firstName").isEqualTo("first name")
                .jsonPath("$.user.lastName").isEqualTo("last name")
                .jsonPath("$.created").exists()
                .jsonPath("$.updated").exists()
                .jsonPath("$.merchant.id").isEqualTo(savedMerchantId.toString())
                .jsonPath("$.merchant.companyName").isEqualTo("company name")
                .jsonPath("$.merchant.companyId").isEqualTo("company id")
                .jsonPath("$.memberRole").isEqualTo("member role")
                .jsonPath("$.status").isEqualTo("ACTIVE");
    }

    @Test
    void getByIdShouldThrowExceptionIfMerchantMemberNotExist() {

        webTestClient.get().uri(url + "merchant_members" + "/" + UUID.randomUUID())
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
        memberInputDto.setAddressId(savedAddressId);
        memberInputDto.setMerchantId(savedMerchantId);
        MerchantMemberOutputDto savedMerchantMember = webTestClient.post().uri(url + "merchant_members")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(memberInputDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MerchantMemberOutputDto.class)
                .returnResult()
                .getResponseBody();

        UUID savedMerchantMemberId = savedMerchantMember.getId();

        webTestClient.delete().uri(url + "merchant_members" + "/" + savedMerchantMemberId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isEqualTo(savedMerchantMemberId.toString());
    }
}
