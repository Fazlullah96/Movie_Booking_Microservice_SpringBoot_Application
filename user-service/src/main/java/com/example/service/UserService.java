package com.example.service;

import com.example.configuration.KeycloakConfig;
import com.example.configuration.Template;
import com.example.dtos.LoginRequest;
import com.example.dtos.TokenResponse;
import com.example.dtos.UserRegistration;
import com.example.dtos.UserResponse;
import com.example.exception.InvalidCredentialsException;
import com.example.exception.KeycloakRegistrationFailedException;
import com.example.exception.UserNotFoundException;
import com.example.models.User;
import com.example.repo.UserRepo;
import jakarta.ws.rs.core.Response;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final KeycloakConfig keycloakConfig;
    private final Template template;

    @Value("${keycloak.server-url}")
    private String serverUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.client-id}")
    private String clientId;
    @Value("${keycloak.client-secret}")
    private String clientSecret;

    public UserResponse addUser(UserRegistration request){
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        credential.setTemporary(false);

        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(request.getEmail());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setCredentials(Collections.singletonList(credential));

        UsersResource usersResource = keycloakConfig.keycloak().realm(realm).users();
        Response response = usersResource.create(user);

        if(response.getStatus() != 201){
            throw new KeycloakRegistrationFailedException("Keycloak Registration Failed. Status: " + response.getStatus());
        }

        String keyCloakUserId = CreatedResponseUtil.getCreatedId(response);

        User localUser = User
                .builder()
                .id(keyCloakUserId)
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .build();

        User savedUser = userRepo.save(localUser);
        return UserResponse
                .builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .phoneNumber(savedUser.getPhoneNumber())
                .build();
    }

    public TokenResponse login(LoginRequest request){
        String tokenEndpoint = serverUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("grant_type", "password");
        map.add("username", request.getEmail());
        map.add("password", request.getPassword());

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, httpHeaders);

        try{
            ResponseEntity<KeycloakTokenResponse> response = template.restTemplate().postForEntity(
                    tokenEndpoint, httpEntity, KeycloakTokenResponse.class
            );
            if(response.getStatusCode().is2xxSuccessful() && response.getBody() != null){
                return TokenResponse
                        .builder()
                        .accessToken(response.getBody().getAccess_token())
                        .refreshToken(response.getBody().getRefresh_token())
                        .expiresIn(response.getBody().getExpires_in())
                        .build();
            }else{
                throw new InvalidCredentialsException("Username or Password is incorrect");
            }
        }catch (HttpClientErrorException.Unauthorized e){
            System.out.println("KEYCLOAK REJECTION REASON : " + e.getResponseBodyAsString());
            throw new RuntimeException("Authentication failed " + e.getResponseBodyAsString());
        }
    }

    @Data
    public static class KeycloakTokenResponse{
        private String access_token;
        private String refresh_token;
        private Integer expires_in;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "USER_CACHE", key = "#id")
    public UserResponse getUserById(String id){
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found for UserId" + id));
        return UserResponse
                .builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
