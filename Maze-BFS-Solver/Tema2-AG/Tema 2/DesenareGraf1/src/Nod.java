import java.util.ArrayList;
import java.util.List;

public class Nod {
    private int linie, coloana;
    private boolean esteIesire;
    private List<Nod> vecini;  //retine conexiunile cu nodurile vecine -> matricea de adiacenta

    public Nod(int linie, int coloana) {
        this.linie = linie;
        this.coloana = coloana;
        this.vecini = new ArrayList<>();
    }

    public void adaugaVecin(Nod n) {
        vecini.add(n);
    }

    public List<Nod> getVecini() {
        return vecini;
    }

    public int getLinie() { return linie; }
    public int getColoana() { return coloana; }

    public boolean esteIesire() { return esteIesire; }
    public void setIesire(boolean iesire) { this.esteIesire = iesire; }

}
