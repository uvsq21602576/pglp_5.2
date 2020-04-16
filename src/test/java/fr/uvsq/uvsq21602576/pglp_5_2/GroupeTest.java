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
 * Classe de test pour Groupe.
 * @author Flora
 */
public class GroupeTest {

    /**
     * Teste la méthode add avec un groupe.
     */
    @Test
    public void addTestGroupe() {
        Groupe g = new Groupe(1, "G");
        g.add(new Groupe(2, "G"));
        assertEquals(1, g.size());
        assertEquals(new Groupe(3, "G"), g.get(0));
    }

    /**
     * Teste la méthode add avec un personnel.
     */
    @Test
    public void addTestPersonnel() {
        Groupe g = new Groupe(1, "G");
        Personnel p = new Personnel.Builder(1, "1", "1",
                LocalDate.of(2000, 01, 05),
                new Telephone(1, "06...", "portable")).build();
        g.add(p);
        assertEquals(1, g.size());
        assertEquals(p, g.get(0));
    }

    /**
     * Teste la méthode remove pour un groupe.
     */
    @Test
    public void removeTestGroupe() {
        Groupe g = new Groupe(1, "G");
        g.add(new Groupe(2, "G"));
        Composant g2 = g.remove(0);
        assertTrue(g2 instanceof Groupe);
        assertEquals(0, g.size());
        assertEquals(new Groupe(3, "G"), (Groupe) g2);
    }

    /**
     * Teste la méthode remove pour un personnel.
     */
    @Test
    public void removeTestPersonnel() {
        Groupe g = new Groupe(1, "G");
        Personnel p = new Personnel.Builder(1, "1", "1",
                LocalDate.of(2000, 01, 05),
                new Telephone(1, "06...", "portable")).build();
        g.add(p);
        Composant g2 = g.remove(0);
        assertTrue(g2 instanceof Personnel);
        assertEquals(0, g.size());
        assertEquals(p, (Personnel) g2);
    }

    /**
     * Teste la méthode equals.
     */
    @Test
    public void equalsTrueTest() {
        Groupe g = new Groupe(1, "G");
        Personnel p = new Personnel.Builder(1, "1", "1",
                LocalDate.of(2000, 01, 05),
                new Telephone(1, "06...", "portable")).build();
        g.add(p);
        Groupe g2 = new Groupe(2, "G");
        g2.add(p);
        assertTrue(g.equals(g2));
    }

    /**
     * Teste la méthode equals.
     */
    @Test
    public void equalsFalseTest() {
        Groupe g = new Groupe(1, "G");
        Personnel p = new Personnel.Builder(1, "1", "1",
                LocalDate.of(2000, 01, 05),
                new Telephone(1, "06...", "portable")).build();
        g.add(p);
        Groupe g2 = new Groupe(2, "G");
        g2.add(p);
        Personnel p2 = new Personnel.Builder(2, "2", "1",
                LocalDate.of(2000, 01, 05),
                new Telephone(2, "06...", "portable")).build();
        g2.add(p2);
        assertFalse(g.equals(g2));
    }

    /**
     * Teste a sérialisation.
     * En sérialisant, puis déserialisant, et comparant avec l'objet initial.
     * @throws IOException En cas d'erreur d'ecriture ou lecture dans les stream
     * @throws ClassNotFoundException Si la classe de l'objet lu n'existe pas
     */
    @Test
    public void serialisationTest() throws IOException, ClassNotFoundException {
        Groupe g = new Groupe(1, "G");
        Personnel p = new Personnel.Builder(1, "1", "1",
                LocalDate.of(2000, 01, 05),
                new Telephone(1, "06...", "portable")).build();
        g.add(p);
        ByteArrayOutputStream outBuff = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(outBuff);
        out.writeObject(g);
        out.close();

        byte[] buff = outBuff.toByteArray();
        outBuff.close();

        ByteArrayInputStream inBuff = new ByteArrayInputStream(buff);
        ObjectInputStream in = new ObjectInputStream(inBuff);
        Object observed = in.readObject();
        in.close();
        inBuff.close();

        assertTrue(observed instanceof Groupe);
        assertEquals(g, observed);
        assertEquals(g.getId(), ((Groupe) observed).getId());
    }

}
