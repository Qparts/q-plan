package q.rest.plan.operation;

import q.rest.plan.dao.DAO;
import q.rest.plan.filter.annotation.InternalApp;
import q.rest.plan.filter.annotation.Jwt;
import q.rest.plan.filter.annotation.UserJwt;
import q.rest.plan.filter.annotation.ValidApp;
import q.rest.plan.helper.Helper;
import q.rest.plan.model.entity.Plan;
import q.rest.plan.model.entity.PlanDuration;
import q.rest.plan.model.entity.PlanPromotion;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/api/v1/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ApiV1 {

    @EJB
    private DAO dao;

    @EJB
    private AsyncService async;


    @GET
    @Path("plans")
    @Jwt
    public Response getPlans(){
        try{
            List<Plan> plans = dao.get(Plan.class);
            for(Plan plan : plans){
                initPlan(plan);
            }
            return Response.status(200).entity(plans).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }

    @GET
    @Jwt
    @Path("general-role-id/plan/{planId}")
    public Response getPlanRoleId(@PathParam(value = "planId") int planId){
        Plan plan = dao.find(Plan.class, planId);
        Map<String,Integer> map = new HashMap<>();
        map.put("generalRoleId", plan.getGeneralRoleId());
        return Response.ok().entity(map).build();
    }


    @GET
    @Jwt
    @Path("names/{planId}")
    public Response getPlanNames(@PathParam(value = "planId") int planId){
        Plan plan = dao.find(Plan.class, planId);
        Map<String,String> map = new HashMap<>();
        map.put("name", plan.getName());
        map.put("nameAr", plan.getNameAr());
        return Response.ok().entity(map).build();
    }


    @POST
    @Path("plan")
    @UserJwt
    public Response createPlan(Plan plan) {
        plan.setCreated(new Date());
        dao.persist(plan);
        return Response.status(200).entity(plan).build();
    }

    @GET
    @Path("plan/basic")
    @Jwt
    public Response getBasicPlan() {
        Plan plan = dao.findCondition(Plan.class, "name", "Basic Plan");
        initPlan(plan);
        return Response.status(200).entity(plan).build();
    }


    @GET
    @Path("plan/basic/id")
    @InternalApp
    public Response getBasicPlanId() {
        Plan plan = dao.findCondition(Plan.class, "name", "Basic Plan");
        PlanDuration duration = dao.findCondition(PlanDuration.class, "planId", plan.getId());
        Map<String,Integer> map = new HashMap<String, Integer>();
        map.put("planId", plan.getId());
        map.put("durationId", duration.getId());
        map.put("roleId", plan.getGeneralRoleId());
        return Response.status(200).entity(map).build();
    }

    @GET
    @Path("plan/{id}")
    @Jwt
    public Response getPlan(@PathParam(value = "id") int id) {
        Plan plan = dao.find(Plan.class, id);
        initPlan(plan);
        return Response.status(200).entity(plan).build();
    }

    public void initPlan(Plan plan) {
        plan.setPromotions(dao.getTwoConditions(PlanPromotion.class, "planId", "status", plan.getId(), 'A'));
    }

    @POST
    @Path("promotion")
    @UserJwt
    public Response createPromotion(PlanPromotion promotion) {
        promotion.setStartDate(Helper.atStartOfDay(promotion.getStartDate()));
        promotion.setEndDate(Helper.atEndOfDay(promotion.getEndDate()));
        promotion.setPromoCode(promotion.getPromoCode().toUpperCase());
        dao.persist(promotion);
        return Response.status(200).entity(promotion).build();
    }

    @GET
    @Path("promotions")
    @UserJwt
    public Response getAllPromotions(){
        String sql = "select b from PlanPromotion b order by b.startDate desc";
        List<PlanPromotion> pps = dao.getJPQLParams(PlanPromotion.class, sql);
        for(PlanPromotion planPromotion : pps){
            if(planPromotion.getStatus() == 'A'){
                if(planPromotion.getEndDate().before(new Date())){
                    planPromotion.setStatus('E');
                    async.update(planPromotion);
                }
            }
        }
        return Response.status(200).entity(pps).build();
    }

    @GET
    @Path("promo-code")
    @Jwt
    public Response checkPromoCode(@Context UriInfo info) {
        String code = info.getQueryParameters().getFirst("code");
        String planString = info.getQueryParameters().getFirst("plan");
        String durationString = info.getQueryParameters().getFirst("duration");
        String companyString = info.getQueryParameters().getFirst("company");
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
        verifyPlanSpecific(planPromotion, companyString);
        return Response.status(200).entity(planPromotion).build();
    }

    private void verifyPlanSpecific(PlanPromotion prom, String company) {
        if (company != null && prom.isSpecific() && prom.getCompanyId() != Integer.parseInt(company)) {
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