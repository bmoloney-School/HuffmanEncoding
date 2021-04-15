
import java.io.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.util.concurrent.*;
import java.time.format.DateTimeFormatter;
import java.time.*;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.stage.*;

import static javafx.application.Application.launch;

public class ImageToTree extends Application{
    /*
    NOTE: I recommend java 8 for this since it was the last version that shipped with jfx in the jdk, you can use a newer
    version if you want to go through the trouble of installing jfx yourself

    REQUIREMENTS:
    Due to limitations of the JVM stack, This program will crash on larger images. I am sure there are optimizations
    I could put in place to minimize this issue but I have found it is easier in the short term to just increase the
    heap size with the -Xss6g flag. Without this flag the largest image I got to work consistently was 100x100. It should also
    be noted that this program can take a while to run due to binary trees having a complexity of O(logn).
    --UPDATE
    The error I thought was caused by the stack may not be? On the 720x480 image, I get the occasional Null Pointer error
    when converting the blue values into bits. While troubleshooting I removed the multithreading to no avail and after many
    hours of trying to figure it out it just worked. I have no clue why so if you do please let me know.

    DESCRIPTION:
    This program
    1.) Takes an image
    2.) Builds a Huffman Tree for the image
    3.) Converts the image into a string of 1's and 0's using the tree generated in step 2
    4.) Converts the string generated in step 3 back into an image file
    5.) displays the original image alongside the one that has been encoded and decoded
    along with the % difference of bits from the original compared to the binary string from step 3.
     */

    //TODO figure out issue where result same length as imagein

    public void start(Stage stage) throws IOException, ExecutionException, InterruptedException {



        String img = "10x10.jpg";
        BufferedImage image = readImage(img,10,10);

        // pixel value is unique TODO -- Possibly add alpha value, I do not know how that impacts the image
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

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.now();

        String redResult = redTree.stringFromTree(ImageRed);
        System.out.println("GREEN");
        String greenResult = greenTree.stringFromTree(ImageGreen);
        System.out.println("BLUE");
        String blueResult = blueTree.stringFromTree(ImageBlue);

        LocalDateTime end = LocalDateTime.now();
        Duration duration = Duration.between(start,end);

        System.out.println("RED " + redResult.length());
        System.out.println("GREEN " + greenResult.length());
        System.out.println("BLUE " + blueResult.length());
        System.out.println("This took " + duration.getSeconds() + " seconds or " + duration.getSeconds()/ 60.0 + " minutes");






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
        BufferedImage imageFromArr = new BufferedImage(720, 480,BufferedImage.TYPE_INT_ARGB);

        return null;
    }


    public static BufferedImage readImage(String file,int width, int height) throws IOException{
        BufferedImage image = null;
        File f = null;

        try{
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
