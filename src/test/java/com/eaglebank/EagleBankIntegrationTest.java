package com.eaglebank;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class EagleBankIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void coreFlowCreateFetchAndTransaction() throws Exception {
        String userPayload = """
            {
              "name": "Test User",
              "address": {
                "line1": "1 Main St",
                "town": "London",
                "county": "Greater London",
                "postcode": "N1 1AA"
              },
              "phoneNumber": "+447700900111",
              "email": "user1@example.com",
              "password": "StrongPass123!"
            }
            """;

        String createUserResponse = mockMvc.perform(post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userPayload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andReturn().getResponse().getContentAsString();

        JsonNode createdUser = objectMapper.readTree(createUserResponse);
        String userId = createdUser.get("id").asText();

        String loginResponse = mockMvc.perform(post("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "user1@example.com",
                      "password": "StrongPass123!"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(loginResponse).get("accessToken").asText();

        mockMvc.perform(get("/v1/users/{userId}", userId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("user1@example.com"));

        String createAccountResponse = mockMvc.perform(post("/v1/accounts")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "Main Account",
                      "accountType": "personal"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accountNumber").exists())
            .andReturn().getResponse().getContentAsString();

        String accountNumber = objectMapper.readTree(createAccountResponse).get("accountNumber").asText();

        mockMvc.perform(post("/v1/accounts/{accountNumber}/transactions", accountNumber)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "amount": 100.00,
                      "currency": "GBP",
                      "type": "deposit",
                      "reference": "pay-in"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.type").value("deposit"));

        mockMvc.perform(post("/v1/accounts/{accountNumber}/transactions", accountNumber)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "amount": 200.00,
                      "currency": "GBP",
                      "type": "withdrawal",
                      "reference": "overspend"
                    }
                    """))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.message").value("Insufficient funds to process transaction"));
    }

    @Test
    void forbidsCrossUserAccountAccess() throws Exception {
        String userThreeToken = createUserAndLogin("user3@example.com", "StrongPass123!");

        String accountResponse = mockMvc.perform(post("/v1/accounts")
                .header("Authorization", "Bearer " + userThreeToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "Private Account",
                      "accountType": "personal"
                    }
                    """))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        String accountNumber = objectMapper.readTree(accountResponse).get("accountNumber").asText();
        String userTwoToken = createUserAndLogin("user2@example.com", "StrongPass123!");

        mockMvc.perform(get("/v1/accounts/{accountNumber}", accountNumber)
                .header("Authorization", "Bearer " + userTwoToken))
            .andExpect(status().isForbidden());
    }

    private String createUserAndLogin(String email, String password) throws Exception {
        mockMvc.perform(post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "User",
                      "address": {
                        "line1": "2 Main St",
                        "town": "London",
                        "county": "Greater London",
                        "postcode": "N1 1AA"
                      },
                      "phoneNumber": "+447700900222",
                      "email": "%s",
                      "password": "%s"
                    }
                    """.formatted(email, password)))
            .andExpect(status().isCreated());

        String loginResponse = mockMvc.perform(post("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "%s",
                      "password": "%s"
                    }
                    """.formatted(email, password)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(loginResponse).get("accessToken").asText();
    }
}


