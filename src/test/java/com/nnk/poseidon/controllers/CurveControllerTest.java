package com.nnk.poseidon.controllers;

import com.nnk.poseidon.dto.CurvePointDTO;
import com.nnk.poseidon.services.CurvePointService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.instanceOf;

/**
 * Tests unitaires pour la classe {@link CurveController} utilisant des DTOs.
 */
@ExtendWith(MockitoExtension.class)
class CurveControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CurvePointService curvePointServiceMock; // Mock du service concret

    @InjectMocks
    private CurveController curveController;

    private CurvePointDTO pointDeCourbeDTOTest1;
    private CurvePointDTO pointDeCourbeDTOTest2;
    private CurvePointDTO pointDeCourbeDTONouveau;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(curveController).build();

        pointDeCourbeDTOTest1 = new CurvePointDTO(1, 10, LocalDateTime.now().minusHours(2), 1.0, 100.0, LocalDateTime.now().minusDays(1));
        pointDeCourbeDTOTest2 = new CurvePointDTO(2, 20, LocalDateTime.now().minusHours(1), 2.0, 200.0, LocalDateTime.now().minusDays(2));
        pointDeCourbeDTONouveau = new CurvePointDTO(null, 30, LocalDateTime.now(), 3.0, 300.0, null); // ID est null pour la création
    }

    @Test
    void testHome_devraitRetournerVueListeAvecDTOs() throws Exception {
        when(curvePointServiceMock.findAll()).thenReturn(Arrays.asList(pointDeCourbeDTOTest1, pointDeCourbeDTOTest2));

        mockMvc.perform(get("/curvePoint/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/list"))
                .andExpect(model().attributeExists("curvePoints"))
                .andExpect(model().attribute("curvePoints", Arrays.asList(pointDeCourbeDTOTest1, pointDeCourbeDTOTest2)));

        verify(curvePointServiceMock, times(1)).findAll();
    }

    @Test
    void testAddBidForm_devraitRetournerVueAjoutAvecDTO() throws Exception {
        mockMvc.perform(get("/curvePoint/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/add"))
                .andExpect(model().attributeExists("curvePoint")) // Le nom de l'attribut dans le modèle
                .andExpect(model().attribute("curvePoint", instanceOf(CurvePointDTO.class)));
    }

    @Test
    void testValidate_avecDTOValide_devraitSauvegarderEtRediriger() throws Exception {
        // Simuler que le DTO passé est valide et que la sauvegarde réussit
        when(curvePointServiceMock.save(any(CurvePointDTO.class))).thenReturn(new CurvePointDTO(3, 30, pointDeCourbeDTONouveau.getAsOfDate(), 3.0, 300.0, LocalDateTime.now()));

        mockMvc.perform(post("/curvePoint/validate")
                        // Utiliser .param() pour simuler les données du formulaire qui seront mappées au DTO
                        .param("curveId", pointDeCourbeDTONouveau.getCurveId().toString())
                        .param("asOfDate", pointDeCourbeDTONouveau.getAsOfDate().toString()) // Assurez-vous que le format est correct ou utilisez un convertisseur/formateur
                        .param("term", pointDeCourbeDTONouveau.getTerm().toString())
                        .param("value", pointDeCourbeDTONouveau.getValue().toString())
                        // Ne pas envoyer "id" pour la création
                        .flashAttr("curvePointDTO", pointDeCourbeDTONouveau) // Le nom de l'objetModelAttribute
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/curvePoint/list"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(curvePointServiceMock, times(1)).save(any(CurvePointDTO.class));
    }

    @Test
    void testValidate_avecDTOInvalide_devraitRetournerVueAjout() throws Exception {
        // Simuler l'envoi de données qui échoueront à la validation
        // Par exemple, 'term' est null alors qu'il est @NotNull
        mockMvc.perform(post("/curvePoint/validate")
                        .param("curveId", "10")
                        // Ne pas envoyer "term" pour simuler une erreur @NotNull
                        .param("value", "100.0")
                        .flashAttr("curvePointDTO", new CurvePointDTO(null, 10, LocalDateTime.now(), null, 100.0, null))
                )
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/add"))
                .andExpect(model().attributeHasFieldErrors("curvePointDTO", "term")); // Vérifier l'erreur sur 'term'

        verify(curvePointServiceMock, never()).save(any(CurvePointDTO.class));
    }


    @Test
    void testShowUpdateForm_siDTOTrouve_devraitRetournerVueMiseAJour() throws Exception {
        when(curvePointServiceMock.findById(1)).thenReturn(Optional.of(pointDeCourbeDTOTest1));

        mockMvc.perform(get("/curvePoint/update/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/update"))
                .andExpect(model().attributeExists("curvePoint"))
                .andExpect(model().attribute("curvePoint", pointDeCourbeDTOTest1));

        verify(curvePointServiceMock, times(1)).findById(1);
    }

    @Test
    void testShowUpdateForm_siDTONonTrouve_devraitRedirigerVersListe() throws Exception {
        when(curvePointServiceMock.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/curvePoint/update/99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/curvePoint/list"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(curvePointServiceMock, times(1)).findById(99);
    }


    @Test
    void testUpdateBid_avecDTOValide_devraitMettreAJourEtRediriger() throws Exception {
        CurvePointDTO dtoPourMiseAJour = new CurvePointDTO(1, 10, LocalDateTime.now(), 1.5, 105.0, pointDeCourbeDTOTest1.getCreationDate());
        when(curvePointServiceMock.save(any(CurvePointDTO.class))).thenReturn(dtoPourMiseAJour);

        mockMvc.perform(post("/curvePoint/update/1")
                        .param("id", dtoPourMiseAJour.getId().toString()) // L'ID est dans le path, mais aussi dans le DTO
                        .param("curveId", dtoPourMiseAJour.getCurveId().toString())
                        .param("asOfDate", dtoPourMiseAJour.getAsOfDate().toString())
                        .param("term", dtoPourMiseAJour.getTerm().toString())
                        .param("value", dtoPourMiseAJour.getValue().toString())
                        .flashAttr("curvePointDTO", dtoPourMiseAJour)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/curvePoint/list"))
                .andExpect(flash().attributeExists("successMessage"));

        // Vérifier que le DTO sauvegardé a bien l'ID 1 et le nouveau terme
        verify(curvePointServiceMock, times(1)).save(argThat(dto ->
                dto.getId().equals(1) && dto.getTerm().equals(1.5)
        ));
    }

    @Test
    void testUpdateBid_avecDTOInvalide_devraitRetournerVueMiseAJour() throws Exception {
        CurvePointDTO dtoInvalide = new CurvePointDTO(1, 10, LocalDateTime.now(), null, 100.0, null); // Term est null

        mockMvc.perform(post("/curvePoint/update/1")
                        .param("id", "1")
                        .param("curveId", "10")
                        // "term" n'est pas envoyé ou envoyé vide pour simuler l'erreur
                        .param("value", "100.0")
                        .flashAttr("curvePointDTO", dtoInvalide)
                )
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/update"))
                .andExpect(model().attributeHasFieldErrors("curvePointDTO", "term"))
                .andExpect(model().attributeExists("curvePoint"));

        verify(curvePointServiceMock, never()).save(any(CurvePointDTO.class));
    }

    @Test
    void testUpdateBid_siIdNonTrouvePendantSauvegarde_devraitRetournerVueMiseAJourAvecErreur() throws Exception {
        CurvePointDTO dtoNonExistant = new CurvePointDTO(99, 10, LocalDateTime.now(), 1.5, 105.0, null);

        when(curvePointServiceMock.save(any(CurvePointDTO.class)))
                .thenThrow(new IllegalArgumentException("Mise à jour impossible : CurvePoint non trouvé avec id: " + dtoNonExistant.getId()));

        mockMvc.perform(post("/curvePoint/update/99")
                        .param("id", "99")
                        .param("curveId", "10")
                        .param("asOfDate", LocalDateTime.now().toString())
                        .param("term", "1.5")
                        .param("value", "105.0")
                        .flashAttr("curvePointDTO", dtoNonExistant))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/update"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("curvePoint", dtoNonExistant));

        verify(curvePointServiceMock, times(1)).save(any(CurvePointDTO.class));
    }


    @Test
    void testDeleteBid_siIdTrouve_devraitSupprimerEtRediriger() throws Exception {
        doNothing().when(curvePointServiceMock).deleteById(1);

        mockMvc.perform(get("/curvePoint/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/curvePoint/list"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(curvePointServiceMock, times(1)).deleteById(1);
    }

    @Test
    void testDeleteBid_siIdNonTrouve_devraitRedirigerAvecMessageErreur() throws Exception {
        doThrow(new IllegalArgumentException("CurvePoint non trouvé avec id : 99 pour suppression."))
                .when(curvePointServiceMock).deleteById(99);

        mockMvc.perform(get("/curvePoint/delete/99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/curvePoint/list"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(curvePointServiceMock, times(1)).deleteById(99);
    }
}