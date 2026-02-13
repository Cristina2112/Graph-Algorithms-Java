import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Graphics2D;


public class Arc
{
    private Point start;
    private Point end;
    private Node n_start;
    private Node n_end;
    private int cost;
    private boolean isMSTEdge = false;

    public Arc(Point start, Point end, Node n_start,Node n_end, int cost)
    {
        this.start = start;
        this.end = end;
        this.n_start=n_start;
        this.n_end=n_end;
        this.cost = cost;
    }

    public Node getNodStart() {
        return n_start;
    }
    public void setNodStart(Node n_start) {
        this.n_start=n_start;
    }

    public Node getNodEnd() {
        return n_end;
    }
    public void setNodEnd(Node n_end) {
        this.n_end=n_end;
    }

    public int getCost() {return cost;}
    public void setCost(int cost) {this.cost=cost;}

    public boolean isMSTEdge() {return isMSTEdge;}
    public void setIsMSTEdge(boolean isMSTEdge) {
        this.isMSTEdge = isMSTEdge;
    }

    public void drawArc(Graphics g, boolean isOriented)
    {
        if (n_start != null && n_end != null)
        {
            // pentru a evita duplicate
            if (n_start.getNumber() >= n_end.getNumber()) {
                return;
            }

            // setare culoare
            if (isMSTEdge) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.RED);
            }

            double xStart = n_start.getCoordX() + 15;
            double yStart = n_start.getCoordY() + 15;
            double xEnd = n_end.getCoordX() + 15;
            double yEnd = n_end.getCoordY() + 15;

            double unghi = Math.atan2(yEnd - yStart, xEnd - xStart);

            // nesuprapunerea liniei cu nodul
            double dist = Math.sqrt((xEnd - xStart)*(xEnd - xStart) + (yEnd - yStart)*(yEnd - yStart));
            double xEndCorect = xEnd - (15 * (xEnd - xStart) / dist);
            double yEndCorect = yEnd - (15 * (yEnd - yStart) / dist);

            // trasează muchia
            g.drawLine((int)xStart, (int)yStart, (int)xEndCorect, (int)yEndCorect);

            // desenarea săgeții (dacă este graf orientat)
            if (isOriented) {
                int lungimeVarf = 15;
                int deschidere = 25;

                double unghi1 = Math.toRadians(deschidere);
                double unghi2 = -Math.toRadians(deschidere);

                int x1 = (int)(xEndCorect - lungimeVarf * Math.cos(unghi + unghi1));
                int y1 = (int)(yEndCorect - lungimeVarf * Math.sin(unghi + unghi1));

                int x2 = (int)(xEndCorect - lungimeVarf * Math.cos(unghi + unghi2));
                int y2 = (int)(yEndCorect - lungimeVarf * Math.sin(unghi + unghi2));

                g.drawLine((int)xEndCorect, (int)yEndCorect, x1, y1);
                g.drawLine((int)xEndCorect, (int)yEndCorect, x2, y2);
            }

            // afișarea cost muchie
            int xMid = (int)((xStart + xEndCorect) / 2);
            int yMid = (int)((yStart + yEndCorect) / 2);

            int offsetX = (int) (15 * Math.sin(unghi));
            int offsetY = (int) (15 * Math.cos(unghi));

            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(cost), xMid + offsetX, yMid - offsetY);
        }
    }
}