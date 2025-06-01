package com.nnk.poseidon.controllers;

import com.nnk.poseidon.domain.Rating;
import com.nnk.poseidon.dto.RatingDto;
import com.nnk.poseidon.services.RatingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
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
		excludeAutoConfiguration = {SecurityAutoConfiguration.class}) // Désactive Spring Security pour ces tests
public class RatingControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private RatingService ratingService;

	// --- Tests pour /rating/list (home) ---
	@Test
	public void home_shouldReturnListViewWithRatings_whenRatingsExist() throws Exception {
		Rating rating1 = new Rating(1, "Moodys1", "SandP1", "Fitch1", 10);
		Rating rating2 = new Rating(2, "Moodys2", "SandP2", "Fitch2", 20);
		List<Rating> ratings = Arrays.asList(rating1, rating2);
		when(ratingService.getAllRatings()).thenReturn(ratings);

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
		when(ratingService.getAllRatings()).thenReturn(Collections.emptyList());

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
		when(ratingService.getAllRatings()).thenThrow(new RuntimeException("Database error"));

		mockMvc.perform(get("/rating/list"))
				.andExpect(status().isOk())
				.andExpect(view().name("rating/list"))
				.andExpect(model().attributeExists("ratings"))
				.andExpect(model().attribute("ratings", hasSize(0)))
				.andExpect(model().attributeExists("error"))
				.andExpect(model().attribute("error", "Erreur lors de la récupération des ratings"));

		verify(ratingService, times(1)).getAllRatings();
	}

	// --- Tests pour /rating/add (addRatingForm) ---
	@Test
	public void addRatingForm_shouldReturnAddViewWithNewRatingDto() throws Exception {
		mockMvc.perform(get("/rating/add"))
				.andExpect(status().isOk())
				.andExpect(view().name("rating/add"))
				.andExpect(model().attributeExists("ratingDto"))
				.andExpect(model().attribute("ratingDto", instanceOf(RatingDto.class)));
	}

	// --- Tests pour /rating/validate (validate) ---
	@Test
	public void validate_shouldRedirectToList_whenRatingDtoIsValidAndSaved() throws Exception {
		RatingDto ratingDto = new RatingDto(null, "ValidMoodys", "ValidSandP", "ValidFitch", 100);

		mockMvc.perform(post("/rating/validate")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("moodysRating", ratingDto.getMoodysRating())
						.param("sandPRating", ratingDto.getSandPRating())
						.param("fitchRating", ratingDto.getFitchRating())
						.param("orderNumber", String.valueOf(ratingDto.getOrderNumber())))
				.andExpect(status().is3xxRedirection())
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
		RatingDto invalidRatingDto = new RatingDto(null, "", "ValidSandP", "ValidFitch", 100);

		mockMvc.perform(post("/rating/validate")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("moodysRating", invalidRatingDto.getMoodysRating())
						.param("sandPRating", invalidRatingDto.getSandPRating())
						.param("fitchRating", invalidRatingDto.getFitchRating())
						.param("orderNumber", String.valueOf(invalidRatingDto.getOrderNumber())))
				.andExpect(status().isOk())
				.andExpect(view().name("rating/add"))
				.andExpect(model().hasErrors())
				.andExpect(model().attributeExists("error"))
				.andExpect(model().attribute("error", "Erreur de validation des données"))
				.andExpect(model().attributeExists("ratingDto"));

		verify(ratingService, never()).saveRating(any(Rating.class));
	}

	@Test
	public void validate_shouldReturnAddViewWithError_whenServiceThrowsExceptionOnSave() throws Exception {
		RatingDto ratingDto = new RatingDto(null, "ValidMoodys", "ValidSandP", "ValidFitch", 100);
		doThrow(new RuntimeException("Database save error")).when(ratingService).saveRating(any(Rating.class));

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
				.andExpect(model().attributeExists("ratingDto"));

		verify(ratingService, times(1)).saveRating(any(Rating.class));
	}

	// --- Tests pour /rating/update/{id} (showUpdateForm) ---
	@Test
	public void showUpdateForm_shouldReturnUpdateViewWithRatingDto_whenRatingExists() throws Exception {
		Integer ratingId = 1;
		Rating existingRating = new Rating(ratingId, "OldMoodys", "OldSandP", "OldFitch", 50);
		when(ratingService.getRatingById(ratingId)).thenReturn(existingRating);

		mockMvc.perform(get("/rating/update/{id}", ratingId))
				.andExpect(status().isOk())
				.andExpect(view().name("rating/update"))
				.andExpect(model().attributeExists("ratingDto"))
				.andExpect(model().attribute("ratingDto", hasProperty("id", is(ratingId))))
				.andExpect(model().attribute("ratingDto", hasProperty("moodysRating", is("OldMoodys"))))
				.andExpect(model().attribute("ratingDto", hasProperty("sandPRating", is("OldSandP"))))
				.andExpect(model().attribute("ratingDto", hasProperty("fitchRating", is("OldFitch"))))
				.andExpect(model().attribute("ratingDto", hasProperty("orderNumber", is(50))));

		verify(ratingService, times(1)).getRatingById(ratingId);
	}

	@Test
	public void showUpdateForm_shouldRedirectToListWithError_whenRatingDoesNotExist() throws Exception {
		Integer ratingId = 99;
		when(ratingService.getRatingById(ratingId)).thenReturn(null);

		mockMvc.perform(get("/rating/update/{id}", ratingId))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/rating/list"));

		verify(ratingService, times(1)).getRatingById(ratingId);
	}

	// --- Tests pour /rating/update/{id} (updateRating) ---
	@Test
	public void updateRating_shouldRedirectToList_whenDtoIsValidAndRatingExists() throws Exception {
		Integer ratingId = 1;
		RatingDto ratingDto = new RatingDto(ratingId, "UpdatedMoodys", "UpdatedSandP", "UpdatedFitch", 150);
		Rating existingRating = new Rating(ratingId, "OldMoodys", "OldSandP", "OldFitch", 50);

		when(ratingService.getRatingById(ratingId)).thenReturn(existingRating);
		// doNothing().when(ratingService).saveRating(any(Rating.class)); // Optionnel

		mockMvc.perform(post("/rating/update/{id}", ratingId)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("id", String.valueOf(ratingDto.getId())) // Important que le DTO ait l'ID
						.param("moodysRating", ratingDto.getMoodysRating())
						.param("sandPRating", ratingDto.getSandPRating())
						.param("fitchRating", ratingDto.getFitchRating())
						.param("orderNumber", String.valueOf(ratingDto.getOrderNumber())))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/rating/list"));

		ArgumentCaptor<Rating> ratingCaptor = ArgumentCaptor.forClass(Rating.class);
		verify(ratingService, times(1)).getRatingById(ratingId);
		verify(ratingService, times(1)).saveRating(ratingCaptor.capture());

		Rating capturedRating = ratingCaptor.getValue();
		assertEquals(ratingDto.getId(), capturedRating.getId());
		assertEquals(ratingDto.getMoodysRating(), capturedRating.getMoodysRating());
		assertEquals(ratingDto.getSandPRating(), capturedRating.getSandPRating());
		assertEquals(ratingDto.getFitchRating(), capturedRating.getFitchRating());
		assertEquals(ratingDto.getOrderNumber(), capturedRating.getOrderNumber());
	}

	@Test
	public void updateRating_shouldReturnUpdateViewWithError_whenDtoIsInvalid() throws Exception {
		Integer ratingId = 1;
		// DTO invalide (moodysRating est vide)
		RatingDto invalidRatingDto = new RatingDto(ratingId, "", "UpdatedSandP", "UpdatedFitch", 150);

		mockMvc.perform(post("/rating/update/{id}", ratingId)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("id", String.valueOf(invalidRatingDto.getId()))
						.param("moodysRating", invalidRatingDto.getMoodysRating())
						.param("sandPRating", invalidRatingDto.getSandPRating())
						.param("fitchRating", invalidRatingDto.getFitchRating())
						.param("orderNumber", String.valueOf(invalidRatingDto.getOrderNumber())))
				.andExpect(status().isOk())
				.andExpect(view().name("rating/update"))
				.andExpect(model().hasErrors())
				.andExpect(model().attributeExists("error"))
				.andExpect(model().attribute("error", "Erreur de validation des données"))
				.andExpect(model().attribute("ratingDto", hasProperty("moodysRating", is("")))); // Vérifie que le DTO est retourné

		verify(ratingService, never()).getRatingById(anyInt());
		verify(ratingService, never()).saveRating(any(Rating.class));
	}

	@Test
	public void updateRating_shouldRedirectToListWithError_whenRatingToUpdateNotFound() throws Exception {
		Integer ratingId = 99; // ID non existant
		RatingDto ratingDto = new RatingDto(ratingId, "UpdatedMoodys", "UpdatedSandP", "UpdatedFitch", 150);

		when(ratingService.getRatingById(ratingId)).thenReturn(null);

		mockMvc.perform(post("/rating/update/{id}", ratingId)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("id", String.valueOf(ratingDto.getId()))
						.param("moodysRating", ratingDto.getMoodysRating())
						.param("sandPRating", ratingDto.getSandPRating())
						.param("fitchRating", ratingDto.getFitchRating())
						.param("orderNumber", String.valueOf(ratingDto.getOrderNumber())))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/rating/list"));

		verify(ratingService, times(1)).getRatingById(ratingId);
		verify(ratingService, never()).saveRating(any(Rating.class));
	}

	@Test
	public void updateRating_shouldReturnUpdateViewWithError_whenServiceThrowsExceptionOnSave() throws Exception {
		Integer ratingId = 1;
		RatingDto ratingDto = new RatingDto(ratingId, "UpdatedMoodys", "UpdatedSandP", "UpdatedFitch", 150);
		Rating existingRating = new Rating(ratingId, "OldMoodys", "OldSandP", "OldFitch", 50);

		when(ratingService.getRatingById(ratingId)).thenReturn(existingRating);
		doThrow(new RuntimeException("Database save error")).when(ratingService).saveRating(any(Rating.class));

		mockMvc.perform(post("/rating/update/{id}", ratingId)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("id", String.valueOf(ratingDto.getId()))
						.param("moodysRating", ratingDto.getMoodysRating())
						.param("sandPRating", ratingDto.getSandPRating())
						.param("fitchRating", ratingDto.getFitchRating())
						.param("orderNumber", String.valueOf(ratingDto.getOrderNumber())))
				.andExpect(status().isOk())
				.andExpect(view().name("rating/update"))
				.andExpect(model().attributeExists("error"))
				.andExpect(model().attribute("error", "Erreur lors de la mise à jour du rating"))
				.andExpect(model().attributeExists("ratingDto"));

		verify(ratingService, times(1)).getRatingById(ratingId);
		verify(ratingService, times(1)).saveRating(any(Rating.class));
	}


	// --- Tests pour /rating/delete/{id} (deleteRating) ---
	@Test
	public void deleteRating_shouldRedirectToList_whenRatingExistsAndDeleted() throws Exception {
		Integer ratingId = 1;
		Rating existingRating = new Rating(ratingId, "Moodys", "SandP", "Fitch", 10);
		when(ratingService.getRatingById(ratingId)).thenReturn(existingRating);
		doNothing().when(ratingService).deleteRating(ratingId);

		mockMvc.perform(get("/rating/delete/{id}", ratingId))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/rating/list"));

		verify(ratingService, times(1)).getRatingById(ratingId);
		verify(ratingService, times(1)).deleteRating(ratingId);
	}

	@Test
	public void deleteRating_shouldRedirectToListWithError_whenRatingNotFound() throws Exception {
		Integer ratingId = 99;
		when(ratingService.getRatingById(ratingId)).thenReturn(null);

		mockMvc.perform(get("/rating/delete/{id}", ratingId))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/rating/list"));

		verify(ratingService, times(1)).getRatingById(ratingId);
		verify(ratingService, never()).deleteRating(anyInt());
	}

	@Test
	public void deleteRating_shouldRedirectToListWithError_whenServiceThrowsExceptionOnDelete() throws Exception {
		Integer ratingId = 1;
		Rating existingRating = new Rating(ratingId, "Moodys", "SandP", "Fitch", 10);
		when(ratingService.getRatingById(ratingId)).thenReturn(existingRating);
		doThrow(new RuntimeException("Database delete error")).when(ratingService).deleteRating(ratingId);

		mockMvc.perform(get("/rating/delete/{id}", ratingId))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/rating/list"));

		verify(ratingService, times(1)).getRatingById(ratingId);
		verify(ratingService, times(1)).deleteRating(ratingId);
	}
}