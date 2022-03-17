package net.purevanilla.uhc.parties;

public class Pair<A, B> {

    private A a;
    private B b;

    public Pair(final A a, final B b) {
        this.a = a;
        this.b = b;
    }

    public B getB() {
        return b;
    }

    public void setB(B b) {
        this.b = b;
    }

    public A getA() {
        return a;
    }

    public void setA(A a) {
        this.a = a;
    }


}
