package com.app.ws.service.impl;

import com.app.ws.exceptions.UserServiceException;
import com.app.ws.io.repositories.UserRepository;
import com.app.ws.io.entity.UserEntity;
import com.app.ws.service.UserService;
import com.app.ws.shared.Utils;
import com.app.ws.shared.dto.AddressDTO;
import com.app.ws.shared.dto.UserDto;
import com.app.ws.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    Utils utils;

    @Override
    public UserDto createUser(UserDto userDto) {
        ModelMapper modelMapper = new ModelMapper();
//        UserEntity userEntity = new UserEntity();
//        BeanUtils.copyProperties(userDto, userEntity);
        for(int i = 0; i< userDto.getAddresses().size(); i++){
            AddressDTO addressDTO = userDto.getAddresses().get(i);
            addressDTO.setAddressId(utils.generateAddressId(10));
            addressDTO.setUserDetails(userDto);
            userDto.getAddresses().set(i, addressDTO);
        }
        UserEntity userEntity =modelMapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        String publicUserId = utils.generateUserId(10);
        userEntity.setUserId(publicUserId);
        if (userRepository.findUserEntityByEmail(userEntity.getEmail()) != null)
            throw new RuntimeException("Record Already Exist");
        UserEntity storedUserDetails = userRepository.save(userEntity);

//        BeanUtils.copyProperties(storedUserDetails, returnValue);
        UserDto returnValue = modelMapper.map(storedUserDetails, UserDto.class);
        return returnValue;
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findUserEntityByEmail(email);

        if (userEntity == null) throw new UsernameNotFoundException(email);

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity, returnValue);
        return returnValue;
    }

    @Override
    public UserDto getUserByUserId(String id) throws UsernameNotFoundException {
        UserDto returnValue = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(id);
        if (userEntity == null) throw new UsernameNotFoundException("User with provided user id : "+id);
        BeanUtils.copyProperties(userEntity, returnValue);
        return returnValue;
    }

    @Override
    public UserDto updateUser(String id, UserDto user) {
        UserDto returnValue = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(id);

        if (userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        if (!user.getFirstName().isEmpty()) userEntity.setFirstName(user.getFirstName());
        if (!user.getLastName().isEmpty()) userEntity.setLastName(user.getLastName());

        UserEntity updatedUserEntity = userRepository.save(userEntity);
        BeanUtils.copyProperties(updatedUserEntity, returnValue);

        return returnValue;
    }

    @Override
    public void deleteUser(String id) {
        UserEntity userEntity = userRepository.findByUserId(id);

        if (userEntity == null)
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userRepository.delete(userEntity);
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> returnValue = new ArrayList<>();
        if (page > 0) page = page - 1;
        Pageable pageableRequest= PageRequest.of(page, limit);
        Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);
        List<UserEntity> users = usersPage.getContent();

        for(UserEntity userEntity: users){
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity, userDto);
            returnValue.add(userDto);
        }

        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findUserEntityByEmail(email);
        if (userEntity == null) throw new UsernameNotFoundException(email);
        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
    }

}
