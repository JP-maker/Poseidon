package com.nnk.poseidon.controllers;

import com.nnk.poseidon.dto.RuleNameDTO;
import com.nnk.poseidon.services.RuleNameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour la classe {@link RuleNameController} utilisant des DTOs.
 */
@ExtendWith(MockitoExtension.class)
class RuleNameControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RuleNameService ruleNameServiceMock;

    @InjectMocks
    private RuleNameController ruleNameController;

    private RuleNameDTO ruleDtoTest1;
    private RuleNameDTO ruleDtoTest2;
    private RuleNameDTO ruleDtoNouveau;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ruleNameController).build();

        ruleDtoTest1 = new RuleNameDTO(1, "Name1", "Desc1", "Json1", "Template1", "Sql1", "SqlPart1");
        ruleDtoTest2 = new RuleNameDTO(2, "Name2", "Desc2", "Json2", "Template2", "Sql2", "SqlPart2");
        ruleDtoNouveau = new RuleNameDTO(null, "NewName", "NewDesc", "NewJson", "NewTemplate", "NewSql", "NewSqlPart");
    }

    @Test
    void testHome_devraitRetournerVueListeAvecDTOs() throws Exception {
        when(ruleNameServiceMock.findAll()).thenReturn(Arrays.asList(ruleDtoTest1, ruleDtoTest2));

        mockMvc.perform(get("/ruleName/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/list"))
                .andExpect(model().attributeExists("ruleNames"))
                .andExpect(model().attribute("ruleNames", Arrays.asList(ruleDtoTest1, ruleDtoTest2)));

        verify(ruleNameServiceMock, times(1)).findAll();
    }

    @Test
    void testHome_quandServiceLeveException_devraitAfficherMessageErreur() throws Exception {
        when(ruleNameServiceMock.findAll()).thenThrow(new RuntimeException("Erreur DB simulée"));

        mockMvc.perform(get("/ruleName/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/list"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attributeDoesNotExist("ruleNames")); // Ou Collections.emptyList() si c'est le fallback

        verify(ruleNameServiceMock, times(1)).findAll();
    }

    @Test
    void testAddRuleForm_devraitRetournerVueAjoutAvecDTO() throws Exception {
        mockMvc.perform(get("/ruleName/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/add"))
                .andExpect(model().attributeExists("ruleName"))
                .andExpect(model().attribute("ruleName", instanceOf(RuleNameDTO.class)));
    }

    @Test
    void testValidate_avecDTOValide_devraitSauvegarderEtRediriger() throws Exception {
        RuleNameDTO dtoSauvegarde = new RuleNameDTO(3, "NewName", "NewDesc", "NewJson", "NewTemplate", "NewSql", "NewSqlPart");
        when(ruleNameServiceMock.save(any(RuleNameDTO.class))).thenReturn(dtoSauvegarde);

        mockMvc.perform(post("/ruleName/validate")
                        .param("name", ruleDtoNouveau.getName())
                        .param("description", ruleDtoNouveau.getDescription())
                        .param("json", ruleDtoNouveau.getJson())
                        .param("template", ruleDtoNouveau.getTemplate())
                        .param("sql", ruleDtoNouveau.getSql())
                        .param("sqlPart", ruleDtoNouveau.getSqlPart())
                        .flashAttr("RuleNameDTO", ruleDtoNouveau) // Objet utilisé par @Valid, nom de l'objet DTO
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ruleName/list"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(ruleNameServiceMock, times(1)).save(any(RuleNameDTO.class));
    }

    @Test
    void testValidate_avecDTOInvalide_devraitRetournerVueAjout() throws Exception {
        mockMvc.perform(post("/ruleName/validate")
                                .param("name", "") // Champ 'name' invalide (vide) pour déclencher @NotBlank
                                .param("description", "Desc")
                                .param("json", "J")
                                .param("template", "T")
                                .param("sql", "S")      // Assurez-vous que ces noms de param correspondent aux champs du DTO
                                .param("sqlPart", "SP")
                )
                .andExpect(status().isOk()) // S'attendre à HTTP 200 OK (on retourne à la vue d'ajout)
                .andExpect(view().name("ruleName/add")) // S'attendre à retourner à la vue "ruleName/add"
                .andExpect(model().attributeHasFieldErrors("ruleName", "name")) // <--- CHANGEMENT ICI
                .andExpect(model().attributeExists("ruleName")); // Vérifie aussi que l'objet "ruleName" est renvoyé

        // Vérifier que la méthode save du service n'a jamais été appelée car la validation a échoué
        verify(ruleNameServiceMock, never()).save(any(RuleNameDTO.class));
    }

    @Test
    void testValidate_quandServiceLeveException_devraitRetournerVueAjoutAvecErreur() throws Exception {
        when(ruleNameServiceMock.save(any(RuleNameDTO.class))).thenThrow(new RuntimeException("Erreur de sauvegarde"));

        mockMvc.perform(post("/ruleName/validate")
                        .param("name", ruleDtoNouveau.getName())
                        // ... autres params
                        .flashAttr("RuleNameDTO", ruleDtoNouveau))
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/add"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(ruleNameServiceMock, times(1)).save(any(RuleNameDTO.class));
    }


    @Test
    void testShowUpdateForm_siDTOTrouve_devraitRetournerVueMiseAJour() throws Exception {
        when(ruleNameServiceMock.findById(1)).thenReturn(Optional.of(ruleDtoTest1));

        mockMvc.perform(get("/ruleName/update/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/update"))
                .andExpect(model().attributeExists("ruleName"))
                .andExpect(model().attribute("ruleName", ruleDtoTest1));

        verify(ruleNameServiceMock, times(1)).findById(1);
    }

    @Test
    void testShowUpdateForm_siDTONonTrouve_devraitRedirigerVersListeAvecErreur() throws Exception {
        when(ruleNameServiceMock.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/ruleName/update/99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ruleName/list"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(ruleNameServiceMock, times(1)).findById(99);
    }

    @Test
    void testUpdateRuleName_avecDTOValide_devraitMettreAJourEtRediriger() throws Exception {
        RuleNameDTO dtoPourMiseAJour = new RuleNameDTO(1, "NameUpdated", "DescUpdated", "JsonUpdated", "TemplateUpdated", "SqlUpdated", "SqlPartUpdated");
        when(ruleNameServiceMock.save(any(RuleNameDTO.class))).thenReturn(dtoPourMiseAJour);

        mockMvc.perform(post("/ruleName/update/1")
                        .param("id", "1")
                        .param("name", dtoPourMiseAJour.getName())
                        .param("description", dtoPourMiseAJour.getDescription())
                        // ... autres params
                        .flashAttr("RuleNameDTO", dtoPourMiseAJour)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ruleName/list"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(ruleNameServiceMock, times(1)).save(argThat(dto ->
                dto.getId().equals(1) && dto.getName().equals("NameUpdated")
        ));
    }

    @Test
    void testUpdateRuleName_avecDTOInvalide_devraitRetournerVueMiseAJour() throws Exception {
        RuleNameDTO dtoInvalide = new RuleNameDTO(1, "", "D", "J", "T", "S", "SP");

        mockMvc.perform(post("/ruleName/update/1")
                                .param("id", "1") // Nécessaire si l'ID fait partie du chemin et est utilisé par la logique
                                .param("name", "") // Champ invalide pour déclencher l'erreur @NotBlank
                                .param("description", "D")
                                .param("json", "J")
                                .param("template", "T")
                                .param("sql", "S") // Correspond au champ 'sql' dans RuleNameDto
                                .param("sqlPart", "SP")
                )
                .andExpect(status().isOk()) // S'attendre à HTTP 200 OK (car la validation échoue et on retourne à la vue)
                .andExpect(view().name("ruleName/update")) // S'attendre à retourner à la vue de mise à jour
                .andExpect(model().attributeHasFieldErrors("ruleName", "name")) // <--- CHANGEMENT ICI: "ruleName"
                .andExpect(model().attributeExists("ruleName")); // Vérifier que l'objet ("ruleName") est bien renvoyé au modèle

        // Vérifier que la méthode save du service n'a jamais été appelée
        verify(ruleNameServiceMock, never()).save(any(RuleNameDTO.class));
    }

    @Test
    void testUpdateRuleName_quandServiceLeveException_devraitRetournerVueMiseAJourAvecErreur() throws Exception {
        RuleNameDTO dtoPourMiseAJour = new RuleNameDTO(1, "ValidName", "ValidDesc", "J", "T", "S", "SP");
        when(ruleNameServiceMock.save(any(RuleNameDTO.class))).thenThrow(new RuntimeException("Erreur DB simulée"));

        mockMvc.perform(post("/ruleName/update/1")
                        .param("id", "1")
                        .param("name", dtoPourMiseAJour.getName())
                        // ... autres params
                        .flashAttr("RuleNameDTO", dtoPourMiseAJour))
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/update"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(ruleNameServiceMock, times(1)).save(any(RuleNameDTO.class));
    }


    @Test
    void testDeleteRuleName_siIdTrouve_devraitSupprimerEtRediriger() throws Exception {
        doNothing().when(ruleNameServiceMock).deleteById(1);

        mockMvc.perform(get("/ruleName/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ruleName/list"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(ruleNameServiceMock, times(1)).deleteById(1);
    }

    @Test
    void testDeleteRuleName_siIdNonTrouveOuErreurService_devraitRedirigerAvecMessageErreur() throws Exception {
        doThrow(new IllegalArgumentException("RuleName non trouvé")).when(ruleNameServiceMock).deleteById(99);

        mockMvc.perform(get("/ruleName/delete/99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ruleName/list"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(ruleNameServiceMock, times(1)).deleteById(99);
    }
}