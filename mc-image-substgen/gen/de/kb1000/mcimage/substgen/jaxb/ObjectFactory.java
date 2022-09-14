
package de.kb1000.mcimage.substgen.jaxb;

import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the de.kb1000.mcimage.substgen.jaxb package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Comment_QNAME = new QName("", "comment");
    private final static QName _Apientry_QNAME = new QName("", "apientry");
    private final static QName _Name_QNAME = new QName("", "name");
    private final static QName _Ptype_QNAME = new QName("", "ptype");
    private final static QName _ExtensionRemoveType_QNAME = new QName("", "type");
    private final static QName _ExtensionRemoveEnum_QNAME = new QName("", "enum");
    private final static QName _ExtensionRemoveCommand_QNAME = new QName("", "command");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.kb1000.mcimage.substgen.jaxb
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Types }
     * 
     */
    public Types createTypes() {
        return new Types();
    }

    /**
     * Create an instance of {@link Group }
     * 
     */
    public Group createGroup() {
        return new Group();
    }

    /**
     * Create an instance of {@link Enums }
     * 
     */
    public Enums createEnums() {
        return new Enums();
    }

    /**
     * Create an instance of {@link Commands }
     * 
     */
    public Commands createCommands() {
        return new Commands();
    }

    /**
     * Create an instance of {@link Feature }
     * 
     */
    public Feature createFeature() {
        return new Feature();
    }

    /**
     * Create an instance of {@link Extension }
     * 
     */
    public Extension createExtension() {
        return new Extension();
    }

    /**
     * Create an instance of {@link Registry }
     * 
     */
    public Registry createRegistry() {
        return new Registry();
    }

    /**
     * Create an instance of {@link Types.Type }
     * 
     */
    public Types.Type createTypesType() {
        return new Types.Type();
    }

    /**
     * Create an instance of {@link Groups }
     * 
     */
    public Groups createGroups() {
        return new Groups();
    }

    /**
     * Create an instance of {@link Group.Enum }
     * 
     */
    public Group.Enum createGroupEnum() {
        return new Group.Enum();
    }

    /**
     * Create an instance of {@link Enums.Enum }
     * 
     */
    public Enums.Enum createEnumsEnum() {
        return new Enums.Enum();
    }

    /**
     * Create an instance of {@link Unused }
     * 
     */
    public Unused createUnused() {
        return new Unused();
    }

    /**
     * Create an instance of {@link Commands.Command }
     * 
     */
    public Commands.Command createCommandsCommand() {
        return new Commands.Command();
    }

    /**
     * Create an instance of {@link Feature.Require }
     * 
     */
    public Feature.Require createFeatureRequire() {
        return new Feature.Require();
    }

    /**
     * Create an instance of {@link Feature.Remove }
     * 
     */
    public Feature.Remove createFeatureRemove() {
        return new Feature.Remove();
    }

    /**
     * Create an instance of {@link Extensions }
     * 
     */
    public Extensions createExtensions() {
        return new Extensions();
    }

    /**
     * Create an instance of {@link Extension.Require }
     * 
     */
    public Extension.Require createExtensionRequire() {
        return new Extension.Require();
    }

    /**
     * Create an instance of {@link Extension.Remove }
     * 
     */
    public Extension.Remove createExtensionRemove() {
        return new Extension.Remove();
    }

    /**
     * Create an instance of {@link Proto }
     * 
     */
    public Proto createProto() {
        return new Proto();
    }

    /**
     * Create an instance of {@link Param }
     * 
     */
    public Param createParam() {
        return new Param();
    }

    /**
     * Create an instance of {@link Alias }
     * 
     */
    public Alias createAlias() {
        return new Alias();
    }

    /**
     * Create an instance of {@link Vecequiv }
     * 
     */
    public Vecequiv createVecequiv() {
        return new Vecequiv();
    }

    /**
     * Create an instance of {@link Glx }
     * 
     */
    public Glx createGlx() {
        return new Glx();
    }

    /**
     * Create an instance of {@link InterfaceElement }
     * 
     */
    public InterfaceElement createInterfaceElement() {
        return new InterfaceElement();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "comment")
    public JAXBElement<String> createComment(String value) {
        return new JAXBElement<String>(_Comment_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "apientry")
    public JAXBElement<String> createApientry(String value) {
        return new JAXBElement<String>(_Apientry_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "name")
    public JAXBElement<String> createName(String value) {
        return new JAXBElement<String>(_Name_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "ptype")
    public JAXBElement<String> createPtype(String value) {
        return new JAXBElement<String>(_Ptype_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InterfaceElement }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link InterfaceElement }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "type", scope = Extension.Remove.class)
    public JAXBElement<InterfaceElement> createExtensionRemoveType(InterfaceElement value) {
        return new JAXBElement<InterfaceElement>(_ExtensionRemoveType_QNAME, InterfaceElement.class, Extension.Remove.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InterfaceElement }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link InterfaceElement }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "enum", scope = Extension.Remove.class)
    public JAXBElement<InterfaceElement> createExtensionRemoveEnum(InterfaceElement value) {
        return new JAXBElement<InterfaceElement>(_ExtensionRemoveEnum_QNAME, InterfaceElement.class, Extension.Remove.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InterfaceElement }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link InterfaceElement }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "command", scope = Extension.Remove.class)
    public JAXBElement<InterfaceElement> createExtensionRemoveCommand(InterfaceElement value) {
        return new JAXBElement<InterfaceElement>(_ExtensionRemoveCommand_QNAME, InterfaceElement.class, Extension.Remove.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InterfaceElement }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link InterfaceElement }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "type", scope = Extension.Require.class)
    public JAXBElement<InterfaceElement> createExtensionRequireType(InterfaceElement value) {
        return new JAXBElement<InterfaceElement>(_ExtensionRemoveType_QNAME, InterfaceElement.class, Extension.Require.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InterfaceElement }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link InterfaceElement }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "enum", scope = Extension.Require.class)
    public JAXBElement<InterfaceElement> createExtensionRequireEnum(InterfaceElement value) {
        return new JAXBElement<InterfaceElement>(_ExtensionRemoveEnum_QNAME, InterfaceElement.class, Extension.Require.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InterfaceElement }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link InterfaceElement }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "command", scope = Extension.Require.class)
    public JAXBElement<InterfaceElement> createExtensionRequireCommand(InterfaceElement value) {
        return new JAXBElement<InterfaceElement>(_ExtensionRemoveCommand_QNAME, InterfaceElement.class, Extension.Require.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InterfaceElement }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link InterfaceElement }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "type", scope = Feature.Remove.class)
    public JAXBElement<InterfaceElement> createFeatureRemoveType(InterfaceElement value) {
        return new JAXBElement<InterfaceElement>(_ExtensionRemoveType_QNAME, InterfaceElement.class, Feature.Remove.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InterfaceElement }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link InterfaceElement }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "enum", scope = Feature.Remove.class)
    public JAXBElement<InterfaceElement> createFeatureRemoveEnum(InterfaceElement value) {
        return new JAXBElement<InterfaceElement>(_ExtensionRemoveEnum_QNAME, InterfaceElement.class, Feature.Remove.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InterfaceElement }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link InterfaceElement }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "command", scope = Feature.Remove.class)
    public JAXBElement<InterfaceElement> createFeatureRemoveCommand(InterfaceElement value) {
        return new JAXBElement<InterfaceElement>(_ExtensionRemoveCommand_QNAME, InterfaceElement.class, Feature.Remove.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InterfaceElement }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link InterfaceElement }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "type", scope = Feature.Require.class)
    public JAXBElement<InterfaceElement> createFeatureRequireType(InterfaceElement value) {
        return new JAXBElement<InterfaceElement>(_ExtensionRemoveType_QNAME, InterfaceElement.class, Feature.Require.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InterfaceElement }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link InterfaceElement }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "enum", scope = Feature.Require.class)
    public JAXBElement<InterfaceElement> createFeatureRequireEnum(InterfaceElement value) {
        return new JAXBElement<InterfaceElement>(_ExtensionRemoveEnum_QNAME, InterfaceElement.class, Feature.Require.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InterfaceElement }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link InterfaceElement }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "command", scope = Feature.Require.class)
    public JAXBElement<InterfaceElement> createFeatureRequireCommand(InterfaceElement value) {
        return new JAXBElement<InterfaceElement>(_ExtensionRemoveCommand_QNAME, InterfaceElement.class, Feature.Require.class, value);
    }

}
