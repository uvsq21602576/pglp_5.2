package fr.uvsq.uvsq21602576.pglp_5_2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

/**
 * CLasse de Test pour Telephone.
 * @author Flora
 */
public class TelephoneTest {

    /**
     * Teste la méthode equals.
     */
    @Test
    public void equalsTest() {
        Telephone t = new Telephone(1, "06", "portable");
        Telephone t2 = new Telephone(2, "06", "portable");
        assertTrue(t.equals(t2));
    }

    /**
     * Teste la méthode equals.
     */
    @Test
    public void equalsTest2() {
        Telephone t = new Telephone(1, "065", "portable");
        Telephone t2 = new Telephone(2, "06", "portable");
        assertFalse(t.equals(t2));
    }

    /**
     * Teste la méthode equals.
     */
    @Test
    public void equalsTest3() {
        Telephone t = new Telephone(1, "06", "porta ble");
        Telephone t2 = new Telephone(2, "06", "portable");
        assertFalse(t.equals(t2));
    }

    /**
     * Teste la méthode toString.
     */
    @Test
    public void toStringTest() {
        Telephone t = new Telephone(1, "06", "portable");
        String expected = "(" + t.getInformation() + ") " + t.getNumero();
        assertEquals(expected, t.toString());
    }

    /**
     * Teste a sérialisation.
     * En sérialisant, puis déserialisant, et comparant avec l'objet initial.
     * @throws IOException En cas d'erreur d'ecriture ou lecture dans les stream
     * @throws ClassNotFoundException Si la classe de l'objet lu n'existe pas
     */
    @Test
    public void serialisationTest() throws IOException, ClassNotFoundException {
        Telephone t = new Telephone(1, "0678", "portable");
        ByteArrayOutputStream outBuff = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(outBuff);
        out.writeObject(t);
        out.close();

        byte[] buff = outBuff.toByteArray();
        outBuff.close();

        ByteArrayInputStream inBuff = new ByteArrayInputStream(buff);
        ObjectInputStream in = new ObjectInputStream(inBuff);
        Object observed = in.readObject();
        in.close();
        inBuff.close();

        assertTrue(observed instanceof Telephone);
        assertEquals(t, observed);
        assertEquals(t.getId(), ((Telephone) observed).getId());
    }

}
