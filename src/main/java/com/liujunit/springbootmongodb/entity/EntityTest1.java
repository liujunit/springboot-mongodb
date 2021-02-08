package com.liujunit.springbootmongodb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ex_entity_test1")
public class EntityTest1 implements Serializable {
    @Id
    private ObjectId id;
    //如果实体类没有为任何字段创建索引将不会自动创建集合
    @Indexed
    private Long parameter1;

    public EntityTest1(Long parameter1) {
        this.parameter1 = parameter1;
    }
}
