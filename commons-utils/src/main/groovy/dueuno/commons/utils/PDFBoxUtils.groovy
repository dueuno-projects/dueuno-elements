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
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.rendering.PDFRenderer

import java.awt.image.BufferedImage

/**
 * @author Gianluca Sartori
 */
@CompileStatic
class PDFBoxUtils {

    static BufferedImage getPreview(String pathname, Integer dpi = 300) {
        PDDocument pd = PDDocument.load(new File(pathname))
        PDFRenderer pr = new PDFRenderer(pd)
        return pr.renderImageWithDPI(0, dpi)
    }

    static void table(PDDocument document, PDPage page, Float y, @DelegatesTo(BaseTable) Closure closure) {
        Float margin = 50
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

    static void write(PDPageContentStream content, Float x, Float y, String text, Float fontSize = 14) {
        content.beginText()
        content.setFont(PDType1Font.HELVETICA_BOLD, fontSize)
        content.newLineAtOffset(x, y)
        content.showText(text)
        content.endText()
    }

}
