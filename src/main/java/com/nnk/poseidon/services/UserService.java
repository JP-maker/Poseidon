package com.nnk.poseidon.service; // Créez ce package si nécessaire

import com.nnk.poseidon.domain.User;
import com.nnk.poseidon.dto.UserDTO;
import com.nnk.poseidon.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des opérations CRUD sur les entités {@link User}.
 * Gère la logique métier, y compris l'encodage des mots de passe,
 * et interagit avec {@link UserRepository} pour l'accès aux données.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder; // Injecter l'encodeur

    /**
     * Constructeur pour l'injection de dépendances.
     *
     * @param userRepository  le repository pour les entités User.
     * @param passwordEncoder l'encodeur de mot de passe.
     */
    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Récupère tous les utilisateurs et les convertit en DTOs.
     *
     * @return une liste de {@link UserDTO}.
     */
    @Transactional(readOnly = true)
    public List<UserDTO> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un utilisateur par son identifiant et le convertit en DTO.
     * Le mot de passe n'est pas inclus dans le DTO retourné par cette méthode
     * pour des raisons de sécurité (il ne doit pas être ré-affiché).
     *
     * @param id l'identifiant de l'utilisateur.
     * @return un {@link Optional} contenant le {@link UserDTO} si trouvé, sinon {@link Optional#empty()}.
     */
    @Transactional(readOnly = true)
    public Optional<UserDTO> findUserById(Integer id) {
        return userRepository.findById(id)
                .map(user -> {
                    UserDTO dto = convertToDTO(user);
                    dto.setPassword(null); // Ne pas exposer le mot de passe (même encodé)
                    return dto;
                });
    }

    /**
     * Crée un nouvel utilisateur. Le mot de passe fourni dans le DTO est encodé.
     *
     * @param userDTO le {@link UserDTO} contenant les informations du nouvel utilisateur.
     * @return le {@link UserDTO} représentant l'utilisateur sauvegardé (sans le mot de passe en clair).
     * @throws IllegalArgumentException si un utilisateur avec le même nom d'utilisateur existe déjà.
     */
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username " + userDTO.getUsername() + " already exists.");
        }
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setFullname(userDTO.getFullname());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Encodage ici
        user.setRole(userDTO.getRole());

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    /**
     * Met à jour un utilisateur existant.
     * Si un nouveau mot de passe est fourni dans le DTO (non vide/null), il est encodé et mis à jour.
     * Sinon, l'ancien mot de passe est conservé.
     *
     * @param id      l'identifiant de l'utilisateur à mettre à jour.
     * @param userDTO le {@link UserDTO} contenant les nouvelles informations.
     * @return un {@link Optional} de {@link UserDTO} mis à jour si l'utilisateur est trouvé.
     * @throws IllegalArgumentException si l'ID utilisateur n'est pas trouvé ou si un autre utilisateur
     *                                  possède déjà le nouveau nom d'utilisateur (si modifié).
     */
    @Transactional
    public Optional<UserDTO> updateUser(Integer id, UserDTO userDTO) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    // Vérifier si le nom d'utilisateur change et s'il est déjà pris par un autre utilisateur
                    if (!existingUser.getUsername().equals(userDTO.getUsername()) &&
                            userRepository.findByUsername(userDTO.getUsername()).filter(u -> !u.getId().equals(id)).isPresent()) {
                        throw new IllegalArgumentException("Username " + userDTO.getUsername() + " is already taken by another user.");
                    }

                    existingUser.setUsername(userDTO.getUsername());
                    existingUser.setFullname(userDTO.getFullname());
                    existingUser.setRole(userDTO.getRole());

                    // Mettre à jour le mot de passe seulement s'il est fourni et non vide
                    if (userDTO.getPassword() != null && !userDTO.getPassword().trim().isEmpty()) {
                        existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
                    }
                    // Sinon, l'ancien mot de passe reste.

                    User updatedUser = userRepository.save(existingUser);
                    return convertToDTO(updatedUser);
                });
    }

    /**
     * Supprime un utilisateur par son identifiant.
     *
     * @param id l'identifiant de l'utilisateur à supprimer.
     * @throws IllegalArgumentException si l'ID utilisateur n'est pas trouvé.
     */
    @Transactional
    public void deleteUserById(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Invalid user Id:" + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Convertit une entité {@link User} en {@link UserDTO}.
     * Le mot de passe n'est pas copié dans le DTO pour des raisons de sécurité.
     *
     * @param user l'entité utilisateur.
     * @return le {@link UserDTO} correspondant.
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullname(user.getFullname());
        dto.setRole(user.getRole());
        // dto.setPassword(null); // Important: Ne pas exposer le mot de passe, même encodé
        return dto;
    }

    // Pas besoin de convertToEntity ici car la création/mise à jour est gérée directement
    // en mappant les champs du DTO vers une nouvelle entité ou une entité existante.
}