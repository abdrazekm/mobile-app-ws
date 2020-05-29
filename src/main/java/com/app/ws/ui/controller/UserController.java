package com.app.ws.ui.controller;

import com.app.ws.exceptions.UserServiceException;
import com.app.ws.service.AddressService;
import com.app.ws.service.UserService;
import com.app.ws.shared.dto.AddressDTO;
import com.app.ws.shared.dto.UserDto;
import com.app.ws.ui.model.request.UserDetailsRequestModel;
import com.app.ws.ui.model.response.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private AddressService addressService;

    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest getUser(@PathVariable String id){
        UserRest returnValue = new UserRest();

        UserDto userDto = userService.getUserByUserId(id);
        BeanUtils.copyProperties(userDto, returnValue);

        return returnValue;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws UserServiceException {

        UserRest returnValue = new UserRest();

        if(userDetails.getFirstName().isEmpty()) throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELDS.getErrorMessage());

//        UserDto userDto = new UserDto();
//        BeanUti*/ls.copyProperties(userDetails, userDto);
        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);
//        BeanUtils.copyProperties(createdUser, returnValue);
        returnValue = modelMapper.map(createdUser, UserRest.class);
        return returnValue;
    }

    @PutMapping(path = "/{id}",
    consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
    produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest updateUser(@PathVariable String id,@RequestBody UserDetailsRequestModel userDetails ){
        UserRest returnValue = new UserRest();

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);
        UserDto updatedUser = userService.updateUser(id, userDto);
        BeanUtils.copyProperties(updatedUser, returnValue);
        return returnValue;
    }

    @DeleteMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel deleteUser(@PathVariable String id){
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(OperationNames.DELETE.name());
        userService.deleteUser(id);
        returnValue.setOperationResult(OperationResults.SUCCESS.name());
        return returnValue;
    }

    @GetMapping
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "1") int page,
                                   @RequestParam(value = "limit", defaultValue = "25") int limit){
        List<UserRest> returnValue = new ArrayList<>();

        List<UserDto> users = userService.getUsers(page, limit);

        for (UserDto userDto: users){
            UserRest userRest = new UserRest();
            BeanUtils.copyProperties(userDto, userRest);
            returnValue.add(userRest);
        }
        return returnValue;
    }

    @GetMapping(path = "/{id}/addresses",
    produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<AddressRest> getUserAddresses(@PathVariable String id){
        List<AddressRest> returnValue = new ArrayList<>();

        List<AddressDTO> addresses = addressService.getAddresses(id);

        ModelMapper modelMapper = new ModelMapper();

        if (addresses != null && !addresses.isEmpty()){
            Type listType = new TypeToken<List<AddressRest>>() {}.getType();
            returnValue = modelMapper.map(addresses, listType);
        }

        return returnValue;
    }

    @GetMapping(path = "/{id}/addresses/{addressId}")
    public AddressRest getAddress(@PathVariable String id, @PathVariable String addressId)
    {
        ModelMapper modelMapper = new ModelMapper();
        AddressDTO addressDTO = addressService.getAddress(addressId);
        return modelMapper.map(addressDTO, AddressRest.class);
    }
}
