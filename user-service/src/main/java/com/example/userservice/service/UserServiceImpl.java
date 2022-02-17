package com.example.userservice.service;

import com.example.userservice.client.OrderServiceClient;
import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.UserEntity;
import com.example.userservice.error.FeignErrorDecoder;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.vo.ResponseOrder;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Environment env;
//    private final RestTemplate restTemplate;
    private final OrderServiceClient orderServiceClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username);

        if (userEntity == null){
            throw  new UsernameNotFoundException(username);
        }

        return new User(userEntity.getEmail(), userEntity.getEncryptedPwd(),
                true, true, true, true,
                new ArrayList<>());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd()));

        userRepository.save(userEntity);

        return userDto;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null){
            throw new UsernameNotFoundException("User not found");
        }

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

//        List<ResponseOrder> orders = new ArrayList<>();
//        String orderUrl = String.format(env.getProperty("order_service.url"), userId);
//        ResponseEntity<List<ResponseOrder>> orderListResponse =
//                restTemplate.exchange(orderUrl, HttpMethod.GET, null,
//                        new ParameterizedTypeReference<List<ResponseOrder>>() {});
//
//        List<ResponseOrder> orderList = orderListResponse.getBody();

        /* Feign exception handling */
//        List<ResponseOrder> orderList = null;
//        try {
//            orderServiceClient.getOrders(userId);
//        } catch (FeignException ex){
//            log.error(ex.getMessage());
//        }

        /* ErrorDecoder */
        List<ResponseOrder> orderList = orderServiceClient.getOrders(userId);

        userDto.setOrders(orderList);

        return userDto;
    }

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDto getUserDetailsByEmail(String userName) {
        UserEntity userEntity = userRepository.findByEmail(userName);

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
        return userDto;
    }

}
