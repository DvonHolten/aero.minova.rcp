
package name.saak.empire.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
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
 *       &lt;choice maxOccurs="unbounded"&gt;
 *         &lt;element ref="{}clear"/&gt;
 *         &lt;element ref="{}mountain"/&gt;
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
    "clearOrMountain"
})
@XmlRootElement(name = "row")
public class Row {

    @XmlElementRefs({
        @XmlElementRef(name = "clear", type = JAXBElement.class),
        @XmlElementRef(name = "mountain", type = JAXBElement.class)
    })
    protected List<JAXBElement<RowMilepost>> clearOrMountain;

    /**
     * Gets the value of the clearOrMountain property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the clearOrMountain property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClearOrMountain().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link RowMilepost }{@code >}
     * {@link JAXBElement }{@code <}{@link RowMilepost }{@code >}
     * 
     * 
     */
    public List<JAXBElement<RowMilepost>> getClearOrMountain() {
        if (clearOrMountain == null) {
            clearOrMountain = new ArrayList<JAXBElement<RowMilepost>>();
        }
        return this.clearOrMountain;
    }

}
