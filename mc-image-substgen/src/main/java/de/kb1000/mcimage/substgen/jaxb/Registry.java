
package de.kb1000.mcimage.substgen.jaxb;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
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
 *         &lt;element ref="{}comment" minOccurs="0"/&gt;
 *         &lt;element ref="{}types" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{}groups" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{}enums" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{}commands" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{}feature" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{}extensions" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "commentOrTypesOrGroups"
})
@XmlRootElement(name = "registry")
public class Registry {

    @XmlElements({
        @XmlElement(name = "comment", type = String.class),
        @XmlElement(name = "types", type = Types.class),
        @XmlElement(name = "groups", type = Groups.class),
        @XmlElement(name = "enums", type = Enums.class),
        @XmlElement(name = "commands", type = Commands.class),
        @XmlElement(name = "feature", type = Feature.class),
        @XmlElement(name = "extensions", type = Extensions.class)
    })
    protected List<Object> commentOrTypesOrGroups;

    /**
     * Gets the value of the commentOrTypesOrGroups property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the commentOrTypesOrGroups property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCommentOrTypesOrGroups().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Commands }
     * {@link Enums }
     * {@link Extensions }
     * {@link Feature }
     * {@link Groups }
     * {@link Types }
     * {@link String }
     * 
     * 
     */
    public List<Object> getCommentOrTypesOrGroups() {
        if (commentOrTypesOrGroups == null) {
            commentOrTypesOrGroups = new ArrayList<Object>();
        }
        return this.commentOrTypesOrGroups;
    }

}
