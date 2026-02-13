import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class MyPanel extends JPanel 
{
	private int nodeNr = 1;
	private int node_diam = 30;
	private Vector<Node> listaNoduri;
	private Vector<Arc> listaArce;
	Point pointStart = null;
	Point pointEnd = null;
	Node n_start=null;
	Node n_end=null;
	boolean isDragging = false;
	private boolean grafOrientat = true; 
	Node nodSelectat = null;
	boolean mutareNod = false;

	private int coordXInitial = -1;
	private int coordYInitial = -1;


	public MyPanel()
	{
		listaNoduri = new Vector<Node>();
		listaArce = new Vector<Arc>();

		// borderul panel-ului
		setBorder(BorderFactory.createLineBorder(Color.black));

		addMouseListener(new MouseAdapter() {
			//evenimentul care se produce la apasarea mousse-ului
			public void mousePressed(MouseEvent e) {
				pointStart = e.getPoint();
				n_start=coleziuniNoduri(e.getX(), e.getY());

				 if (e.getButton() == MouseEvent.BUTTON3) {
                    mutareNod = true;
                    nodSelectat = n_start;
					pointStart=null;
                } else {
                    mutareNod = false;
                    nodSelectat = null;
                }

					if (nodSelectat != null) {
			coordXInitial = nodSelectat.getCoordX();
			coordYInitial = nodSelectat.getCoordY();
		}
				
			}
			
			//evenimentul care se produce la eliberarea mousse-ului
			public void mouseReleased(MouseEvent e) {
				if (mutareNod==true && nodSelectat != null) {
					if (esteSuprapusCuAltul()) {
						nodSelectat.setCoordX(coordXInitial);
						nodSelectat.setCoordY(coordYInitial);
						System.out.println("Spatiu insuficient - revenire la pozitia initiala.");
    				}

                    nodSelectat = null;
                    mutareNod = false;
					coordXInitial = coordYInitial = -1;
                    repaint();
                    return;
                }


				if (!isDragging) {
					if(coleziuniNoduri(e.getX(), e.getY())==null){
						addNode(e.getX(), e.getY());
						matriceAdiacenta();
					}
				}
				else {

					n_end = coleziunieArce(e.getX(), e.getY());
					if (n_start != null && n_end != null && n_start != n_end) {
						 Arc arc = new Arc(pointStart, pointEnd, n_start, n_end);
						 listaArce.add(arc);
						 matriceAdiacenta();
					}
				}

				pointStart = null;
				pointEnd=null;
				n_start=null;
				n_end=null;
				isDragging = false;
			}
		});
		
		addMouseMotionListener(new MouseMotionAdapter() {
			//evenimentul care se produce la drag&drop pe mousse
			public void mouseDragged(MouseEvent e) {
				pointEnd = e.getPoint();

				 if (nodSelectat != null && mutareNod==true) {
                    nodSelectat.setCoordX(e.getX());
                    nodSelectat.setCoordY(e.getY());
					isDragging=false;
                    repaint();
                    return;
                }

				 if (!mutareNod) {
					isDragging = true;
					repaint();
       			}
			}
		});
	}

	public void setGrafOrientat(boolean orientat) {
    this.grafOrientat = orientat;
}

    public boolean getGrafOrientat(){
		return grafOrientat;
	}
	
	private Node coleziuniNoduri(int x,int y){
		for (int i=0;i<listaNoduri.size();i++){
			Node a =listaNoduri.get(i);
			if(a.getCoordX()-30<x && a.getCoordX()+30>x && a.getCoordY()-30<y && a.getCoordY()+30>y){
				//System.out.println("Coliziune noduri"); 
				return a;
			}

		}
		return null;
	}

	private Node coleziunieArce(int x,int y){
		for (int i=0;i<listaNoduri.size();i++){
			Node a =listaNoduri.get(i);
			if(a.getCoordX()-1<x && a.getCoordX()+30>x && a.getCoordY()-1<y && a.getCoordY()+30>y){
				return a;
			}

		}
		//System.out.println("Coliziune arce"); 
		return null;
	}

	private boolean esteSuprapusCuAltul() {
    if (nodSelectat == null) return false;

    for (int i = 0; i < listaNoduri.size(); i++) {
        Node n = listaNoduri.get(i);

        if (n != nodSelectat) {
            if (nodSelectat.getCoordX() - 45 < n.getCoordX() &&
                nodSelectat.getCoordX() + 45 > n.getCoordX()  &&
                nodSelectat.getCoordY() - 45 < n.getCoordY() &&
                nodSelectat.getCoordY() + 45 > n.getCoordY()){
                return true; 
			}   
        }
    }
    return false; 
}

	//metoda care se apeleaza la eliberarea mouse-ului
	private void addNode(int x, int y) {
		Node node = new Node(x, y, nodeNr);
		listaNoduri.add(node);
		nodeNr++;
		repaint();
	}

	public void matriceAdiacenta(){
		int n=listaNoduri.size();
		int [][] matrice=new int[n][n];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				 matrice[i][j] = 0;
        }
    }

	for(int i=0;i<listaArce.size();i++){
		Arc a=listaArce.get(i);
		int i1=a.getNodStart().getNumber()-1;
		int i2=a.getNodEnd().getNumber()-1;

		matrice[i1][i2]=1;
		if(!grafOrientat){
		 matrice[i2][i1] = 1;
		}
	}

	    try (FileWriter fout = new FileWriter("matrice.txt")) {
        fout.write(n + "\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                fout.write(matrice[i][j] + " ");
            }
            fout.write("\n");
        }
        System.out.println("Fisierul 'matrice.txt' s-a modificat.");
    } catch (IOException e) {
        System.out.println("Eroare la scrierea fisierului: " + e.getMessage());
    }
}

	//se executa atunci cand apelam repaint()
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);//apelez metoda paintComponent din clasa de baza
		g.drawString("This is my Graph!", 10, 20);
		//deseneaza arcele existente in lista

		for (Arc a : listaArce)
		{
			a.drawArc(g,grafOrientat);
		}
		//deseneaza arcul curent; cel care e in curs de desenare
		if (pointStart != null)
		{
			g.setColor(Color.RED);
			g.drawLine(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y);
		}
		//deseneaza lista de noduri
		for(int i=0; i<listaNoduri.size(); i++)
		{
			listaNoduri.elementAt(i).drawNode(g, node_diam);
		}
	}
}

