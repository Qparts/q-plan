package q.rest.plan.operation;


import q.rest.plan.dao.DAO;
import q.rest.plan.helper.AppConstants;
import q.rest.plan.helper.ValidAppRequester;
import q.rest.plan.model.contract.MessagingModel;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.core.Response;

@Stateless
public class AsyncService {

    @EJB
    private DAO dao;


    @Asynchronous
    public void update(Object object){
        dao.update(object);
    }

    @Asynchronous
    public void sendSms(MessagingModel smsModel){
        Response r = ValidAppRequester.postSecuredRequest(AppConstants.SEND_SMS, smsModel);
        System.out.println("SMS Response " + r.getStatus());
    }

    @Asynchronous
    public void sendEmail(MessagingModel model){
        Response r =ValidAppRequester.postSecuredRequest(AppConstants.SEND_EMAIL, model);
        System.out.println("EMAIL Response " + r.getStatus() + " - " + r.readEntity(String.class));
    }
}
