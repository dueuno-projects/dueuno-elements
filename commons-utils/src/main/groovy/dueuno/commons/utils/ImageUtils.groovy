/*
 * Copyright 2021 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dueuno.commons.utils

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import javax.imageio.ImageIO
import java.awt.*
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage

/**
 * @author Gianluca Sartori
 */

@Slf4j
@CompileStatic
class ImageUtils {

    /**
     * Loads an image
     *
     * @param image The image
     * @param pathname The pathname of the file to load
     */
    static BufferedImage load(String pathname) {
        return ImageIO.read(new File(pathname))
    }

    /**
     * Saves an image
     *
     * @param image The image
     * @param pathname The pathname of the file to save
     * @param format File format, it may be 'gif', 'png' or 'jpg'
     */
    static void save(BufferedImage image, String pathname, ImageUtilsFormat format = getFormatFromFilename(pathname)) {
        File file = new File(pathname)

        try {
            ImageIO.write(image, format.extension, file)

        } catch (IOException e) {
            throw new Exception("Error writing file '${file}': ${e.message}")
        }
    }

    /**
     * Returns a byte[] from a BufferedImage
     *
     * @param image The image
     * @param format File format, it may be 'gif', 'png' or 'jpg'
     */
    static byte[] toByteArray(BufferedImage image, ImageUtilsFormat format) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ImageIO.write(image, format.extension, baos)
        return baos.toByteArray()
    }

    static BufferedImage scaleWidth(BufferedImage image , Integer width) {
        return resize(image, width, -1)
    }

    static BufferedImage scaleHeight(BufferedImage image , Integer height) {
        return resize(image, -1, height)
    }

    static BufferedImage resize(BufferedImage image , Integer width, Integer height = -1 /* auto calculate height by default */) {
        Image resizedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH)
        return imageToBufferedImage(resizedImage)
    }

    static BufferedImage rotate(BufferedImage bi, Boolean right = true) {
        Integer w = bi.width
        Integer h = bi.height
        Integer angle = right ? 90 : -90
        BufferedImage rotated = new BufferedImage(h, w, bi.type)
        Graphics2D g2d = rotated.createGraphics()

        AffineTransform at = new AffineTransform()
        at.translate((h - w) / 2 as double, (w - h) / 2 as double)
        at.rotate(Math.toRadians(angle), w/2 as double, h/2 as double)
        g2d.setTransform(at)
        g2d.drawImage(bi, 0, 0, null)
        g2d.dispose()

        return rotated
    }

    static ImageUtilsFormat getFormatFromFilename(String filename) {
        String extension = FileUtils.stripExtension(filename)
        return ImageUtilsFormat.get(extension)
    }

    private static BufferedImage imageToBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img
        }

        // Create a buffered image with transparency
        BufferedImage bi = new BufferedImage(
                img.getWidth(null), img.getHeight(null),
                BufferedImage.TYPE_INT_RGB)

        Graphics2D graphics2D = bi.createGraphics()
        graphics2D.drawImage(img, 0, 0, null)
        graphics2D.dispose()

        return bi
    }

}
