package com.hao.controller.base;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hao.DTO.base.GoodsTypeDto;
import com.hao.common.CustomIdGenerator;
import com.hao.pojo.base.GoodsType;
import com.hao.pojo.middle.TruckTypeGoodsType;
import com.hao.service.base.IGoodsTypeService;
import com.hao.service.middle.ITruckTypeGoodsTypeService;
import com.hao.utils.Constant;
import com.hao.utils.PageResponse;
import com.hao.utils.Result;
import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping("/goodsType")
@Api(tags = "货物类型管理")
public class GoodsTypeController {
    @Autowired
    private IGoodsTypeService goodsTypeService;
    @Autowired
    private CustomIdGenerator idGenerator;
    @Autowired
    private ITruckTypeGoodsTypeService truckTypeGoodsTypeService;

    /**
     * 新增货物类型
     *
     * @param goodsTypeDto
     * @return
     */
    @PostMapping("")
    public GoodsTypeDto insert(@Validated @RequestBody GoodsTypeDto goodsTypeDto) {
        goodsTypeDto.setId(idGenerator.nextId(goodsTypeDto) + "");
        GoodsType goodsType = new GoodsType();
        BeanUtils.copyProperties(goodsTypeDto, goodsType);
        boolean save = goodsTypeService.save(goodsType);
        if (save) {
            if (goodsTypeDto.getTruckTypeIds() != null) {
                List<TruckTypeGoodsType> truckTypeGoodsTypes = goodsTypeDto.getTruckTypeIds().stream().map(truckTypeGoodsTypeId -> {
                    TruckTypeGoodsType truckTypeGoodsType = new TruckTypeGoodsType();
                    truckTypeGoodsType.setGoodsTypeId(goodsTypeDto.getId());
                    truckTypeGoodsType.setTruckTypeId(truckTypeGoodsTypeId);
                    return truckTypeGoodsType;
                }).collect(Collectors.toList());
                boolean saveBatch = truckTypeGoodsTypeService.saveTruckTypeGoodsTypes(truckTypeGoodsTypes);
                if (saveBatch) {
                    return goodsTypeDto;
                }
                return null;
            }
            return goodsTypeDto;
        }
        return null;
    }

    /**
     * 根据id查询 货物类型
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public GoodsTypeDto findById(@PathVariable("id") String id) {
        if (id != null) {
            GoodsType goodsType = goodsTypeService.findById(id);
            GoodsTypeDto goodsTypeDto = new GoodsTypeDto();
            BeanUtils.copyProperties(goodsType, goodsTypeDto);
            List<TruckTypeGoodsType> truckTypeGoodsTypes = truckTypeGoodsTypeService.findByTypeId(null, id);
            if (truckTypeGoodsTypes != null) {
                List<String> truckTypeGoodsTypeIds = truckTypeGoodsTypes.stream().map(TruckTypeGoodsType::getTruckTypeId).collect(Collectors.toList());
                goodsTypeDto.setTruckTypeIds(truckTypeGoodsTypeIds);
            }
            return goodsTypeDto;
        }
        return null;
    }

    /**
     * 查询所有货物类型
     *
     * @return
     */
    @GetMapping("/all")
    public List<GoodsTypeDto> findAll() {
        List<GoodsType> goodsTypes = goodsTypeService.findAll();
        if (goodsTypes != null && goodsTypes.size() > 0) {
            List<GoodsTypeDto> goodsTypeDtos = goodsTypes.stream().map(goodsType -> {
                GoodsTypeDto goodsTypeDto = new GoodsTypeDto();
                BeanUtils.copyProperties(goodsType, goodsTypeDto);
                return goodsTypeDto;
            }).collect(Collectors.toList());
            goodsTypeDtos.forEach(goodsTypeDto -> {
                List<TruckTypeGoodsType> truckTypeGoodsTypes = truckTypeGoodsTypeService.findByTypeId(null, goodsTypeDto.getId());
                if (truckTypeGoodsTypes != null && truckTypeGoodsTypes.size() > 0) {
                    List<String> truckTypeGoodsTypeIds = truckTypeGoodsTypes.stream().map(TruckTypeGoodsType::getTruckTypeId).collect(Collectors.toList());
                    goodsTypeDto.setTruckTypeIds(truckTypeGoodsTypeIds);
                }
            });
            return goodsTypeDtos;
        }
        return null;
    }

    /**
     * 分页条件查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @param truckTypeId
     * @param truckTypeName
     * @return
     */
    @GetMapping("/page")
    public PageResponse<GoodsTypeDto> findByPage(@RequestParam(name = "page") Integer page,
                                                 @RequestParam(name = "pageSize") Integer pageSize,
                                                 @RequestParam(name = "name", required = false) String name,
                                                 @RequestParam(name = "truckTypeId", required = false) String truckTypeId,
                                                 @RequestParam(name = "truckTypeName", required = false) String truckTypeName) {
        IPage<GoodsType> iPage = goodsTypeService.findByPage(page, pageSize, name, truckTypeId, truckTypeName);
        List<GoodsType> goodsTypes = iPage.getRecords();
        if (goodsTypes != null && goodsTypes.size() > 0) {
            List<GoodsTypeDto> goodsTypeDtos = goodsTypes.stream().map(goodsType -> {
                List<TruckTypeGoodsType> truckTypeGoodsTypes = truckTypeGoodsTypeService.findByTypeId(null, goodsType.getId());
                List<String> truckTypeGoodsTypeIds = truckTypeGoodsTypes.stream().map(TruckTypeGoodsType::getTruckTypeId).collect(Collectors.toList());
                GoodsTypeDto goodsTypeDto = new GoodsTypeDto();
                BeanUtils.copyProperties(goodsType, goodsTypeDto);
                goodsTypeDto.setTruckTypeIds(truckTypeGoodsTypeIds);
                return goodsTypeDto;
            }).collect(Collectors.toList());
            return PageResponse.<GoodsTypeDto>builder()
                    .items(goodsTypeDtos)
                    .page(page)
                    .pagesize(pageSize)
                    .counts(iPage.getTotal())
                    .pages(iPage.getPages()).build();
        }
        return null;
    }

    /**
     * 修改货物类型
     *
     * @param id
     * @param goodsTypeDto
     * @return
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable("id") String id, @RequestBody GoodsTypeDto goodsTypeDto) {
        GoodsType goodsType = new GoodsType();
        goodsTypeDto.setId(id);
        BeanUtils.copyProperties(goodsTypeDto, goodsType);
        boolean update = goodsTypeService.updateById(goodsType);
        if (update) {
            if (goodsTypeDto.getTruckTypeIds() != null && goodsTypeDto.getTruckTypeIds().size() > 0) {
                boolean delete = truckTypeGoodsTypeService.deleteByTypeId(null, goodsType.getId());
                List<TruckTypeGoodsType> truckTypeGoodsTypes = goodsTypeDto.getTruckTypeIds().stream().map(truckTypeGoodsTypeId -> {
                    TruckTypeGoodsType truckTypeGoodsType = new TruckTypeGoodsType();
                    truckTypeGoodsType.setGoodsTypeId(goodsType.getId());
                    truckTypeGoodsType.setTruckTypeId(truckTypeGoodsTypeId);
                    return truckTypeGoodsType;
                }).collect(Collectors.toList());
                boolean save = truckTypeGoodsTypeService.saveTruckTypeGoodsTypes(truckTypeGoodsTypes);
                if (save) {
                    return Result.ok();
                }
                return Result.error();
            }
            return Result.ok();
        }
        return Result.error();
    }

    /**
     * 删除货物类型
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable("id") String id) {
        if (id != null) {
            GoodsType goodsType = new GoodsType();
            goodsType.setId(id);
            goodsType.setStatus(Constant.DATA_DISABLE_DELETE);
            boolean update = goodsTypeService.updateById(goodsType);
            if (update) {
                return Result.ok();
            }
            return Result.error();
        }
        return Result.error();
    }
}
