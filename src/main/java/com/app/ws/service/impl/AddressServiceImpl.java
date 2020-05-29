package com.app.ws.service.impl;

import com.app.ws.exceptions.UserServiceException;
import com.app.ws.io.entity.AddressEntity;
import com.app.ws.io.entity.UserEntity;
import com.app.ws.io.repositories.AddressRepository;
import com.app.ws.io.repositories.UserRepository;
import com.app.ws.service.AddressService;
import com.app.ws.shared.dto.AddressDTO;
import com.app.ws.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    @Override
    public List<AddressDTO> getAddresses(String userId) {
        List<AddressDTO> returnValue = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = userRepository.findByUserId(userId);
        if(userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        Iterable<AddressEntity> addressEntities = addressRepository.findAllByUserDetails(userEntity);
        for (AddressEntity address: addressEntities){
            returnValue.add(modelMapper.map(address, AddressDTO.class));
        }
        return returnValue;
    }

    @Override
    public AddressDTO getAddress(String addressId) {
        AddressDTO returnValue = null;
        AddressEntity addressEntity = addressRepository.findByAddressId(addressId);
        if(addressEntity != null)
            returnValue = new ModelMapper().map(addressEntity, AddressDTO.class);
        return returnValue;
    }
}
