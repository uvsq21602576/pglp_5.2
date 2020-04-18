package fr.uvsq.uvsq21602576.pglp_5_2;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Représente un annuaire.
 * Contient la racine de l'arbre de composant.
 * @author Flora
 */
public class Annuaire implements Serializable {

    /**
     * Identifiant unique d'annuaire.
     */
    private final int id;
    /**
     * Racine de l'arbre de composant.
     */
    private Composant racine;

    /**
     * Constructeur.
     * Crée un annuaire avec pour racine r
     * @param i Identifiant
     * @param r Racine de l'arbre de composant
     */
    public Annuaire(final int i, final Composant r) {
        this.id = i;
        this.racine = r;
    }

    /**
     * Crée une représentation hierarchique de l'annuaire.
     * La renvoie sous forme de chaine de caractère.
     * (Parcours en profondeur)
     * @return représentation hiérarchique
     */
    public String hierachie() {
        String s = "";
        ArrayList<String> list = racine.hierarchie();
        for (String str : list) {
            s = s.concat(str + "\n");
        }
        return s;
    }

    /**
     * Crée une représentation par groupe de l'annuaire.
     * La renvoie sous forme de chaine de caractère.
     * (Parcours en largeur)
     * @return représentation par groupe
     */
    public String groupe() {
        ArrayList<Composant> aTraiter = new ArrayList<Composant>();
        ArrayList<Composant> aTraiterSuiv = new ArrayList<Composant>();
        aTraiterSuiv.add(racine);
        Composant c;
        String s = "";
        while (!aTraiter.isEmpty() || !aTraiterSuiv.isEmpty()) {
            if (aTraiter.isEmpty()) {
                aTraiter.addAll(aTraiterSuiv);
                aTraiterSuiv.clear();
                s = s.concat("---\n");
            }
            c = aTraiter.remove(0);
            s = s.concat(c.toString() + "\n");
            if (c instanceof IterableComposant) {
                IterateurComposant ite = ((IterableComposant) c).iterateur();
                while (ite.hasNext()) {
                    aTraiterSuiv.add(ite.next());
                }
            }
        }
        return s;
    }

    /**
     * Méthode hashCode.
     * @return haché
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((racine == null) ? 0 : racine.hashCode());
        return result;
    }

    /**
     * Teste l'égalité entre deux Annuaire.
     * Deux annuaires sont égaux quand
     * l'arbre qu'ils présentent est le même.
     * @param obj objet à comparer
     * @return true si égaux, false sinon
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Annuaire)) {
            return false;
        }
        Annuaire other = (Annuaire) obj;
        if (racine == null) {
            if (other.racine != null) {
                return false;
            }
        } else if (!racine.equals(other.racine)) {
            return false;
        }
        return true;
    }

    /**
     * Retourne l'identifiant.
     * @return Identifiant
     */
    public int getId() {
        return id;
    }

    /**
     * Retourne la racine.
     * @return  Composant racine
     */
    public Composant getRacine() {
        return racine;
    }
    
    
}
