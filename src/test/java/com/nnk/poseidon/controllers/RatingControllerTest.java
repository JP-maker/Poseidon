package com.nnk.poseidon.controllers;

import com.nnk.poseidon.dto.RatingDTO;
import com.nnk.poseidon.services.RatingService;
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
 * Tests unitaires pour la classe {@link RatingController} utilisant des DTOs.
 */
@ExtendWith(MockitoExtension.class)
class RatingControllerTest {

	private MockMvc mockMvc;

	@Mock
	private RatingService ratingServiceMock;

	@InjectMocks
	private RatingController ratingController;

	private RatingDTO RatingDTOTest1;
	private RatingDTO RatingDTOTest2;
	private RatingDTO RatingDTONouveau;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(ratingController).build();

		RatingDTOTest1 = new RatingDTO(1, "Aaa", "AA", "A+", 1);
		RatingDTOTest2 = new RatingDTO(2, "Bbb", "BB", "B+", 2);
		RatingDTONouveau = new RatingDTO(null, "Ccc", "CC", "C+", 3);
	}

	@Test
	void testHome_devraitRetournerVueListeAvecDTOs() throws Exception {
		when(ratingServiceMock.getAllRatings()).thenReturn(Arrays.asList(RatingDTOTest1, RatingDTOTest2));

		mockMvc.perform(get("/rating/list"))
				.andExpect(status().isOk())
				.andExpect(view().name("rating/list"))
				.andExpect(model().attributeExists("ratings"))
				.andExpect(model().attribute("ratings", Arrays.asList(RatingDTOTest1, RatingDTOTest2)));

		verify(ratingServiceMock, times(1)).getAllRatings();
	}

	@Test
	void testHome_quandServiceLeveException_devraitAfficherMessageErreur() throws Exception {
		when(ratingServiceMock.getAllRatings()).thenThrow(new RuntimeException("Erreur DB simulée"));

		mockMvc.perform(get("/rating/list"))
				.andExpect(status().isOk())
				.andExpect(view().name("rating/list"))
				.andExpect(model().attributeExists("errorMessage"))
				.andExpect(model().attribute("errorMessage", "Erreur lors de la récupération des notations."));


		verify(ratingServiceMock, times(1)).getAllRatings();
	}


	@Test
	void testAddRatingForm_devraitRetournerVueAjoutAvecDTO() throws Exception {
		mockMvc.perform(get("/rating/add"))
				.andExpect(status().isOk())
				.andExpect(view().name("rating/add"))
				.andExpect(model().attributeExists("RatingDTO"))
				.andExpect(model().attribute("RatingDTO", instanceOf(RatingDTO.class)));
	}

	@Test
	void testValidate_avecDTOValide_devraitSauvegarderEtRediriger() throws Exception {
		RatingDTO dtoSauvegarde = new RatingDTO(3, "Ccc", "CC", "C+", 3); // Simule l'ID après sauvegarde
		when(ratingServiceMock.saveRating(any(RatingDTO.class))).thenReturn(dtoSauvegarde);

		mockMvc.perform(post("/rating/validate")
						.param("moodysRating", RatingDTONouveau.getMoodysRating())
						.param("sandPRating", RatingDTONouveau.getSandPRating())
						.param("fitchRating", RatingDTONouveau.getFitchRating())
						.param("orderNumber", RatingDTONouveau.getOrderNumber().toString())
						.flashAttr("RatingDTO", RatingDTONouveau) // Objet utilisé par @Valid
				)
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/rating/list"))
				.andExpect(flash().attributeExists("successMessage"));

		verify(ratingServiceMock, times(1)).saveRating(any(RatingDTO.class));
	}

	@Test
	void testValidate_avecDTOInvalide_devraitRetournerVueAjout() throws Exception {
		// Simuler un DTO avec un champ moodysRating vide
		mockMvc.perform(post("/rating/validate")
						.param("moodysRating", "") // Invalide
						.param("sandPRating", "AA")
						.param("fitchRating", "A+")
						.param("orderNumber", "1")
						.flashAttr("RatingDTO", new RatingDTO(null, "", "AA", "A+", 1))
				)
				.andExpect(status().isOk())
				.andExpect(view().name("rating/add"))
				.andExpect(model().attributeHasFieldErrors("RatingDTO", "moodysRating"));

		verify(ratingServiceMock, never()).saveRating(any(RatingDTO.class));
	}

	@Test
	void testValidate_quandServiceLeveException_devraitRetournerVueAjoutAvecErreur() throws Exception {
		when(ratingServiceMock.saveRating(any(RatingDTO.class))).thenThrow(new RuntimeException("Erreur de sauvegarde"));

		mockMvc.perform(post("/rating/validate")
						.param("moodysRating", RatingDTONouveau.getMoodysRating())
						.param("sandPRating", RatingDTONouveau.getSandPRating())
						.param("fitchRating", RatingDTONouveau.getFitchRating())
						.param("orderNumber", RatingDTONouveau.getOrderNumber().toString())
						.flashAttr("RatingDTO", RatingDTONouveau))
				.andExpect(status().isOk())
				.andExpect(view().name("rating/add"))
				.andExpect(model().attributeExists("errorMessage"));

		verify(ratingServiceMock, times(1)).saveRating(any(RatingDTO.class));
	}


	@Test
	void testShowUpdateForm_siDTOTrouve_devraitRetournerVueMiseAJour() throws Exception {
		when(ratingServiceMock.getRatingById(1)).thenReturn(Optional.of(RatingDTOTest1));

		mockMvc.perform(get("/rating/update/1"))
				.andExpect(status().isOk())
				.andExpect(view().name("rating/update"))
				.andExpect(model().attributeExists("RatingDTO"))
				.andExpect(model().attribute("RatingDTO", RatingDTOTest1));

		verify(ratingServiceMock, times(1)).getRatingById(1);
	}

	@Test
	void testShowUpdateForm_siDTONonTrouve_devraitRedirigerVersListeAvecErreur() throws Exception {
		when(ratingServiceMock.getRatingById(99)).thenReturn(Optional.empty());

		mockMvc.perform(get("/rating/update/99"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/rating/list"))
				.andExpect(flash().attributeExists("errorMessage"));

		verify(ratingServiceMock, times(1)).getRatingById(99);
	}

	@Test
	void testUpdateRating_avecDTOValide_devraitMettreAJourEtRediriger() throws Exception {
		RatingDTO dtoPourMiseAJour = new RatingDTO(1, "AaaUpdated", "AA-Updated", "A+Updated", 10);
		when(ratingServiceMock.saveRating(any(RatingDTO.class))).thenReturn(dtoPourMiseAJour);

		mockMvc.perform(post("/rating/update/1")
						.param("id", "1") // Important pour le DTO dans le binding
						.param("moodysRating", dtoPourMiseAJour.getMoodysRating())
						.param("sandPRating", dtoPourMiseAJour.getSandPRating())
						.param("fitchRating", dtoPourMiseAJour.getFitchRating())
						.param("orderNumber", dtoPourMiseAJour.getOrderNumber().toString())
						.flashAttr("RatingDTO", dtoPourMiseAJour)
				)
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/rating/list"))
				.andExpect(flash().attributeExists("successMessage"));

		verify(ratingServiceMock, times(1)).saveRating(argThat(dto ->
				dto.getId().equals(1) && dto.getMoodysRating().equals("AaaUpdated")
		));
	}

	@Test
	void testUpdateRating_avecDTOInvalide_devraitRetournerVueMiseAJour() throws Exception {
		mockMvc.perform(post("/rating/update/1")
						.param("id", "1")
						.param("moodysRating", "") // Invalide
						.param("sandPRating", "AA")
						.param("fitchRating", "A+")
						.param("orderNumber", "1")
						.flashAttr("RatingDTO", new RatingDTO(1, "", "AA", "A+", 1))
				)
				.andExpect(status().isOk())
				.andExpect(view().name("rating/update"))
				.andExpect(model().attributeHasFieldErrors("RatingDTO", "moodysRating"))
				.andExpect(model().attributeExists("RatingDTO"));

		verify(ratingServiceMock, never()).saveRating(any(RatingDTO.class));
	}

	@Test
	void testUpdateRating_quandServiceLeveException_devraitRetournerVueMiseAJourAvecErreur() throws Exception {
		RatingDTO dtoPourMiseAJour = new RatingDTO(1, "Valid", "Valid", "Valid", 1);
		when(ratingServiceMock.saveRating(any(RatingDTO.class))).thenThrow(new RuntimeException("Erreur DB simulée"));

		mockMvc.perform(post("/rating/update/1")
						.param("id", "1")
						.param("moodysRating", dtoPourMiseAJour.getMoodysRating())
						.param("sandPRating", dtoPourMiseAJour.getSandPRating())
						.param("fitchRating", dtoPourMiseAJour.getFitchRating())
						.param("orderNumber", dtoPourMiseAJour.getOrderNumber().toString())
						.flashAttr("RatingDTO", dtoPourMiseAJour))
				.andExpect(status().isOk())
				.andExpect(view().name("rating/update"))
				.andExpect(model().attributeExists("errorMessage"));

		verify(ratingServiceMock, times(1)).saveRating(any(RatingDTO.class));
	}


	@Test
	void testDeleteRating_siIdTrouve_devraitSupprimerEtRediriger() throws Exception {
		doNothing().when(ratingServiceMock).deleteRating(1);

		mockMvc.perform(get("/rating/delete/1"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/rating/list"))
				.andExpect(flash().attributeExists("successMessage"));

		verify(ratingServiceMock, times(1)).deleteRating(1);
	}

	@Test
	void testDeleteRating_siIdNonTrouveOuErreurService_devraitRedirigerAvecMessageErreur() throws Exception {
		doThrow(new IllegalArgumentException("Rating non trouvé")).when(ratingServiceMock).deleteRating(99);

		mockMvc.perform(get("/rating/delete/99"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/rating/list"))
				.andExpect(flash().attributeExists("errorMessage"));

		verify(ratingServiceMock, times(1)).deleteRating(99);
	}
}