package q.rest.plan.operation;

import q.rest.plan.dao.DAO;
import q.rest.plan.filter.annotation.InternalApp;
import q.rest.plan.filter.annotation.Jwt;
import q.rest.plan.filter.annotation.SubscriberJwt;
import q.rest.plan.filter.annotation.UserJwt;
import q.rest.plan.helper.Helper;
import q.rest.plan.model.contract.PublicPlan;
import q.rest.plan.model.contract.PublicPlanPromotion;
import q.rest.plan.model.entity.Plan;
import q.rest.plan.model.entity.PlanDuration;
import q.rest.plan.model.entity.PlanPromotion;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/api/v2/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ApiV2 {

    @EJB
    private DAO dao;

    @EJB
    private AsyncService async;


    @GET
    @Path("plans")
    public Response getPlans(){
        try{
            List<PublicPlan> plans = dao.getCondition(PublicPlan.class, "status", 'A');
            return Response.status(200).entity(plans).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }

    @GET
    @Path("promo-code")
    @SubscriberJwt
    public Response checkPromoCode(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @Context UriInfo info) {
        String code = info.getQueryParameters().getFirst("code");
        String planString = info.getQueryParameters().getFirst("plan");
        String durationString = info.getQueryParameters().getFirst("duration");
        int companyId = Helper.getCompanyFromJWT(header);
        verifyPromoCode(code, planString, durationString);
        int plan = Integer.parseInt(planString);
        int duration = Integer.parseInt(durationString);
        code = code.toUpperCase();
        String sql = "select b from PlanPromotion b where " +
                " b.promoCode = :value0 " +
                " and b.durationId = :value1 " +
                " and b.planId = :value2 " +
                " and b.status = :value3 " +
                " and b.endDate >= :value4 ";
        PlanPromotion planPromotion = dao.findJPQLParams(PlanPromotion.class, sql, code, duration, plan, 'A', new Date());
        if (planPromotion == null) throwError(404, null);
        verifyPlanSpecific(planPromotion, companyId);
        PublicPlanPromotion ppp = dao.find(PublicPlanPromotion.class, planPromotion.getId());
        return Response.status(200).entity(ppp).build();
    }

    private void verifyPlanSpecific(PlanPromotion prom, int companyId) {
        if (companyId != 0 && prom.isSpecific() && prom.getCompanyId() != companyId) {
            throwError(404);
        }
    }

    private void verifyPromoCode(String code, String plan, String duration) {
        if (null == code || null == plan || null == duration) {
            throwError(409, "Subscriber already registered");
        }
    }

    public void throwError(int code) {
        throwError(code, null);
    }

    public void throwError(int code, String msg) {
        throw new WebApplicationException(
                Response.status(code).entity(msg).build()
        );
    }
}