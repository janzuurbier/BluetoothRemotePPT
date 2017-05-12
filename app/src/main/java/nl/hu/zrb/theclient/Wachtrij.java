package nl.hu.zrb.theclient;

import java.util.Vector;

public class Wachtrij<T> {
    private Vector<T> rij = new Vector<T>();

    public synchronized void voegToe(T s) {
        //achteraan toevoegen
        rij.add(s);
        notifyAll();
    }

    public synchronized T neemWeg() {
        //vooraan wegnemen
        while(rij.isEmpty() ) {
            try{
                wait();
            }
            catch(InterruptedException e) {}
        }
        T s = rij.firstElement();
        rij.remove(s);
        return s;
    }

    public int lengte() {
        return rij.size();
    }
}