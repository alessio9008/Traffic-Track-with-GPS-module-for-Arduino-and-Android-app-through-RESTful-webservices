/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservice.service;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.PathSegment;
import webservice.Position;
import webservice.PositionPK;

/**
 *
 * @author Alessio
 */
@Stateless
@Path("webservice.position")
public class PositionFacadeREST extends AbstractFacade<Position> {

    @PersistenceContext(unitName = "trafficPU")
    private EntityManager em;

    public PositionFacadeREST() {
        super(Position.class);
    }

    @POST
    @Consumes({"application/json"})
    @Produces("text/plain")
    public String createItem(Position entity) {
        if (entity != null && !checkItem(entity.getPositionPK().getId(), entity.getPositionPK().getTimestamp())) {
            entity.getPositionPK().setTimestamp(System.currentTimeMillis());
            super.create(entity);
            return "elemento creato";
        }
        return "impossibile creare elemento";
    }

    @GET
    @Path("edit/{id}/{timestamp}")
    @Consumes({"application/json"})
    @Produces("text/plain")
    public String edit(@PathParam("id") String id, @PathParam("timestamp") long timestamp, Position entity) {
        if (checkItem(id, timestamp) && entity != null && id.equals(entity.getPositionPK().getId()) && timestamp == entity.getPositionPK().getTimestamp()) {
            super.edit(entity);
            return "elemento modificato";
        }
        return "errore elemento non trovato";
    }

    @GET
    @Path("delete/{id}/{timestamp}")
    @Produces({"text/plain"})
    public String remove(@PathParam("id") String id, @PathParam("timestamp") long timestamp) {
        if (checkItem(id, timestamp)) {
            webservice.PositionPK key = new webservice.PositionPK(id, timestamp);
            super.remove(super.find(key));
            return "elemento eliminato";
        }
        return "errore elemento non trovato";
    }

    @GET
    @Path("{id}/{timestamp}")
    @Produces({"application/json"})
    public Position find(@PathParam("id") String id, @PathParam("timestamp") long timestamp) {
        webservice.PositionPK key = new webservice.PositionPK(id, timestamp);
        return super.find(key);
    }

    @GET
    @Override
    @Produces({"application/json"})
    public List<Position> findAll() {
        return super.findAll();
    }

    @GET
    @Path("range/{from}/{to}")
    @Produces({"application/json"})
    public List<Position> findRange(@PathParam("from") long from, @PathParam("to") long to) {
        Query q = getEntityManager().createQuery("SELECT p FROM Position p WHERE p.positionPK.timestamp >= :from AND p.positionPK.timestamp <= :to");
        q.setParameter("from", from);
        q.setParameter("to", to);
        return q.getResultList();
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    private boolean checkItem(String id, long timestamp) {
        Query q = getEntityManager().createQuery("SELECT p FROM Position p WHERE p.positionPK.timestamp = :timestamp AND p.positionPK.id = :id");
        q.setParameter("id", id);
        q.setParameter("timestamp", timestamp);
        try {
            Position p = (Position) q.getSingleResult();
            if (p == null) {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
        return true;
    }
}
