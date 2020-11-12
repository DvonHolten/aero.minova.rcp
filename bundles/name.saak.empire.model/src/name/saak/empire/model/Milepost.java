
package name.saak.empire.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für milepost complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="milepost"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="position-x" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="position-y" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "milepost")
@XmlSeeAlso({
    SmallCity.class
})
public class Milepost {

    @XmlAttribute(name = "position-x", required = true)
    protected int positionX;
    @XmlAttribute(name = "position-y", required = true)
    protected int positionY;

    /**
     * Ruft den Wert der positionX-Eigenschaft ab.
     * 
     */
    public int getPositionX() {
        return positionX;
    }

    /**
     * Legt den Wert der positionX-Eigenschaft fest.
     * 
     */
    public void setPositionX(int value) {
        this.positionX = value;
    }

    /**
     * Ruft den Wert der positionY-Eigenschaft ab.
     * 
     */
    public int getPositionY() {
        return positionY;
    }

    /**
     * Legt den Wert der positionY-Eigenschaft fest.
     * 
     */
    public void setPositionY(int value) {
        this.positionY = value;
    }

}
