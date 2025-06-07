package com.nnk.poseidon.repositories;

import com.nnk.poseidon.domain.CurvePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Importation pour l'exemple dans la Javadoc

/**
 * Interface de repository pour la persistance des entités {@link CurvePoint}.
 * <p>
 * Cette interface s'appuie sur le framework Spring Data JPA pour fournir une couche d'accès aux données
 * complète pour l'entité {@link CurvePoint}. Spring génère automatiquement une implémentation de cette
 * interface au moment de l'exécution, évitant ainsi la nécessité d'écrire du code de persistance répétitif.
 * </p>
 * <p>
 * En étendant {@link JpaRepository}, cette interface hérite d'un large éventail de méthodes standards,
 * incluant les opérations CRUD (Create, Read, Update, Delete) telles que {@code save()}, {@code findById()},
 * {@code findAll()} et {@code deleteById()}, ainsi que des fonctionnalités de pagination et de tri.
 * </p>
 * <p>
 * Pour des requêtes plus spécifiques, il suffit de déclarer des méthodes qui respectent les conventions
 * de nommage de Spring Data. Par exemple, pour trouver tous les points d'une courbe donnée, triés par terme :
 * <pre>
 * {@code
 * List<CurvePoint> findByCurveIdOrderByTermAsc(Integer curveId);
 * }
 * </pre>
 *
 *
 * @see CurvePoint L'entité JPA gérée par ce repository.
 * @see JpaRepository L'interface de base de Spring Data JPA.
 */
@Repository // Optionnel mais recommandé pour la sémantique et pour activer la traduction d'exceptions.
public interface CurvePointRepository extends JpaRepository<CurvePoint, Integer> {

    // Aucune méthode à implémenter ici.
    // Les requêtes personnalisées peuvent être ajoutées en suivant les conventions de Spring Data.

}
