package com.nnk.poseidon.repositories;

import com.nnk.poseidon.domain.RuleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Importation pour l'exemple dans la Javadoc

/**
 * Interface de repository pour la gestion des entités {@link RuleName}.
 * <p>
 * Cette interface utilise la puissance de Spring Data JPA pour fournir une couche de persistance
 * complète pour les définitions de règles métier. Spring génère une implémentation de cette
 * interface au moment de l'exécution, ce qui permet aux développeurs de se concentrer sur la
 * logique métier plutôt que sur le code de persistance standard.
 * </p>
 * <p>
 * En étendant {@link JpaRepository}, cette interface hérite d'un ensemble complet de méthodes CRUD
 * (Create, Read, Update, Delete) telles que {@code save()}, {@code findById()}, {@code findAll()},
 * {@code deleteById()}, et bien d'autres.
 * </p>
 * <p>
 * Des méthodes de requête personnalisées peuvent être ajoutées en déclarant simplement leur signature,
 * en respectant les conventions de nommage de Spring Data. Par exemple, pour récupérer une règle
 * par son nom unique, qui sert d'identifiant métier :
 * <pre>
 * {@code
 * // Retourne une règle spécifique en se basant sur son nom.
 * Optional<RuleName> findByName(String name);
 * }
 * </pre>
 *
 *
 * @see RuleName L'entité JPA gérée par ce repository.
 * @see JpaRepository L'interface de base de Spring Data JPA.
 */
@Repository // Recommandé pour la sémantique et pour activer la traduction d'exceptions.
public interface RuleNameRepository extends JpaRepository<RuleName, Integer> {

    // Aucune implémentation n'est requise pour les opérations de base.
    // Il suffit de déclarer les signatures des méthodes pour les requêtes personnalisées.

}