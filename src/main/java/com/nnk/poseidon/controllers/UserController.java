package com.nnk.poseidon.controllers;

import com.nnk.poseidon.dto.UserDTO;
import com.nnk.poseidon.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;


/**
 * Contrôleur MVC pour la gestion des utilisateurs.
 * Interagit avec {@link UserService} pour effectuer les opérations CRUD.
 * Utilise {@link UserDTO} pour la communication des données et la validation.
 */
@Controller
@RequestMapping("/user") // Mieux de mettre le préfixe de base ici
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    /**
     * Constructeur pour l'injection de dépendance du service User.
     *
     * @param userService Le service {@link UserService}.
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Affiche la liste de tous les utilisateurs.
     *
     * @param model L'objet Model pour passer les données à la vue.
     * @return Le nom de la vue Thymeleaf "user/list".
     */
    @GetMapping("/list")
    public String home(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        logger.info("Affichage de la liste des utilisateurs.");
        return "user/list";
    }

    /**
     * Affiche le formulaire pour ajouter un nouvel utilisateur.
     *
     * @param model L'objet Model pour lier les données du formulaire.
     * @return Le nom de la vue Thymeleaf "user/add".
     */
    @GetMapping("/add")
    public String addUserForm(Model model) {
        // S'assurer qu'un objet "user" (DTO) est dans le modèle pour le formulaire th:object
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new UserDTO());
        }
        logger.info("Affichage du formulaire d'ajout d'utilisateur.");
        return "user/add";
    }

    /**
     * Valide et sauvegarde un nouvel utilisateur.
     *
     * @param userDTO Le {@link UserDTO} soumis depuis le formulaire.
     * @param result  Le {@link BindingResult} pour vérifier les erreurs de validation.
     * @param model   L'objet Model.
     * @param redirectAttributes Attributs pour passer des messages après redirection.
     * @return Redirection vers "/user/list" en cas de succès, sinon ré-affiche "user/add".
     */
    @PostMapping("/validate")
    public String validate(@Valid @ModelAttribute("user") UserDTO userDTO, BindingResult result,
                           Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            logger.warn("Erreurs de validation lors de l'ajout d'un utilisateur: {}", result.getAllErrors());
            // model.addAttribute("user", userDTO); // @ModelAttribute le fait déjà
            return "user/add";
        }
        try {
            userService.createUser(userDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Utilisateur ajouté avec succès !");
            logger.info("Nouvel utilisateur créé: {}", userDTO.getUsername());
            return "redirect:/user/list";
        } catch (IllegalArgumentException e) {
            logger.error("Erreur lors de la création de l'utilisateur: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage()); // Pour afficher l'erreur de username dupliqué
            // model.addAttribute("user", userDTO); // @ModelAttribute le fait déjà
            return "user/add";
        }
    }

    /**
     * Affiche le formulaire pour mettre à jour un utilisateur existant.
     *
     * @param id    L'identifiant de l'utilisateur à mettre à jour.
     * @param model L'objet Model.
     * @param redirectAttributes Attributs pour redirection si l'utilisateur n'est pas trouvé.
     * @return Le nom de la vue "user/update" ou une redirection si l'utilisateur n'est pas trouvé.
     */
    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        if (!model.containsAttribute("user")) { // Pour éviter d'écraser si on vient d'un POST avec erreurs
            Optional<UserDTO> userDTOOptional = userService.findUserById(id);
            if (userDTOOptional.isPresent()) {
                UserDTO userToUpdate = userDTOOptional.get();
                // Le mot de passe ne doit pas être pré-rempli dans le formulaire de mise à jour pour des raisons de sécurité
                // Le DTO de findUserById met déjà password à null. Le formulaire demandera un nouveau mot de passe
                // s'il doit être changé.
                model.addAttribute("user", userToUpdate);
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Utilisateur non trouvé avec l'ID: " + id);
                logger.warn("Tentative de mise à jour d'un utilisateur non trouvé. ID: {}", id);
                return "redirect:/user/list";
            }
        }
        logger.info("Affichage du formulaire de mise à jour pour l'utilisateur ID: {}", id);
        return "user/update";
    }

    /**
     * Met à jour un utilisateur existant.
     *
     * @param id      L'identifiant de l'utilisateur à mettre à jour.
     * @param userDTO Le {@link UserDTO} soumis avec les données mises à jour.
     * @param result  Le {@link BindingResult} pour la validation.
     * @param model   L'objet Model.
     * @param redirectAttributes Attributs pour passer des messages après redirection.
     * @return Redirection vers "/user/list" en cas de succès, sinon ré-affiche "user/update".
     */
    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") Integer id, @Valid @ModelAttribute("user") UserDTO userDTO,
                             BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        // S'assurer que l'ID du DTO est bien celui du path variable
        userDTO.setId(id);

        if (result.hasErrors()) {
            logger.warn("Erreurs de validation lors de la mise à jour de l'utilisateur ID {}: {}", id, result.getAllErrors());
            // model.addAttribute("user", userDTO); // @ModelAttribute le fait déjà
            return "user/update";
        }

        try {
            Optional<UserDTO> updatedUser = userService.updateUser(id, userDTO);
            if (updatedUser.isPresent()) {
                redirectAttributes.addFlashAttribute("successMessage", "Utilisateur mis à jour avec succès !");
                logger.info("Utilisateur ID {} mis à jour.", id);
            } else {
                // Ce cas est moins probable si findById est utilisé avant, mais pour la robustesse
                redirectAttributes.addFlashAttribute("errorMessage", "Échec de la mise à jour, utilisateur non trouvé avec l'ID: " + id);
                logger.error("Échec de la mise à jour de l'utilisateur ID {}, non trouvé.", id);
            }
            return "redirect:/user/list";
        } catch (IllegalArgumentException e) {
            logger.error("Erreur lors de la mise à jour de l'utilisateur ID {}: {}", id, e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            // model.addAttribute("user", userDTO); // @ModelAttribute le fait déjà
            return "user/update";
        }
    }

    /**
     * Supprime un utilisateur.
     *
     * @param id    L'identifiant de l'utilisateur à supprimer.
     * @param redirectAttributes Attributs pour passer des messages après redirection.
     * @return Redirection vers "/user/list".
     */
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUserById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Utilisateur supprimé avec succès !");
            logger.info("Utilisateur ID {} supprimé.", id);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            logger.error("Erreur lors de la suppression de l'utilisateur ID {}: {}", id, e.getMessage());
        }
        return "redirect:/user/list";
    }
}