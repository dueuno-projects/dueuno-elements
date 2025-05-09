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
package dueuno.elements.types

import groovy.transform.CompileStatic

//
// See: https://en.wikipedia.org/wiki/International_System_of_Quantities
//
/**
 * @author Gianluca Sartori
 * @author Francesco Piceghello
 * @author Alessandro Stecca
 */

@CompileStatic
enum QuantityUnit {

    ND(null, 'Not Defined', 0),                           // Not Defined

    QTY(null, 'Quantity', 0),                             // Quantità generica
    NR(null, 'Number', 0),                                // Numero
    PCS(null, 'Pieces', 0),                               // Pezzi

    MASS(),
    TON('MASS', 'quantity.unit.tonne', 6),                // tonnellate 10^5 g
    QUI('MASS', 'quantity.unit.quintal', 5),              // quintali   10^4 g
    KG('MASS', 'quantity.unit.kilogramm', 3),             // kilogrammi 10^3 g
    HG('MASS', 'quantity.unit.hectogram', 2),             // ettogrammi 10^2 g
    DAG('MASS', 'quantity.unit.decagram', 1),             // decagrammi 10 g
    G('MASS', 'quantity.unit.gram', 0),                   // grammi
    DG('MASS', 'quantity.unit.decigram', -1),             // decigrammi 10^-1 g
    CG('MASS', 'quantity.unit.centigram', -2),            // centigrammi 10^-2 g
    MH('MASS', 'quantity.unit.milligram', -3),            // milligrammi 10^-3 g

    LENGTH(),
    KM('LENGTH', 'quantity.unit.kilometre', 3),           // kilometro 10^3 m
    HM('LENGTH', 'quantity.unit.hectometre', 2),          // ettometro 10^2 m
    DAM('LENGTH', 'quantity.unit.decametre', 1),          // decametro 10^1 m
    M('LENGTH', 'quantity.unit.metre', 0),                // metre
    DM('LENGTH', 'quantity.unit.decimetre', -1),          // decimetro 10^-1 m
    CM('LENGTH', 'quantity.unit.centimetre', -2),         // centimetro 10^-2 m
    MM('LENGTH', 'quantity.unit.millimetre', -3),         // millimetro 10^-3 m
    UM('LENGTH', 'quantity.unit.micrometre', -6),         // µm (micrometro) 10^-6 m

    AREA(),
    KM2('AREA', 'quantity.unit.square.kilometre', 6),     // kilometro 10^6 m^2
    HM2('AREA', 'quantity.unit.square.hectometre', 4),    // ettometro 10^4 m^2
    DAM2('AREA', 'quantity.unit.square.decametre', 2),    // decametro 10^2 m^2
    M2('AREA', 'quantity.unit.square.metre', 0),          // square metre
    DM2('AREA', 'quantity.unit.square.decimetre', -2),    // decimetro 10^-2 m^2
    CM2('AREA', 'quantity.unit.square.centimetre', -4),   // centimetro 10^-4 m^2
    MM2('AREA', 'quantity.unit.square.millimetre', -6),   // millimetro 10^-6 m^2
    UM2('AREA', 'quantity.unit.square.micrometre', -8),   // µm (micrometro) 10^-8 m^2

    VOLUME(),
    KM3('VOLUME', 'quantity.unit.cubic.kilometre', 9),    // kilometro 10^9 m^3
    HM3('VOLUME', 'quantity.unit.cubic.hectometre', 6),   // ettometro 10^6 m^3
    DAM3('VOLUME', 'quantity.unit.cubic.decametre', 3),   // decametro 10^3 m^3
    M3('VOLUME', 'quantity.unit.cubic.metre', 0),         // cubic metre
    DM3('VOLUME', 'quantity.unit.cubic.decimetre', -3),   // decimetro 10^-3 m^3
    CM3('VOLUME', 'quantity.unit.cubic.centimetre', -6),  // centimetro 10^-6 m^3
    MM3('VOLUME', 'quantity.unit.cubic.millimetre', -9),  // millimetro 10^-9 m^3
    UM3('VOLUME', 'quantity.unit.cubic.micrometre', -12), // µm (micrometro) 10^-12 m^3

    VOLUME_LITRE(),
    HL('VOLUME_LITRE', 'quantity.unit.hectolitre', 3),    // litre 10^3 m^3
    DAL('VOLUME_LITRE', 'quantity.unit.decalitre', 0),    // litre 10^0 m^3
    L('VOLUME_LITRE', 'quantity.unit.litre', -3),         // litre 10^-3 m^3
    DL('VOLUME_LITRE', 'quantity.unit.decilitre', -6),    // decilitro 10^-6 m^3
    CL('VOLUME_LITRE', 'quantity.unit.centilitre', -9),   // centilitro 10^-9 m^3
    ML('VOLUME_LITRE', 'quantity.unit.millilitre', -12),  // millilitro 10^-12 m^3

    TIME(),
    YEAR('TIME', 'quantity.unit.year', 0),
    MONTH('TIME', 'quantity.unit.month', 0),
    WEEK('TIME', 'quantity.unit.week', 0),
    DAY('TIME', 'quantity.unit.day', 0),
    HOUR('TIME', 'quantity.unit.hour', 0),
    MIN('TIME', 'quantity.unit.minute', 0),
    SEC('TIME', 'quantity.unit.second', 0),
    MSEC('TIME', 'quantity.unit.millisecond', 0),

    POWER(),
    TW('POWER', 'quantity.unit.power.terawatt', 12),     // terawatt 10^9 m^3
    GW('POWER', 'quantity.unit.power.gigawatt', 9),      // gigawatt 10^9 m^3
    MW('POWER', 'quantity.unit.power.megawatt', 6),      // megawatt 10^6 m^3
    KW('POWER', 'quantity.unit.power.kilowatt', 3),      // kilowatt 10^3 m^3
    W('POWER', 'quantity.unit.power.watt', 0),           // watt

    ENERGY(),
    TWH('ENERGY', 'quantity.unit.power.terawatthour', 12),     // terawatthour 10^9 m^3
    GWH('ENERGY', 'quantity.unit.power.gigawatthour', 9),      // gigawatthour 10^9 m^3
    MWH('ENERGY', 'quantity.unit.power.megawatthour', 6),      // megawatthour 10^6 m^3
    KWH('ENERGY', 'quantity.unit.power.kilowatthour', 3),      // kilowatthour 10^3 m^3
    WH('ENERGY', 'quantity.unit.power.watthour', 0),           // watthour

    final String parent
    final String desc
    final Integer magnitude

    QuantityUnit() {}

    QuantityUnit(String parent, String desc, Integer magnitude) {
        this.parent = parent
        this.desc = desc
        this.magnitude = magnitude
    }

    String toString() {
        return name()
    }
}

