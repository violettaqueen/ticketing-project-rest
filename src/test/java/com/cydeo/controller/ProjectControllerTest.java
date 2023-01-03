package com.cydeo.controller;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.RoleDTO;
import com.cydeo.dto.TestResponseDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.enums.Gender;
import com.cydeo.enums.Status;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest {

    @Autowired
    private MockMvc mvc;

    static String token;

    static UserDTO manager;  // static because I will use in @BeforeAll for sample data
    static ProjectDTO project;

    @BeforeAll
    static void setUp() {   //creating sample data/objects before starting a to test

        token = "Bearer " + getToken();

        manager = new UserDTO(2L,      //create manager
                "",
                "",
                "ozzy",
                "abc1",
                "",
                true,
                "",
                new RoleDTO(2L, "Manager"),
                Gender.MALE);

        project = new ProjectDTO(         //create project
                "API Project",
                "PR001",
                manager,
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                "Some details",
                Status.OPEN
        );

    }

    @Test   //if I don't send any token
    void givenNoToken_getProjects() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/project"))
                .andExpect(status().is4xxClientError());  //400
    }

    @Test   //send token
    void givenToken_getProjects() throws Exception {     //testing get request

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/project")
                        .header("Authorization", token)   //pass/provide token
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].projectCode").exists())                 //test inside the Json and get data
                .andExpect(jsonPath("$.data[0].assignedManager.userName").exists())    //if we have a field
                .andExpect(jsonPath("$.data[0].assignedManager.userName").isNotEmpty())//testing if field in not empty
                .andExpect(jsonPath("$.data[0].assignedManager.userName").isString())  //if data comes in string
                .andExpect(jsonPath("$.data[0].assignedManager.userName").value("ozzy")); //if username is ozzy or not

    }

    @Test
    void givenToken_createProject() throws Exception {   //testing post request

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/project")   //end point for post method
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(toJsonString(project)))  //posting a non hardcoded object
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Project is created"));

    }

    @Test
    void givenToken_updateProject() throws Exception {

        project.setProjectName("API Project-2");

        mvc.perform(MockMvcRequestBuilders.put("/api/v1/project")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(toJsonString(project)))  //sending/passing that code that convert obj to json
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Project is successfully updated")); // take it from controller

    }

    @Test
    void givenToken_deleteProject() throws Exception {

       MvcResult result= mvc.perform(MockMvcRequestBuilders.delete("/api/v1/project/" + project.getProjectCode()) //--> path variable passing here
                        .header("Authorization", token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Project is successfully deleted"))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());

    }
    // code to convert Object to Json by using Jackson library
    private String toJsonString(final Object obj) throws JsonProcessingException {  //this will allow us not to hardcode Json obj
        ObjectMapper objectMapper = new ObjectMapper();  //object mapper is coming from jackson --> serialization, and deserialization
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);  //remove time stamps, use dates
        objectMapper.registerModule(new JavaTimeModule());  //changing date format from 2022/12/12 to 2022/12/12
        return objectMapper.writeValueAsString(obj);  //project will be converted to Json in a string format
    }

    private static String getToken() { //--> we are not going to use

        RestTemplate restTemplate = new RestTemplate();  //sending real API requests to keycloak to get token

        HttpHeaders headers = new HttpHeaders(); //header
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);//header

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        map.add("grant_type", "password");  //assigning keycloak properties
        map.add("client_id", "ticketing-app");
        map.add("client_secret", "iUl5hoRbNovNlC61IxnszxH7pWTrzoSd");
        map.add("username", "ozzy");
        map.add("password", "abc1");
        map.add("scope", "openid");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<TestResponseDTO> response =
                restTemplate.exchange("http://localhost:8080/auth/realms/cydeo-dev/protocol/openid-connect/token",
                        HttpMethod.POST,
                        entity,
                        TestResponseDTO.class);

        if (response.getBody() != null) {  //if has body return body otherwise empty string
            return response.getBody().getAccess_token();
        }

        return "";

    }

}
