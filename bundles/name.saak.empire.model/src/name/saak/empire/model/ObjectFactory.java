
package name.saak.empire.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the name.saak.empire.model package. 
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

    private final static QName _Name_QNAME = new QName("", "name");
    private final static QName _Clear_QNAME = new QName("", "clear");
    private final static QName _Mountain_QNAME = new QName("", "mountain");
    private final static QName _SmallCityLoad_QNAME = new QName("", "load");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: name.saak.empire.model
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Game }
     * 
     */
    public Game createGame() {
        return new Game();
    }

    /**
     * Create an instance of {@link Map }
     * 
     */
    public Map createMap() {
        return new Map();
    }

    /**
     * Create an instance of {@link Shape }
     * 
     */
    public Shape createShape() {
        return new Shape();
    }

    /**
     * Create an instance of {@link Point }
     * 
     */
    public Point createPoint() {
        return new Point();
    }

    /**
     * Create an instance of {@link Row }
     * 
     */
    public Row createRow() {
        return new Row();
    }

    /**
     * Create an instance of {@link RowMilepost }
     * 
     */
    public RowMilepost createRowMilepost() {
        return new RowMilepost();
    }

    /**
     * Create an instance of {@link Loads }
     * 
     */
    public Loads createLoads() {
        return new Loads();
    }

    /**
     * Create an instance of {@link Load }
     * 
     */
    public Load createLoad() {
        return new Load();
    }

    /**
     * Create an instance of {@link Cities }
     * 
     */
    public Cities createCities() {
        return new Cities();
    }

    /**
     * Create an instance of {@link SmallCity }
     * 
     */
    public SmallCity createSmallCity() {
        return new SmallCity();
    }

    /**
     * Create an instance of {@link MediumCity }
     * 
     */
    public MediumCity createMediumCity() {
        return new MediumCity();
    }

    /**
     * Create an instance of {@link MajorCity }
     * 
     */
    public MajorCity createMajorCity() {
        return new MajorCity();
    }

    /**
     * Create an instance of {@link Cards }
     * 
     */
    public Cards createCards() {
        return new Cards();
    }

    /**
     * Create an instance of {@link DemandCard }
     * 
     */
    public DemandCard createDemandCard() {
        return new DemandCard();
    }

    /**
     * Create an instance of {@link Demand }
     * 
     */
    public Demand createDemand() {
        return new Demand();
    }

    /**
     * Create an instance of {@link City }
     * 
     */
    public City createCity() {
        return new City();
    }

    /**
     * Create an instance of {@link Milepost }
     * 
     */
    public Milepost createMilepost() {
        return new Milepost();
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
     * Create an instance of {@link JAXBElement }{@code <}{@link RowMilepost }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RowMilepost }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "clear")
    public JAXBElement<RowMilepost> createClear(RowMilepost value) {
        return new JAXBElement<RowMilepost>(_Clear_QNAME, RowMilepost.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RowMilepost }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RowMilepost }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "mountain")
    public JAXBElement<RowMilepost> createMountain(RowMilepost value) {
        return new JAXBElement<RowMilepost>(_Mountain_QNAME, RowMilepost.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Object }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "load", scope = SmallCity.class)
    @XmlIDREF
    public JAXBElement<Object> createSmallCityLoad(Object value) {
        return new JAXBElement<Object>(_SmallCityLoad_QNAME, Object.class, SmallCity.class, value);
    }

}
