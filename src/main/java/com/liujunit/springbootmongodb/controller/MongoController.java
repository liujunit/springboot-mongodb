package com.liujunit.springbootmongodb.controller;

import com.liujunit.springbootmongodb.entity.EntityTest;
import com.liujunit.springbootmongodb.entity.EntityTest1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class MongoController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping(value = "/")
    public String index() {
        return "redirect:/find";
    }

    //新增文档
    @GetMapping(value = "/insert")
    public String insert() {
        //insert方法并不提供级联类的保存，所以级联类需要先自己先保存
        EntityTest1 entityTest1_1 = new EntityTest1(1000L);
        //执行完insert后对象entityTest1将会获得保存后的id
        mongoTemplate.insert(entityTest1_1);
        //再添加一条
        EntityTest1 entityTest1_2 = new EntityTest1(1001L);
        mongoTemplate.insert(entityTest1_2);
        //创建列表并将保存后的关联对象添加进去
        ArrayList<EntityTest1> entityTest1List = new ArrayList<EntityTest1>();
        entityTest1List.add(entityTest1_1);
        entityTest1List.add(entityTest1_2);
        Random random = new Random();
        long parameter1 = random.nextLong();
        int parameter2 = random.nextInt();
        int parameter4 = random.nextInt();
        //新增主体对象
        EntityTest entityTest = new EntityTest(parameter1, "test" + parameter2, new Date(), parameter4, entityTest1List);
        //新增数据的主键已经存在，则会抛DuplicateKeyException异常
        mongoTemplate.insert(entityTest);
        return "redirect:/find";
    }

    //保存文档
    //保存与新增的主要区别在于，如果主键已经存在，新增抛出异常，保存修改数据
    @GetMapping(value = "/save")
    public String save() {
        //查询最后一条数据并更新
        Sort sort = Sort.by(Sort.Direction.DESC, "parameter3");
        EntityTest entityTest = mongoTemplate.findOne(Query.query(Criteria.where("")).with(sort), EntityTest.class);
        entityTest.setParameter4(3000);
        //保存数据的主键已经存在，则会对已经存在的数据修改
        mongoTemplate.save(entityTest);
        return "redirect:/find";
    }

    //删除文档
    @GetMapping(value = "/delete")
    public String delete() {
        //查询第一条数据并删除
        EntityTest entityTest = mongoTemplate.findOne(Query.query(Criteria.where("")), EntityTest.class);
        //remove方法不支持级联删除所以要单独删除子数据
        List<EntityTest1> entityTest1List = entityTest.getParameter5();
        for (EntityTest1 entityTest1 : entityTest1List) {
            mongoTemplate.remove(entityTest1);
        }
        //删除主数据
        mongoTemplate.remove(entityTest);
        return "redirect:/find";
    }

    //更新文档
    @GetMapping(value = "/update")
    public String update() {
        //将查询条件符合的全部文档更新
        Query query = new Query();
        Update update = Update.update("parameter2_", "update");
        mongoTemplate.updateMulti(query, update, EntityTest.class);
        return "redirect:/find";
    }

    @GetMapping("/find")
    @ResponseBody
    public Map<String, Object> find() {
        Map<String, Object> result = new HashMap<>();
        //查询小于当前时间的数据 并按时间倒序排列
        Sort sort = Sort.by(Sort.Direction.DESC, "parameter3");
        List<EntityTest> findTestList = mongoTemplate.find(Query.query(Criteria.where("parameter3").lt(new Date())).with(sort), EntityTest.class);
        result.put("findTestList", findTestList);
        //使用findOne查询结果集为多条，返回最上面的一条数据
        EntityTest findOneTest = mongoTemplate.findOne(Query.query(Criteria.where("parameter3").lt(new Date())).with(sort), EntityTest.class);
        System.out.println(findOneTest.getId().toString());
        result.put("findOneTest", findOneTest);
        //模糊查询
        List<EntityTest> findTestList1 = mongoTemplate.find(Query.query(Criteria.where("parameter3").lt(new Date()).and("parameter2").regex("es")), EntityTest.class);
        result.put("findTestList1", findTestList1);
        //分页查询 每页三行 第二页
        Pageable pageable = PageRequest.of(1, 3, sort);
        List<EntityTest> findTestList2 = mongoTemplate.find(Query.query(Criteria.where("parameter3").lt(new Date())).with(pageable), EntityTest.class);
        //共多少条
        long count = mongoTemplate.count(Query.query(Criteria.where("parameter3").lt(new Date())), EntityTest.class);
        //返回分页对象
        Page<EntityTest> page = new PageImpl<EntityTest>(findTestList2, pageable, count);
        result.put("page", page);

        //分页查询（通过起始行和数量也可以自己实现分页逻辑）
        List<EntityTest> findTestList3 = mongoTemplate.find(Query.query(Criteria.where("parameter3").lt(new Date())).with(sort).skip(3).limit(3), EntityTest.class);
        result.put("findTestList3", findTestList3);
        return result;
    }

}
