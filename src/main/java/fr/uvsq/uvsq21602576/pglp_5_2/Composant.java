package fr.uvsq.uvsq21602576.pglp_5_2;

import java.util.ArrayList;

/**
 * Interface composant.
 * Noeud de l'arbre
 * @author Flora
 */
public interface Composant {

    /**
     * Crée une représentation hierarchique de l'annuaire.
     * La renvoie sous forme de chaine de caractère.
     * (Parcours en profondeur récursif)
     * @return représentation hiérarchique
     */
    ArrayList<String> hierarchie();
}
