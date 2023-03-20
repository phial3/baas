package org.phial.baas.service.controller;

import org.phial.baas.service.domain.entity.Entity;
import org.phial.baas.service.service.BaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author gyf
 * @date 2022/12/12
 */
@RestController
public abstract class BaseController<T extends Entity> {

    public abstract BaseService<T> service();

    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.HEAD})
    public ResponseEntity root() {
        return ResponseEntity.ok("");
    }

    @GetMapping("{id}")
    public Object get(@PathVariable long id) {
        return ResponseEntity.ok(service().get(id));
    }

    @PostMapping("delete")
    public Object delete(@RequestBody Long[] ids) {
        Assert.isTrue(ids != null && ids.length > 0, "数据ID错误");
        for (Long id : ids) {
            service().delete(id);
        }
        return ResponseEntity.ok("");
    }

    @PostMapping("save")
    public Object save(@RequestBody T bean) {
        return ResponseEntity.ok(  service().save(bean));
    }

    @PostMapping("update")
    public Object update(@RequestBody T bean) {
        return ResponseEntity.ok(service().update(bean));
    }
}
