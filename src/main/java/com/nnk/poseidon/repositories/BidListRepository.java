package com.nnk.poseidon.repositories;

import com.nnk.poseidon.domain.BidList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface de repository pour l'entité {@link BidList}.
 * <p>
 * Cette interface utilise la puissance de Spring Data JPA pour fournir un ensemble complet
 * d'opérations CRUD (Create, Read, Update, Delete) pour l'entité {@link BidList}
 * sans nécessiter de code d'implémentation. Spring génère automatiquement un bean proxy
 * qui implémente cette interface au démarrage de l'application.
 * </p>
 * <p>
 * En étendant {@link JpaRepository}, cette interface hérite de nombreuses méthodes prêtes à l'emploi,
 * telles que {@code save(S entity)}, {@code findById(ID id)}, {@code findAll()},
 * {@code deleteById(ID id)}, ainsi que des fonctionnalités de pagination et de tri.
 * </p>
 * <p>
 * Des méthodes de requête personnalisées peuvent être ajoutées en déclarant simplement des
 * signatures de méthode qui suivent les conventions de nommage de Spring Data. Par exemple :
 * <pre>
 * {@code
 * List<BidList> findByAccount(String account);
 * }
 * </pre>
 *
 *
 * @see BidList L'entité JPA gérée par ce repository.
 * @see JpaRepository L'interface de base de Spring Data JPA fournissant les opérations CRUD.
 */
@Repository // Optionnel, mais recommandé pour la clarté et la détection d'exceptions.
public interface BidListRepository extends JpaRepository<BidList, Integer> {

    // Aucune implémentation n'est nécessaire ici.
    // Spring Data JPA implémente cette interface pour nous.
    // Il suffit d'ajouter des signatures de méthodes pour des requêtes personnalisées.

}