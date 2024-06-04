/*
 * Copyright 2021 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
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

import javax.imageio.ImageIO
import java.awt.*
import java.awt.image.BufferedImage

/**
 * @author Gianluca Sartori
 */

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
     * @param formatName File format, it may be 'gif', 'png' or 'jpg'
     */
    static void save(BufferedImage image, String pathname, String formatName) {
        File file = new File(pathname)

        try {
            ImageIO.write(image, formatName, file)

        } catch (IOException e) {
            throw new Exception("Error writing file '${file}': ${e.message}")
        }
    }

    static BufferedImage resize(BufferedImage image , Integer width, Integer height = -1 /* auto calculate height by default */) {
        /**
         * SCALE_AREA_AVERAGING
         * SCALE_DEFAULT
         * SCALE_FAST
         * SCALE_REPLICATE
         * SCALE_SMOOTH
         */
        Image resizedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH)
        return convertToBufferedImage(resizedImage)
    }

    private static BufferedImage convertToBufferedImage(Image img) {
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
