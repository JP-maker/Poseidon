package com.nnk.poseidon.repositories;

import com.nnk.poseidon.domain.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Importation pour l'exemple dans la Javadoc

/**
 * Interface de repository pour la gestion des entités {@link Rating}.
 * <p>
 * Cette interface s'appuie sur le framework Spring Data JPA pour fournir une couche d'accès aux données
 * robuste et complète pour les notations de crédit. Spring génère dynamiquement une implémentation de cette
 * interface au démarrage, éliminant ainsi le besoin de code de persistance standard.
 * </p>
 * <p>
 * En étendant {@link JpaRepository}, cette interface bénéficie immédiatement d'un ensemble complet de méthodes
 * CRUD (Create, Read, Update, Delete) telles que {@code save()}, {@code findById()}, {@code findAll()},
 * {@code deleteById()}, et bien d'autres.
 * </p>
 * <p>
 * Des méthodes de requête personnalisées peuvent être facilement ajoutées en suivant les conventions
 * de nommage de Spring Data. Par exemple, pour trouver toutes les notations d'une qualité égale ou
 * supérieure à un certain niveau (en utilisant le champ {@code orderNumber}) :
 * <pre>
 * {@code
 * // Retourne toutes les notations où orderNumber est <= à la valeur fournie.
 * List<Rating> findByOrderNumberLessThanEqual(Integer maxOrderNumber);
 * }
 * </pre>
 *
 *
 * @see Rating L'entité JPA gérée par ce repository.
 * @see JpaRepository L'interface de base de Spring Data JPA.
 */
@Repository // Annotation recommandée pour la sémantique de la couche de persistance et la traduction d'exceptions.
public interface RatingRepository extends JpaRepository<Rating, Integer> {

    // Aucune méthode n'est nécessaire ici pour les opérations de base.
    // Les requêtes personnalisées sont définies par leur simple signature.

}