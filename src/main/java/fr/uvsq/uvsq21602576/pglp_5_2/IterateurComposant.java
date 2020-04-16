package fr.uvsq.uvsq21602576.pglp_5_2;

import java.util.ArrayList;

/**
 * Iterateur de composants.
 * @author Flora
 */
public class IterateurComposant {
    /**
     * Liste de composant.
     */
    private ArrayList<Composant> list;

    /**
     * Constructeur.
     * Crée un itérateur à partir d'une liste de composants.
     * @param groupe Liste de composants
     */
    public IterateurComposant(final ArrayList<Composant> groupe) {
        this.list = new ArrayList<Composant>();
        this.list.addAll(groupe);
    }

    /**
     * Teste si l'iterateur possède encore un élément.
     * @return false s'il est vide, true sinon
     */
    public boolean hasNext() {
        return !this.list.isEmpty();
    }

    /**
     * Retourne le prochain élément de l'itérateur et le retire de celui-ci.
     * @return Composant
     */
    public Composant next() {
        return list.remove(0);
    }

}
