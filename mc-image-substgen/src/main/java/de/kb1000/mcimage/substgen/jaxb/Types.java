
package de.kb1000.mcimage.substgen.jaxb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlMixed;
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
 *       &lt;group ref="{}Type" maxOccurs="unbounded" minOccurs="0"/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "type"
})
@XmlRootElement(name = "types")
public class Types {

    protected List<Types.Type> type;

    /**
     * Gets the value of the type property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the type property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Types.Type }
     * 
     * 
     */
    public List<Types.Type> getType() {
        if (type == null) {
            type = new ArrayList<Types.Type>();
        }
        return this.type;
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
     *       &lt;sequence&gt;
     *         &lt;element ref="{}apientry" minOccurs="0"/&gt;
     *         &lt;element ref="{}name" minOccurs="0"/&gt;
     *       &lt;/sequence&gt;
     *       &lt;attribute name="api" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
     *       &lt;attribute name="requires" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
     *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
     *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
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
        "content"
    })
    public static class Type {

        @XmlElementRefs({
            @XmlElementRef(name = "apientry", type = JAXBElement.class, required = false),
            @XmlElementRef(name = "name", type = JAXBElement.class, required = false)
        })
        @XmlMixed
        protected List<Serializable> content;
        @XmlAttribute(name = "api")
        @XmlSchemaType(name = "anySimpleType")
        protected String api;
        @XmlAttribute(name = "requires")
        @XmlSchemaType(name = "anySimpleType")
        protected String requires;
        @XmlAttribute(name = "name")
        @XmlSchemaType(name = "anySimpleType")
        protected String name;
        @XmlAttribute(name = "type")
        @XmlSchemaType(name = "anySimpleType")
        protected String type;
        @XmlAttribute(name = "comment")
        @XmlSchemaType(name = "anySimpleType")
        protected String comment;

        /**
         * Gets the value of the content property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a <CODE>set</CODE> method for the content property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getContent().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link JAXBElement }{@code <}{@link String }{@code >}
         * {@link JAXBElement }{@code <}{@link String }{@code >}
         * {@link String }
         * 
         * 
         */
        public List<Serializable> getContent() {
            if (content == null) {
                content = new ArrayList<Serializable>();
            }
            return this.content;
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
         * Gets the value of the requires property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRequires() {
            return requires;
        }

        /**
         * Sets the value of the requires property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRequires(String value) {
            this.requires = value;
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
         * Gets the value of the type property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getType() {
            return type;
        }

        /**
         * Sets the value of the type property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setType(String value) {
            this.type = value;
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
