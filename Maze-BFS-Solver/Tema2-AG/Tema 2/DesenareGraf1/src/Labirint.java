import java.io.*;
import java.util.*;

public class Labirint {
    private int[][] matrice;
    private int randuri, coloane;
    private Nod[][] noduri;
    private List<Nod> iesiri;

    public Labirint(String fisier) {
        citesteDinFisier(fisier);
        construiesteGraf();
    }

    private void citesteDinFisier(String fisier) {
        try (BufferedReader br = new BufferedReader(new FileReader(fisier))) {
            List<int[]> linii = new ArrayList<>();
            String linie;
            while ((linie = br.readLine()) != null) {
                String[] valori = linie.trim().split("\\s+");
                int[] rand = new int[valori.length];
                for (int i = 0; i < valori.length; i++)
                    rand[i] = Integer.parseInt(valori[i]);
                linii.add(rand);
            }
            randuri = linii.size();
            coloane = linii.get(0).length;
            matrice = linii.toArray(new int[randuri][coloane]);
        } catch (Exception e) {
            System.out.println("Eroare la citirea fisierului: " + e.getMessage());
        }
    }

    private void construiesteGraf() {
        noduri = new Nod[randuri][coloane];
        iesiri = new ArrayList<>();

        for (int i = 0; i < randuri; i++) {
            for (int j = 0; j < coloane; j++) {
                if (matrice[i][j] == 1) {
                    Nod n = new Nod(i, j);
                    noduri[i][j] = n;
                    if (i == 0 || j == 0 || i == randuri - 1 || j == coloane - 1) {
                        n.setIesire(true);
                        iesiri.add(n);
                    }
                }
            }
        }

        int[][] directii = {{-1,0}, {1,0}, {0,-1}, {0,1}};
        for (int i = 0; i < randuri; i++) {
            for (int j = 0; j < coloane; j++) {
                Nod n = noduri[i][j];
                if (n != null) {
                    for (int[] d : directii) {
                        int ni = i + d[0], nj = j + d[1];
                        if (ni >= 0 && ni < randuri && nj >= 0 && nj < coloane && noduri[ni][nj] != null)
                            n.adaugaVecin(noduri[ni][nj]);
                    }
                }
            }
        }
    }

    public List<List<Nod>> gasesteDrumuri(Nod start) {
        List<List<Nod>> drumuri = new ArrayList<>();
        for (Nod iesire : iesiri) {

            /* daca nu consideram drum cand start==iesire
            if (start.equals(iesire)) {
                drumuri.add(null);
                continue;
            }
            */
            List<Nod> drum = bfs(start, iesire);
            drumuri.add(drum);
        }
        return drumuri;
    }

    private List<Nod> bfs(Nod start, Nod tinta) {
        Queue<Nod> coada = new LinkedList<>();
        Map<Nod, Nod> parinte = new HashMap<>();
        Set<Nod> vizitat = new HashSet<>();

        coada.add(start);
        vizitat.add(start);
        parinte.put(start, null);

        while (!coada.isEmpty()) {
            Nod curent = coada.poll();

            if (curent.equals(tinta)) {
                return reconstruiesteDrum(parinte, tinta);
            }

            for (Nod vecin : curent.getVecini()) {
                if (!vizitat.contains(vecin)) {
                    vizitat.add(vecin);
                    parinte.put(vecin, curent);
                    coada.add(vecin);
                }
            }
        }
        return null; // fără drum
    }

    private List<Nod> reconstruiesteDrum(Map<Nod, Nod> parinte, Nod tinta) {
        List<Nod> drum = new ArrayList<>();
        for (Nod n = tinta; n != null; n = parinte.get(n)) {
            drum.add( n); //drum inversat
        }
        Collections.reverse(drum);
        return drum;
    }

    public int[][] getMatrice() { return matrice; }
    public Nod[][] getNoduri() { return noduri; }
    public List<Nod> getIesiri() { return iesiri; }
    public int getRanduri() { return randuri; }
    public int getColoane() { return coloane; }
}
