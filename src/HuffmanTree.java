import java.util.*;

public class HuffmanTree {
    //How pixels are initially stored, should be merged with pixelArray but due to time constraints thats not happening
    private ArrayList<Pixel> pixelArray;
    //Queue used to build tree
    private PriorityQueue<Pixel> pixelQueue;
    //Dictionary to store codes for each pixel -- this is SO much easier (and faster) than searching the tree for the correct code every time
    private Hashtable pixelDictionary = new Hashtable();

    Pixel root = null;

    HuffmanTree(ArrayList<Pixel> pixelArray){
        this.pixelArray = pixelArray;
        pixelQueue = new PriorityQueue<Pixel>(pixelArray.size(), new ComparePixelCount());
        populateQueue();
        buildTree();
        generateCodes(root,"");
    }

    public ArrayList<Integer> arrToImage(String color){
        ArrayList<Integer> pixelValues = new ArrayList<Integer>(color.length());
        for(int i = 0; i < color.length() - 1; i++){
            for(int j = i; j < color.length() - 1; j++) {
                if (pixelDictionary.containsValue(color.substring(i,j))){
                    pixelValues.add(getKeyFromValue(color.substring(i,j)));
                    i = j;
                }
            }
        }
        return pixelValues;
    }

    //Janky code so I can get the key from the Value
    private Integer getKeyFromValue(String value){

        Integer key = null;

        //get an iterator for the keys
        Iterator<Integer> itr = pixelDictionary.keySet().iterator();
        Integer currentKey = null;

        while( itr.hasNext() ){
            currentKey = itr.next();
            if( pixelDictionary.get(currentKey).equals(value) ){
                return currentKey;
            }
        }
        return key;
    }






    void populateQueue(){
        for(int i = 0; i < pixelArray.size(); i++){
            pixelQueue.add(pixelArray.get(i));
        }
    }

    void buildTree(){
        // no reason to run again if its already built
        if (root != null){
            return;
        }

        while (pixelQueue.size() > 1){
            Pixel p1 = pixelQueue.peek();
            pixelQueue.poll();

            Pixel p2 = pixelQueue.peek();
            pixelQueue.poll();

            Pixel p3 = new Pixel(-1, p1.pixelCount + p2.pixelCount);
            p3.left = p1;
            p3.right = p2;
            root = p3;
            pixelQueue.add(p3);
        }
    }

    private void generateCodes(Pixel pix, String s){
        if(pix.left == null && pix.right == null ){
            System.out.println(pix.pixelVal + "\t:\t" + s);
            pixelDictionary.put(pix.pixelVal,s);
            return;
        }
        generateCodes(pix.left,s + '0');
        generateCodes(pix.right, s + '1');
    }

    public String getCodeForImage(ArrayList<Integer> image){
        String huffmanCode = "";
        for (int i:image) {
            huffmanCode += pixelDictionary.get(i);
        }
        return huffmanCode;
    }

}
