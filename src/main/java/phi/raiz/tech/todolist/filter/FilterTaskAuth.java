package phi.raiz.tech.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import phi.raiz.tech.todolist.user.IUserRepository;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        var servletPath = request.getServletPath();
        //System.out.println(servletPath);
        if (servletPath.startsWith("/tasks/")) {
            //take authentication data username and password
            var authorization = request.getHeader("Authorization");
            //System.out.println("Authorization: "+authorization);
            var authEncoded = authorization.substring("Basic".length()).trim();
            //System.out.println("authEncoded: "+authEncoded);
            byte[] authDecoded = Base64.getDecoder().decode(authEncoded);
            var authString = new String(authDecoded);
            //System.out.println("authEncoded: "+authString);
            String[] credentials = authString.split(":");

            String username = credentials[0];
            String password = credentials[1];
            //System.out.println("username: "+username);
            //System.out.println("password: "+password);
            //validate username
            var user = this.userRepository.findByUsername(username);
            if (user == null) {
                //System.out.println("user is null");
                response.sendError(401, "User without authorization!");
            } else {
                //validate password
                //System.out.println("password validate");
                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if (passwordVerify.verified) {
                    //forward
                    //System.out.println("password is fine and forward");
                    request.setAttribute("idUser",user.getId());
                    filterChain.doFilter(request, response);
                } else {
                    //System.out.println("Password not match!");
                    response.sendError(401, "Password not match!");
                }
            }
        } else {
            //forward
            //System.out.println("forward when not /tasks/ route");
            filterChain.doFilter(request, response);
        }
    }
}
