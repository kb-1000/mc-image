
package de.kb1000.mcimage.substgen.jaxb;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *         &lt;element name="require"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;group ref="{}InterfaceElement" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;attribute name="profile" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *                 &lt;attribute name="comment" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="remove"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;group ref="{}InterfaceElement" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;attribute name="profile" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *                 &lt;attribute name="comment" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/choice&gt;
 *       &lt;attGroup ref="{}Name"/&gt;
 *       &lt;attribute name="api" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *       &lt;attribute name="number" use="required" type="{http://www.w3.org/2001/XMLSchema}float" /&gt;
 *       &lt;attribute name="protect" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *       &lt;attribute name="comment" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "requireOrRemove"
})
@XmlRootElement(name = "feature")
public class Feature {

    @XmlElements({
        @XmlElement(name = "require", type = Feature.Require.class),
        @XmlElement(name = "remove", type = Feature.Remove.class)
    })
    protected List<Object> requireOrRemove;
    @XmlAttribute(name = "api", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String api;
    @XmlAttribute(name = "number", required = true)
    protected float number;
    @XmlAttribute(name = "protect")
    @XmlSchemaType(name = "anySimpleType")
    protected String protect;
    @XmlAttribute(name = "comment")
    @XmlSchemaType(name = "anySimpleType")
    protected String comment;
    @XmlAttribute(name = "name", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String name;

    /**
     * Gets the value of the requireOrRemove property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the requireOrRemove property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRequireOrRemove().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Feature.Remove }
     * {@link Feature.Require }
     * 
     * 
     */
    public List<Object> getRequireOrRemove() {
        if (requireOrRemove == null) {
            requireOrRemove = new ArrayList<Object>();
        }
        return this.requireOrRemove;
    }

    /**
     * Gets the value of the api property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApi() {
        return api;
    }

    /**
     * Sets the value of the api property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApi(String value) {
        this.api = value;
    }

    /**
     * Gets the value of the number property.
     * 
     */
    public float getNumber() {
        return number;
    }

    /**
     * Sets the value of the number property.
     * 
     */
    public void setNumber(float value) {
        this.number = value;
    }

    /**
     * Gets the value of the protect property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProtect() {
        return protect;
    }

    /**
     * Sets the value of the protect property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProtect(String value) {
        this.protect = value;
    }

    /**
     * Gets the value of the comment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComment(String value) {
        this.comment = value;
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
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;group ref="{}InterfaceElement" maxOccurs="unbounded" minOccurs="0"/&gt;
     *       &lt;attribute name="profile" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
     *       &lt;attribute name="comment" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "interfaceElement"
    })
    public static class Remove {

        @XmlElementRefs({
            @XmlElementRef(name = "type", type = JAXBElement.class, required = false),
            @XmlElementRef(name = "enum", type = JAXBElement.class, required = false),
            @XmlElementRef(name = "command", type = JAXBElement.class, required = false)
        })
        protected List<JAXBElement<InterfaceElement>> interfaceElement;
        @XmlAttribute(name = "profile")
        @XmlSchemaType(name = "anySimpleType")
        protected String profile;
        @XmlAttribute(name = "comment")
        @XmlSchemaType(name = "anySimpleType")
        protected String comment;

        /**
         * Gets the value of the interfaceElement property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a <CODE>set</CODE> method for the interfaceElement property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getInterfaceElement().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link JAXBElement }{@code <}{@link InterfaceElement }{@code >}
         * {@link JAXBElement }{@code <}{@link InterfaceElement }{@code >}
         * {@link JAXBElement }{@code <}{@link InterfaceElement }{@code >}
         * 
         * 
         */
        public List<JAXBElement<InterfaceElement>> getInterfaceElement() {
            if (interfaceElement == null) {
                interfaceElement = new ArrayList<JAXBElement<InterfaceElement>>();
            }
            return this.interfaceElement;
        }

        /**
         * Gets the value of the profile property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getProfile() {
            return profile;
        }

        /**
         * Sets the value of the profile property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setProfile(String value) {
            this.profile = value;
        }

        /**
         * Gets the value of the comment property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getComment() {
            return comment;
        }

        /**
         * Sets the value of the comment property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setComment(String value) {
            this.comment = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;group ref="{}InterfaceElement" maxOccurs="unbounded" minOccurs="0"/&gt;
     *       &lt;attribute name="profile" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
     *       &lt;attribute name="comment" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "interfaceElement"
    })
    public static class Require {

        @XmlElementRefs({
            @XmlElementRef(name = "type", type = JAXBElement.class, required = false),
            @XmlElementRef(name = "enum", type = JAXBElement.class, required = false),
            @XmlElementRef(name = "command", type = JAXBElement.class, required = false)
        })
        protected List<JAXBElement<InterfaceElement>> interfaceElement;
        @XmlAttribute(name = "profile")
        @XmlSchemaType(name = "anySimpleType")
        protected String profile;
        @XmlAttribute(name = "comment")
        @XmlSchemaType(name = "anySimpleType")
        protected String comment;

        /**
         * Gets the value of the interfaceElement property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a <CODE>set</CODE> method for the interfaceElement property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getInterfaceElement().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link JAXBElement }{@code <}{@link InterfaceElement }{@code >}
         * {@link JAXBElement }{@code <}{@link InterfaceElement }{@code >}
         * {@link JAXBElement }{@code <}{@link InterfaceElement }{@code >}
         * 
         * 
         */
        public List<JAXBElement<InterfaceElement>> getInterfaceElement() {
            if (interfaceElement == null) {
                interfaceElement = new ArrayList<JAXBElement<InterfaceElement>>();
            }
            return this.interfaceElement;
        }

        /**
         * Gets the value of the profile property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getProfile() {
            return profile;
        }

        /**
         * Sets the value of the profile property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setProfile(String value) {
            this.profile = value;
        }

        /**
         * Gets the value of the comment property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getComment() {
            return comment;
        }

        /**
         * Sets the value of the comment property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setComment(String value) {
            this.comment = value;
        }

    }

}
