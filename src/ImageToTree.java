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
import javafx.stage.Stage;

import static javafx.application.Application.launch;

public class ImageToTree extends Application{
    /*
    NOTE: I recommend java 8 for this since it was the last version that shipped with jfx in the jdk, you can use a newer
    version if you want to go through the trouble of installing jfx yourself
     */

    //A way to store both the rgb value of the pixel and the number of occurrences of that value.

    public void start(Stage stage) throws IOException, ExecutionException, InterruptedException {

        String img = "100x100.jpg";
        BufferedImage image = readImage(img,100,100);

        // pixel value is unique TODO -- Possibly add alpha value, dunno what that shit is yet
        ArrayList<Integer> ImageRed = new ArrayList<Integer>(image.getWidth() * image.getHeight());
        ArrayList<Integer> ImageGreen = new ArrayList<Integer>(image.getWidth() * image.getHeight());
        ArrayList<Integer> ImageBlue = new ArrayList<Integer>(image.getWidth() * image.getHeight());

        //Turn the image file into 3 arrays, one for each color
        for(int x = 0; x < image.getWidth(); x ++){
            for(int y = 0; y < image.getHeight(); y ++){
                Color c = new Color(image.getRGB(x,y), true);
                ImageRed.add(c.getRed());
                ImageGreen.add(c.getGreen());
                ImageBlue.add(c.getBlue());
            }
        }
        ArrayList<Pixel> redPixels = imageArrToPixelArr(ImageRed);
        ArrayList<Pixel> greenPixels = imageArrToPixelArr(ImageGreen);
        ArrayList<Pixel> bluePixels = imageArrToPixelArr(ImageBlue);

        HuffmanTree redTree = new HuffmanTree(redPixels);
        HuffmanTree greenTree = new HuffmanTree(greenPixels);
        HuffmanTree blueTree = new HuffmanTree(bluePixels);


        ExecutorService threadpool = Executors.newCachedThreadPool();
        Future<String> redString = threadpool.submit(() -> redTree.stringFromTree(ImageRed));
        Future<String> greenString = threadpool.submit(() -> greenTree.stringFromTree(ImageGreen));
        Future<String> blueString = threadpool.submit(() -> blueTree.stringFromTree(ImageBlue));

        String redResult = redString.get();
        String greenResult = greenString.get();
        String blueResult = blueString.get();

        threadpool.shutdown();

        System.out.println("RED " + redResult.length());
        System.out.println("GREEN " + greenResult.length());
        System.out.println("BLUE " + blueResult.length());
        System.out.println("this would normally take 10 * 10 * 256 = 25600 bits per color");






        /*
        CODE TO DISPLAY IMAGES
         */

        //creating the image object
        InputStream stream = new FileInputStream(img);

        Image leftImage = SwingFXUtils.toFXImage(image, null );
        //Image image = new Image(stream);
        //Creating the image view
        ImageView imageView = new ImageView();
        //Setting image to the image view
        imageView.setImage(leftImage);
        //Setting the image view parameters
        imageView.setX(10);
        imageView.setY(10);
        imageView.setFitWidth(680);

        imageView.setPreserveRatio(true);
        //Setting the Scene object
        Group root = new Group(imageView);
        Scene scene = new Scene(root, 680, 540);
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

    //HAVE TO BUILD TREE FIRST
    public static BufferedImage arrToImage(ArrayList<Pixel> red){
        BufferedImage imageFromArr = new BufferedImage(100, 100,BufferedImage.TYPE_INT_ARGB);

        return null;
    }

    /*
    Takes
     */
    public static BufferedImage readImage(String file,int width, int height) throws IOException{
        BufferedImage image = null;
        File f = null;

        try{
            //TEST IMAGE DIMENTIONS T
            f = new File(file);

            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            image = ImageIO.read(f);
            System.out.println("Read Successfully.");
            return image;
        }
        catch(IOException e){
            System.out.println("Your shit is fucked dude: \n" + e);
            throw new IOException();
        }
    }

}
