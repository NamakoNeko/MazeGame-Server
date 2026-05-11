package com.javaclass.game.utility;

import com.javaclass.game.dao.PlayerDao;
import com.javaclass.game.model.Player;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String PLAYER_STATUS_BANNED = "BANNED";

    private final JwtUtility jwtUtility;
    private final PlayerDao playerDao;

    public JwtAuthenticationFilter(JwtUtility jwtUtility, PlayerDao playerDao) {
        this.jwtUtility = jwtUtility;
        this.playerDao = playerDao;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length());

        try {
            Long adminId = jwtUtility.extractAdminId(token);

            if (adminId != null) {
                String role = jwtUtility.extractRole(token);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    adminId,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
                return;
            }

            Long playerId = jwtUtility.extractPlayerId(token);

            if (playerId != null) {
                Optional<Player> playerOptional = playerDao.findById(playerId);

                if (playerOptional.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                Player player = playerOptional.get();
                boolean isBanned = PLAYER_STATUS_BANNED.equals(player.getStatus());
                if (isBanned) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    playerId,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_PLAYER"))
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (JwtException jwtException) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}