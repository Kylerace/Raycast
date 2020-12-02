/**  
 *  Title of project
 * 
 *  Date of completion
 * 
 *  This program was created under the collaboration of Nathan Grimsey, Eric Lumpkin, Dylan Gibbons-Churchward, and Matthew McGuinn
 *  for Martin Hock's CS143 class in the Fall quarter of 2020.
 * 
 *  This code may be found at https://github.com/CS143-Raycasting-Project/Raycast along with documentation.
 */

package raycast;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.*;

public class Texture {
    public int[] pixels;
    public int size;

    public Texture(String filePath, int size) {
        this.size = size;
        BufferedImage texture;
        WritableRaster texRaster;
        pixels = new int[size * size];
        try {
            texture = ImageIO.read(new File(filePath));
            for(int x = 0; x < size; x++) {
                for(int y = 0; y < size; y++) {
                    pixels[x*size +y] = texture.getRGB(x, y);
                }
            }
            texRaster = texture.getRaster();
            texture.setAccelerationPriority(1.0f);
        }
        catch (Exception e) {
            System.out.println("You need the asset files!");
        }
    }

}
