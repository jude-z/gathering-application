package api.security.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtValidator jwtValidator;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if(token != null){
            if(!token.substring(0,7).equals("Bearer ")){
                throw new AuthenticationServiceException("Not Bearer form!!");
            }
            String jwtToken = token.substring(7);
            Claims claims = jwtValidator.validateToken(jwtToken);
            String username = claims.getSubject();
            String role = (String) claims.get("role");
            Long userId = ((Integer)claims.get("id")).longValue();
            JwtSubject jwtSubject = JwtSubject.of(userId, role, username);
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_"+role));
            CustomUserDetail customUserDetail = new CustomUserDetail(jwtSubject,authorities);
            Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetail, "", customUserDetail.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }



}
