/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webservice;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Alessio
 */
@Entity
@Table(name = "position")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Position.findAll", query = "SELECT p FROM Position p"),
    @NamedQuery(name = "Position.findById", query = "SELECT p FROM Position p WHERE p.positionPK.id = :id"),
    @NamedQuery(name = "Position.findByTimestamp", query = "SELECT p FROM Position p WHERE p.positionPK.timestamp = :timestamp"),
    @NamedQuery(name = "Position.findByLat", query = "SELECT p FROM Position p WHERE p.lat = :lat"),
    @NamedQuery(name = "Position.findByLt", query = "SELECT p FROM Position p WHERE p.lt = :lt"),
    @NamedQuery(name = "Position.findByLon", query = "SELECT p FROM Position p WHERE p.lon = :lon"),
    @NamedQuery(name = "Position.findByLn", query = "SELECT p FROM Position p WHERE p.ln = :ln"),
    @NamedQuery(name = "Position.findBySpeed", query = "SELECT p FROM Position p WHERE p.speed = :speed"),
    @NamedQuery(name = "Position.findByAngle", query = "SELECT p FROM Position p WHERE p.angle = :angle"),
    @NamedQuery(name = "Position.findByAltitude", query = "SELECT p FROM Position p WHERE p.altitude = :altitude")})
public class Position implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected PositionPK positionPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "lat")
    private double lat;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "lt")
    private String lt;
    @Basic(optional = false)
    @NotNull
    @Column(name = "lon")
    private double lon;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "ln")
    private String ln;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "speed")
    private Double speed;
    @Column(name = "angle")
    private Double angle;
    @Column(name = "altitude")
    private Double altitude;

    public Position() {
    }

    public Position(PositionPK positionPK) {
        this.positionPK = positionPK;
    }

    public Position(PositionPK positionPK, double lat, String lt, double lon, String ln) {
        this.positionPK = positionPK;
        this.lat = lat;
        this.lt = lt;
        this.lon = lon;
        this.ln = ln;
    }

    public Position(String id, long timestamp) {
        this.positionPK = new PositionPK(id, timestamp);
    }

    public PositionPK getPositionPK() {
        return positionPK;
    }

    public void setPositionPK(PositionPK positionPK) {
        this.positionPK = positionPK;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getLt() {
        return lt;
    }

    public void setLt(String lt) {
        this.lt = lt;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getLn() {
        return ln;
    }

    public void setLn(String ln) {
        this.ln = ln;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getAngle() {
        return angle;
    }

    public void setAngle(Double angle) {
        this.angle = angle;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (positionPK != null ? positionPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Position)) {
            return false;
        }
        Position other = (Position) object;
        if ((this.positionPK == null && other.positionPK != null) || (this.positionPK != null && !this.positionPK.equals(other.positionPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "webservice.Position[ positionPK=" + positionPK + " ]";
    }
    
}
