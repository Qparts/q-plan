package q.rest.plan.filter;


import q.rest.plan.filter.annotation.UserJwt;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.Provider;

@Provider
@UserJwt
@Priority(Priorities.AUTHENTICATION)
public class JwtFilterUser extends JwtFilter {

    @Override
    public void validateType(Object type) throws Exception{
        if(!type.toString().equals("U")){
            throw new Exception();
        }
    }

}
