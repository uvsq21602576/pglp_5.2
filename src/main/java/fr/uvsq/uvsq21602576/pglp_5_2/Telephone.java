package fr.uvsq.uvsq21602576.pglp_5_2;

import java.io.Serializable;

/**
 * Représente un téléhone.
 * Possède un numéro et une information associée.
 * @author Flora
 */
public class Telephone implements Serializable {
    /** Identifiant unique. */
    private final int id;
    /** Information. */
    private String information;
    /** Numero. */
    private String numero;

    /**
     * Constructeur.
     * Crée un téléphone grâce à un numéro et l'information correspondante.
     * @param i Identifiant
     * @param num Numero
     * @param info Information
     */
    public Telephone(final int i, final String num, final String info) {
        this.id = i;
        this.information = info;
        this.numero = num;
    }

    /**
     * Retourne l'identifiant.
     * @return Identifiant
     */
    public int getId() {
        return id;
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
                + ((information == null) ? 0 : information.hashCode());
        result = prime * result + ((numero == null) ? 0 : numero.hashCode());
        return result;
    }

    /**
     * Teste l'égalite de deux téléphone.
     * Deux téléphone sont égaux quand à la fois leurs numéros et informations
     * sont la même.
     * @param obj Téléphone à comparer
     * @return true si égaux, false sinon
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Telephone)) {
            return false;
        }
        Telephone other = (Telephone) obj;
        if (information == null) {
            if (other.information != null) {
                return false;
            }
        } else if (!information.equals(other.information)) {
            return false;
        }
        if (numero == null) {
            if (other.numero != null) {
                return false;
            }
        } else if (!numero.equals(other.numero)) {
            return false;
        }
        return true;
    }

    /**
     * Retourne l'information du numéro.
     * @return information
     */
    public String getInformation() {
        return information;
    }

    /**
     * Retourne le numéro.
     * @return numéro
     */
    public String getNumero() {
        return numero;
    }

    /**
     * Retourne une chaine de caractère répresentant le numero.
     * Sous la forme "(information) numero"
     * @return Réprésentation textuelle du téléphone
     */
    @Override
    public String toString() {
        return "(" + this.information + ") " + this.numero;
    }

}
