package com.liujunit.springbootmongodb.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
//注释此类将被映射到数据库的一个集合（collection为集合名称）
@Document(collection = "ex_entity_test")
@CompoundIndexes({
        //联合索引 name 索引名称 、def 索引字段、parameter1升序、parameter3降序
        @CompoundIndex(name = "compound_index", def = "{'parameter1': 1, 'parameter3': -1}")
})
public class EntityTest implements Serializable {
    //标记ID字段
    @Id
    private ObjectId id;
    //创建单字段索引（默认ASCENDING 升序、DESCENDING 降序）
    @Indexed(direction = IndexDirection.DESCENDING)
    private Long parameter1;
    //修改映射到数据库的名称
    @Field("parameter2_")
    private String parameter2;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date parameter3;
    private Integer parameter4;
    //关联其他集合（不添加此注释时List将会保存具体的实体值，而添加了此注释List保存的是关联集合的id）
    @DBRef
    private List<EntityTest1> parameter5;
    //此字段不映射到数据库
    @Transient
    private Integer parameter6;
    //声明构造函数 用于实例化查询结果
    @PersistenceConstructor
    public EntityTest(Long parameter1, String parameter2, Date parameter3, Integer parameter4, List<EntityTest1> parameter5) {
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.parameter3 = parameter3;
        this.parameter4 = parameter4;
        this.parameter5 = parameter5;
    }
}
