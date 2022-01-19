package com.choose.choose_back.service;

import com.choose.choose_back.SecurityUtils;
import com.choose.choose_back.domain.postgres.Community;
import com.choose.choose_back.domain.postgres.PostEntity;
import com.choose.choose_back.domain.postgres.User;
import com.choose.choose_back.dto.RegisterConfirmationRequestDTO;
import com.choose.choose_back.dto.RegisterEmailRequestDTO;
import com.choose.choose_back.dto.RegisterUsernameRequestDTO;
import com.choose.choose_back.model.Role;
import com.choose.choose_back.model.UserDTO;
import com.choose.choose_back.repository.postgres.CommunityRepository;
import com.choose.choose_back.repository.postgres.PostRepository;
import com.choose.choose_back.repository.postgres.UserRepository;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.transaction.Transactional;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Transactional
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommunityRepository communityRepository;
    private final SendGrid sendGrid;
    private final CacheManager cacheManager;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    private AuthenticationManager daoAuthenticationProvider;

    public List<UserDTO> findAll() {
        return userRepository.findAll()
                .stream()
                .map(user -> mapToDTO(user, new UserDTO()))
                .collect(Collectors.toList());
    }

    public UserDTO get(final Long id) {
        return userRepository.findById(id)
                .map(user -> mapToDTO(user, new UserDTO()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Long create(final UserDTO userDTO) {
        final User user = new User();
        mapToEntity(userDTO, user);
        return userRepository.save(user).getId();
    }

    public void update(final Long id, final UserDTO userDTO) {
        final User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        mapToEntity(userDTO, user);
        userRepository.save(user);
    }

    public void delete(final Long id) {
        userRepository.deleteById(id);
    }

    private UserDTO mapToDTO(final User user, final UserDTO userDTO) {
        userDTO.setId(user.getId());
        userDTO.setName(user.getFirstName() + " " + user.getLastName());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        userDTO.setUserCommunitys(user.getCommunities() == null ? null : user.getCommunities().stream()
                .map(Community::getId)
                .collect(Collectors.toList()));
        return userDTO;
    }

    private User mapToEntity(final UserDTO userDTO, final User user) {
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        if (userDTO.getUserCommunitys() != null) {
            final List<Community> userCommunitys = communityRepository.findAllById(userDTO.getUserCommunitys());
            if (userCommunitys.size() != userDTO.getUserCommunitys().size()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "one of userCommunitys not found");
            }
            user.setCommunities(new HashSet<>(userCommunitys));
        }
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByUsername(email).orElseThrow(() ->
                new UsernameNotFoundException("Email not found " + email));
    }

    public void registerUsername(RegisterUsernameRequestDTO request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("username already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.REGISTRATION);
        userRepository.save(user);

        daoAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
    }

    public void registerEmail(RegisterEmailRequestDTO request) {
        User user = SecurityUtils.getCurrentUser();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        userRepository.save(user);
        Email from = new Email("shkamarida.b@gmail.com", "choose");
        String subject = "Sending with SendGrid is Fun";
        Email to = new Email(request.getEmail());
        Random random = new Random();
        String code = ""+random.nextInt(10) + random.nextInt(10) + random.nextInt(10) + random.nextInt(10);
        Content content = new Content("text/plain", code);
        Mail mail = new Mail(from, subject, to, content);

        Request emailRequest = new Request();
        try {
            emailRequest.setMethod(Method.POST);
            emailRequest.setEndpoint("mail/send");
            emailRequest.setBody(mail.build());
            Response response = sendGrid.api(emailRequest);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Cache cache = cacheManager.getCache("codes");
        cache.put(user.getUsername(), code);
    }

    public void registerConfirm(RegisterConfirmationRequestDTO request) {
        User user = SecurityUtils.getCurrentUser();
        Cache cache = cacheManager.getCache("codes");
        String code = cache.get(user.getUsername(), String.class);
        if (code == null || !request.getCode().equals(code)) {
            throw new AccessDeniedException("invalid code");
        }
        user.setRole(Role.DEFAULT);
        userRepository.save(user);
    }

    @Autowired
    @Qualifier("soska")
    @Lazy
    public void setDaoAuthenticationProvider(AuthenticationManager daoAuthenticationProvider) {
        this.daoAuthenticationProvider = daoAuthenticationProvider;
    }
}
