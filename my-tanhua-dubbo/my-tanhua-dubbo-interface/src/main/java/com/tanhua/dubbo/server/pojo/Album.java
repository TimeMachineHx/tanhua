package com.tanhua.dubbo.server.pojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
/**
* 相册表，⽤于存储⾃⼰发布的数据，每⼀个⽤户⼀张表进⾏存储
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "quanzi_album_{userId}")
public class Album implements java.io.Serializable {
 private static final long serialVersionUID = 432183095092216817L;
 @Id
 private ObjectId id; //主键id
 private ObjectId publishId; //发布id
 private Long created; //发布时间
}