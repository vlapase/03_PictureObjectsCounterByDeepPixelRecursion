// file: Deep pixel recursion objects counter from b/w image source.

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PictureObjectsCounterByDeepPixelRecursion {

    // sensitivity level
    public static int pictureSensitivityLevel = 100;
    public static int pictureMaxSizeLevel = 800;

    // new small picture to avoid "Stackoverflow"
    public static int pictureNewWidth = 300;

    public static int[][] ocean;

    // 0 - no pixel
    // 1 - pixel is present
    // so, 2 will be first object number
    public static int marker = 2;

    // drop objects smaller then:
    public static int maxPixelsPerCountableObject = 30;

    // current object pixel counter
    public static int inObjectPixelsCounter;

    // recursion level counter
    private static int level = 0;


    public static void main(String[] args) {


        String inputFile;

        try {
            inputFile = args[0];
        } catch (Exception e) {
            System.out.println("Sorry, no Program arguments added, trying to process \"test.jpg\"");
            inputFile = "test.jpg";
        }


        // creating the "ocean" to find an "islands"
        ocean = PictureToIntArray(inputFile);

        System.out.println(" ");

        // MAIN ENGINE
        for (int y = 1; y < ocean.length - 1; y++) {

            for (int x = 1; x < ocean[0].length - 1; x++) {

                inObjectPixelsCounter = 0;
                if (ocean[y][x] == 1) {

                        PixelCrawl(y, x);

                    System.out.println("Objects with less then " + maxPixelsPerCountableObject + " pixels is not countable. Object # " + (marker - 1) + " have " + inObjectPixelsCounter + " pixels.");
                    if (inObjectPixelsCounter > maxPixelsPerCountableObject) marker++;
                }
            }
        }

        // summary
        System.out.println(" ");
        System.out.println("Last recursion level was: " + level);
        System.out.println("Objects found: " + (marker - 2));

        PicToConsole();

        // summary again after picture to console print
        System.out.println(" ");
        System.out.println("Last recursion level was: " + level);
        System.out.println("Objects found: " + (marker - 2));

    }

    // picture to console print
    private static void PicToConsole() {
        // picture to console print
        for (int y = 1; y < ocean.length; y++) {
            System.out.println(" ");
            for (int x = 1; x < ocean[0].length; x++) {
                System.out.print(ocean[y][x]);
            }
        }
    }

    /** pixel recursion single pixel crawler
     * @param y crawler initial position
     * @param x crawler initial position
     */
    static void PixelCrawl(int y, int x) {
        inObjectPixelsCounter++;
        level++;

        ocean[y][x] = marker;

        // find near
        if (y > 0 && ocean[y - 1][x] == 1) {
            // top
            PixelCrawl(y - 1, x);
        }
        if (y < ocean.length - 1 && ocean[y + 1][x] == 1) {
            // bottom
            PixelCrawl(y + 1, x);
        }
        if (x > 0 && ocean[y][x - 1] == 1) {
            // left
            PixelCrawl(y, x - 1);
        }
        if (x < ocean[0].length - 1 && ocean[y][x + 1] == 1) {
            // right
            PixelCrawl(y, x + 1);
        }
    }


    /** Zoom picture
     * @param sourcePic source picture
     * @param newW new width
     * @param newH new height
     * @return result picture
     */
    static BufferedImage ZoomIn(BufferedImage sourcePic, int newW, int newH) {

        BufferedImage smallerPic = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);

        // Creates a Graphics2D, which can be used to draw into this BufferedImage.
        Graphics2D smallerPic2D = smallerPic.createGraphics();

        // Draws as much of the specified image as has already been scaled
        // to fit inside the specified rectangle.
        smallerPic2D.drawImage(sourcePic, 0, 0, newW, newH, null);

        // Deletes of this graphics context and releases any system resources that it is using.
        smallerPic2D.dispose();
        return smallerPic;
    }


    /** read file, zoom it and return as array
     * @param fileFromMethodStart read file
     * @return return as array
     */
    private static int[][] PictureToIntArray(String fileFromMethodStart) {
        int[][] inPictureAsArray = new int[0][];
        try {
            File inPicture = new File(fileFromMethodStart);
            BufferedImage inPictureInMemory = ImageIO.read(inPicture);
            BufferedImage smallerPic;

            int w = inPictureInMemory.getWidth();
            int h = inPictureInMemory.getHeight();

            System.out.println("picture initial size: W " + inPictureInMemory.getWidth() + " H " + inPictureInMemory.getHeight());

            // resize big image
            if ((inPictureInMemory.getWidth() + inPictureInMemory.getHeight()) > pictureMaxSizeLevel) {
                //noinspection RedundantCast
                smallerPic = ZoomIn(inPictureInMemory, pictureNewWidth, (int) (pictureNewWidth * h / w));
            } else smallerPic = inPictureInMemory;

            System.out.print("picture processing size: W " + smallerPic.getWidth() + " H " +
                    smallerPic.getHeight());

            inPictureAsArray = new int[smallerPic.getHeight()][smallerPic.getWidth()];

            // fill new array from source
            for (int y = 0; y < smallerPic.getHeight(); y++) {
                for (int x = 0; x < smallerPic.getWidth(); x++) {

                    // take current pixel color
                    Color color = new Color(smallerPic.getRGB(x, y));

                    // it can be any color, either red or blue
                    int red = color.getRed();
                    int green = color.getGreen();
                    int blue = color.getBlue();

                    if (red < pictureSensitivityLevel || green < pictureSensitivityLevel || blue < pictureSensitivityLevel) {
                        inPictureAsArray[y][x] = 1;
                    } else inPictureAsArray[y][x] = 0;
                }
            }
        } catch (IOException e) {
            System.out.println("source image read error");
        }
        return inPictureAsArray;
    }
}
