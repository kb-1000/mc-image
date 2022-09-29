
package de.kb1000.mcimage.substgen.jaxb;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
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
 *         &lt;group ref="{}Enum"/&gt;
 *         &lt;element ref="{}unused"/&gt;
 *       &lt;/choice&gt;
 *       &lt;attribute name="namespace" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *       &lt;attribute name="group" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *       &lt;attribute name="start" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *       &lt;attribute name="end" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *       &lt;attribute name="vendor" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
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
    "enumOrUnused"
})
@XmlRootElement(name = "enums")
public class Enums {

    @XmlElements({
        @XmlElement(name = "enum", type = Enums.Enum.class),
        @XmlElement(name = "unused", type = Unused.class)
    })
    protected List<Object> enumOrUnused;
    @XmlAttribute(name = "namespace")
    @XmlSchemaType(name = "anySimpleType")
    protected String namespace;
    @XmlAttribute(name = "group")
    @XmlSchemaType(name = "anySimpleType")
    protected String group;
    @XmlAttribute(name = "type")
    @XmlSchemaType(name = "anySimpleType")
    protected String type;
    @XmlAttribute(name = "start")
    @XmlSchemaType(name = "anySimpleType")
    protected String start;
    @XmlAttribute(name = "end")
    @XmlSchemaType(name = "anySimpleType")
    protected String end;
    @XmlAttribute(name = "vendor")
    @XmlSchemaType(name = "anySimpleType")
    protected String vendor;
    @XmlAttribute(name = "comment")
    @XmlSchemaType(name = "anySimpleType")
    protected String comment;

    /**
     * Gets the value of the enumOrUnused property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the enumOrUnused property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEnumOrUnused().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Enums.Enum }
     * {@link Unused }
     * 
     * 
     */
    public List<Object> getEnumOrUnused() {
        if (enumOrUnused == null) {
            enumOrUnused = new ArrayList<Object>();
        }
        return this.enumOrUnused;
    }

    /**
     * Gets the value of the namespace property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Sets the value of the namespace property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNamespace(String value) {
        this.namespace = value;
    }

    /**
     * Gets the value of the group property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroup() {
        return group;
    }

    /**
     * Sets the value of the group property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroup(String value) {
        this.group = value;
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
     * Gets the value of the start property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStart() {
        return start;
    }

    /**
     * Sets the value of the start property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStart(String value) {
        this.start = value;
    }

    /**
     * Gets the value of the end property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnd() {
        return end;
    }

    /**
     * Sets the value of the end property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnd(String value) {
        this.end = value;
    }

    /**
     * Gets the value of the vendor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * Sets the value of the vendor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVendor(String value) {
        this.vendor = value;
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
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
     *       &lt;attribute name="api" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
     *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
     *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
     *       &lt;attribute name="group" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
     *       &lt;attribute name="alias" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
     *       &lt;attribute name="comment" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Enum {

        @XmlAttribute(name = "value", required = true)
        @XmlSchemaType(name = "anySimpleType")
        protected String value;
        @XmlAttribute(name = "api")
        @XmlSchemaType(name = "anySimpleType")
        protected String api;
        @XmlAttribute(name = "type")
        @XmlSchemaType(name = "anySimpleType")
        protected String type;
        @XmlAttribute(name = "name", required = true)
        @XmlSchemaType(name = "anySimpleType")
        protected String name;
        @XmlAttribute(name = "group")
        @XmlSchemaType(name = "anySimpleType")
        protected String group;
        @XmlAttribute(name = "alias")
        @XmlSchemaType(name = "anySimpleType")
        protected String alias;
        @XmlAttribute(name = "comment")
        @XmlSchemaType(name = "anySimpleType")
        protected String comment;

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
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
         * Gets the value of the group property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getGroup() {
            return group;
        }

        /**
         * Sets the value of the group property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setGroup(String value) {
            this.group = value;
        }

        /**
         * Gets the value of the alias property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAlias() {
            return alias;
        }

        /**
         * Sets the value of the alias property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAlias(String value) {
            this.alias = value;
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
