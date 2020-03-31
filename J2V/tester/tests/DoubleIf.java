class DoubleIf{
    public static void main(String[] a){
        boolean var;
        var = true;
        if (var) {
            System.out.println(1);
            if (var) {
            System.out.println(1);
            }
            else {
                System.out.println(0);
            }
        }
        else {
            System.out.println(0);
            if (var) {
            System.out.println(1);
        }
        else {
            System.out.println(0);
        }
        }
    }
}
