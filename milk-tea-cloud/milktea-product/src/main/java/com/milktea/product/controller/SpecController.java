package com.milktea.product.controller;

import com.milktea.common.result.Result;
import com.milktea.model.entity.SpecOption;
import com.milktea.model.entity.SpecType;
import com.milktea.product.service.SpecService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spec")
public class SpecController {

    private final SpecService specService;

    public SpecController(SpecService specService) {
        this.specService = specService;
    }

    @GetMapping("/types")
    public Result<List<SpecType>> listTypes() {
        return Result.success(specService.listTypes());
    }

    @PostMapping("/type")
    public Result<Void> addType(@RequestBody SpecType specType) {
        specService.addType(specType);
        return Result.success();
    }

    @PutMapping("/type")
    public Result<Void> updateType(@RequestBody SpecType specType) {
        specService.updateType(specType);
        return Result.success();
    }

    @DeleteMapping("/type/{id}")
    public Result<Void> deleteType(@PathVariable Long id) {
        specService.deleteType(id);
        return Result.success();
    }

    @GetMapping("/options/{typeId}")
    public Result<List<SpecOption>> listOptions(@PathVariable Long typeId) {
        return Result.success(specService.listOptions(typeId));
    }

    @PostMapping("/option")
    public Result<Void> addOption(@RequestBody SpecOption specOption) {
        specService.addOption(specOption);
        return Result.success();
    }

    @PutMapping("/option")
    public Result<Void> updateOption(@RequestBody SpecOption specOption) {
        specService.updateOption(specOption);
        return Result.success();
    }

    @DeleteMapping("/option/{id}")
    public Result<Void> deleteOption(@PathVariable Long id) {
        specService.deleteOption(id);
        return Result.success();
    }
}
