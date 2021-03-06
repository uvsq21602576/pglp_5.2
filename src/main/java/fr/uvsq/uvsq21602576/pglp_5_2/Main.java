package fr.uvsq.uvsq21602576.pglp_5_2;

import java.time.LocalDate;

import fr.uvsq.uvsq21602576.pglp_5_2.dao.DAO;
import fr.uvsq.uvsq21602576.pglp_5_2.dao.FabriqueDAO;

/**
 * Singleton contenant le main.
 * @author Flora
 */
public enum Main {
    /**
     * Main.
     */
    MAIN;

    /**
     * Exécution du programme.
     */
    public void run() {
        Personnel p =
                new Personnel.Builder(1, "1", "1", LocalDate.of(2000, 01, 05),
                        new Telephone(1, "06...", "portable")).build();
        Personnel p2 =
                new Personnel.Builder(2, "1", "2", LocalDate.of(2000, 01, 05),
                        new Telephone(2, "06...", "portable")).build();
        Personnel p3 =
                new Personnel.Builder(3, "2", "1", LocalDate.of(2000, 01, 05),
                        new Telephone(3, "06...", "portable")).build();
        Personnel p4 =
                new Personnel.Builder(4, "2", "2", LocalDate.of(2000, 01, 05),
                        new Telephone(4, "06...", "portable")).build();
        Personnel p5 =
                new Personnel.Builder(5, "3", "1", LocalDate.of(2000, 01, 05),
                        new Telephone(5, "06...", "portable")).build();
        Personnel p6 =
                new Personnel.Builder(6, "4", "1", LocalDate.of(2000, 01, 05),
                        new Telephone(6, "06...", "portable")).build();

        Groupe g = new Groupe(1, "G1");
        g.add(p);
        g.add(p2);
        Groupe g2 = new Groupe(2, "G2");
        g2.add(p3);
        g2.add(p4);
        g2.add(g);
        g2.add(p6);
        Groupe g3 = new Groupe(3, "G3");
        g3.add(p5);
        g3.add(g2);

        Annuaire a = new Annuaire(1, g3);
        System.out.println(a.hierachie());
        System.out.println(a.groupe());
    }

    /**
     * Execution du programme.
     * Pour tester le DAO JDBC de Telephone.
     */
    public void runJDBCTelephone() {
        DAO<Telephone> dao = FabriqueDAO
                .getFabriqueDAO(FabriqueDAO.TypeDAO.JDBC).getTelephoneDAO();
        Telephone t1 = new Telephone(1, "06", "portable");
        System.out.println("Telephone : " + t1.toString());
        dao.create(t1);

        Telephone t2 = dao.find("1");
        System.out.println("Tel recupere : " + t2.toString());

        t1 = new Telephone(1, "098765", "portable");
        System.out.println("Modif : " + t1.toString());
        dao.update(t1);

        t2 = dao.find("1");
        System.out.println("Tel modifié recupere : " + t2.toString());

        dao.delete(t2);

        t2 = dao.find("1");
        System.out.print("Delete ? ");
        System.out.println(t2 == null);
    }

    /**
     * Execution du programme.
     * Pour tester le DAO JDBC de Personnel.
     */
    public void runJDBCPersonnel() {
        DAO<Personnel> dao = FabriqueDAO
                .getFabriqueDAO(FabriqueDAO.TypeDAO.JDBC).getPersonnelDAO();
        Personnel p =
                new Personnel.Builder(1, "1", "1", LocalDate.of(2000, 01, 05),
                        new Telephone(2, "06...", "portable"))
                                .addNumero(new Telephone(3, "05..", "fixe"))
                                .build();
        System.out.println("Personnel : " + p.toString());
        dao.create(p);

        Personnel p2 = dao.find("1");
        System.out.println("Personnel récupéré : " + p2.toString());

        Personnel p3 = new Personnel.Builder(1, "1", "111",
                LocalDate.of(2008, 01, 05), new Telephone(2, "06...", "poable"))
                        .addNumero(new Telephone(5, "07..", "fixe")).build();
        System.out.println("Personnel modif : " + p3.toString());
        dao.update(p3);

        Personnel p4 = dao.find("1");
        System.out.println("Personnel modif récupéré : " + p4.toString());

        dao.delete(p4);

        p2 = dao.find("1");
        System.out.println("Delete ? ");
        System.out.println(p2 == null);
    }

    /**
     * Execution du programme.
     * Pour tester le DAO JDBC de Groupe.
     */
    public void runJDBCGroupe() {
        Personnel p =
                new Personnel.Builder(1, "1", "1", LocalDate.of(2000, 01, 05),
                        new Telephone(1, "06...", "portable")).build();
        Personnel p2 =
                new Personnel.Builder(2, "1", "2", LocalDate.of(2000, 01, 05),
                        new Telephone(2, "05...", "portable"))
                                .addNumero(new Telephone(3, "04...", "pole"))
                                .build();
        Groupe g = new Groupe(1, "G1");
        g.add(p);
        g.add(p2);
        Groupe gg = new Groupe(2, "GG");
        Personnel p100 =
                new Personnel.Builder(100, "1", "1", LocalDate.of(2000, 01, 05),
                        new Telephone(100, "06.0876..", "portable")).build();
        gg.add(p100);
        g.add(gg);
        System.out.println("Groupe : " + g.hierarchie());
        DAO<Groupe> dao = FabriqueDAO.getFabriqueDAO(FabriqueDAO.TypeDAO.JDBC)
                .getGroupeDAO();
        dao.create(g);

        Groupe g1 = dao.find("1");
        System.out.println("Groupe récupéré : " + g1.hierarchie());

        Personnel p3 =
                new Personnel.Builder(3, "1", "1", LocalDate.of(2000, 01, 05),
                        new Telephone(1, "0656.", "portable")).build();
        Personnel p4 =
                new Personnel.Builder(2, "1", "2", LocalDate.of(2000, 01, 05),
                        new Telephone(2, "055..", "portable"))
                                .addNumero(new Telephone(3, "04...", "pole"))
                                .build();
        Groupe g2 = new Groupe(1, "G1modif");
        g2.add(p3);
        g2.add(p4);
        g2.add(gg);
        System.out.println("Groupe modif : " + g2.hierarchie());

        dao.update(g2);
        Groupe g3 = dao.find("1");
        System.out.println("Groupe modif récupéré : " + g3.hierarchie());

        dao.delete(gg);
        FabriqueDAO.getFabriqueDAO(FabriqueDAO.TypeDAO.JDBC).getPersonnelDAO()
                .delete(p3);
        FabriqueDAO.getFabriqueDAO(FabriqueDAO.TypeDAO.JDBC).getTelephoneDAO()
                .delete(new Telephone(2, "num", "info"));
        g2 = dao.find("1");
        /*
         * System.out.println("Delete ? ");
         * System.out.println(g2==null);
         */
        System.out.println("Groupe supp récupéré : " + g2.hierarchie());
    }

    /**
     * Execution du programme.
     * Pour tester le DAO JDBC de Annuaire.
     */
    public void runJDBCAnnuaire() {
        Personnel p =
                new Personnel.Builder(1, "1", "1", LocalDate.of(2000, 01, 05),
                        new Telephone(1, "061...", "portable")).build();
        Personnel p2 =
                new Personnel.Builder(2, "1", "2", LocalDate.of(2000, 01, 05),
                        new Telephone(2, "062...", "portable")).build();
        Personnel p3 =
                new Personnel.Builder(3, "2", "1", LocalDate.of(2000, 01, 05),
                        new Telephone(3, "063...", "portable")).build();
        Personnel p4 =
                new Personnel.Builder(4, "2", "2", LocalDate.of(2000, 01, 05),
                        new Telephone(4, "064...", "portable")).build();
        Personnel p5 =
                new Personnel.Builder(5, "3", "1", LocalDate.of(2000, 01, 05),
                        new Telephone(5, "065...", "portable")).build();
        Personnel p6 =
                new Personnel.Builder(6, "4", "1", LocalDate.of(2000, 01, 05),
                        new Telephone(6, "066...", "portable")).build();

        Groupe g = new Groupe(1, "G1");
        g.add(p);
        g.add(p2);
        Groupe g2 = new Groupe(2, "G2");
        g2.add(p3);
        g2.add(p4);
        g2.add(g);
        g2.add(p6);
        Groupe g3 = new Groupe(3, "G3");
        g3.add(p5);
        g3.add(g2);

        Annuaire a = new Annuaire(1, g3);
        System.out.println("---- Annuaire : " + a.hierachie());

        DAO<Annuaire> dao = FabriqueDAO.getFabriqueDAO(FabriqueDAO.TypeDAO.JDBC)
                .getAnnuaireDAO();
        dao.create(a);

        Annuaire a2 = dao.find("1");
        System.out.println("---- Annuaire récupéré : " + a2.hierachie());

        Groupe g4 = new Groupe(4, "G4");
        g2.add(p);
        g4.add(g2);
        Annuaire a3 = new Annuaire(1, g4);
        System.out.println("---- Annuaire modif : " + a3.hierachie());

        dao.update(a3);
        Annuaire a4 = dao.find("1");
        System.out.println("---- Annuaire modif récupéré : " + a4.hierachie());

        dao.delete(a4);

        a2 = dao.find("1");
        System.out.println("Delete ? ");
        System.out.println(a2 == null);
    }

    /**
     * main.
     * @param args arguments
     */
    public static void main(final String[] args) {
        // MAIN.run();
        MAIN.runJDBCTelephone();
        // MAIN.runJDBCPersonnel();
        // MAIN.runJDBCGroupe();
        // MAIN.runJDBCAnnuaire();
    }
}
