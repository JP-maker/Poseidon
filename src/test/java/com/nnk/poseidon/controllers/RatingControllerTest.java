package com.nnk.poseidon.controllers;

import com.nnk.poseidon.domain.Rating;
import com.nnk.poseidon.dto.RatingDto;
import com.nnk.poseidon.services.RatingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = RatingController.class,
		excludeAutoConfiguration = {SecurityAutoConfiguration.class}) // Teste uniquement la couche Web pour RatingController
public class RatingControllerTest {

	@Autowired
	private MockMvc mockMvc; // Pour simuler les requêtes HTTP

	@MockitoBean // Crée un mock de RatingService et l'injecte dans le contexte
	private RatingService ratingService;

	@Test
	public void home_shouldReturnListViewWithRatings_whenRatingsExist() throws Exception {
		// Arrange
		Rating rating1 = new Rating(1, "Moodys1", "SandP1", "Fitch1", 10);
		Rating rating2 = new Rating(2, "Moodys2", "SandP2", "Fitch2", 20);
		List<Rating> ratings = Arrays.asList(rating1, rating2);
		when(ratingService.getAllRatings()).thenReturn(ratings);

		// Act & Assert
		mockMvc.perform(get("/rating/list"))
				.andExpect(status().isOk())
				.andExpect(view().name("rating/list"))
				.andExpect(model().attributeExists("ratings"))
				.andExpect(model().attribute("ratings", hasSize(2)))
				.andExpect(model().attribute("ratings", containsInAnyOrder(rating1, rating2)))
				.andExpect(model().attributeDoesNotExist("error"));

		verify(ratingService, times(1)).getAllRatings();
	}

	@Test
	public void home_shouldReturnListViewWithEmptyList_whenNoRatings() throws Exception {
		// Arrange
		when(ratingService.getAllRatings()).thenReturn(Collections.emptyList());

		// Act & Assert
		mockMvc.perform(get("/rating/list"))
				.andExpect(status().isOk())
				.andExpect(view().name("rating/list"))
				.andExpect(model().attributeExists("ratings"))
				.andExpect(model().attribute("ratings", hasSize(0)))
				.andExpect(model().attributeDoesNotExist("error"));

		verify(ratingService, times(1)).getAllRatings();
	}

	@Test
	public void home_shouldReturnListViewWithError_whenServiceThrowsException() throws Exception {
		// Arrange
		when(ratingService.getAllRatings()).thenThrow(new RuntimeException("Database error"));

		// Act & Assert
		mockMvc.perform(get("/rating/list"))
				.andExpect(status().isOk()) // Le contrôleur gère l'exception et retourne une vue
				.andExpect(view().name("rating/list"))
				.andExpect(model().attributeExists("ratings")) // Devrait être la liste vide initialisée
				.andExpect(model().attribute("ratings", hasSize(0)))
				.andExpect(model().attributeExists("error"))
				.andExpect(model().attribute("error", "Erreur lors de la récupération des ratings"));

		verify(ratingService, times(1)).getAllRatings();
	}

	@Test
	public void addRatingForm_shouldReturnAddViewWithNewRatingDto() throws Exception {
		// Act & Assert
		mockMvc.perform(get("/rating/add"))
				.andExpect(status().isOk())
				.andExpect(view().name("rating/add"))
				.andExpect(model().attributeExists("ratingDto"))
				.andExpect(model().attribute("ratingDto", instanceOf(RatingDto.class)));
	}

	@Test
	public void validate_shouldRedirectToList_whenRatingDtoIsValidAndSaved() throws Exception {
		// Arrange
		RatingDto ratingDto = new RatingDto(null, "ValidMoodys", "ValidSandP", "ValidFitch", 100);
		// Pas besoin de mocker saveRating explicitement si on ne vérifie pas son retour, juste qu'elle est appelée.
		// doNothing().when(ratingService).saveRating(any(Rating.class)); // Alternative

		// Act & Assert
		mockMvc.perform(post("/rating/validate")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("moodysRating", ratingDto.getMoodysRating())
						.param("sandPRating", ratingDto.getSandPRating())
						.param("fitchRating", ratingDto.getFitchRating())
						.param("orderNumber", String.valueOf(ratingDto.getOrderNumber())))
				.andExpect(status().is3xxRedirection()) // Redirection
				.andExpect(redirectedUrl("/rating/list"));

		ArgumentCaptor<Rating> ratingCaptor = ArgumentCaptor.forClass(Rating.class);
		verify(ratingService, times(1)).saveRating(ratingCaptor.capture());

		Rating capturedRating = ratingCaptor.getValue();
		assertEquals(ratingDto.getMoodysRating(), capturedRating.getMoodysRating());
		assertEquals(ratingDto.getSandPRating(), capturedRating.getSandPRating());
		assertEquals(ratingDto.getFitchRating(), capturedRating.getFitchRating());
		assertEquals(ratingDto.getOrderNumber(), capturedRating.getOrderNumber());
	}

	@Test
	public void validate_shouldReturnAddViewWithError_whenRatingDtoIsInvalid() throws Exception {
		// Arrange
		// Envoi d'un champ vide qui devrait déclencher @NotBlank
		RatingDto invalidRatingDto = new RatingDto(null, "", "ValidSandP", "ValidFitch", 100);

		// Act & Assert
		mockMvc.perform(post("/rating/validate")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("moodysRating", invalidRatingDto.getMoodysRating()) // Ce champ est invalide
						.param("sandPRating", invalidRatingDto.getSandPRating())
						.param("fitchRating", invalidRatingDto.getFitchRating())
						.param("orderNumber", String.valueOf(invalidRatingDto.getOrderNumber())))
				.andExpect(status().isOk())
				.andExpect(view().name("rating/add"))
				.andExpect(model().hasErrors()) // Vérifie qu'il y a des erreurs de binding
				.andExpect(model().attributeExists("error"))
				.andExpect(model().attribute("error", "Erreur de validation des données"))
				.andExpect(model().attributeExists("ratingDto")); // Le DTO doit être retourné au formulaire

		verify(ratingService, never()).saveRating(any(Rating.class));
	}

	@Test
	public void validate_shouldReturnAddViewWithError_whenServiceThrowsExceptionOnSave() throws Exception {
		// Arrange
		RatingDto ratingDto = new RatingDto(null, "ValidMoodys", "ValidSandP", "ValidFitch", 100);
		doThrow(new RuntimeException("Database save error")).when(ratingService).saveRating(any(Rating.class));

		// Act & Assert
		mockMvc.perform(post("/rating/validate")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("moodysRating", ratingDto.getMoodysRating())
						.param("sandPRating", ratingDto.getSandPRating())
						.param("fitchRating", ratingDto.getFitchRating())
						.param("orderNumber", String.valueOf(ratingDto.getOrderNumber())))
				.andExpect(status().isOk())
				.andExpect(view().name("rating/add"))
				.andExpect(model().attributeExists("error"))
				.andExpect(model().attribute("error", "Erreur lors de l'ajout du rating"))
				.andExpect(model().attributeExists("ratingDto")); // Le DTO doit être retourné au formulaire

		verify(ratingService, times(1)).saveRating(any(Rating.class));
	}
}
