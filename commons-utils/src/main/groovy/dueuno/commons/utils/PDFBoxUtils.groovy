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

import be.quodlibet.boxable.BaseTable
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.apache.pdfbox.rendering.PDFRenderer

import java.awt.image.BufferedImage

/**
 * @author Gianluca Sartori
 * @author Francesco Piceghello
 */
@Slf4j
@CompileStatic
class PDFBoxUtils {

    static Float mmToPt(Float mm) {
        Float pointsPerInch = 72
        Float pointsPerMm = 1 / (10 * 2.54f) * pointsPerInch as Float
        return mm.multiply(pointsPerMm) as Float
    }

    static PDPage createPage(Float width, Float height) {
        return new PDPage(new PDRectangle(mmToPt(width), mmToPt(height)))
    }

    static BufferedImage getPreview(String pathname, Integer dpi = 300) {
        PDDocument pd = Loader.loadPDF(new File(pathname))
        PDFRenderer pr = new PDFRenderer(pd)
        return pr.renderImageWithDPI(0, dpi)
    }

    static void table(PDDocument document, PDPage page, Float y, @DelegatesTo(BaseTable) Closure closure) {
        Float margin = 25
        // starting y position is whole page height subtracted by top and bottom margin
        Float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin) as Float
        // we want table across whole page width (subtracted by left and right margin ofcourse)
        Float tableWidth = page.getMediaBox().getWidth() - (2 * margin) as Float

        Boolean drawContent = true
        Float yStart = yStartNewPage
        Float bottomMargin = 70
        // y position is your coordinate of top left corner of the table
        Float yPosition = y

        BaseTable table = new BaseTable(yPosition, yStartNewPage, bottomMargin, tableWidth, margin, document, page, true, drawContent)
        closure.delegate = table
        closure.call(table)
        table.draw()
    }

    static void write(PDPageContentStream content, Float x, Float y, String text, PDFont font, Float fontSize) {
        content.beginText()
        content.setFont(font, fontSize)
        content.newLineAtOffset(x, y)
        content.showText(text)
        content.endText()
    }

    static void write(PDPageContentStream content, Float x, Float y, String text, String fontName, Float fontSize) {
        PDFont font = new PDType1Font(Standard14Fonts.FontName.valueOf(fontName))

        write content, x, y, text, font, fontSize
    }

    static void write(PDPageContentStream content, Float x, Float y, String text, Float fontSize = 14) {
        String fontName = Standard14Fonts.FontName.HELVETICA_BOLD.toString()

        write content, x, y, text, fontName, fontSize
    }

    static void writeCentered(PDPageContentStream content, PDDocument doc, String text, Float fontSize = 14, Float marginTop = 0) {
        PDRectangle mediaBox = doc.getPage(0).mediaBox
        PDFont font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD)

        Float titleWidth = font.getStringWidth(text) / 1000 * fontSize as Float
        Float titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize as Float
        Float x = (mediaBox.width - titleWidth) / 2 as Float
        Float y = mediaBox.height - marginTop - titleHeight as Float

        write content, x, y, text, font, fontSize
    }

    static void image(PDPageContentStream content, PDDocument doc, String imageName, Float x, Float y, Float width, Float height) {
        PDImageXObject image = PDImageXObject.createFromFile(imageName, doc)

        width = mmToPt(width)
        height = mmToPt(height)

        content.drawImage(image, x, y, width, height)
    }

    static void imageCentered(PDPageContentStream content, PDDocument doc, String imageName, Float width, Float height, Float marginTop = 0) {
        PDRectangle mediaBox = doc.getPage(0).mediaBox
        Float x = (mediaBox.width - mmToPt(width)) / 2 as Float
        Float y = mediaBox.height - marginTop - mmToPt(height) as Float

        image content, doc, imageName, x, y, width, height
    }
}
