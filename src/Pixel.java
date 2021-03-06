import java.util.Comparator;
public class Pixel{
    protected int pixelVal = 0;
    //Number of occurrences of that value
    protected int pixelCount = 1;
    Pixel(int pixelVal){
        this.pixelVal = pixelVal;
    }
    Pixel(int pixelVal, int pixelCount){
        this.pixelCount = pixelCount;
        this.pixelVal = pixelVal;

    }
    protected void incr(){
        pixelCount++;
    }
    protected void decr(){
        pixelCount--;
    }

    Pixel left;
    Pixel right;

}
class ComparePixelCount implements Comparator<Pixel>{
    @Override
    public int compare(Pixel p1, Pixel p2) {
        return p1.pixelCount - p2.pixelCount;
    }
}