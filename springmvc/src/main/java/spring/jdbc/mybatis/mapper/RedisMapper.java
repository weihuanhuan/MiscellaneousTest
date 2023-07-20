package spring.jdbc.mybatis.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import spring.jdbc.mybatis.bean.MybatisRedis;

import java.util.List;
import java.util.Map;

public interface RedisMapper<T extends MybatisRedis> {

    @Select("SELECT * FROM redis WHERE id = #{id}")
    T selectRedisById(int id);

    T selectRedisByName(String name);

    List<T> listRedis();

    //使用 @param 来指定不同的参数在 mapper xml 中所对应的参数名字, 这使得我们可以混合使用多种不同的数据类型作为 mapper xml 中的入参
    List<T> findRedisByGroupNameAndCondition(@Param("groupName") String groupName, @Param("condition") Map<String, String> condition);

    int insertRedis(T redis);

    int deleteRedisByName(T redis);

    int updateRedisByName(T redis);

}
