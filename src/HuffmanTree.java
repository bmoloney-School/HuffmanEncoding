import java.util.ArrayList;
import java.util.Collections;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class HuffmanTree {
    ArrayList<Pixel> pixelArray;
    Node root;

    HuffmanTree(ArrayList<Pixel> pixelArray){
        this.pixelArray = pixelArray;
        sortArray();
        buildTree();
    }

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();

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
                root.left = n1;
                root.right = n2;
                i++;
            }
            else if(root.pixel < n1.pixel){
                Node temp = root;
                root = new Node(root.pixel + n1.pixel);
                root.left = temp;
                root.right = n1;
            }
            else if(n1.pixel < root.pixel){
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
            Node node = root;
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
        }
        return huffmanString;
    }

}
