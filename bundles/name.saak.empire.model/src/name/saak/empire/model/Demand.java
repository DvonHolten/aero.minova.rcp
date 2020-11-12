
package name.saak.empire.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
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
 *       &lt;attribute name="city" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" /&gt;
 *       &lt;attribute name="load" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" /&gt;
 *       &lt;attribute name="money" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "demand")
public class Demand {

    @XmlAttribute(name = "city", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object city;
    @XmlAttribute(name = "load", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object load;
    @XmlAttribute(name = "money", required = true)
    protected int money;

    /**
     * Ruft den Wert der city-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getCity() {
        return city;
    }

    /**
     * Legt den Wert der city-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setCity(Object value) {
        this.city = value;
    }

    /**
     * Ruft den Wert der load-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getLoad() {
        return load;
    }

    /**
     * Legt den Wert der load-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setLoad(Object value) {
        this.load = value;
    }

    /**
     * Ruft den Wert der money-Eigenschaft ab.
     * 
     */
    public int getMoney() {
        return money;
    }

    /**
     * Legt den Wert der money-Eigenschaft fest.
     * 
     */
    public void setMoney(int value) {
        this.money = value;
    }

}
