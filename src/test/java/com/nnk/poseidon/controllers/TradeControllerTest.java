package com.nnk.poseidon.controllers;

import com.nnk.poseidon.dto.TradeDTO;
import com.nnk.poseidon.services.TradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf; // Si CSRF est activé
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*; // Pour instanceOf, hasSize, etc.


/**
 * Classe de test pour {@link TradeController}.
 * Utilise {@link WebMvcTest} pour tester la couche MVC sans démarrer un serveur complet.
 * Le {@link TradeService} est simulé (mocké) avec {@link MockitoBean}.
 */
@WebMvcTest(controllers = TradeController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@ExtendWith(MockitoExtension.class)
class TradeControllerTest {

    @Autowired
    private MockMvc mockMvc; // Permet de simuler des requêtes HTTP

    @MockitoBean // Crée un mock du service et l'injecte dans le contexte de test
    private TradeService tradeService;

    private TradeDTO sampleTradeDTO1;
    private TradeDTO sampleTradeDTO2;

    @BeforeEach
    void setUp() {
        // Initialiser des DTOs de test réutilisables
        sampleTradeDTO1 = new TradeDTO();
        sampleTradeDTO1.setTradeId(1);
        sampleTradeDTO1.setAccount("Account Test 1");
        sampleTradeDTO1.setType("Type Test 1");
        sampleTradeDTO1.setBuyQuantity(100.0);
        sampleTradeDTO1.setCreationDate(LocalDateTime.now().minusDays(1));
        sampleTradeDTO1.setRevisionDate(LocalDateTime.now());

        sampleTradeDTO2 = new TradeDTO();
        sampleTradeDTO2.setTradeId(2);
        sampleTradeDTO2.setAccount("Account Test 2");
        sampleTradeDTO2.setType("Type Test 2");
        sampleTradeDTO2.setBuyQuantity(200.0);
    }

    @Nested
    @DisplayName("Tests pour la liste des trades (GET /trade/list)")
    class ListTradesTests {
        @Test
        @DisplayName("Devrait retourner la vue list et le modèle avec les trades")
        void home_ShouldReturnListViewWithTrades() throws Exception {
            List<TradeDTO> trades = Arrays.asList(sampleTradeDTO1, sampleTradeDTO2);
            when(tradeService.findAllTrades()).thenReturn(trades);

            mockMvc.perform(get("/trade/list"))
                    .andExpect(status().isOk()) // HTTP 200
                    .andExpect(view().name("trade/list")) // Nom de la vue
                    .andExpect(model().attributeExists("trades")) // Attribut "trades" présent dans le modèle
                    .andExpect(model().attribute("trades", hasSize(2))) // Vérifie la taille de la liste
                    .andExpect(model().attribute("trades", containsInAnyOrder(sampleTradeDTO1, sampleTradeDTO2))); // Vérifie le contenu
        }

        @Test
        @DisplayName("Devrait retourner la vue list avec un modèle vide si aucun trade")
        void home_ShouldReturnListViewWithEmptyModel_WhenNoTrades() throws Exception {
            when(tradeService.findAllTrades()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/trade/list"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("trade/list"))
                    .andExpect(model().attribute("trades", hasSize(0)));
        }
    }


    @Nested
    @DisplayName("Tests pour l'ajout de trades")
    class AddTradeTests {
        @Test
        @DisplayName("GET /trade/add - Devrait retourner la vue add avec un TradeDTO vide")
        void addTradeForm_ShouldReturnAddViewWithEmptyTradeDTO() throws Exception {
            mockMvc.perform(get("/trade/add"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("trade/add"))
                    .andExpect(model().attributeExists("trade"))
                    .andExpect(model().attribute("trade", instanceOf(TradeDTO.class)));
        }

        @Test
        @DisplayName("POST /trade/validate - Devrait sauvegarder le trade et rediriger vers la liste si valide")
        void validate_WhenValidTrade_ShouldSaveAndRedirectToList() throws Exception {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("account", "New Account");
            params.add("type", "New Type");
            params.add("buyQuantity", "150.0");

            // Le service ne retourne rien d'important pour ce test, ou on peut mocker un retour
            when(tradeService.saveTrade(any(TradeDTO.class))).thenReturn(new TradeDTO());


            mockMvc.perform(post("/trade/validate")
                            .params(params)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .with(csrf())) // Ajouter si CSRF est activé
                    .andExpect(status().is3xxRedirection()) // Redirection (HTTP 302)
                    .andExpect(redirectedUrl("/trade/list"));

            verify(tradeService, times(1)).saveTrade(any(TradeDTO.class)); // Vérifie que saveTrade a été appelé
        }

        @Test
        @DisplayName("POST /trade/validate - Devrait retourner la vue add avec erreurs si invalide")
        void validate_WhenInvalidTrade_ShouldReturnAddViewWithErrors() throws Exception {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("account", ""); // Compte vide, devrait être invalide
            params.add("type", "New Type");
            params.add("buyQuantity", "150.0");

            mockMvc.perform(post("/trade/validate")
                            .params(params)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .with(csrf()))
                    .andExpect(status().isOk()) // Retourne à la page du formulaire
                    .andExpect(view().name("trade/add"))
                    .andExpect(model().hasErrors()) // Vérifie la présence d'erreurs de validation
                    .andExpect(model().attributeHasFieldErrors("trade", "account")); // Erreur sur le champ 'account'

            verify(tradeService, never()).saveTrade(any(TradeDTO.class)); // Ne doit pas appeler saveTrade
        }
    }

    @Nested
    @DisplayName("Tests pour la mise à jour des trades")
    class UpdateTradeTests {
        @Test
        @DisplayName("GET /trade/update/{id} - Devrait retourner la vue update avec le trade si trouvé")
        void showUpdateForm_WhenTradeFound_ShouldReturnUpdateViewWithTrade() throws Exception {
            when(tradeService.findTradeById(1)).thenReturn(Optional.of(sampleTradeDTO1));

            mockMvc.perform(get("/trade/update/1"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("trade/update"))
                    .andExpect(model().attribute("trade", sampleTradeDTO1));
        }

        @Test
        @DisplayName("GET /trade/update/{id} - Devrait rediriger vers la liste si trade non trouvé")
        void showUpdateForm_WhenTradeNotFound_ShouldRedirectToList() throws Exception {
            when(tradeService.findTradeById(99)).thenReturn(Optional.empty());

            mockMvc.perform(get("/trade/update/99"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/trade/list"));
        }

        @Test
        @DisplayName("POST /trade/update/{id} - Devrait mettre à jour et rediriger si valide")
        void updateTrade_WhenValid_ShouldUpdateAndRedirect() throws Exception {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("tradeId", "1"); // Important pour l'objet DTO
            params.add("account", "Updated Account");
            params.add("type", "Updated Type");
            params.add("buyQuantity", "250.0");

            when(tradeService.updateTrade(eq(1), any(TradeDTO.class))).thenReturn(Optional.of(sampleTradeDTO1)); // Simule la mise à jour réussie

            mockMvc.perform(post("/trade/update/1")
                            .params(params)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/trade/list"));

            verify(tradeService, times(1)).updateTrade(eq(1), any(TradeDTO.class));
        }

        @Test
        @DisplayName("POST /trade/update/{id} - Devrait retourner la vue update avec erreurs si invalide")
        void updateTrade_WhenInvalid_ShouldReturnUpdateViewWithErrors() throws Exception {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("tradeId", "1");
            params.add("account", ""); // Invalide
            params.add("type", "Updated Type");
            params.add("buyQuantity", "250.0");

            // Pas besoin de mocker tradeService.updateTrade car il ne sera pas appelé
            // L'objet tradeDTO sera repopulé dans le modèle par Spring grâce à @ModelAttribute

            mockMvc.perform(post("/trade/update/1")
                            .params(params)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("trade/update"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrors("trade", "account"));

            verify(tradeService, never()).updateTrade(anyInt(), any(TradeDTO.class));
        }

        @Test
        @DisplayName("POST /trade/update/{id} - Devrait rediriger vers la liste si trade non trouvé lors de la mise à jour (cas rare)")
        void updateTrade_WhenTradeNotFoundForUpdate_ShouldRedirect() throws Exception {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("tradeId", "99");
            params.add("account", "Account For NonExistent");
            params.add("type", "Type For NonExistent");
            params.add("buyQuantity", "50.0");

            when(tradeService.updateTrade(eq(99), any(TradeDTO.class))).thenReturn(Optional.empty()); // Simule trade non trouvé

            mockMvc.perform(post("/trade/update/99")
                            .params(params)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/trade/list"));

            verify(tradeService).updateTrade(eq(99), any(TradeDTO.class));
        }
    }

    @Nested
    @DisplayName("Tests pour la suppression de trades")
    class DeleteTradeTests {
        @Test
        @DisplayName("GET /trade/delete/{id} - Devrait supprimer et rediriger si trade existe")
        void deleteTrade_WhenTradeExists_ShouldDeleteAndRedirect() throws Exception {
            when(tradeService.findTradeById(1)).thenReturn(Optional.of(sampleTradeDTO1)); // Pour la vérification dans le contrôleur
            doNothing().when(tradeService).deleteTradeById(1); // deleteById ne retourne rien

            mockMvc.perform(get("/trade/delete/1")
                            .with(csrf())) // GET peut aussi être protégé par CSRF selon la config
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/trade/list"));

            verify(tradeService, times(1)).deleteTradeById(1);
        }

        @Test
        @DisplayName("GET /trade/delete/{id} - Devrait rediriger si trade non trouvé (sans erreur)")
        void deleteTrade_WhenTradeNotFound_ShouldRedirect() throws Exception {
            when(tradeService.findTradeById(99)).thenReturn(Optional.empty());

            mockMvc.perform(get("/trade/delete/99")
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/trade/list"));

            verify(tradeService, never()).deleteTradeById(99); // Ne devrait pas appeler delete si non trouvé
        }
    }
}