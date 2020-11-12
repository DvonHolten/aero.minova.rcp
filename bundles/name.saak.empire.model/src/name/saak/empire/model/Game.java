
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
 *         &lt;element ref="{}name"/&gt;
 *         &lt;element ref="{}map"/&gt;
 *         &lt;element ref="{}loads"/&gt;
 *         &lt;element ref="{}cities"/&gt;
 *         &lt;element ref="{}cards"/&gt;
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
    "name",
    "map",
    "loads",
    "cities",
    "cards"
})
@XmlRootElement(name = "game")
public class Game {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected Map map;
    @XmlElement(required = true)
    protected Loads loads;
    @XmlElement(required = true)
    protected Cities cities;
    @XmlElement(required = true)
    protected Cards cards;

    /**
     * Ruft den Wert der name-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Legt den Wert der name-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Ruft den Wert der map-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Map }
     *     
     */
    public Map getMap() {
        return map;
    }

    /**
     * Legt den Wert der map-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Map }
     *     
     */
    public void setMap(Map value) {
        this.map = value;
    }

    /**
     * Ruft den Wert der loads-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Loads }
     *     
     */
    public Loads getLoads() {
        return loads;
    }

    /**
     * Legt den Wert der loads-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Loads }
     *     
     */
    public void setLoads(Loads value) {
        this.loads = value;
    }

    /**
     * Ruft den Wert der cities-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Cities }
     *     
     */
    public Cities getCities() {
        return cities;
    }

    /**
     * Legt den Wert der cities-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Cities }
     *     
     */
    public void setCities(Cities value) {
        this.cities = value;
    }

    /**
     * Ruft den Wert der cards-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Cards }
     *     
     */
    public Cards getCards() {
        return cards;
    }

    /**
     * Legt den Wert der cards-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Cards }
     *     
     */
    public void setCards(Cards value) {
        this.cards = value;
    }

}
