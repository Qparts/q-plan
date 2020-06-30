package q.rest.plan.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import q.rest.plan.filter.annotation.Jwt;
import q.rest.plan.filter.annotation.SubscriberJwt;
import q.rest.plan.helper.KeyConstant;


import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.PublicKey;

@Provider
@Jwt
@Priority(Priorities.AUTHENTICATION)
public class JwtFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        String token = authorizationHeader.substring("Bearer".length()).trim();
        try{
            PublicKey key = KeyConstant.PUBLIC_KEY;
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            validateType(claims.get("typ"));
            validateAppCode(claims.get("appCode"));
        }catch (Exception e){
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    public void validateType(Object type) throws Exception{

    }

    public void validateAppCode(Object appCode) throws Exception{
        Integer ac = (Integer) appCode;
        //verify ac has access
    }
}
