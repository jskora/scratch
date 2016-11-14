package jfskora.reflection;

class Summer {
    private int total;
    private final String name;

    Summer(String name) {
        this.name = name;
        this.total = 0;
    }

    void add(Integer n) {
        this.total += n;
    }

    String getTotal() {
        return String.format("%s is %d", this.name, this.total);
    }
}
