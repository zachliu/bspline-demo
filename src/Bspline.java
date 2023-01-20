// Interactive 2D B-spline, Zach Liu
// Jan 20th, 2023

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.StringTokenizer;

public class Bspline extends JPanel {

    int n = 12, p = 3, m, Tmin, Tmax, w = 1350, h = 800, h1, w2;
    //double[] Px = { 0.2*w, 0.3*w, 0.7*w, 0.8*w };
    //double[] Py = { 0.2*h/2, 0.8*h/2, 0.8*h/2, 0.2*h/2 };
    int DX = 22;
    int DY = 20;
    double[] Px = { 1, 5,   9.2, 13.3, 17, 21.3, 1, 5,   9.2, 13.3, 17, 21.3 };
    double[] Py = { 1, 1.5, 4.3, -2.2, 2,  4,    1, 1.5, 4.3, -2.2, 2,  4    };
    double[] U;
    double N[][];
    Color col[];

    int x, y, x2, y2;

    public static void main(String[] args) {
        JFrame f = new JFrame("Draw Bspline");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setContentPane(new Bspline());
        f.setSize(1350+100, 800+100);
        f.setVisible(true);
    }

    Bspline() {
        x = y = x2 = y2 = 0; //

        h1 = h;
        w2 = w;

        // String s = getParameter("N");
        String s = null;
        if ( s != null )
        {
            StringTokenizer st = new StringTokenizer(s);
            n = Integer.parseInt(st.nextToken());   // n is # of ctrl pts
            p = Integer.parseInt(st.nextToken());
        }
        m = n + p + 1;              // p is the degree of B-spline, m is the length of knot vector
        //Px = new double[n];        // vector for storing ctrl pts x coordinate
        //Py = new double[n];        // vector for storing ctrl pts y coordinate
        U = new double[m+p];        //
        col = new Color[w2];        //
        N = new double[m + 1][w2];  //
        // s = getParameter("pts");
        s = null;
        if ( s != null )
        {
            StringTokenizer st = new StringTokenizer(s);
            for ( int i = 0; i < n; i++ )
            {
                Px[i] = w2*Double.valueOf(st.nextToken()).doubleValue();
                Py[i] = h1*Double.valueOf(st.nextToken()).doubleValue();
            }
        }
        else
        {
            for ( int i = 0; i < n; i++ )
            {
                Px[i] = w2*Px[i]/DX;
                Py[i] = h1*Py[i]/DY+h1/2;
            }
        }
        // s = getParameter( "knots" );
        s = null;
        if ( s != null )
        {
            StringTokenizer st = new StringTokenizer( s );
            for ( int i = 0; i < m; i++ )
                U[i] = Double.valueOf(st.nextToken()).doubleValue();
        }
        else
        {
            for ( int i = 0; i < m; i++ )
                U[i] = i;
        }
        double Umin = U[0];
        double dU = U[m-1]-Umin;
        for (int i = 0; i < m; i++)
        {
            U[i] = 0 + (w2-20)*(U[i]-Umin) / dU;
        }

        MyMouseListener listener = new MyMouseListener();
        addMouseListener(listener);
        addMouseMotionListener(listener);
    }

    public void drawFun(Graphics buffGraphics) {
        double step = (U[m-1]-U[0]) / (w2-0.99);
        //double step = 1;
        double u = U[0];
        Color[] iColor = { Color.red, new Color(0f,.7f,0f), Color.blue, Color.magenta, new Color(0f,.8f,.8f), new Color(.9f,.9f,0f), Color.gray };
        buffGraphics.clearRect( 0,h1, w, h1 );
        Tmin = (int)((U[p]-U[0])/step) + 1;
        Tmax = (int)((U[n]-U[0])/step);
        int i1 = 0;
        for ( int l = 0; l < w2; l++ )
        {
            while ( u >= U[i1] )
                i1++;
            int i = i1-1;
            col[l] = iColor[(i+8-(p+1)) % 7];
            for (int j = 0; j < m; j++)
                N[j][l] = 0;
            N[i][l] = 1;

            for ( int a = 2; a <= p+1; a++ )
            {        //  basis functions calculation
                int jb = i-a+1;
                if ( jb < 0 )
                    jb = 0;
                for ( int j = jb; j <= i; j++ )
                {
                    N[j][l] = N[j][l]*(u - U[j])/(U[j+a-1] - U[j]) + N[j+1][l]*(U[j+a] - u)/(U[j+a] - U[j+1]);
                }
            }
            u += step;
        }
        for (int j = 0; j < n; j++)
        {
            buffGraphics.setColor(iColor[j % 7]);
            u = U[0];
            int to = (int)u;
            for (int l = 1; l < w2; l++)
            {
                u += step;
                int t1 = (int)u;
                buffGraphics.drawLine(to, (h-1)-(int)(h1*N[j][l-1]), t1, (h-1)-(int)(h1*N[j][l]) );
                to = t1;
            }
        }
        for (int l = p+1; l <= n; l++)
        {
            buffGraphics.setColor(iColor[(l-(p+1)) % 7]);
            buffGraphics.drawLine((int)U[l-1], h1+1, (int)U[l], h1+1);
        }
        buffGraphics.setColor(Color.black);
        String pns;
        int pn = 1;
        for (int i = 0; i < m; i++)
        {
            buffGraphics.drawRect( (int)U[i], h1, 5, 5 );
            pns = Integer.toString(pn);
            buffGraphics.drawString(pns, (int)U[i], h1+20);
            pn = pn + 1;
        }
    }

    public void drawSpline(Graphics buffGraphics) {
        int X,Y;
        int width = 3;
        boolean calMode = false;
        buffGraphics.clearRect(0,0, w2, h1);
        buffGraphics.setColor(Color.blue);
        int pn = 1;
        String pns;

        for (int i = 0; i < n; i++)
        {
            X = (int)Px[i];
            Y = h1-(int)Py[i];
            buffGraphics.drawRect( X-1, Y-1, 5, 5 );
            pns = Integer.toString(pn);
            buffGraphics.setFont(new Font("Courier", Font.BOLD, 42));
            buffGraphics.drawString(pns, X, Y-2);   // draw ctrl pts number
            pn = pn + 1;
        }
        if ( p+1 > 2 )
        {
            int Xo = (int)Px[0];
            int Yo = h1-(int)Py[0];
            for (int i = 1; i < n; i++)
            {
                X = (int)Px[i];
                Y = h1-(int)Py[i];
                buffGraphics.drawLine(Xo,Yo, X,Y);
                Xo = X;
                Yo = Y;
            }
        }
        double sX0 = 0;
        double sY0 = 0;
        for (int j = 0; j < n; j++)
        {
            sX0 += Px[j]*N[j][Tmin];
            sY0 += Py[j]*N[j][Tmin];
        }
        int Xold = (int)sX0;
        int Yold = h1-(int)sY0;
        for ( int b = Tmin+1; b <= Tmax; b++ )
        {
            double sX = 0;
            double sY = 0;
            for (int j = 0; j < n; j++)
            {
                sX += Px[j]*N[j][b];
                sY += Py[j]*N[j][b];
            }
            X = (int)sX;
            Y = h1-(int)sY;
            buffGraphics.setColor(col[b]);
            if ( (X < w2) && (Xold < w2) )
            {
                //buffGraphics.drawLine( Xold,Yold, X,Y );
                /* the following for loop draws a line with variable thickness */
                for (int s = 0, j; s < width; s++)
                {
		    j = s;
		    if (calMode)
                        buffGraphics.drawLine(Xold + j, Yold, Xold + j, Yold);
		    else
                    { // non calligraphy is a little screwy
			if (Math.abs(Xold - X) == Math.abs (Yold - Y) )
                        {
			    buffGraphics.drawLine(Xold, Yold + j, X, Y + j);
			    buffGraphics.drawLine(Xold + j, Yold, X + j, Y);
			}
                        else if (Math.abs(Xold - X) > Math.abs(Yold - Y))
                            buffGraphics.drawLine(Xold, Yold + j, X, Y + j);
			else
                            buffGraphics.drawLine(Xold + j, Yold, X + j, Y);
		    } // else
		    j++;
		    if (j < width)
                    {
			if (calMode)
                            buffGraphics.drawLine(Xold - j, Yold, X - j, Y);
			else
                        { // non calligraphy is a little screwy
			    if (Math.abs(Xold - X) == Math.abs(Yold - Y))
                            {
				buffGraphics.drawLine(Xold, Yold - j, X, Y - j);
				buffGraphics.drawLine(Xold - j, Yold, X - j, Y);
			    }
			    else if (Math.abs(Xold - X) > Math.abs(Yold - Y) )
                                buffGraphics.drawLine(Xold, Yold - j, X, Y - j);
			    else
                                buffGraphics.drawLine(Xold -j, Yold, X - j, Y);
			} // else
		    } // if
		} // for
            }
            Xold = X;
            Yold = Y;
        }
    }

    public void setStartPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setEndPoint(int x, int y) {
        x2 = (x);
        y2 = (y);
    }

    public void drawPerfectRect(Graphics g) {
        int px = Math.min(x,x2);
        int py = Math.min(y,y2);
        int pw=Math.abs(x-x2);
        int ph=Math.abs(y-y2);
        g.drawRect(px, py, pw, ph);
    }

    class MyMouseListener extends MouseAdapter {

        public void mousePressed(MouseEvent e) {
            setStartPoint(e.getX(), e.getY());
        }

        public void mouseDragged(MouseEvent e) {
            // setEndPoint(e.getX(), e.getY());
            int y = h1 - e.getY();

            int x = e.getX();
            if ( y < 0 )
                y = 0;
            if ( y > h1 )
                y = h1;

            int iMin = 0;
            double Rmin = 1e10, r2, xi, yi;

            if ( x < w2 )
            {
                if (x > w2+10)
                    return;
                if (x < 0)
                    x = 0;
                for (int i = 0; i < n; i++)
                {
                    xi = (x - Px[i]);
                    yi = (y - Py[i]);
                    r2 = xi*xi + yi*yi;
                    if ( r2 < Rmin )
                    {
                        iMin = i;
                        Rmin = r2;
                    }
                }
                Px[iMin] = x;
                Py[iMin] = y;
            }
            else
            {
                if ( x > w )
                    x = w;
                for ( int i = 0; i < m; i++ )
                {
                    if ( (r2 = Math.abs(U[i]-x) ) < Rmin )
                    {
                        iMin = i;
                        Rmin = r2;
                    }
                }
                U[iMin] = x;
                // drawFun();
            }
            // drawSpline();
            // repaint();
            repaint();
        }

        public void mouseReleased(MouseEvent e) {
            setEndPoint(e.getX(), e.getY());
            repaint();
        }
    }

    public void paint(Graphics g) {
        // super.paintComponent(g);
        super.paint(g);
        g.setColor(Color.RED);
        // drawPerfectRect(g);
        drawFun(g);
        drawSpline(g);
    }

}
