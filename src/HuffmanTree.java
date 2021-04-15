import java.util.ArrayList;
import java.util.Collections;


public class HuffmanTree {
    ArrayList<Pixel> pixelArray;
    Node root;

    HuffmanTree(ArrayList<Pixel> pixelArray){
        this.pixelArray = pixelArray;
        sortArray();
        buildTree();
    }

    protected class Node {
        int pixel = 0;
        //Sub nodes in the tree
        Node left;
        Node right;
        Node(int pixel){
            this.pixel = pixel;
        }
        protected boolean hasLeft(){
            if(left == null) return false;
            return true;
        }
        protected boolean hasRight(){
            if(right == null) return false;
            return true;
        }
    }

    void sortArray(){
        Collections.sort(pixelArray,new ComparePixelCount());
        for (Pixel p:pixelArray) {
            System.out.println(p.pixelCount + " " + p.pixelVal);
        }
    }

    void buildTree(){
        // no reason to run again if its already built
        if (root != null){
            return;
        }
        Node n1;
        Node n2;
        for(int i = 0; i < pixelArray.size(); i ++){
            n1 = new Node(pixelArray.get(i).pixelVal);

            if(root == null){
                n2 = new Node(pixelArray.get(i + 1).pixelVal);
                root = new Node(n1.pixel + n2.pixel);
                root.left = n2;
                root.right = n1;
                i++;
            }
            else if(root.pixel < n1.pixel){
                Node temp = root;
                root = new Node(root.pixel + n1.pixel);
                root.left = temp;
                root.right = n1;
            }
            else {
                Node temp = root;
                root = new Node(root.pixel + n1.pixel);
                root.left = n1;
                root.right = temp;
            }
            System.out.println("ROOT: " + root.pixel + " LEFT: " + root.left.pixel + " RIGHT: " + root.right.pixel);
        }
    }


    public String stringFromTree(ArrayList<Integer> pixelValues) {
        String huffmanString = "";
        for (int p:pixelValues) {
            huffmanString = stringFromTree2(p, root, huffmanString);
            System.out.println("");
            /*Node node = root;
            while (true) {
                if (p == node.left.pixel) {
                    huffmanString += "0";
                    break;
                } else if (p == node.right.pixel) {
                    huffmanString += "1";
                    break;
                }
                huffmanString += "1";
                node = node.right;
            }
            */
        }
        return huffmanString;
    }


    public String stringFromTree2(int pixel, Node root, String bits) {
        try {
            if (root.right.pixel == pixel) {
                System.out.print("1");
                return bits += "1";
            } else if (root.left.pixel == pixel) {
                System.out.print("0");
                return bits += "0";
            } else {
                System.out.print("1 (deeper)");
                stringFromTree2(pixel, root.right, bits += "1");
            }
        }
        catch (NullPointerException e) {
            System.out.println(e + " Pixel: " + pixel + " root: " + root.pixel);
        }
        return bits;
    }


}
