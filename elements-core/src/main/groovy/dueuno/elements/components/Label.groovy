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
package dueuno.elements.components

import dueuno.elements.core.Component
import dueuno.elements.core.PrettyPrinterProperties
import dueuno.elements.style.TextAlign
import dueuno.elements.style.TextStyle
import dueuno.elements.style.TextWrap
import groovy.transform.CompileStatic

/**
 * @author Gianluca Sartori
 */

@CompileStatic
class Label extends Component {

    Object text
    String html
    String url
    String icon

    TextAlign textAlign
    TextWrap textWrap
    List<TextStyle> textStyle
    Boolean border

    PrettyPrinterProperties prettyPrinterProperties

    Label(Map args) {
        super(args)

        skipFocus = true

        html = args.html
        url = args.url
        icon = args.icon

        textAlign = args.textAlign == null ? TextAlign.DEFAULT : args.textAlign as TextAlign
        textWrap = args.textWrap == null ? TextWrap.NO_WRAP : args.textWrap as TextWrap
        setTextStyle(args.textStyle)

        border = args.border == null ? false : args.border

        prettyPrinterProperties = new PrettyPrinterProperties(args)
        prettyPrinterProperties.messageArgs = args.textArgs as List
        prettyPrinterProperties.renderMessagePrefix = args.renderMessagePrefix == null ? false : args.renderMessagePrefix
        prettyPrinterProperties.renderBoolean = args.renderBoolean == null ? true : args.renderBoolean
        prettyPrinterProperties.highlightNegative = args.highlightNegative == null ? true : args.highlightNegative

        containerSpecs.label = args.label ?: ''

        setText(args.text)
    }

    void setText(Object value) {
        Object textObj

        if (value == null) {
            textObj = buildLabel(id, prettyPrinterProperties.messagePrefix, prettyPrinterProperties.messageArgs)

        } else if (value in Boolean && prettyPrinterProperties.renderBoolean) {
            if (value) icon = 'fa-solid fa-check'
            textObj = ''

        } else if (value in Number && prettyPrinterProperties.highlightNegative) {
            if ((value as Number) < 0) textColor = '#cc0000'
            textObj = value

        } else {
            textObj = value
        }

        text = prettyPrint(textObj, prettyPrinterProperties)
    }

    void setTextArgs(List value) {
        prettyPrinterProperties.messageArgs = value
    }

    void setTextStyle(Object value) {
        switch (value) {
            case TextStyle:
                textStyle = [value as TextStyle]
                break

            case List<TextStyle>:
                textStyle = value as List<TextStyle>
                break

            default:
                textStyle = [TextStyle.NORMAL]
        }
    }

    String getTextStyle() {
        return textStyle.join(' ')
    }

    void setHtml(String value) {
        if (html) textWrap = TextWrap.DEFAULT
        html = value
    }

    String getPrettyHtml() {
        return prettyPrint(html, prettyPrinterProperties)
    }

}
