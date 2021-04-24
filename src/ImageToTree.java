import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.*;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static javafx.application.Application.launch;

public class ImageToTree extends Application{

    public void start(Stage stage) throws IOException, ExecutionException, InterruptedException {
        int width = 10, height = 10;
        String img = width + "x" + height +".jpg";

        BufferedImage image = readImage(img);

        /*
        I know the following code is not great but it was the best way I could think of to calculate each color with
        its own tree. Unfortunately that mean there is 3 occurrences of each line.
         */

        ArrayList<Integer> imageRed = new ArrayList<Integer>(image.getWidth() * image.getHeight());
        ArrayList<Integer> imageGreen = new ArrayList<Integer>(image.getWidth() * image.getHeight());
        ArrayList<Integer> imageBlue = new ArrayList<Integer>(image.getWidth() * image.getHeight());

        //Turn the image file into 3 arrays, one for each color
        for(int x = 0; x < image.getWidth(); x ++){
            for(int y = 0; y < image.getHeight(); y ++){
                Color c = new Color(image.getRGB(x,y), true);
                imageRed.add(c.getRed());
                imageGreen.add(c.getGreen());
                imageBlue.add(c.getBlue());
            }
        }
        ArrayList<Pixel> redPixels = imageArrToPixelArr(imageRed);
        ArrayList<Pixel> greenPixels = imageArrToPixelArr(imageGreen);
        ArrayList<Pixel> bluePixels = imageArrToPixelArr(imageBlue);

        System.out.println("RED");
        HuffmanTree redTree = new HuffmanTree(redPixels);
        System.out.println("GREEN");
        HuffmanTree greenTree = new HuffmanTree(greenPixels);
        System.out.println("BLUE");
        HuffmanTree blueTree = new HuffmanTree(bluePixels);

        //Speed things up a bit and calculate the huffman encoding for each color at the same time with multithreading
        //I was very Impressed I got this to work

        ExecutorService threadpool = Executors.newCachedThreadPool();
        Future<String> redString = threadpool.submit(() -> redTree.getCodeForImage(imageRed));
        Future<String> greenString = threadpool.submit(() -> greenTree.getCodeForImage(imageGreen));
        Future<String> blueString = threadpool.submit(() -> blueTree.getCodeForImage(imageBlue));

        String redResult = redString.get();
        String greenResult = greenString.get();
        String blueResult = blueString.get();
        threadpool.shutdown();

        System.out.println("Red takes:" + redResult.length() + " bits to store as opposed to the "
                + width * height * 8 + " bits it would normally take" );
        System.out.println("Green takes:" + greenResult.length() + " bits to store as opposed to the "
                + width * height * 8 + " bits it would normally take" );
        System.out.println("Blue takes:" + blueResult.length() + " bits to store as opposed to the "
                + width * height * 8 + " bits it would normally take" );

        ArrayList<Integer> redImageFromTree = redTree.arrToImage(redResult);
        ArrayList<Integer> greenImageFromTree = greenTree.arrToImage(greenResult);
        ArrayList<Integer> blueImageFromTree = blueTree.arrToImage(blueResult);

        BufferedImage imageFromTree = intArrayToImage(redImageFromTree, greenImageFromTree, blueImageFromTree,width,height);

        InputStream stream = new FileInputStream(img);

        HBox hbox = new HBox();

        Image leftImage = SwingFXUtils.toFXImage(image, null );
        ImageView leftImageView = new ImageView();
        leftImageView.setImage(leftImage);
        leftImageView.setX(10);
        leftImageView.setY(10);
        leftImageView.setPreserveRatio(true);

        Image rightImage = SwingFXUtils.toFXImage(imageFromTree,null);
        ImageView rightImageView = new ImageView();
        rightImageView.setImage(rightImage);
        rightImageView.setX(10);
        rightImageView.setY(10);
        rightImageView.setPreserveRatio(true);
        hbox.getChildren().add(0,leftImageView);
        hbox.getChildren().add(1,rightImageView);
        Group root = new Group(hbox);


        Scene scene = new Scene(root, width * 2, height + 20);
        stage.setTitle("Displaying Image");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    /*
    The following code compares each pixel in the image to the values of the ones already checked. If the value is
    already in the pixel array, it increments the pixel count for that pixelValue. If it is not in the array,
    it adds it to the array. The reason for the weird inner for loop is because if done the same as the outer,
    the JVM interprets this loop as using an iterator and causes a concurrent modification exception.
     */
    public static ArrayList<Pixel> imageArrToPixelArr(ArrayList<Integer> image){
        ArrayList<Pixel> pixelArr = new ArrayList<Pixel>();
        //fix dumb error caused by empty array
        pixelArr.add(new Pixel(image.get(0)));
        pixelArr.get(0).decr();

        for (int i: image) {
            boolean contains = false;
            for (int j = 0; j < pixelArr.size(); j++) {
                if(i == pixelArr.get(j).pixelVal){
                    pixelArr.get(j).pixelCount ++;
                    contains = true;
                }
            }
            if(!contains){
                pixelArr.add(new Pixel(i));
            }
        }
        return pixelArr;
    }

    public static BufferedImage readImage(String file) throws IOException{
        BufferedImage image = null;
        File f = null;

        try{
            f = new File(file);

            image = ImageIO.read(f);
            System.out.println("Read Successfully.");
            return image;
        }
        catch(IOException e){
            System.out.println("Invalid image: \n" + e);
            throw new IOException();
        }
    }

    public static BufferedImage intArrayToImage(ArrayList<Integer> red, ArrayList<Integer> green, ArrayList<Integer> blue, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        int arrayIndex = 0;
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++) {
                g.setColor(new Color(red.get(arrayIndex),green.get(arrayIndex),blue.get(arrayIndex)));
                g.drawLine(i,j,i,j);
                if(arrayIndex < width * height - 2) {
                    arrayIndex++;
                }
            }
        }
        g.dispose();
        return image;
    }
}
