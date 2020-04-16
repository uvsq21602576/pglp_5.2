package fr.uvsq.uvsq21602576.pglp_5_2.dao;

/**
 * Classe abstraite du DAO.
 * @author Flora
 * @param <T> Classe des objets manipulés
 */
public abstract class DAO<T> {

    /**
     * Pour la création.
     * @param obj objet à créer
     * @return object créé
     */
    public abstract T create(T obj);

    /**
     * Pour la recherche.
     * @param id Identifiant de l'objet à trouvée
     * @return object trouvé
     */
    public abstract T find(String id);

    /**
     * Pour la modification.
     * @param obj objet modifié à réécrire
     * @return object modifié
     */
    public abstract T update(T obj);

    /**
     * Pour la suppression.
     * @param obj objet à supprimer
     */
    public abstract void delete(T obj);

}
