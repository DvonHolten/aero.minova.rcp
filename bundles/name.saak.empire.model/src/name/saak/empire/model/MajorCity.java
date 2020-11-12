
package name.saak.empire.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für major-city complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="major-city"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{}small-city"&gt;
 *       &lt;attribute name="radius" type="{http://www.w3.org/2001/XMLSchema}int" default="2" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "major-city")
public class MajorCity
    extends SmallCity
{

    @XmlAttribute(name = "radius")
    protected Integer radius;

    /**
     * Ruft den Wert der radius-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getRadius() {
        if (radius == null) {
            return  2;
        } else {
            return radius;
        }
    }

    /**
     * Legt den Wert der radius-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRadius(Integer value) {
        this.radius = value;
    }

}
