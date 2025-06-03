package com.nnk.poseidon.controllers;

import com.nnk.poseidon.dto.UserDTO;
import com.nnk.poseidon.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Classe de test pour {@link UserController} (version refactorisée avec UserService et UserDTO).
 * Utilise {@link WebMvcTest} pour tester la couche MVC.
 * Le {@link UserService} est simulé (mocké) avec {@link MockitoBean}.
 */
@WebMvcTest(UserController.class)
// @Import(com.nnk.poseidon.config.SecurityConfig.class) // Si nécessaire
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private UserDTO sampleUserDTO1;
    private UserDTO sampleUserDTO2;

    @BeforeEach
    void setUp() {
        sampleUserDTO1 = new UserDTO();
        sampleUserDTO1.setId(1);
        sampleUserDTO1.setUsername("testuser1");
        sampleUserDTO1.setFullname("Test User One");
        // Le mot de passe n'est pas stocké en clair dans le DTO après récupération
        sampleUserDTO1.setRole("USER");

        sampleUserDTO2 = new UserDTO();
        sampleUserDTO2.setId(2);
        sampleUserDTO2.setUsername("adminuser");
        sampleUserDTO2.setFullname("Admin User");
        sampleUserDTO2.setRole("ADMIN");
    }

    @Nested
    @DisplayName("Tests pour la liste des utilisateurs (GET /user/list)")
    class ListUsersTests {
        @Test
        @DisplayName("Devrait retourner la vue list et le modèle avec les UserDTOs")
        @WithMockUser(roles = "ADMIN")
        void home_ShouldReturnListViewWithUserDTOs() throws Exception {
            List<UserDTO> users = Arrays.asList(sampleUserDTO1, sampleUserDTO2);
            when(userService.findAllUsers()).thenReturn(users);

            mockMvc.perform(get("/user/list"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("user/list"))
                    .andExpect(model().attributeExists("users"))
                    .andExpect(model().attribute("users", hasSize(2)))
                    .andExpect(model().attribute("users", containsInAnyOrder(sampleUserDTO1, sampleUserDTO2)));

            verify(userService).findAllUsers();
        }
    }

    @Nested
    @DisplayName("Tests pour l'ajout d'utilisateurs")
    class AddUserTests {
        @Test
        @DisplayName("GET /user/add - Devrait retourner la vue add avec un UserDTO vide")
        @WithMockUser(roles = "ADMIN")
        void addUserForm_ShouldReturnAddViewWithEmptyUserDTO() throws Exception {
            mockMvc.perform(get("/user/add"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("user/add"))
                    .andExpect(model().attributeExists("user"))
                    .andExpect(model().attribute("user", instanceOf(UserDTO.class)));
        }

        @Test
        @DisplayName("POST /user/validate - Devrait créer l'utilisateur et rediriger si valide")
        @WithMockUser(roles = "ADMIN")
        void validate_WhenValidUser_ShouldCreateAndRedirect() throws Exception {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("username", "newuser");
            params.add("password", "Password123!");
            params.add("fullname", "New User Full");
            params.add("role", "USER");

            UserDTO createdUserDTO = new UserDTO(3, "newuser", null, "New User Full", "USER");
            when(userService.createUser(any(UserDTO.class))).thenReturn(createdUserDTO);

            mockMvc.perform(post("/user/validate")
                            .params(params)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/user/list"))
                    .andExpect(flash().attributeExists("successMessage"));

            verify(userService).createUser(argThat(dto ->
                    dto.getUsername().equals("newuser") &&
                            dto.getPassword().equals("Password123!") && // Le DTO soumis a le mdp en clair
                            dto.getFullname().equals("New User Full") &&
                            dto.getRole().equals("USER")
            ));
        }

        @Test
        @DisplayName("POST /user/validate - Devrait retourner vue add avec erreurs si invalide (validation)")
        @WithMockUser(roles = "ADMIN")
        void validate_WhenInvalidUser_ShouldReturnAddViewWithValidationErrors() throws Exception {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("username", ""); // Invalide
            params.add("password", "short"); // Invalide (si validation @Size(min=6))
            params.add("fullname", "New User Full");
            params.add("role", "USER");

            mockMvc.perform(post("/user/validate")
                            .params(params)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("user/add"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrors("user", "username", "password"));

            verify(userService, never()).createUser(any(UserDTO.class));
        }

        @Test
        @DisplayName("POST /user/validate - Devrait retourner vue add avec message si username existe déjà")
        @WithMockUser(roles = "ADMIN")
        void validate_WhenUsernameExists_ShouldReturnAddViewWithErrorMsg() throws Exception {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("username", "existinguser");
            params.add("password", "Password123!");
            params.add("fullname", "Existing User Full");
            params.add("role", "USER");

            when(userService.createUser(any(UserDTO.class)))
                    .thenThrow(new IllegalArgumentException("Username existinguser already exists."));

            mockMvc.perform(post("/user/validate")
                            .params(params)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("user/add"))
                    .andExpect(model().attributeExists("errorMessage"))
                    .andExpect(model().attribute("errorMessage", "Username existinguser already exists."));

            verify(userService).createUser(any(UserDTO.class));
        }
    }

    @Nested
    @DisplayName("Tests pour la mise à jour des utilisateurs")
    class UpdateUserTests {
        @Test
        @DisplayName("GET /user/update/{id} - Devrait retourner la vue update avec UserDTO si trouvé")
        @WithMockUser(roles = "ADMIN")
        void showUpdateForm_WhenUserFound_ShouldReturnUpdateViewWithUserDTO() throws Exception {
            when(userService.findUserById(1)).thenReturn(Optional.of(sampleUserDTO1));

            mockMvc.perform(get("/user/update/1"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("user/update"))
                    .andExpect(model().attributeExists("user"))
                    .andExpect(model().attribute("user", hasProperty("username", is("testuser1"))))
                    // Le mot de passe doit être null dans le DTO pour le formulaire de mise à jour
                    .andExpect(model().attribute("user", hasProperty("password", is(nullValue()))));

            verify(userService).findUserById(1);
        }

        @Test
        @DisplayName("GET /user/update/{id} - Devrait rediriger vers la liste si UserDTO non trouvé")
        @WithMockUser(roles = "ADMIN")
        void showUpdateForm_WhenUserNotFound_ShouldRedirectToList() throws Exception {
            when(userService.findUserById(99)).thenReturn(Optional.empty());

            mockMvc.perform(get("/user/update/99"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/user/list"))
                    .andExpect(flash().attributeExists("errorMessage"));

            verify(userService).findUserById(99);
        }

        @Test
        @DisplayName("POST /user/update/{id} - Devrait mettre à jour et rediriger si valide")
        @WithMockUser(roles = "ADMIN")
        void updateUser_WhenValid_ShouldUpdateAndRedirect() throws Exception {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            // L'ID est dans le path, mais le DTO lié au formulaire l'aura aussi via <input type="hidden" th:field="*{id}">
            params.add("id", "1");
            params.add("username", "updateduser");
            params.add("password", "NewPassword123!"); // Nouveau mot de passe
            params.add("fullname", "Updated User Full");
            params.add("role", "ADMIN");

            UserDTO updatedDTO = new UserDTO(1, "updateduser", null, "Updated User Full", "ADMIN");
            when(userService.updateUser(eq(1), any(UserDTO.class))).thenReturn(Optional.of(updatedDTO));

            mockMvc.perform(post("/user/update/1")
                            .params(params)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/user/list"))
                    .andExpect(flash().attributeExists("successMessage"));

            verify(userService).updateUser(eq(1), argThat(dto ->
                    dto.getId() == 1 &&
                            dto.getUsername().equals("updateduser") &&
                            dto.getPassword().equals("NewPassword123!") && // Le DTO soumis contient le mdp en clair
                            dto.getFullname().equals("Updated User Full") &&
                            dto.getRole().equals("ADMIN")
            ));
        }

        @Test
        @DisplayName("POST /user/update/{id} - Devrait retourner vue update avec erreurs si invalide (validation)")
        @WithMockUser(roles = "ADMIN")
        void updateUser_WhenInvalid_ShouldReturnUpdateViewWithValidationErrors() throws Exception {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("id", "1");
            params.add("username", ""); // Invalide
            params.add("password", "NewPassword123!");
            params.add("fullname", "Updated User Full");
            params.add("role", "ADMIN");

            mockMvc.perform(post("/user/update/1")
                            .params(params)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("user/update"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrors("user", "username"));

            verify(userService, never()).updateUser(anyInt(), any(UserDTO.class));
        }

        @Test
        @DisplayName("POST /user/update/{id} - Devrait retourner vue update avec message si username déjà pris")
        @WithMockUser(roles = "ADMIN")
        void updateUser_WhenUsernameTaken_ShouldReturnUpdateViewWithErrorMsg() throws Exception {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("id", "1");
            params.add("username", "anotherExistingUser");
            params.add("password", "Password123!");
            params.add("fullname", "User Full Name");
            params.add("role", "USER");

            when(userService.updateUser(eq(1), any(UserDTO.class)))
                    .thenThrow(new IllegalArgumentException("Username anotherExistingUser is already taken by another user."));

            mockMvc.perform(post("/user/update/1")
                            .params(params)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("user/update"))
                    .andExpect(model().attributeExists("errorMessage"))
                    .andExpect(model().attribute("errorMessage", "Username anotherExistingUser is already taken by another user."));

            verify(userService).updateUser(eq(1), any(UserDTO.class));
        }
    }

    @Nested
    @DisplayName("Tests pour la suppression d'utilisateurs")
    class DeleteUserTests {
        @Test
        @DisplayName("GET /user/delete/{id} - Devrait supprimer et rediriger si utilisateur existe")
        @WithMockUser(roles = "ADMIN")
        void deleteUser_WhenUserExists_ShouldDeleteAndRedirect() throws Exception {
            doNothing().when(userService).deleteUserById(1); // deleteUserById ne retourne rien

            mockMvc.perform(get("/user/delete/1")
                            .with(csrf())) // Si CSRF est configuré pour GET qui modifient
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/user/list"))
                    .andExpect(flash().attributeExists("successMessage"));

            verify(userService).deleteUserById(1);
        }

        @Test
        @DisplayName("GET /user/delete/{id} - Devrait rediriger avec message d'erreur si utilisateur non trouvé")
        @WithMockUser(roles = "ADMIN")
        void deleteUser_WhenUserNotFound_ShouldRedirectWithErrorMsg() throws Exception {
            doThrow(new IllegalArgumentException("Invalid user Id:99"))
                    .when(userService).deleteUserById(99);

            mockMvc.perform(get("/user/delete/99")
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/user/list"))
                    .andExpect(flash().attributeExists("errorMessage"))
                    .andExpect(flash().attribute("errorMessage", "Invalid user Id:99"));

            verify(userService).deleteUserById(99);
        }
    }
}