package com.nnk.poseidon.repositories;

import com.nnk.poseidon.domain.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Importation pour l'exemple dans la Javadoc

/**
 * Interface de repository pour la gestion des entités {@link Trade}.
 * <p>
 * Cette interface s'appuie sur le framework Spring Data JPA pour fournir une couche de persistance complète
 * pour les transactions (trades). Spring génère une implémentation de cette interface au moment de l'exécution,
 * ce qui permet de se concentrer sur la logique métier plutôt que sur le code de persistance standard.
 * </p>
 * <p>
 * En étendant {@link JpaRepository}, cette interface hérite d'un ensemble complet de méthodes CRUD
 * (Create, Read, Update, Delete) telles que {@code save()}, {@code findById()}, {@code findAll()},
 * {@code deleteById()}, ainsi que des fonctionnalités de pagination et de tri.
 * </p>
 * <p>
 * Des méthodes de requête personnalisées peuvent être ajoutées en déclarant simplement leur signature,
 * en respectant les conventions de nommage de Spring Data. Par exemple, pour récupérer toutes les transactions
 * associées à un compte client spécifique :
 * <pre>
 * {@code
 * // Retourne tous les trades associés à un compte donné.
 * List<Trade> findByAccount(String account);
 * }
 * </pre>
 *
 *
 * @see Trade L'entité JPA gérée par ce repository.
 * @see JpaRepository L'interface de base de Spring Data JPA.
 */
@Repository // Recommandé pour la sémantique de la couche de persistance et pour la traduction d'exceptions.
public interface TradeRepository extends JpaRepository<Trade, Integer> {

    // Aucune implémentation n'est requise ici pour les opérations de base.
    // Les requêtes personnalisées sont définies par leur simple signature.

}