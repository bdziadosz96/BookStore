package pl.bookstore.ebook;

class Testing {
    public void iterationTest() {
        int x = 5;
        int y = ++x;
        System.out.println(x);
        System.out.println(y);
    }
    public static void main(String[] args) {
        Testing testing = new Testing();
        testing.iterationTest();
    }
}
