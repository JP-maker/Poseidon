package com.nnk.poseidon.repositories;


import com.nnk.poseidon.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * Interface de repository Spring Data JPA pour l'entité {@link User}.
 * Fournit des méthodes pour effectuer des opérations de persistance (CRUD)
 * sur les utilisateurs stockés dans la base de données, ainsi que des méthodes
 * de recherche personnalisées.
 */
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    /**
     * Recherche et retourne un {@link Optional} contenant l'utilisateur correspondant
     * au username fournie.
     *
     * @param username Le username de l'utilisateur à rechercher.
     * @return Un {@link Optional} contenant l'{@link User} trouvé, ou {@link Optional#empty()}
     *         si aucun utilisateur ne correspond à ce username.
     */
    Optional<User> findByUsername(String username);
}
