
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
 *       &lt;group ref="{}Command" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;attribute name="namespace" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "command"
})
@XmlRootElement(name = "commands")
public class Commands {

    protected List<Commands.Command> command;
    @XmlAttribute(name = "namespace")
    @XmlSchemaType(name = "anySimpleType")
    protected String namespace;

    /**
     * Gets the value of the command property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the command property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCommand().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Commands.Command }
     * 
     * 
     */
    public List<Commands.Command> getCommand() {
        if (command == null) {
            command = new ArrayList<Commands.Command>();
        }
        return this.command;
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
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element ref="{}proto"/&gt;
     *         &lt;element ref="{}param" maxOccurs="unbounded" minOccurs="0"/&gt;
     *         &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
     *           &lt;element ref="{}alias"/&gt;
     *           &lt;element ref="{}vecequiv"/&gt;
     *           &lt;element ref="{}glx"/&gt;
     *         &lt;/choice&gt;
     *       &lt;/sequence&gt;
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
        "proto",
        "param",
        "aliasOrVecequivOrGlx"
    })
    public static class Command {

        @XmlElement(required = true)
        protected Proto proto;
        protected List<Param> param;
        @XmlElements({
            @XmlElement(name = "alias", type = Alias.class),
            @XmlElement(name = "vecequiv", type = Vecequiv.class),
            @XmlElement(name = "glx", type = Glx.class)
        })
        protected List<Object> aliasOrVecequivOrGlx;
        @XmlAttribute(name = "comment")
        @XmlSchemaType(name = "anySimpleType")
        protected String comment;

        /**
         * Gets the value of the proto property.
         * 
         * @return
         *     possible object is
         *     {@link Proto }
         *     
         */
        public Proto getProto() {
            return proto;
        }

        /**
         * Sets the value of the proto property.
         * 
         * @param value
         *     allowed object is
         *     {@link Proto }
         *     
         */
        public void setProto(Proto value) {
            this.proto = value;
        }

        /**
         * Gets the value of the param property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a <CODE>set</CODE> method for the param property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getParam().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Param }
         * 
         * 
         */
        public List<Param> getParam() {
            if (param == null) {
                param = new ArrayList<Param>();
            }
            return this.param;
        }

        /**
         * Gets the value of the aliasOrVecequivOrGlx property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a <CODE>set</CODE> method for the aliasOrVecequivOrGlx property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAliasOrVecequivOrGlx().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Alias }
         * {@link Glx }
         * {@link Vecequiv }
         * 
         * 
         */
        public List<Object> getAliasOrVecequivOrGlx() {
            if (aliasOrVecequivOrGlx == null) {
                aliasOrVecequivOrGlx = new ArrayList<Object>();
            }
            return this.aliasOrVecequivOrGlx;
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
