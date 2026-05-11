package com.javaclass.game.service;

import com.javaclass.game.constants.AuthDefiner;
import com.javaclass.game.dao.PlayerDao;
import com.javaclass.game.dto.PlayerLoginResponse;
import com.javaclass.game.dto.PlayerRegisterRequest;
import com.javaclass.game.dto.PlayerRegisterResponse;
import com.javaclass.game.model.Player;
import com.javaclass.game.model.PlayerEquipment;
import com.javaclass.game.model.PlayerStats;
import com.javaclass.game.utility.JwtUtility;
import com.javaclass.game.utility.PlayerBannedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PlayerAuthService {

    private final PlayerDao playerDao;
    private final JwtUtility jwtUtility;
    private final BCryptPasswordEncoder passwordEncoder;

    public PlayerAuthService(
        PlayerDao playerDao,
        JwtUtility jwtUtility,
        BCryptPasswordEncoder passwordEncoder
    ) {
        this.playerDao = playerDao;
        this.jwtUtility = jwtUtility;
        this.passwordEncoder = passwordEncoder;
    }

    public PlayerLoginResponse login(String account, String password) {
        Player player = playerDao.findByAccountId(account)
            .orElseThrow(() -> new IllegalArgumentException(AuthDefiner.ERROR_INVALID_CREDENTIALS));

        boolean isPasswordCorrect = passwordEncoder.matches(password, player.getPassword());
        if (!isPasswordCorrect) {
            throw new IllegalArgumentException(AuthDefiner.ERROR_INVALID_CREDENTIALS);
        }

        boolean isBanned = "BANNED".equals(player.getStatus());
        if (isBanned) {
            throw new PlayerBannedException(AuthDefiner.ERROR_PLAYER_BANNED);
        }

        String token = jwtUtility.generatePlayerToken(player.getId());
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(AuthDefiner.TOKEN_VALID_HOURS);

        return PlayerLoginResponse.builder()
            .token(token)
            .playerId(player.getId())
            .accountId(player.getAccountId())
            .nickname(player.getNickname())
            .money(player.getStats() != null ? player.getStats().getMoney() : 0L)
            .expiresAt(expiresAt)
            .build();
    }

    public PlayerRegisterResponse register(PlayerRegisterRequest registerRequest) {
        boolean isAccountAlreadyExists = playerDao.findByAccountId(registerRequest.getAccountId()).isPresent();
        if (isAccountAlreadyExists) {
            throw new IllegalArgumentException(AuthDefiner.ERROR_ACCOUNT_ALREADY_EXISTS);
        }

        Player newPlayer = new Player();
        newPlayer.setAccountId(registerRequest.getAccountId());
        newPlayer.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newPlayer.setNickname(registerRequest.getNickname());
        newPlayer.setEmail(registerRequest.getEmail());

        PlayerStats stats = new PlayerStats();
        stats.setPlayer(newPlayer);
        newPlayer.setStats(stats);

        PlayerEquipment equipment = new PlayerEquipment();
        equipment.setPlayer(newPlayer);
        newPlayer.setEquipment(equipment);

        Player savedPlayer = playerDao.save(newPlayer);

        return PlayerRegisterResponse.builder()
            .playerId(savedPlayer.getId())
            .accountId(savedPlayer.getAccountId())
            .nickname(savedPlayer.getNickname())
            .email(savedPlayer.getEmail())
            .createdAt(savedPlayer.getCreatedAt())
            .build();
    }
}