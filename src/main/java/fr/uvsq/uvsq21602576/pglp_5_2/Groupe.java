package fr.uvsq.uvsq21602576.pglp_5_2;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Représente un groupe de composant.
 * Noeud interne de l'arbre de composant.
 * Implémentes Composant ainsi que IterableComposant.
 * @author Flora
 */
public class Groupe implements Composant, IterableComposant, Serializable {
    /**
     * Identifiant unique d'annuaire.
     */
    private final int id;
    /**
     * Nom du groupe.
     */
    private String nom;
    /**
     * Liste de composant appartenant à ce groupe.
     */
    private ArrayList<Composant> composantFils;

    /**
     * Constructeur.
     * Crée un groupe vide a partir d'un nom.
     * @param i Identifiant
     * @param n Nom du groupe
     */
    public Groupe(final int i, final String n) {
        this.id = i;
        this.nom = n;
        composantFils = new ArrayList<Composant>();
    }

    /**
     * Ajoute un composant au groupe.
     * @param c Composant à ajouter
     */
    public void add(final Composant c) {
        composantFils.add(c);
    }

    /**
     * Retourne le composant du groupe se trouvant à index.
     * @param index indice du composant à retourner
     * @return Composant recherché
     */
    public Composant get(final int index) {
        return composantFils.get(index);
    }
    
    /**
     * Retourne le nom.
     * @return Nom du groupe
     */
    public String getNom() {
        return this.nom;
    }

    /**
     * Retourne l'identifiant.
     * @return Identifiant
     */
    public int getId() {
        return id;
    }

    /**
     * Retourne le composant du groupe se trouvant à index.
     * Le retire du groupe.
     * @param index indice du composant à retourner
     * @return Composant recherché
     */
    public Composant remove(final int index) {
        return composantFils.remove(index);
    }

    /**
     * Méthode hashCode.
     * @return le haché
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((composantFils == null) ? 0 : composantFils.hashCode());
        result = prime * result + ((nom == null) ? 0 : nom.hashCode());
        return result;
    }

    /**
     * Teste l'égalité entre deux groupes.
     * Deux groupes sont égaux quand ils contiennent exactement les mêmes
     * éléments.
     * @param obj composant à comparer
     * @return true si égaux, false sinon
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Groupe)) {
            return false;
        }
        Groupe other = (Groupe) obj;
        if (composantFils == null) {
            if (other.composantFils != null) {
                return false;
            }
        } else if (!composantFils.equals(other.composantFils)) {
            return false;
        }
        if (nom == null) {
            if (other.nom != null) {
                return false;
            }
        } else if (!nom.equals(other.nom)) {
            return false;
        }
        return true;
    }

    /**
     * Teste si le groupe contient c.
     * c étant une liste de composant.
     * @param c liste de composants
     * @return true si le groupe contient tout c, false sinon
     */
    public boolean containsAll(final ArrayList<Composant> c) {
        return composantFils.containsAll(c);
    }

    /**
     * Retorune la taille du groupe.
     * i.e. le nombre de composants qu'il contient.
     * @return taille du groupe
     */
    public int size() {
        return composantFils.size();
    }

    /**
     * Crée une représentation hierarchique de l'annuaire.
     * La renvoie sous forme de chaine de caractère.
     * (Parcours en profondeur récursif)
     * @return représentation hiérarchique
     */
    public ArrayList<String> hierarchie() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(this.toString());
        IterateurComposant ite = this.iterateur();
        Composant c;
        while (ite.hasNext()) {
            c = ite.next();
            for (String s : c.hierarchie()) {
                if (s.substring(0, 1).equals("\t")) {
                    s = "\t" + s;
                } else {
                    s = "\t|-   " + s;
                }
                list.add(s);
            }
        }
        return list;
    }

    /**
     * Retourne une chaine de caractère représentant le groupe.
     * @return représentation du groupe
     */
    public String toString() {
        return "Groupe " + this.nom + " (" + this.composantFils.size() + ")";
    }

    /**
     * Retourne un itérateur de la composition du groupe.
     * @return Iterateur
     */
    public IterateurComposant iterateur() {
        IterateurComposant ite = new IterateurComposant(this.composantFils);
        return ite;
    }

}
