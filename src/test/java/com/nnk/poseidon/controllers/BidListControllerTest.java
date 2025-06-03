package com.nnk.poseidon.controllers;

import com.nnk.poseidon.dto.BidListDTO;
import com.nnk.poseidon.services.BidListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour la classe {@link BidListController} utilisant des DTOs.
 */
@ExtendWith(MockitoExtension.class)
class BidListControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BidListService bidListServiceMock;

    @InjectMocks
    private BidListController bidListController;

    private BidListDTO bidListDTOTest1;
    private BidListDTO bidListDTOTest2;
    private BidListDTO bidListDTONouveau;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bidListController).build();

        bidListDTOTest1 = new BidListDTO(1, "Account1", "TypeA", 100.0, LocalDateTime.now().minusDays(1));
        bidListDTOTest2 = new BidListDTO(2, "Account2", "TypeB", 200.0, LocalDateTime.now().minusDays(2));
        bidListDTONouveau = new BidListDTO(null, "NewAccount", "NewType", 150.0, null);
    }

    @Test
    void testHome_devraitRetournerVueListeAvecDTOs() throws Exception {
        when(bidListServiceMock.findAll()).thenReturn(Arrays.asList(bidListDTOTest1, bidListDTOTest2));

        mockMvc.perform(get("/bidList/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/list"))
                .andExpect(model().attributeExists("bidLists"))
                .andExpect(model().attribute("bidLists", Arrays.asList(bidListDTOTest1, bidListDTOTest2)));

        verify(bidListServiceMock, times(1)).findAll();
    }

    @Test
    void testHome_devraitRetournerVueListeAvecListeVide_quandAucunBid() throws Exception {
        when(bidListServiceMock.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bidList/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/list"))
                .andExpect(model().attribute("bidLists", Collections.emptyList()));

        verify(bidListServiceMock, times(1)).findAll();
    }


    @Test
    void testAddBidForm_devraitRetournerVueAjoutAvecDTO() throws Exception {
        mockMvc.perform(get("/bidList/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/add"))
                .andExpect(model().attributeExists("bidList"))
                .andExpect(model().attribute("bidList", instanceOf(BidListDTO.class)));
    }

    @Test
    void testValidate_avecDTOValide_devraitSauvegarderEtRediriger() throws Exception {
        BidListDTO dtoSauvegarde = new BidListDTO(3, "NewAccount", "NewType", 150.0, LocalDateTime.now());
        when(bidListServiceMock.save(any(BidListDTO.class))).thenReturn(dtoSauvegarde);

        mockMvc.perform(post("/bidList/validate")
                        .param("account", bidListDTONouveau.getAccount())
                        .param("type", bidListDTONouveau.getType())
                        .param("bidQuantity", bidListDTONouveau.getBidQuantity().toString())
                        .flashAttr("bidListDTO", bidListDTONouveau) // Objet utilisé par @Valid
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(bidListServiceMock, times(1)).save(any(BidListDTO.class));
    }

    @Test
    void testValidate_avecDTOInvalide_devraitRetournerVueAjout() throws Exception {
        // Simuler un DTO avec un champ 'account' vide, qui devrait échouer la validation @NotBlank
        BidListDTO dtoInvalide = new BidListDTO(null, "", "TypeValide", 100.0, null);

        mockMvc.perform(post("/bidList/validate")
                        .param("account", "") // Champ invalide
                        .param("type", "TypeValide")
                        .param("bidQuantity", "100.0")
                        .flashAttr("bidListDTO", dtoInvalide)
                )
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/add"))
                .andExpect(model().attributeHasFieldErrors("bidListDTO", "account")); // Erreur sur 'account'

        verify(bidListServiceMock, never()).save(any(BidListDTO.class));
    }

    @Test
    void testShowUpdateForm_siDTOTrouve_devraitRetournerVueMiseAJour() throws Exception {
        when(bidListServiceMock.findById(1)).thenReturn(Optional.of(bidListDTOTest1));

        mockMvc.perform(get("/bidList/update/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/update"))
                .andExpect(model().attributeExists("bidList"))
                .andExpect(model().attribute("bidList", bidListDTOTest1));

        verify(bidListServiceMock, times(1)).findById(1);
    }

    @Test
    void testShowUpdateForm_siDTONonTrouve_devraitRedirigerVersListe() throws Exception {
        when(bidListServiceMock.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/bidList/update/99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(bidListServiceMock, times(1)).findById(99);
    }

    @Test
    void testUpdateBid_avecDTOValide_devraitMettreAJourEtRediriger() throws Exception {
        BidListDTO dtoPourMiseAJour = new BidListDTO(1, "AccountUpdated", "TypeUpdated", 120.0, bidListDTOTest1.getCreationDate());
        when(bidListServiceMock.save(any(BidListDTO.class))).thenReturn(dtoPourMiseAJour);

        mockMvc.perform(post("/bidList/update/1")
                        .param("bidListId", "1") // Important pour que le DTO ait l'ID
                        .param("account", dtoPourMiseAJour.getAccount())
                        .param("type", dtoPourMiseAJour.getType())
                        .param("bidQuantity", dtoPourMiseAJour.getBidQuantity().toString())
                        .flashAttr("bidListDTO", dtoPourMiseAJour)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(bidListServiceMock, times(1)).save(argThat(dto ->
                dto.getBidListId().equals(1) && dto.getAccount().equals("AccountUpdated")
        ));
    }

    @Test
    void testUpdateBid_avecDTOInvalide_devraitRetournerVueMiseAJour() throws Exception {
        BidListDTO dtoInvalidePourUpdate = new BidListDTO(1, "", "TypeValide", 100.0, null);

        mockMvc.perform(post("/bidList/update/1")
                        .param("bidListId", "1")
                        .param("account", "") // Invalide
                        .param("type", "TypeValide")
                        .param("bidQuantity", "100.0")
                        .flashAttr("bidListDTO", dtoInvalidePourUpdate)
                )
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/update"))
                .andExpect(model().attributeHasFieldErrors("bidListDTO", "account"))
                .andExpect(model().attributeExists("bidList"));

        verify(bidListServiceMock, never()).save(any(BidListDTO.class));
    }

    @Test
    void testUpdateBid_siServiceLeveException_devraitRetournerVueMiseAJourAvecErreur() throws Exception {
        BidListDTO dtoPourMiseAJour = new BidListDTO(1, "AccountValid", "TypeValid", 120.0, null);
        when(bidListServiceMock.save(any(BidListDTO.class)))
                .thenThrow(new IllegalArgumentException("Erreur de sauvegarde simulée"));

        mockMvc.perform(post("/bidList/update/1")
                        .param("bidListId", "1")
                        .param("account", dtoPourMiseAJour.getAccount())
                        .param("type", dtoPourMiseAJour.getType())
                        .param("bidQuantity", dtoPourMiseAJour.getBidQuantity().toString())
                        .flashAttr("bidListDTO", dtoPourMiseAJour))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/update"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("bidList", dtoPourMiseAJour));

        verify(bidListServiceMock, times(1)).save(any(BidListDTO.class));
    }


    @Test
    void testDeleteBid_siIdTrouve_devraitSupprimerEtRediriger() throws Exception {
        doNothing().when(bidListServiceMock).deleteById(1);

        mockMvc.perform(get("/bidList/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(bidListServiceMock, times(1)).deleteById(1);
    }

    @Test
    void testDeleteBid_siIdNonTrouve_devraitRedirigerAvecMessageErreur() throws Exception {
        doThrow(new IllegalArgumentException("BidList non trouvé avec id : 99 pour suppression."))
                .when(bidListServiceMock).deleteById(99);

        mockMvc.perform(get("/bidList/delete/99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(bidListServiceMock, times(1)).deleteById(99);
    }
}