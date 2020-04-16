package fr.uvsq.uvsq21602576.pglp_5_2;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Représente un personnel.
 * Possède un nom et prénom, une fonction, une date de naissance et une liste de
 * numéros de téléphone.
 * @author Flora
 */
public final class Personnel implements Composant, Serializable {

    /** Identifiant unique. */
    private final int id;
    /** Nom du personnel. */
    private final String nom;
    /** Prenom du personnel. */
    private final String prenom;
    /** Fonction du personnel. */
    private final String fonction;
    /** Date de naissance du personnel. */
    private final LocalDate dateNaissance;
    /** Liste de numeros de téléphone. */
    private final ArrayList<Telephone> numeros;

    /**
     * Builder, permet la création du Personnel.
     * @author Flora
     */
    public static class Builder {
        /**
         * Identifiant unique.
         */
        private final int id;
        /** Nom du personnel. */
        private final String nom;
        /** Prenom du personnel. */
        private final String prenom;
        /** Date de naissance du personnel. */
        private final LocalDate dateNaissance;

        /**
         * Fonction du personnel. Par défaut : "Employé".
         */
        private String fonction = "Employé";
        /** Liste de numeros de téléphone. */
        private ArrayList<Telephone> numeros;

        /**
         * Constructeur.
         * Crée un builder pour Personnel.
         * @param i Identifiant du personnel
         * @param n Nom du personnel
         * @param p Prenom du personnel
         * @param date Date de naissance du personnel
         * @param num Un numéro de téléphone
         */
        public Builder(final int i, final String n, final String p,
                final LocalDate date, final Telephone num) {
            this.id = i;
            this.nom = n;
            this.prenom = p;
            this.dateNaissance = date;
            this.numeros = new ArrayList<Telephone>();
            numeros.add(num);
        }

        /**
         * Change la fonction du Personnel.
         * @param f Nouvelle fonction
         * @return Builder du Personnel
         */
        public Builder fonction(final String f) {
            this.fonction = f;
            return this;
        }

        /**
         * Ajoute un numéro de téléphone.
         * @param num Numéro à ajouter
         * @return Builder du Personnel
         */
        public Builder addNumero(final Telephone num) {
            this.numeros.add(num);
            return this;
        }

        /**
         * Build.
         * Retourne le Personnel crée à partir de ce Builder.
         * @return Personnel issus du Builder.
         */
        public Personnel build() {
            return new Personnel(this);
        }
    }

    /**
     * Constructeur.
     * Crée un Personnel à partir des informations du builder.
     * @param builder Builder du Personnel
     */
    private Personnel(final Builder builder) {
        this.id = builder.id;
        this.nom = builder.nom;
        this.prenom = builder.prenom;
        this.dateNaissance = builder.dateNaissance;
        this.fonction = builder.fonction;
        this.numeros = builder.numeros;
    }

    /**
     * Retourne le nom du personnel.
     * @return Nom du personnel
     */
    public String getNom() {
        return nom;
    }

    /**
     * Retourne le prénom du personnel.
     * @return prénom du personnel
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * Retourne la fonction du personnnel.
     * @return fonction du personnel
     */
    public String getFonction() {
        return fonction;
    }

    /**
     * Retourne la date de Naissance du personnel.
     * @return dte de naissance du personnel
     */
    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    /**
     * Retourne une liste non modifiable.
     * Des numeros de téléphones du personnel
     * @return liste de numeros de téléphones
     */
    public List<Telephone> getNumeros() {
        return Collections.unmodifiableList(numeros);
    }

    /**
     * Retourne l'identifiant.
     * @return Identifiant
     */
    public int getId() {
        return id;
    }

    /**
     * Retourne une chaine de caractère représentant le personnel.
     * Sous la forme "nom prenom, fonction (date de naissance) [numeros de
     * teléphone]
     * @return représentation textuelle du Personnel
     */
    @Override
    public String toString() {
        String s = this.nom + " " + this.prenom;
        s = s.concat(", " + this.fonction);
        s = s.concat(" (" + this.dateNaissance.toString() + ")");
        s = s.concat(" [");
        for (Telephone t : numeros) {
            s = s.concat(t.toString() + ", ");
        }
        s = s.substring(0, s.length() - 2);
        s = s.concat("]");
        return s;
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
        return list;
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
                + ((dateNaissance == null) ? 0 : dateNaissance.hashCode());
        result = prime * result
                + ((fonction == null) ? 0 : fonction.hashCode());
        result = prime * result + ((nom == null) ? 0 : nom.hashCode());
        result = prime * result + ((numeros == null) ? 0 : numeros.hashCode());
        result = prime * result + ((prenom == null) ? 0 : prenom.hashCode());
        return result;
    }

    /**
     * Teste si deux personnel sont égaux.
     * Deux personnels sont égaux si toutes leurs caractéristiques sont égales.
     * @param obj personnel à comparer
     * @return true si égaux, false sinon
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Personnel)) {
            return false;
        }
        Personnel other = (Personnel) obj;
        if (dateNaissance == null) {
            if (other.dateNaissance != null) {
                return false;
            }
        } else if (!dateNaissance.equals(other.dateNaissance)) {
            return false;
        }
        if (fonction == null) {
            if (other.fonction != null) {
                return false;
            }
        } else if (!fonction.equals(other.fonction)) {
            return false;
        }
        if (nom == null) {
            if (other.nom != null) {
                return false;
            }
        } else if (!nom.equals(other.nom)) {
            return false;
        }
        if (numeros == null) {
            if (other.numeros != null) {
                return false;
            }
        } else if (!numeros.equals(other.numeros)) {
            return false;
        }
        if (prenom == null) {
            if (other.prenom != null) {
                return false;
            }
        } else if (!prenom.equals(other.prenom)) {
            return false;
        }
        return true;
    }

}
