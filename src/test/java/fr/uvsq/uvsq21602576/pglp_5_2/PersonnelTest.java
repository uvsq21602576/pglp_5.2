package fr.uvsq.uvsq21602576.pglp_5_2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;

import org.junit.Test;

/**
 * Classe de test pour personnel.
 * @author Flora
 */
public class PersonnelTest {

    /**
     * Teste le contructeur de Personnel avec Builder.
     * Avec les arguments par défault.
     */
    @Test
    public void constructeurTest() {
        Personnel p = new Personnel.Builder(1, "Nom", "Prenom",
                LocalDate.of(2000, 01, 05),
                new Telephone(1, "06...", "portable")).build();
        assertEquals("Nom", p.getNom());
        assertEquals("Prenom", p.getPrenom());
        assertEquals(LocalDate.of(2000, 01, 05), p.getDateNaissance());
        assertEquals("Employé", p.getFonction());

        assertEquals(1, p.getNumeros().size());
        assertEquals(new Telephone(2, "06...", "portable"),
                p.getNumeros().get(0));
    }

    /**
     * Teste le contructeur de Personnel avec Builder.
     * Avec des arguments personnalisés.
     */
    @Test
    public void constructeurTest2() {
        Personnel p = new Personnel.Builder(1, "Nom", "Prenom",
                LocalDate.of(2000, 01, 05),
                new Telephone(2, "06...", "portable"))
                        .addNumero(new Telephone(1, "01...", "fixe"))
                        .fonction("Directeur").build();
        assertEquals("Nom", p.getNom());
        assertEquals("Prenom", p.getPrenom());
        assertEquals(LocalDate.of(2000, 01, 05), p.getDateNaissance());
        assertEquals("Directeur", p.getFonction());

        assertEquals(2, p.getNumeros().size());
        assertEquals(new Telephone(3, "01...", "fixe"), p.getNumeros().get(1));
        assertEquals(new Telephone(2, "06...", "portable"),
                p.getNumeros().get(0));
    }

    /**
     * Teste le toString.
     */
    @Test
    public void toStringTest() {
        Personnel p = new Personnel.Builder(1, "Nom", "Prenom",
                LocalDate.of(2000, 01, 05),
                new Telephone(1, "06...", "portable")).build();
        String expected = p.getNom() + " " + p.getPrenom() + ", "
                + p.getFonction() + " (" + p.getDateNaissance().toString()
                + ") [" + p.getNumeros().get(0).toString() + "]";
        assertEquals(expected, p.toString());
    }

    /**
     * Teste la méthode equals. Renvoie vrai.
     */
    @Test
    public void equalsTrueTest() {
        Personnel p = new Personnel.Builder(1, "Nom", "Prenom",
                LocalDate.of(2000, 01, 05),
                new Telephone(1, "06...", "portable")).build();
        Personnel p2 = new Personnel.Builder(2, "Nom", "Prenom",
                LocalDate.of(2000, 01, 05),
                new Telephone(2, "06...", "portable")).build();
        assertTrue(p.equals(p2));
    }

    /**
     * Teste la méthode equals. Renvoie faux.
     */
    @Test
    public void equalsFalseTest() {
        Personnel p = new Personnel.Builder(1, "Nom", "Prenom",
                LocalDate.of(2000, 01, 05),
                new Telephone(1, "06...", "portable")).fonction("Directeur")
                        .build();
        Personnel p2 = new Personnel.Builder(2, "Nom", "Prenom",
                LocalDate.of(2000, 01, 05),
                new Telephone(2, "06...", "portable")).build();
        assertFalse(p.equals(p2));
    }

    /**
     * Teste a sérialisation.
     * En sérialisant, puis déserialisant, et comparant avec l'objet initial.
     * @throws IOException En cas d'erreur d'ecriture ou lecture dans les stream
     * @throws ClassNotFoundException Si la classe de l'objet lu n'existe pas
     */
    @Test
    public void serialisationTest() throws IOException, ClassNotFoundException {
        Personnel p = new Personnel.Builder(1, "Nom", "Prenom",
                LocalDate.of(2000, 01, 05),
                new Telephone(1, "06...", "portable")).fonction("Directeur")
                        .build();
        ByteArrayOutputStream outBuff = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(outBuff);
        out.writeObject(p);
        out.close();

        byte[] buff = outBuff.toByteArray();
        outBuff.close();

        ByteArrayInputStream inBuff = new ByteArrayInputStream(buff);
        ObjectInputStream in = new ObjectInputStream(inBuff);
        Object observed = in.readObject();
        in.close();
        inBuff.close();

        assertTrue(observed instanceof Personnel);
        assertEquals(p, observed);
        assertEquals(p.getId(), ((Personnel) observed).getId());
    }
}
