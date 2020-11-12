
package name.saak.empire.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für anonymous complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence maxOccurs="unbounded"&gt;
 *         &lt;choice&gt;
 *           &lt;element name="small-city" type="{}small-city"/&gt;
 *           &lt;element name="medium-city" type="{}medium-city"/&gt;
 *           &lt;element name="major-city" type="{}major-city"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "smallCityOrMediumCityOrMajorCity"
})
@XmlRootElement(name = "cities")
public class Cities {

    @XmlElements({
        @XmlElement(name = "small-city"),
        @XmlElement(name = "medium-city", type = MediumCity.class),
        @XmlElement(name = "major-city", type = MajorCity.class)
    })
    protected List<SmallCity> smallCityOrMediumCityOrMajorCity;

    /**
     * Gets the value of the smallCityOrMediumCityOrMajorCity property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the smallCityOrMediumCityOrMajorCity property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSmallCityOrMediumCityOrMajorCity().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SmallCity }
     * {@link MediumCity }
     * {@link MajorCity }
     * 
     * 
     */
    public List<SmallCity> getSmallCityOrMediumCityOrMajorCity() {
        if (smallCityOrMediumCityOrMajorCity == null) {
            smallCityOrMediumCityOrMajorCity = new ArrayList<SmallCity>();
        }
        return this.smallCityOrMediumCityOrMajorCity;
    }

}
