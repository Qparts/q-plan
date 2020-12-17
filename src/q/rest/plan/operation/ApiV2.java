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
    @SubscriberJwt
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
        System.out.println("verifying promocode");
        verifyPromoCode(code, planString, durationString);
        System.out.println("promocode verirfied");
        int plan = Integer.parseInt(planString);
        int duration = Integer.parseInt(durationString);
        code = code.toUpperCase();
        String sql = "select b from PlanPromotion b where " +
                " b.promoCode = :value0 " +
                " and b.durationId = :value1 " +
                " and b.planId = :value2 " +
                " and b.status = :value3 " +
                " and b.endDate >= :value4 ";
        System.out.println("sql " + sql);
        PlanPromotion planPromotion = dao.findJPQLParams(PlanPromotion.class, sql, code, duration, plan, 'A', new Date());
        System.out.println("from dao ");
        if (planPromotion == null) throwError(404, null);
        System.out.println("verifying specific ");
        verifyPlanSpecific(planPromotion, companyId);
        System.out.println("ok ");
        PublicPlanPromotion ppp = dao.find(PublicPlanPromotion.class, planPromotion.getId());
        System.out.println("returning promotion ");
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