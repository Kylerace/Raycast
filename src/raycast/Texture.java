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

public class Texture {
    public int[] pixels;
    public int size;

    public Texture(String filePath, int size) {
        this.size = size;
        pixels = new int[size * size];
        try {
            BufferedImage texture = ImageIO.read(new File(filePath));
            texture.getRGB(0, 0, size, size, pixels, 0, size);
            System.out.println("Texture loaded");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
