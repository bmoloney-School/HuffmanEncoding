import java.util.*;

public class HuffmanTree {
    //--GLOBAL VARS--
    //How pixels are initially stored, should be merged with pixelArray but due to time constraints that is not happening
    private ArrayList<Pixel> pixelArray;
    //Queue used to build tree
    private PriorityQueue<Pixel> pixelQueue;
    //Dictionary to store codes for each pixel -- this is SO much easier (and faster) than searching the tree for the correct code every time
    private Hashtable pixelDictionary = new Hashtable();
    //root of the tree -- this is only the root after the tree has been built
    Pixel root = null;


    //Constructor for the HuffmanTree object. It builds the tree and populates the queue from here since those should
    // both only ever be ran once.
    HuffmanTree(ArrayList<Pixel> pixelArray){
        this.pixelArray = pixelArray;
        pixelQueue = new PriorityQueue<Pixel>(pixelArray.size(), new ComparePixelCount());
        populateQueue();
        buildTree();
        generateCodes(root,"");
    }

    //converts the Huffman encoding for a single color to an arraylist of pixels. It uses two iterators and moves
    // the outer one every time a match is found in the tree and moves the inner one through each value in the
    // huffman encoding checking to see if there is a matching node in the tree. If there is, it adds that to the
    // arrayList and then moves the outer iterator to its current position.
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

    //Abusing the HashTable since I know each key only has one object so it is a 1:1 mapping
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

    //Should be removed eventually since it should be populated from the start instead of the ArrayList
    private void populateQueue(){
        for(int i = 0; i < pixelArray.size(); i++){
            pixelQueue.add(pixelArray.get(i));
        }
    }

    //Build the tree from priority queue
    private void buildTree(){
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

    //Uses the tree to generate huffman encoding for each Pixel. Much faster to do this once and store it than traverse
    // the tree each time
    private void generateCodes(Pixel pix, String s){
        if(pix.left == null && pix.right == null ){
            System.out.println(pix.pixelVal + "\t:\t" + s);
            pixelDictionary.put(pix.pixelVal,s);
            return;
        }
        generateCodes(pix.left,s + '0');
        generateCodes(pix.right, s + '1');
    }

    //takes in array of pixels (single color) and turns them into a huffman encoding
    public String getCodeForImage(ArrayList<Integer> image){
        String huffmanCode = "";
        for (int i:image) {
            huffmanCode += pixelDictionary.get(i);
        }
        return huffmanCode;
    }

}
