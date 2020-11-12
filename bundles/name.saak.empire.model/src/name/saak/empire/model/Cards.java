
package name.saak.empire.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *       &lt;sequence&gt;
 *         &lt;element ref="{}demand-card"/&gt;
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
    "demandCard"
})
@XmlRootElement(name = "cards")
public class Cards {

    @XmlElement(name = "demand-card", required = true)
    protected DemandCard demandCard;

    /**
     * Ruft den Wert der demandCard-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DemandCard }
     *     
     */
    public DemandCard getDemandCard() {
        return demandCard;
    }

    /**
     * Legt den Wert der demandCard-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DemandCard }
     *     
     */
    public void setDemandCard(DemandCard value) {
        this.demandCard = value;
    }

}
