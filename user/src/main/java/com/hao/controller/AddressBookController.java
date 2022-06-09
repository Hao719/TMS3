package com.hao.controller;

import com.hao.common.CustomIdGenerator;
import com.hao.j2cache.annotation.Cache;
import com.hao.j2cache.annotation.CacheEvictor;
import com.hao.pojo.AddressBook;
import com.hao.service.IAddressBookService;
import com.hao.utils.Result;
import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j2;
import net.oschina.j2cache.CacheChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/addressBook")
@Api(tags = "地址簿管理")
public class AddressBookController {

    @Autowired
    private IAddressBookService addressBookService;
    @Autowired
    private CustomIdGenerator idGenerator;
    @Autowired
    private CacheChannel cacheChannel;
    private final String region = "addressBook";

    /**
     * 新增地址簿
     * @param addressBook
     * @return
     */
    @PostMapping("")
    public Result save(@Validated @RequestBody AddressBook addressBook){
        if (1 == addressBook.getIsDefault()) {
            addressBookService.lambdaUpdate().set(AddressBook::getIsDefault, 0).eq(AddressBook::getUserId, addressBook.getUserId()).update();
        }
        addressBook.setId(idGenerator.nextId(addressBook)+"");
        boolean save = addressBookService.save(addressBook);
        if(save){
            cacheChannel.set(region,addressBook.getId(),addressBook);
            return Result.ok();
        }
        return Result.error();
    }

    /**
     * 根据id查询地址簿
     * @param ids
     * @return
     */
    @GetMapping("")
    @Cache(region = region,key = "ab",params = "ids")
    public List<AddressBook> findById(@RequestParam(value = "ids",required = false) List<String> ids){
        return addressBookService.listByIds(ids);
    }

    @DeleteMapping("")
    @CacheEvictor({@Cache(region = region,key = "ab",params = "ids")})
    public Result delete(@RequestParam("ids") List<String> ids){
//        boolean b = addressBookService.removeById(id);
//        if(b){
//            return Result.ok();
//        }
        return null;
    }
}
