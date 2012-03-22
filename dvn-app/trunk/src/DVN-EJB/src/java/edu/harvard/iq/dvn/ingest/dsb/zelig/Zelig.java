/*
   Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
   Version 3.0.
*/
package edu.harvard.iq.dvn.ingest.dsb.zelig;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="model" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="helpLink">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="url" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *                           &lt;attribute name="rhelp" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="packageDependency" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="relationship" type="{http://gking.harvard.edu/zelig}PACK_REL" default="required" />
 *                           &lt;attribute name="CRAN" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="formula">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="equation" maxOccurs="unbounded">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="outcome" maxOccurs="unbounded">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="modelingType" maxOccurs="unbounded" minOccurs="0">
 *                                                   &lt;complexType>
 *                                                     &lt;simpleContent>
 *                                                       &lt;extension base="&lt;http://gking.harvard.edu/zelig>MODEL">
 *                                                       &lt;/extension>
 *                                                     &lt;/simpleContent>
 *                                                   &lt;/complexType>
 *                                                 &lt;/element>
 *                                                 &lt;element name="dataType" maxOccurs="unbounded" minOccurs="0">
 *                                                   &lt;complexType>
 *                                                     &lt;simpleContent>
 *                                                       &lt;extension base="&lt;http://gking.harvard.edu/zelig>DATA">
 *                                                       &lt;/extension>
 *                                                     &lt;/simpleContent>
 *                                                   &lt;/complexType>
 *                                                 &lt;/element>
 *                                               &lt;/sequence>
 *                                               &lt;attribute name="minEquations" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" default="1" />
 *                                               &lt;attribute name="maxEquations" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" default="1" />
 *                                               &lt;attribute name="minVar" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="1" />
 *                                               &lt;attribute name="maxVar" type="{http://www.w3.org/2001/XMLSchema}integer" default="1" />
 *                                               &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                       &lt;element name="explanatory" maxOccurs="unbounded" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="modelingType" maxOccurs="unbounded" minOccurs="0">
 *                                                   &lt;complexType>
 *                                                     &lt;simpleContent>
 *                                                       &lt;extension base="&lt;http://gking.harvard.edu/zelig>MODEL">
 *                                                       &lt;/extension>
 *                                                     &lt;/simpleContent>
 *                                                   &lt;/complexType>
 *                                                 &lt;/element>
 *                                                 &lt;element name="dataType" maxOccurs="unbounded" minOccurs="0">
 *                                                   &lt;complexType>
 *                                                     &lt;simpleContent>
 *                                                       &lt;extension base="&lt;http://gking.harvard.edu/zelig>DATA">
 *                                                       &lt;/extension>
 *                                                     &lt;/simpleContent>
 *                                                   &lt;/complexType>
 *                                                 &lt;/element>
 *                                               &lt;/sequence>
 *                                               &lt;attribute name="minVar" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="1" />
 *                                               &lt;attribute name="maxVar" type="{http://www.w3.org/2001/XMLSchema}integer" default="-1" />
 *                                               &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                     &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="interceptAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean" default="1" />
 *                                     &lt;attribute name="crossedAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean" default="1" />
 *                                     &lt;attribute name="nestedAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean" default="1" />
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="minEquations" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" default="1" />
 *                           &lt;attribute name="maxEquations" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" default="1" />
 *                           &lt;attribute name="simulEq" type="{http://www.w3.org/2001/XMLSchema}boolean" default="0" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="setx">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="maxSetx" type="{http://gking.harvard.edu/zelig}MAXSETX" default="2" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="specialFunction" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "model"
})
@XmlRootElement(name = "zelig")
public class Zelig {

    @XmlElement(required = true)
    protected List<Zelig.Model> model;
    @XmlAttribute
    protected String version;

    /**
     * Gets the value of the model property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the model property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getModel().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Zelig.Model }
     * 
     * 
     */
    public List<Zelig.Model> getModel() {
        if (model == null) {
            model = new ArrayList<Zelig.Model>();
        }
        return this.model;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="helpLink">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="url" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
     *                 &lt;attribute name="rhelp" type="{http://www.w3.org/2001/XMLSchema}string" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="packageDependency" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="relationship" type="{http://gking.harvard.edu/zelig}PACK_REL" default="required" />
     *                 &lt;attribute name="CRAN" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="formula">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="equation" maxOccurs="unbounded">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="outcome" maxOccurs="unbounded">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="modelingType" maxOccurs="unbounded" minOccurs="0">
     *                                         &lt;complexType>
     *                                           &lt;simpleContent>
     *                                             &lt;extension base="&lt;http://gking.harvard.edu/zelig>MODEL">
     *                                             &lt;/extension>
     *                                           &lt;/simpleContent>
     *                                         &lt;/complexType>
     *                                       &lt;/element>
     *                                       &lt;element name="dataType" maxOccurs="unbounded" minOccurs="0">
     *                                         &lt;complexType>
     *                                           &lt;simpleContent>
     *                                             &lt;extension base="&lt;http://gking.harvard.edu/zelig>DATA">
     *                                             &lt;/extension>
     *                                           &lt;/simpleContent>
     *                                         &lt;/complexType>
     *                                       &lt;/element>
     *                                     &lt;/sequence>
     *                                     &lt;attribute name="minEquations" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" default="1" />
     *                                     &lt;attribute name="maxEquations" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" default="1" />
     *                                     &lt;attribute name="minVar" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="1" />
     *                                     &lt;attribute name="maxVar" type="{http://www.w3.org/2001/XMLSchema}integer" default="1" />
     *                                     &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                             &lt;element name="explanatory" maxOccurs="unbounded" minOccurs="0">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="modelingType" maxOccurs="unbounded" minOccurs="0">
     *                                         &lt;complexType>
     *                                           &lt;simpleContent>
     *                                             &lt;extension base="&lt;http://gking.harvard.edu/zelig>MODEL">
     *                                             &lt;/extension>
     *                                           &lt;/simpleContent>
     *                                         &lt;/complexType>
     *                                       &lt;/element>
     *                                       &lt;element name="dataType" maxOccurs="unbounded" minOccurs="0">
     *                                         &lt;complexType>
     *                                           &lt;simpleContent>
     *                                             &lt;extension base="&lt;http://gking.harvard.edu/zelig>DATA">
     *                                             &lt;/extension>
     *                                           &lt;/simpleContent>
     *                                         &lt;/complexType>
     *                                       &lt;/element>
     *                                     &lt;/sequence>
     *                                     &lt;attribute name="minVar" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="1" />
     *                                     &lt;attribute name="maxVar" type="{http://www.w3.org/2001/XMLSchema}integer" default="-1" />
     *                                     &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                           &lt;/sequence>
     *                           &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                           &lt;attribute name="interceptAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean" default="1" />
     *                           &lt;attribute name="crossedAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean" default="1" />
     *                           &lt;attribute name="nestedAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean" default="1" />
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *                 &lt;attribute name="minEquations" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" default="1" />
     *                 &lt;attribute name="maxEquations" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" default="1" />
     *                 &lt;attribute name="simulEq" type="{http://www.w3.org/2001/XMLSchema}boolean" default="0" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="setx">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="maxSetx" type="{http://gking.harvard.edu/zelig}MAXSETX" default="2" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="specialFunction" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "description",
        "helpLink",
        "packageDependency",
        "formula",
        "setx"
    })
    public static class Model {

        @XmlElement(required = true)
        protected String description;
        @XmlElement(required = true)
        protected Zelig.Model.HelpLink helpLink;
        protected List<Zelig.Model.PackageDependency> packageDependency;
        @XmlElement(required = true)
        protected Zelig.Model.Formula formula;
        @XmlElement(required = true)
        protected Zelig.Model.Setx setx;
        @XmlAttribute(required = true)
        protected String name;
        @XmlAttribute
        protected String label;
        @XmlAttribute
        protected String specialFunction;

        /**
         * Gets the value of the description property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDescription() {
            return description;
        }

        /**
         * Sets the value of the description property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDescription(String value) {
            this.description = value;
        }

        /**
         * Gets the value of the helpLink property.
         * 
         * @return
         *     possible object is
         *     {@link Zelig.Model.HelpLink }
         *     
         */
        public Zelig.Model.HelpLink getHelpLink() {
            return helpLink;
        }

        /**
         * Sets the value of the helpLink property.
         * 
         * @param value
         *     allowed object is
         *     {@link Zelig.Model.HelpLink }
         *     
         */
        public void setHelpLink(Zelig.Model.HelpLink value) {
            this.helpLink = value;
        }

        /**
         * Gets the value of the packageDependency property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the packageDependency property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPackageDependency().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Zelig.Model.PackageDependency }
         * 
         * 
         */
        public List<Zelig.Model.PackageDependency> getPackageDependency() {
            if (packageDependency == null) {
                packageDependency = new ArrayList<Zelig.Model.PackageDependency>();
            }
            return this.packageDependency;
        }

        /**
         * Gets the value of the formula property.
         * 
         * @return
         *     possible object is
         *     {@link Zelig.Model.Formula }
         *     
         */
        public Zelig.Model.Formula getFormula() {
            return formula;
        }

        /**
         * Sets the value of the formula property.
         * 
         * @param value
         *     allowed object is
         *     {@link Zelig.Model.Formula }
         *     
         */
        public void setFormula(Zelig.Model.Formula value) {
            this.formula = value;
        }

        /**
         * Gets the value of the setx property.
         * 
         * @return
         *     possible object is
         *     {@link Zelig.Model.Setx }
         *     
         */
        public Zelig.Model.Setx getSetx() {
            return setx;
        }

        /**
         * Sets the value of the setx property.
         * 
         * @param value
         *     allowed object is
         *     {@link Zelig.Model.Setx }
         *     
         */
        public void setSetx(Zelig.Model.Setx value) {
            this.setx = value;
        }

        /**
         * Gets the value of the name property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the value of the name property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setName(String value) {
            this.name = value;
        }

        /**
         * Gets the value of the label property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLabel() {
            return label;
        }

        /**
         * Sets the value of the label property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLabel(String value) {
            this.label = value;
        }

        /**
         * Gets the value of the specialFunction property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSpecialFunction() {
            return specialFunction;
        }

        /**
         * Sets the value of the specialFunction property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSpecialFunction(String value) {
            this.specialFunction = value;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="equation" maxOccurs="unbounded">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="outcome" maxOccurs="unbounded">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="modelingType" maxOccurs="unbounded" minOccurs="0">
         *                               &lt;complexType>
         *                                 &lt;simpleContent>
         *                                   &lt;extension base="&lt;http://gking.harvard.edu/zelig>MODEL">
         *                                   &lt;/extension>
         *                                 &lt;/simpleContent>
         *                               &lt;/complexType>
         *                             &lt;/element>
         *                             &lt;element name="dataType" maxOccurs="unbounded" minOccurs="0">
         *                               &lt;complexType>
         *                                 &lt;simpleContent>
         *                                   &lt;extension base="&lt;http://gking.harvard.edu/zelig>DATA">
         *                                   &lt;/extension>
         *                                 &lt;/simpleContent>
         *                               &lt;/complexType>
         *                             &lt;/element>
         *                           &lt;/sequence>
         *                           &lt;attribute name="minEquations" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" default="1" />
         *                           &lt;attribute name="maxEquations" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" default="1" />
         *                           &lt;attribute name="minVar" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="1" />
         *                           &lt;attribute name="maxVar" type="{http://www.w3.org/2001/XMLSchema}integer" default="1" />
         *                           &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                   &lt;element name="explanatory" maxOccurs="unbounded" minOccurs="0">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="modelingType" maxOccurs="unbounded" minOccurs="0">
         *                               &lt;complexType>
         *                                 &lt;simpleContent>
         *                                   &lt;extension base="&lt;http://gking.harvard.edu/zelig>MODEL">
         *                                   &lt;/extension>
         *                                 &lt;/simpleContent>
         *                               &lt;/complexType>
         *                             &lt;/element>
         *                             &lt;element name="dataType" maxOccurs="unbounded" minOccurs="0">
         *                               &lt;complexType>
         *                                 &lt;simpleContent>
         *                                   &lt;extension base="&lt;http://gking.harvard.edu/zelig>DATA">
         *                                   &lt;/extension>
         *                                 &lt;/simpleContent>
         *                               &lt;/complexType>
         *                             &lt;/element>
         *                           &lt;/sequence>
         *                           &lt;attribute name="minVar" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="1" />
         *                           &lt;attribute name="maxVar" type="{http://www.w3.org/2001/XMLSchema}integer" default="-1" />
         *                           &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                 &lt;/sequence>
         *                 &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                 &lt;attribute name="interceptAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean" default="1" />
         *                 &lt;attribute name="crossedAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean" default="1" />
         *                 &lt;attribute name="nestedAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean" default="1" />
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *       &lt;attribute name="minEquations" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" default="1" />
         *       &lt;attribute name="maxEquations" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" default="1" />
         *       &lt;attribute name="simulEq" type="{http://www.w3.org/2001/XMLSchema}boolean" default="0" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "equation"
        })
        public static class Formula {

            @XmlElement(required = true)
            protected List<Zelig.Model.Formula.Equation> equation;
            @XmlAttribute
            @XmlSchemaType(name = "positiveInteger")
            protected BigInteger minEquations;
            @XmlAttribute
            @XmlSchemaType(name = "positiveInteger")
            protected BigInteger maxEquations;
            @XmlAttribute
            protected Boolean simulEq;

            /**
             * Gets the value of the equation property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the equation property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getEquation().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Zelig.Model.Formula.Equation }
             * 
             * 
             */
            public List<Zelig.Model.Formula.Equation> getEquation() {
                if (equation == null) {
                    equation = new ArrayList<Zelig.Model.Formula.Equation>();
                }
                return this.equation;
            }

            /**
             * Gets the value of the minEquations property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getMinEquations() {
                if (minEquations == null) {
                    return new BigInteger("1");
                } else {
                    return minEquations;
                }
            }

            /**
             * Sets the value of the minEquations property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setMinEquations(BigInteger value) {
                this.minEquations = value;
            }

            /**
             * Gets the value of the maxEquations property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getMaxEquations() {
                if (maxEquations == null) {
                    return new BigInteger("1");
                } else {
                    return maxEquations;
                }
            }

            /**
             * Sets the value of the maxEquations property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setMaxEquations(BigInteger value) {
                this.maxEquations = value;
            }

            /**
             * Gets the value of the simulEq property.
             * 
             * @return
             *     possible object is
             *     {@link Boolean }
             *     
             */
            public boolean isSimulEq() {
                if (simulEq == null) {
                    return false;
                } else {
                    return simulEq;
                }
            }

            /**
             * Sets the value of the simulEq property.
             * 
             * @param value
             *     allowed object is
             *     {@link Boolean }
             *     
             */
            public void setSimulEq(Boolean value) {
                this.simulEq = value;
            }


            /**
             * <p>Java class for anonymous complex type.
             * 
             * <p>The following schema fragment specifies the expected content contained within this class.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;sequence>
             *         &lt;element name="outcome" maxOccurs="unbounded">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="modelingType" maxOccurs="unbounded" minOccurs="0">
             *                     &lt;complexType>
             *                       &lt;simpleContent>
             *                         &lt;extension base="&lt;http://gking.harvard.edu/zelig>MODEL">
             *                         &lt;/extension>
             *                       &lt;/simpleContent>
             *                     &lt;/complexType>
             *                   &lt;/element>
             *                   &lt;element name="dataType" maxOccurs="unbounded" minOccurs="0">
             *                     &lt;complexType>
             *                       &lt;simpleContent>
             *                         &lt;extension base="&lt;http://gking.harvard.edu/zelig>DATA">
             *                         &lt;/extension>
             *                       &lt;/simpleContent>
             *                     &lt;/complexType>
             *                   &lt;/element>
             *                 &lt;/sequence>
             *                 &lt;attribute name="minEquations" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" default="1" />
             *                 &lt;attribute name="maxEquations" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" default="1" />
             *                 &lt;attribute name="minVar" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="1" />
             *                 &lt;attribute name="maxVar" type="{http://www.w3.org/2001/XMLSchema}integer" default="1" />
             *                 &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" />
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *         &lt;element name="explanatory" maxOccurs="unbounded" minOccurs="0">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="modelingType" maxOccurs="unbounded" minOccurs="0">
             *                     &lt;complexType>
             *                       &lt;simpleContent>
             *                         &lt;extension base="&lt;http://gking.harvard.edu/zelig>MODEL">
             *                         &lt;/extension>
             *                       &lt;/simpleContent>
             *                     &lt;/complexType>
             *                   &lt;/element>
             *                   &lt;element name="dataType" maxOccurs="unbounded" minOccurs="0">
             *                     &lt;complexType>
             *                       &lt;simpleContent>
             *                         &lt;extension base="&lt;http://gking.harvard.edu/zelig>DATA">
             *                         &lt;/extension>
             *                       &lt;/simpleContent>
             *                     &lt;/complexType>
             *                   &lt;/element>
             *                 &lt;/sequence>
             *                 &lt;attribute name="minVar" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="1" />
             *                 &lt;attribute name="maxVar" type="{http://www.w3.org/2001/XMLSchema}integer" default="-1" />
             *                 &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" />
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *       &lt;/sequence>
             *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
             *       &lt;attribute name="interceptAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean" default="1" />
             *       &lt;attribute name="crossedAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean" default="1" />
             *       &lt;attribute name="nestedAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean" default="1" />
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "outcome",
                "explanatory"
            })
            public static class Equation {

                @XmlElement(required = true)
                protected List<Zelig.Model.Formula.Equation.Outcome> outcome;
                protected List<Zelig.Model.Formula.Equation.Explanatory> explanatory;
                @XmlAttribute
                protected String name;
                @XmlAttribute
                protected Boolean interceptAllowed;
                @XmlAttribute
                protected Boolean crossedAllowed;
                @XmlAttribute
                protected Boolean nestedAllowed;

                /**
                 * Gets the value of the outcome property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the outcome property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getOutcome().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link Zelig.Model.Formula.Equation.Outcome }
                 * 
                 * 
                 */
                public List<Zelig.Model.Formula.Equation.Outcome> getOutcome() {
                    if (outcome == null) {
                        outcome = new ArrayList<Zelig.Model.Formula.Equation.Outcome>();
                    }
                    return this.outcome;
                }

                /**
                 * Gets the value of the explanatory property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the explanatory property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getExplanatory().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link Zelig.Model.Formula.Equation.Explanatory }
                 * 
                 * 
                 */
                public List<Zelig.Model.Formula.Equation.Explanatory> getExplanatory() {
                    if (explanatory == null) {
                        explanatory = new ArrayList<Zelig.Model.Formula.Equation.Explanatory>();
                    }
                    return this.explanatory;
                }

                /**
                 * Gets the value of the name property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getName() {
                    return name;
                }

                /**
                 * Sets the value of the name property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setName(String value) {
                    this.name = value;
                }

                /**
                 * Gets the value of the interceptAllowed property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link Boolean }
                 *     
                 */
                public boolean isInterceptAllowed() {
                    if (interceptAllowed == null) {
                        return true;
                    } else {
                        return interceptAllowed;
                    }
                }

                /**
                 * Sets the value of the interceptAllowed property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link Boolean }
                 *     
                 */
                public void setInterceptAllowed(Boolean value) {
                    this.interceptAllowed = value;
                }

                /**
                 * Gets the value of the crossedAllowed property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link Boolean }
                 *     
                 */
                public boolean isCrossedAllowed() {
                    if (crossedAllowed == null) {
                        return true;
                    } else {
                        return crossedAllowed;
                    }
                }

                /**
                 * Sets the value of the crossedAllowed property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link Boolean }
                 *     
                 */
                public void setCrossedAllowed(Boolean value) {
                    this.crossedAllowed = value;
                }

                /**
                 * Gets the value of the nestedAllowed property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link Boolean }
                 *     
                 */
                public boolean isNestedAllowed() {
                    if (nestedAllowed == null) {
                        return true;
                    } else {
                        return nestedAllowed;
                    }
                }

                /**
                 * Sets the value of the nestedAllowed property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link Boolean }
                 *     
                 */
                public void setNestedAllowed(Boolean value) {
                    this.nestedAllowed = value;
                }


                /**
                 * <p>Java class for anonymous complex type.
                 * 
                 * <p>The following schema fragment specifies the expected content contained within this class.
                 * 
                 * <pre>
                 * &lt;complexType>
                 *   &lt;complexContent>
                 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *       &lt;sequence>
                 *         &lt;element name="modelingType" maxOccurs="unbounded" minOccurs="0">
                 *           &lt;complexType>
                 *             &lt;simpleContent>
                 *               &lt;extension base="&lt;http://gking.harvard.edu/zelig>MODEL">
                 *               &lt;/extension>
                 *             &lt;/simpleContent>
                 *           &lt;/complexType>
                 *         &lt;/element>
                 *         &lt;element name="dataType" maxOccurs="unbounded" minOccurs="0">
                 *           &lt;complexType>
                 *             &lt;simpleContent>
                 *               &lt;extension base="&lt;http://gking.harvard.edu/zelig>DATA">
                 *               &lt;/extension>
                 *             &lt;/simpleContent>
                 *           &lt;/complexType>
                 *         &lt;/element>
                 *       &lt;/sequence>
                 *       &lt;attribute name="minVar" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="1" />
                 *       &lt;attribute name="maxVar" type="{http://www.w3.org/2001/XMLSchema}integer" default="-1" />
                 *       &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "modelingType",
                    "dataType"
                })
                public static class Explanatory {

                    protected List<Zelig.Model.Formula.Equation.Explanatory.ModelingType> modelingType;
                    protected List<Zelig.Model.Formula.Equation.Explanatory.DataType> dataType;
                    @XmlAttribute
                    @XmlSchemaType(name = "nonNegativeInteger")
                    protected BigInteger minVar;
                    @XmlAttribute
                    protected BigInteger maxVar;
                    @XmlAttribute
                    protected String label;

                    /**
                     * Gets the value of the modelingType property.
                     * 
                     * <p>
                     * This accessor method returns a reference to the live list,
                     * not a snapshot. Therefore any modification you make to the
                     * returned list will be present inside the JAXB object.
                     * This is why there is not a <CODE>set</CODE> method for the modelingType property.
                     * 
                     * <p>
                     * For example, to add a new item, do as follows:
                     * <pre>
                     *    getModelingType().add(newItem);
                     * </pre>
                     * 
                     * 
                     * <p>
                     * Objects of the following type(s) are allowed in the list
                     * {@link Zelig.Model.Formula.Equation.Explanatory.ModelingType }
                     * 
                     * 
                     */
                    public List<Zelig.Model.Formula.Equation.Explanatory.ModelingType> getModelingType() {
                        if (modelingType == null) {
                            modelingType = new ArrayList<Zelig.Model.Formula.Equation.Explanatory.ModelingType>();
                        }
                        return this.modelingType;
                    }

                    /**
                     * Gets the value of the dataType property.
                     * 
                     * <p>
                     * This accessor method returns a reference to the live list,
                     * not a snapshot. Therefore any modification you make to the
                     * returned list will be present inside the JAXB object.
                     * This is why there is not a <CODE>set</CODE> method for the dataType property.
                     * 
                     * <p>
                     * For example, to add a new item, do as follows:
                     * <pre>
                     *    getDataType().add(newItem);
                     * </pre>
                     * 
                     * 
                     * <p>
                     * Objects of the following type(s) are allowed in the list
                     * {@link Zelig.Model.Formula.Equation.Explanatory.DataType }
                     * 
                     * 
                     */
                    public List<Zelig.Model.Formula.Equation.Explanatory.DataType> getDataType() {
                        if (dataType == null) {
                            dataType = new ArrayList<Zelig.Model.Formula.Equation.Explanatory.DataType>();
                        }
                        return this.dataType;
                    }

                    /**
                     * Gets the value of the minVar property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link BigInteger }
                     *     
                     */
                    public BigInteger getMinVar() {
                        if (minVar == null) {
                            return new BigInteger("1");
                        } else {
                            return minVar;
                        }
                    }

                    /**
                     * Sets the value of the minVar property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link BigInteger }
                     *     
                     */
                    public void setMinVar(BigInteger value) {
                        this.minVar = value;
                    }

                    /**
                     * Gets the value of the maxVar property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link BigInteger }
                     *     
                     */
                    public BigInteger getMaxVar() {
                        if (maxVar == null) {
                            return new BigInteger("-1");
                        } else {
                            return maxVar;
                        }
                    }

                    /**
                     * Sets the value of the maxVar property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link BigInteger }
                     *     
                     */
                    public void setMaxVar(BigInteger value) {
                        this.maxVar = value;
                    }

                    /**
                     * Gets the value of the label property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getLabel() {
                        return label;
                    }

                    /**
                     * Sets the value of the label property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setLabel(String value) {
                        this.label = value;
                    }


                    /**
                     * <p>Java class for anonymous complex type.
                     * 
                     * <p>The following schema fragment specifies the expected content contained within this class.
                     * 
                     * <pre>
                     * &lt;complexType>
                     *   &lt;simpleContent>
                     *     &lt;extension base="&lt;http://gking.harvard.edu/zelig>DATA">
                     *     &lt;/extension>
                     *   &lt;/simpleContent>
                     * &lt;/complexType>
                     * </pre>
                     * 
                     * 
                     */
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "value"
                    })
                    public static class DataType {

                        @XmlValue
                        protected DATA value;

                        /**
                         * Gets the value of the value property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link DATA }
                         *     
                         */
                        public DATA getValue() {
                            return value;
                        }

                        /**
                         * Sets the value of the value property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link DATA }
                         *     
                         */
                        public void setValue(DATA value) {
                            this.value = value;
                        }

                    }


                    /**
                     * <p>Java class for anonymous complex type.
                     * 
                     * <p>The following schema fragment specifies the expected content contained within this class.
                     * 
                     * <pre>
                     * &lt;complexType>
                     *   &lt;simpleContent>
                     *     &lt;extension base="&lt;http://gking.harvard.edu/zelig>MODEL">
                     *     &lt;/extension>
                     *   &lt;/simpleContent>
                     * &lt;/complexType>
                     * </pre>
                     * 
                     * 
                     */
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "value"
                    })
                    public static class ModelingType {

                        @XmlValue
                        protected MODEL value;

                        /**
                         * Gets the value of the value property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link MODEL }
                         *     
                         */
                        public MODEL getValue() {
                            return value;
                        }

                        /**
                         * Sets the value of the value property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link MODEL }
                         *     
                         */
                        public void setValue(MODEL value) {
                            this.value = value;
                        }

                    }

                }


                /**
                 * <p>Java class for anonymous complex type.
                 * 
                 * <p>The following schema fragment specifies the expected content contained within this class.
                 * 
                 * <pre>
                 * &lt;complexType>
                 *   &lt;complexContent>
                 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *       &lt;sequence>
                 *         &lt;element name="modelingType" maxOccurs="unbounded" minOccurs="0">
                 *           &lt;complexType>
                 *             &lt;simpleContent>
                 *               &lt;extension base="&lt;http://gking.harvard.edu/zelig>MODEL">
                 *               &lt;/extension>
                 *             &lt;/simpleContent>
                 *           &lt;/complexType>
                 *         &lt;/element>
                 *         &lt;element name="dataType" maxOccurs="unbounded" minOccurs="0">
                 *           &lt;complexType>
                 *             &lt;simpleContent>
                 *               &lt;extension base="&lt;http://gking.harvard.edu/zelig>DATA">
                 *               &lt;/extension>
                 *             &lt;/simpleContent>
                 *           &lt;/complexType>
                 *         &lt;/element>
                 *       &lt;/sequence>
                 *       &lt;attribute name="minEquations" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" default="1" />
                 *       &lt;attribute name="maxEquations" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" default="1" />
                 *       &lt;attribute name="minVar" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="1" />
                 *       &lt;attribute name="maxVar" type="{http://www.w3.org/2001/XMLSchema}integer" default="1" />
                 *       &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "modelingType",
                    "dataType"
                })
                public static class Outcome {

                    protected List<Zelig.Model.Formula.Equation.Outcome.ModelingType> modelingType;
                    protected List<Zelig.Model.Formula.Equation.Outcome.DataType> dataType;
                    @XmlAttribute
                    @XmlSchemaType(name = "positiveInteger")
                    protected BigInteger minEquations;
                    @XmlAttribute
                    @XmlSchemaType(name = "positiveInteger")
                    protected BigInteger maxEquations;
                    @XmlAttribute
                    @XmlSchemaType(name = "nonNegativeInteger")
                    protected BigInteger minVar;
                    @XmlAttribute
                    protected BigInteger maxVar;
                    @XmlAttribute
                    protected String label;

                    /**
                     * Gets the value of the modelingType property.
                     * 
                     * <p>
                     * This accessor method returns a reference to the live list,
                     * not a snapshot. Therefore any modification you make to the
                     * returned list will be present inside the JAXB object.
                     * This is why there is not a <CODE>set</CODE> method for the modelingType property.
                     * 
                     * <p>
                     * For example, to add a new item, do as follows:
                     * <pre>
                     *    getModelingType().add(newItem);
                     * </pre>
                     * 
                     * 
                     * <p>
                     * Objects of the following type(s) are allowed in the list
                     * {@link Zelig.Model.Formula.Equation.Outcome.ModelingType }
                     * 
                     * 
                     */
                    public List<Zelig.Model.Formula.Equation.Outcome.ModelingType> getModelingType() {
                        if (modelingType == null) {
                            modelingType = new ArrayList<Zelig.Model.Formula.Equation.Outcome.ModelingType>();
                        }
                        return this.modelingType;
                    }

                    /**
                     * Gets the value of the dataType property.
                     * 
                     * <p>
                     * This accessor method returns a reference to the live list,
                     * not a snapshot. Therefore any modification you make to the
                     * returned list will be present inside the JAXB object.
                     * This is why there is not a <CODE>set</CODE> method for the dataType property.
                     * 
                     * <p>
                     * For example, to add a new item, do as follows:
                     * <pre>
                     *    getDataType().add(newItem);
                     * </pre>
                     * 
                     * 
                     * <p>
                     * Objects of the following type(s) are allowed in the list
                     * {@link Zelig.Model.Formula.Equation.Outcome.DataType }
                     * 
                     * 
                     */
                    public List<Zelig.Model.Formula.Equation.Outcome.DataType> getDataType() {
                        if (dataType == null) {
                            dataType = new ArrayList<Zelig.Model.Formula.Equation.Outcome.DataType>();
                        }
                        return this.dataType;
                    }

                    /**
                     * Gets the value of the minEquations property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link BigInteger }
                     *     
                     */
                    public BigInteger getMinEquations() {
                        if (minEquations == null) {
                            return new BigInteger("1");
                        } else {
                            return minEquations;
                        }
                    }

                    /**
                     * Sets the value of the minEquations property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link BigInteger }
                     *     
                     */
                    public void setMinEquations(BigInteger value) {
                        this.minEquations = value;
                    }

                    /**
                     * Gets the value of the maxEquations property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link BigInteger }
                     *     
                     */
                    public BigInteger getMaxEquations() {
                        if (maxEquations == null) {
                            return new BigInteger("1");
                        } else {
                            return maxEquations;
                        }
                    }

                    /**
                     * Sets the value of the maxEquations property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link BigInteger }
                     *     
                     */
                    public void setMaxEquations(BigInteger value) {
                        this.maxEquations = value;
                    }

                    /**
                     * Gets the value of the minVar property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link BigInteger }
                     *     
                     */
                    public BigInteger getMinVar() {
                        if (minVar == null) {
                            return new BigInteger("1");
                        } else {
                            return minVar;
                        }
                    }

                    /**
                     * Sets the value of the minVar property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link BigInteger }
                     *     
                     */
                    public void setMinVar(BigInteger value) {
                        this.minVar = value;
                    }

                    /**
                     * Gets the value of the maxVar property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link BigInteger }
                     *     
                     */
                    public BigInteger getMaxVar() {
                        if (maxVar == null) {
                            return new BigInteger("1");
                        } else {
                            return maxVar;
                        }
                    }

                    /**
                     * Sets the value of the maxVar property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link BigInteger }
                     *     
                     */
                    public void setMaxVar(BigInteger value) {
                        this.maxVar = value;
                    }

                    /**
                     * Gets the value of the label property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getLabel() {
                        return label;
                    }

                    /**
                     * Sets the value of the label property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setLabel(String value) {
                        this.label = value;
                    }


                    /**
                     * <p>Java class for anonymous complex type.
                     * 
                     * <p>The following schema fragment specifies the expected content contained within this class.
                     * 
                     * <pre>
                     * &lt;complexType>
                     *   &lt;simpleContent>
                     *     &lt;extension base="&lt;http://gking.harvard.edu/zelig>DATA">
                     *     &lt;/extension>
                     *   &lt;/simpleContent>
                     * &lt;/complexType>
                     * </pre>
                     * 
                     * 
                     */
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "value"
                    })
                    public static class DataType {

                        @XmlValue
                        protected DATA value;

                        /**
                         * Gets the value of the value property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link DATA }
                         *     
                         */
                        public DATA getValue() {
                            return value;
                        }

                        /**
                         * Sets the value of the value property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link DATA }
                         *     
                         */
                        public void setValue(DATA value) {
                            this.value = value;
                        }

                    }


                    /**
                     * <p>Java class for anonymous complex type.
                     * 
                     * <p>The following schema fragment specifies the expected content contained within this class.
                     * 
                     * <pre>
                     * &lt;complexType>
                     *   &lt;simpleContent>
                     *     &lt;extension base="&lt;http://gking.harvard.edu/zelig>MODEL">
                     *     &lt;/extension>
                     *   &lt;/simpleContent>
                     * &lt;/complexType>
                     * </pre>
                     * 
                     * 
                     */
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "value"
                    })
                    public static class ModelingType {

                        @XmlValue
                        protected MODEL value;

                        /**
                         * Gets the value of the value property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link MODEL }
                         *     
                         */
                        public MODEL getValue() {
                            return value;
                        }

                        /**
                         * Sets the value of the value property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link MODEL }
                         *     
                         */
                        public void setValue(MODEL value) {
                            this.value = value;
                        }

                    }

                }

            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;attribute name="url" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
         *       &lt;attribute name="rhelp" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class HelpLink {

            @XmlAttribute(required = true)
            @XmlSchemaType(name = "anyURI")
            protected String url;
            @XmlAttribute
            protected String rhelp;

            /**
             * Gets the value of the url property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getUrl() {
                return url;
            }

            /**
             * Sets the value of the url property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setUrl(String value) {
                this.url = value;
            }

            /**
             * Gets the value of the rhelp property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getRhelp() {
                return rhelp;
            }

            /**
             * Sets the value of the rhelp property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setRhelp(String value) {
                this.rhelp = value;
            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="relationship" type="{http://gking.harvard.edu/zelig}PACK_REL" default="required" />
         *       &lt;attribute name="CRAN" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class PackageDependency {

            @XmlAttribute(required = true)
            protected String name;
            @XmlAttribute(required = true)
            protected String version;
            @XmlAttribute
            protected PACKREL relationship;
            @XmlAttribute(name = "CRAN")
            @XmlSchemaType(name = "anyURI")
            protected String cran;

            /**
             * Gets the value of the name property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getName() {
                return name;
            }

            /**
             * Sets the value of the name property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setName(String value) {
                this.name = value;
            }

            /**
             * Gets the value of the version property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getVersion() {
                return version;
            }

            /**
             * Sets the value of the version property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setVersion(String value) {
                this.version = value;
            }

            /**
             * Gets the value of the relationship property.
             * 
             * @return
             *     possible object is
             *     {@link PACKREL }
             *     
             */
            public PACKREL getRelationship() {
                if (relationship == null) {
                    return PACKREL.REQUIRED;
                } else {
                    return relationship;
                }
            }

            /**
             * Sets the value of the relationship property.
             * 
             * @param value
             *     allowed object is
             *     {@link PACKREL }
             *     
             */
            public void setRelationship(PACKREL value) {
                this.relationship = value;
            }

            /**
             * Gets the value of the cran property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCRAN() {
                return cran;
            }

            /**
             * Sets the value of the cran property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCRAN(String value) {
                this.cran = value;
            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;attribute name="maxSetx" type="{http://gking.harvard.edu/zelig}MAXSETX" default="2" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Setx {

            @XmlAttribute
            protected Integer maxSetx;

            /**
             * Gets the value of the maxSetx property.
             * 
             * @return
             *     possible object is
             *     {@link Integer }
             *     
             */
            public int getMaxSetx() {
                if (maxSetx == null) {
                    return  2;
                } else {
                    return maxSetx;
                }
            }

            /**
             * Sets the value of the maxSetx property.
             * 
             * @param value
             *     allowed object is
             *     {@link Integer }
             *     
             */
            public void setMaxSetx(Integer value) {
                this.maxSetx = value;
            }

        }

    }

}
